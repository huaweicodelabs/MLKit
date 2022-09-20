/*
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hms.mlkit.vision.livenessdetection.test.activity;

import static com.huawei.hms.mlkit.vision.livenessdetection.test.activity.MainActivity.IS_CUSTOM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlkit.vision.livenessdetection.test.R;
import com.huawei.hms.mlkit.vision.livenessdetection.test.service.ScreenRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.ui.MaskingFaceView;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.DetectionInfo;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.FaceBitmapDataService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.InteractiveLivenessDetectionUtils;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.MediaRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.TimeUtils;

import java.io.File;

/**
 * 跳转检测成功页面
 */
public class InteractiveLivenessDetectionSuccessActivity extends AppCompatActivity {
    private static final String TAG = InteractiveLivenessDetectionSuccessActivity.class.getSimpleName();
    public static InteractiveLivenessDetectionSuccessActivity instance = null;

    private Button retestButton;
    private Button exitButton;
    private ImageView faceSuccessImageView;
    private Bitmap faceSuccessBitmap;
    private ImageView mBackImage;
    private MaskingFaceView mMaskingFaceView;
    private Boolean isCustom = false;

    private MediaProjectionManager mProjectionManager;
    private MediaRecordService mediaRecord;

    private int REQUEST_CODE_SCREEN = 100;
    private int displayWidth = 1080;
    private int displayHeight = 1920;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_detection_success);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ObsoleteSdkInt")
    private void init() {
        instance = this;
        Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.livenessbg));
        }
        Intent isCustomIntent = getIntent();
        if (isCustomIntent != null) {
            isCustom = isCustomIntent.getBooleanExtra(IS_CUSTOM, false);
        }
        FaceBitmapDataService instanceBitmap = FaceBitmapDataService.getInstance();
        if (instanceBitmap.getFaceBitmap() != null) {
            Log.d(TAG, "instanceBitmap.getFaceBitmap is not null");
            faceSuccessBitmap = instanceBitmap.getFaceBitmap();
        }
        retestButton = (Button) findViewById(R.id.retest_button);
        exitButton = (Button) findViewById(R.id.exit_button);
        faceSuccessImageView = (ImageView) findViewById(R.id.imageView);
        retestButton.setOnClickListener(new ClickListenerImpl());
        exitButton.setOnClickListener(new ClickListenerImpl());
        mBackImage = (ImageView) findViewById(R.id.img_back);
        mBackImage.setOnClickListener(new ClickListenerImpl());
        faceSuccessImageView.setImageBitmap(faceSuccessBitmap);
        mMaskingFaceView = (MaskingFaceView) findViewById(R.id.masking_face_view);
        if (DetectionInfo.getInstance().getFaceMasking()) {
            mMaskingFaceView.setVisibility(View.VISIBLE);
        } else {
            mMaskingFaceView.setVisibility(View.INVISIBLE);
        }
        if (InteractiveLivenessCustomDetectionActivity.instance != null) {
            InteractiveLivenessCustomDetectionActivity.instance.finish();
        }
        if (InteractiveLivenessDetectionActivity.instance != null) {
            InteractiveLivenessDetectionActivity.instance.finish();
        }
        if (InteractiveLivenessDetectionFailActivity.instance != null) {
            InteractiveLivenessDetectionFailActivity.instance.finish();
        }
    }

    private class ClickListenerImpl implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.retest_button:
                    if (InteractiveLivenessDetectionUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intentRetest;
                    if (isCustom) {
                        intentRetest = new Intent(
                                InteractiveLivenessDetectionSuccessActivity.this,
                                InteractiveLivenessCustomDetectionActivity.class);
                    } else {
                        intentRetest = new Intent(
                                InteractiveLivenessDetectionSuccessActivity.this,
                                InteractiveLivenessDetectionActivity.class);
                    }
                    intentRetest.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intentRetest);
                    break;

                case R.id.exit_button:
                    if (InteractiveLivenessDetectionUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intentExit =
                            new Intent(InteractiveLivenessDetectionSuccessActivity.this, MainActivity.class);
                    intentExit.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intentExit);
                    break;

                case R.id.img_back:
                    onBackPressed();
                    break;

                default:
                    break;
            }
        }
    }

    // stop screen record.
    private void stopScreenRecord() {
        Intent service = new Intent(this, ScreenRecordService.class);
        stopService(service);
        Toast.makeText(this, "录屏成功", Toast.LENGTH_SHORT).show();
    }

    // 开始录屏
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startRecord() {
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mProjectionManager != null) {
            Intent intent = mProjectionManager.createScreenCaptureIntent();
            PackageManager packageManager = getPackageManager();
            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                // 存在录屏授权的Activity
                startActivityForResult(intent, REQUEST_CODE_SCREEN);
            } else {
                Toast.makeText(this, "没有录屏权限！", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 停止录屏
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopRecord() {
        if (isCustom) {
            if (DetectionInfo.getInstance().isScreenRecording()) {
                if (DetectionInfo.getInstance().getMediaRecordService() != null) {
                    DetectionInfo.getInstance().getMediaRecordService().release();
                    DetectionInfo.getInstance().setScreenRecording(false);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkPermissionAndToActivity() {
        startRecord();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN) {
            if (resultCode == RESULT_OK) {
                try {
                    // mediaProjection 如果不在权限申请中回调，获取到的对象为空
                    MediaProjection mediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                    if (mediaProjection == null) {
                        Log.e(TAG, "media projection is null");
                        return;
                    }
                    String videoPath =
                            "/sdcard/Pictures/Screenshots/" + TimeUtils.getTimeString("yyyy-MM-dd_HH_mm_ss") + ".mp4";
                    DetectionInfo.getInstance().setVideoPath(videoPath);
                    File file = new File(videoPath); // 录屏生成文件
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    mediaRecord =
                            new MediaRecordService(
                                    displayWidth, displayHeight, 6000000, 1, mediaProjection, file.getAbsolutePath());
                    DetectionInfo.getInstance().setMediaRecordService(mediaRecord);
                    DetectionInfo.getInstance().setScreenRecording(true);
                    mediaRecord.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isCustom) {
                    Intent intent0 =
                            new Intent(
                                    InteractiveLivenessDetectionSuccessActivity.this,
                                    InteractiveLivenessDetectionActivity.class);
                    intent0.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intent0);
                } else {
                    Intent intent1 =
                            new Intent(
                                    InteractiveLivenessDetectionSuccessActivity.this,
                                    InteractiveLivenessCustomDetectionActivity.class);
                    intent1.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intent1);
                }
            } else {
                Toast.makeText(this, "录屏失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
