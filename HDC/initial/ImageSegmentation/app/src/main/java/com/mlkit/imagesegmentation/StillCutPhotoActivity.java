/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mlkit.imagesegmentation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;
import com.mlkit.imagesegmentation.callback.ImageSegmentationResultCallBack;
import com.mlkit.imagesegmentation.overlay.GraphicOverlay;
import com.mlkit.imagesegmentation.util.BitmapUtils;
import com.mlkit.imagesegmentation.util.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StillCutPhotoActivity extends BaseActivity implements ImageSegmentationResultCallBack,View.OnClickListener {
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final String[] ALL_PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private RelativeLayout relativeLayoutLoadPhoto;

    private RelativeLayout relativeLayoutCut;

    private RelativeLayout relativeLayoutBackgrounds;

    private RelativeLayout relativeLayoutSave;

    private GraphicOverlay graphicOverlay;

    private ImageView preview;

    private Uri imageUri;

    private Uri imgBackgroundUri;

    private Bitmap originBitmap;

    private Bitmap backgroundBitmap;

    private static String TAG = "CaptureImageFragment";

    private Integer maxWidthOfImage;

    private Integer maxHeightOfImage;

    boolean isLandScape;

    private int REQUEST_CHOOSE_ORIGINPIC = 2001;

    private int REQUEST_CHOOSE_BACKGROUND = 2002;

    private static final int REQUEST_TAKE_PHOTOR = 2003;

    private static final String KEY_IMAGE_URI = "KEY_IMAGE_URI";

    private static final String KEY_IMAGE_MAX_WIDTH = "KEY_IMAGE_MAX_WIDTH";

    private static final String KEY_IMAGE_MAX_HEIGHT = "KEY_IMAGE_MAX_HEIGHT";

    private Bitmap processedImage;

    // Portrait foreground image.
    private Bitmap foreground;

    boolean isPermissionRequested;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.activity_still_cut);
        this.preview = this.findViewById(R.id.previewPane);
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StillCutPhotoActivity.this.finish();
            }
        });
        this.isLandScape =
                (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        this.initView();

        // Checking Camera Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED) {
        } else {
            this.checkPermission();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean hasAllGranted = true;
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    showWaringDialog();
                } else {
                    Toast.makeText(this, R.string.permissions, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        this.relativeLayoutLoadPhoto = this.findViewById(R.id.relativate_chooseImg);
        this.relativeLayoutLoadPhoto.setOnClickListener(this);
        this.relativeLayoutCut = this.findViewById(R.id.relativate_cut);
        this.relativeLayoutCut.setOnClickListener(this);
        this.relativeLayoutBackgrounds = this.findViewById(R.id.relativate_backgrounds);
        this.relativeLayoutBackgrounds.setOnClickListener(this);
        this.relativeLayoutSave = this.findViewById(R.id.relativate_save);
        this.relativeLayoutSave.setOnClickListener(this);
        this.preview = this.findViewById(R.id.previewPane);
        this.graphicOverlay = this.findViewById(R.id.previewOverlay);
    }

    private void selectLocalImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == this.REQUEST_CHOOSE_ORIGINPIC) && (resultCode == Activity.RESULT_OK)) {
            // In this case, imageUri is returned by the chooser, save it.
            this.imageUri = data.getData();
            this.loadOriginImage();
        } else if ((requestCode == this.REQUEST_CHOOSE_BACKGROUND)) {
            if (data == null) {
                Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            } else {
                this.imgBackgroundUri = data.getData();
                this.loadOriginImage();
                Pair<Integer, Integer> targetedSize = this.getTargetSize();
                this.backgroundBitmap = BitmapUtils.loadFromPath(StillCutPhotoActivity.this, this.imgBackgroundUri, targetedSize.first, targetedSize.second);
                this.changeBackground(this.backgroundBitmap);
            }
        }
    }

    private void changeBackground(Bitmap backgroundBitmap) {
        if (this.isChosen(this.foreground) && this.isChosen(backgroundBitmap)) {
            BitmapDrawable drawable = new BitmapDrawable(backgroundBitmap);
            this.preview.setDrawingCacheEnabled(true);
            this.preview.setBackground(drawable);
            this.preview.setImageBitmap(this.foreground);
            this.processedImage = Bitmap.createBitmap(this.preview.getDrawingCache());
            this.preview.setDrawingCacheEnabled(false);
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private MLImageSegmentationAnalyzer analyzer;

    private void createImageTransactor() {
        // todo step 2: add on-device ImageSegmentation analyzer


        if (this.isChosen(this.originBitmap)) {
            // todo step 3: add on-device ImageSegmentation mlFrame

           // todo step 4: add on-device ImageSegmentation task

            task.addOnSuccessListener(new OnSuccessListener<MLImageSegmentation>() {
                @Override
                public void onSuccess(MLImageSegmentation mlImageSegmentationResults) {
                    // Transacting logic for segment success.
                    if (mlImageSegmentationResults != null) {
                        // todo step 5: add on-device ImageSegmentation result.

                    } else {
                        StillCutPhotoActivity.this.displayFailure();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Transacting logic for segment failure.
                    StillCutPhotoActivity.this.displayFailure();
                    return;
                }
            });
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void displayFailure() {
        Toast.makeText(this.getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
    }

    private boolean isChosen(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        } else {
            return true;
        }
    }

    private void loadOriginImage() {
        if (this.imageUri == null) {
            return;
        }
        // Clear the overlay first.
        this.graphicOverlay.clear();
        Pair<Integer, Integer> targetedSize = this.getTargetSize();
        int targetWidth = targetedSize.first;
        int targetHeight = targetedSize.second;
        this.originBitmap = BitmapUtils.loadFromPath(StillCutPhotoActivity.this, this.imageUri, targetWidth, targetHeight);
        // Determine how much to scale down the image.
        Log.i("imageSlicer", "resized image size width:" + this.originBitmap.getWidth() + ",height: " + this.originBitmap.getHeight());
        this.preview.setImageBitmap(this.originBitmap);
    }

    // Returns max width of image.
    private Integer getMaxWidthOfImage() {
        if (this.maxWidthOfImage == null) {
            if (this.isLandScape) {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getHeight();
            } else {
                this.maxWidthOfImage = ((View) this.preview.getParent()).getWidth();
            }
        }
        return this.maxWidthOfImage;
    }

    // Returns max height of image.
    private Integer getMaxHeightOfImage() {
        if (this.maxHeightOfImage == null) {
            if (this.isLandScape) {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getWidth();
            } else {
                this.maxHeightOfImage = ((View) this.preview.getParent()).getHeight();
            }
        }
        return this.maxHeightOfImage;
    }

    // Gets the targeted size(width / height).
    private Pair<Integer, Integer> getTargetSize() {
        Integer targetWidth;
        Integer targetHeight;
        Integer maxWidth = this.getMaxWidthOfImage();
        Integer maxHeight = this.getMaxHeightOfImage();
        targetWidth = this.isLandScape ? maxHeight : maxWidth;
        targetHeight = this.isLandScape ? maxWidth : maxHeight;
        Log.i(StillCutPhotoActivity.TAG, "height:" + targetHeight + ",width:" + targetWidth);
        return new Pair<>(targetWidth, targetHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, "Stop analyzer failed: " + e.getMessage());
            }
        }
        this.imageUri = null;
        this.imgBackgroundUri = null;
        BitmapUtils.recycleBitmap(this.originBitmap, this.backgroundBitmap, this.foreground, this.processedImage);
        if (this.graphicOverlay != null) {
            this.graphicOverlay.clear();
            this.graphicOverlay = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(StillCutPhotoActivity.KEY_IMAGE_URI, this.imageUri);
        if (this.maxWidthOfImage != null) {
            outState.putInt(StillCutPhotoActivity.KEY_IMAGE_MAX_WIDTH, this.maxWidthOfImage);
        }
        if (this.maxHeightOfImage != null) {
            outState.putInt(StillCutPhotoActivity.KEY_IMAGE_MAX_HEIGHT, this.maxHeightOfImage);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void callResultBitmap(Bitmap bitmap) {
        this.processedImage = bitmap;
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            for (String perm : getAllPermission()) {
                if (PackageManager.PERMISSION_GRANTED != this.checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                }
            }

            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[0]), 1);
            }
        }
    }


    public static List<String> getAllPermission() {
        return Collections.unmodifiableList(Arrays.asList(ALL_PERMISSION));
    }

    private void showWaringDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.permissions)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Guide the user to the setting page for manual authorization.
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Instruct the user to perform manual authorization. The permission request fails.
                        finish();
                    }
                }).setOnCancelListener(dialogClick);
        dialog.setCancelable(false);
        dialog.show();
    }

    static DialogInterface.OnCancelListener  dialogClick =new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            //Instruct the user to perform manual authorization. The permission request fails.
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativate_chooseImg:
                StillCutPhotoActivity.this.selectLocalImage(StillCutPhotoActivity.this.REQUEST_CHOOSE_ORIGINPIC);
                break;
            case R.id.relativate_cut:
                // Outline the edge.
                if (StillCutPhotoActivity.this.imageUri == null) {
                    Toast.makeText(StillCutPhotoActivity.this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                } else {
                    StillCutPhotoActivity.this.createImageTransactor();
                    Toast.makeText(StillCutPhotoActivity.this.getApplicationContext(), R.string.cut_success, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.relativate_backgrounds:
                // Replace background.

                if (StillCutPhotoActivity.this.imageUri == null) {
                    Toast.makeText(StillCutPhotoActivity.this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
                } else {
                    StillCutPhotoActivity.this.selectLocalImage(StillCutPhotoActivity.this.REQUEST_CHOOSE_BACKGROUND);
                }

                break;
            case R.id.relativate_save:
                // Save the processed picture.
                if (StillCutPhotoActivity.this.processedImage == null) {
                    Toast.makeText(StillCutPhotoActivity.this.getApplicationContext(), R.string.no_pic_neededSave, Toast.LENGTH_SHORT).show();
                } else {
                    ImageUtils imageUtils = new ImageUtils(StillCutPhotoActivity.this.getApplicationContext());
                    imageUtils.saveToAlbum(StillCutPhotoActivity.this.processedImage);
                    Toast.makeText(StillCutPhotoActivity.this.getApplicationContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }
}
