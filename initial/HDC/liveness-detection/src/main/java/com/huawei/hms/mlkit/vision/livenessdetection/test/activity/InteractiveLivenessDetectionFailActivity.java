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
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlkit.vision.livenessdetection.test.R;
import com.huawei.hms.mlkit.vision.livenessdetection.test.service.ScreenRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.ui.MediumBoldTextView;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.DetectionInfo;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.FileUtil;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.InteractiveLivenessDetectionUtils;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.MediaRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.TimeUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 跳转检测失败页面
 */
public class InteractiveLivenessDetectionFailActivity extends AppCompatActivity {
    private static final String TAG = InteractiveLivenessCustomDetectionActivity.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    public static InteractiveLivenessDetectionFailActivity instance = null;

    private static final String DETECTION_RESULT = "detection_result";

    private Button mRetestButton;
    private Button mExitButton;
    private TextView mDetectionFailedTextView;
    private MediumBoldTextView mFailureCauseTextView;
    private ImageView mDetectionFailedImageView;
    private ImageView mBackImage;
    private Boolean isCustom = false;

    private MediaProjectionManager mProjectionManager;
    private MediaRecordService mediaRecord;

    private int REQUEST_CODE_PERMISSIONS = 99;
    private int REQUEST_CODE_SCREEN = 100;
    private int displayWidth = 1080;
    private int displayHeight = 1920;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_detection_fail);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        instance = this;
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.livenessbg));
        }
        String srcFileString =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator
                        + "mlkit"
                        + File.separator
                        + "logs"
                        + File.separator
                        + "liveness";
        String zipFileString =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator
                        + "mlkit"
                        + File.separator
                        + "logs"
                        + File.separator
                        + "livenessZip";
        String oldFilePath = DetectionInfo.getInstance().getFilePathName();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String newFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator
                        + "Pictures"
                        + File.separator
                        + "Screenshots"
                        + File.separator
                        + simpleDateFormat.format(new Date())
                        + ".mp4";
        ;
        try {
            FileUtil.ZipFolder(srcFileString, zipFileString);
            Boolean deleteSuccess = FileUtil.deleteDirectory(srcFileString);
            FileUtil.copyFile(oldFilePath, newFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRetestButton = (Button) findViewById(R.id.retest_button);
        mExitButton = (Button) findViewById(R.id.exit_button);
        Intent intent = getIntent();
        String detectionResult = intent.getStringExtra(DETECTION_RESULT);
        mDetectionFailedTextView = (TextView) findViewById(R.id.detection_failed_textView);
        mFailureCauseTextView = (MediumBoldTextView) findViewById(R.id.failure_cause_textView);
        mFailureCauseTextView.setText(detectionResult);
        mDetectionFailedImageView = (ImageView) findViewById(R.id.detection_failed_imageView);
        mBackImage = (ImageView) findViewById(R.id.img_back);
        mBackImage.setOnClickListener(new ClickListenerImpl());
        mRetestButton.setOnClickListener(new ClickListenerImpl());
        mExitButton.setOnClickListener(new ClickListenerImpl());

        mDetectionFailedImageView.setImageResource(R.mipmap.detectionfailed);
        if (InteractiveLivenessCustomDetectionActivity.instance != null) {
            InteractiveLivenessCustomDetectionActivity.instance.finish();
        }
        if (InteractiveLivenessDetectionActivity.instance != null) {
            InteractiveLivenessDetectionActivity.instance.finish();
        }
        if (InteractiveLivenessDetectionSuccessActivity.instance != null) {
            InteractiveLivenessDetectionSuccessActivity.instance.finish();
        }
        Intent isCustomIntent = getIntent();
        if (isCustomIntent != null) {
            isCustom = isCustomIntent.getBooleanExtra(IS_CUSTOM, false);
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
                                InteractiveLivenessDetectionFailActivity.this,
                                InteractiveLivenessCustomDetectionActivity.class);
                    } else {
                        intentRetest = new Intent(
                                InteractiveLivenessDetectionFailActivity.this,
                                InteractiveLivenessDetectionActivity.class);
                    }
                    intentRetest.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intentRetest);
                    break;

                case R.id.exit_button:
                    if (InteractiveLivenessDetectionUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intentExit = new Intent(InteractiveLivenessDetectionFailActivity.this, MainActivity.class);
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
                                    InteractiveLivenessDetectionFailActivity.this,
                                    InteractiveLivenessDetectionActivity.class);
                    intent0.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intent0);
                } else {
                    Intent intent1 =
                            new Intent(
                                    InteractiveLivenessDetectionFailActivity.this,
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
