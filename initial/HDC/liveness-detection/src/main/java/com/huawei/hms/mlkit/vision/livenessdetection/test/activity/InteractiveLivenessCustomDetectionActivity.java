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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.mlkit.vision.livenessdetection.test.R;
import com.huawei.hms.mlkit.vision.livenessdetection.test.ui.MaskingFaceView;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.DetectionInfo;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.FaceBitmapDataService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.InteractiveLivenessDetectionResultEnum;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureConfig;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureError;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessCaptureResult;
import com.huawei.hms.mlsdk.interactiveliveness.MLInteractiveLivenessDetectView;
import com.huawei.hms.mlsdk.interactiveliveness.OnMLInteractiveLivenessDetectCallback;
import com.huawei.hms.mlsdk.interactiveliveness.action.GuideDetectionInfo;
import com.huawei.hms.mlsdk.interactiveliveness.action.InteractiveLivenessStateCode;
import com.huawei.hms.mlsdk.interactiveliveness.action.MLInteractiveLivenessConfig;

import java.util.ArrayList;
import java.util.List;

public class InteractiveLivenessCustomDetectionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = InteractiveLivenessCustomDetectionActivity.class.getSimpleName();

    private static final long TIME_OUT_THRESHOLD = 10000L;
    private static final int LEFT_HEIGHT = 0;
    private static final int TOP_HEIGHT = 0;
    private static final int FRAME_HEIGHT = 480;
    private static final int FACE_LEFT = 85;
    private static final int FACE_TOP = 103;
    private static final int FACE_RIGHT = 395;
    private static final int FACE_BOTTOM = 497;

    @SuppressLint("StaticFieldLeak")
    public static InteractiveLivenessCustomDetectionActivity instance = null;

    public static final int PERMISSION_REQUEST = 1;

    public static final String DETECTION_RESULT = "detection_result";

    private String[] permissions =
            new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> mPermissionList = new ArrayList<>();

    private MLInteractiveLivenessDetectView mlInteractiveLivenessDetectView;
    private FrameLayout mPreviewContainer;
    private TextView mTextView;
    private Switch mSwitch;
    private MaskingFaceView mMaskingFaceView;
    private ImageView mBackImage;

    private Boolean guideDetectionIsEnd = false;
    private Boolean isCustom = true;
    private Boolean isGo = false;
    private int frameRectLeft;
    private int frameRectTop;
    private int frameRectRight;
    private int frameRectBottom;
    private int faceRectLeft;
    private int faceRectTop;
    private int faceRectRight;
    private int faceRectBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_custom_detection);
        init();

        // 动作活体调用设置
        Rect frameRect = new Rect(frameRectLeft, frameRectTop, frameRectRight, frameRectBottom);
        Rect faceRect = new Rect(faceRectLeft, faceRectTop, faceRectRight, faceRectBottom);

        MLInteractiveLivenessConfig interactiveLivenessConfig = new MLInteractiveLivenessConfig.Builder().build();

        mlInteractiveLivenessDetectView =
                new MLInteractiveLivenessDetectView.Builder()
                        .setContext(this)
                        .setOptions(MLInteractiveLivenessCaptureConfig.DETECT_MASK)
                        .setFrameRect(frameRect)
                        .setType(MLInteractiveLivenessCaptureConfig.TYPE_INTERACTIVE)
                        .setActionConfig(interactiveLivenessConfig)
                        .setFaceRect(faceRect)
                        .setDetectionTimeOut(TIME_OUT_THRESHOLD)
                        .setDetectCallback(
                                new OnMLInteractiveLivenessDetectCallback() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onCompleted(MLInteractiveLivenessCaptureResult result) {
                                        Log.e(TAG, "result.getStateCode(): " + result.getStateCode());

                                        int actionStringId =
                                                MLInteractiveLivenessConfig.getActionDescByType(result.getActionType());
                                        int detectionResult = result.getStateCode();
                                        InteractiveLivenessDetectionResultEnum.customDetectionResultProcess(
                                                mTextView,
                                                actionStringId,
                                                GuideDetectionInfo.getInstance().firstGuideDetectionIsEnd,
                                                detectionResult,
                                                InteractiveLivenessCustomDetectionActivity.this,
                                                InteractiveLivenessDetectionFailActivity.class,
                                                isCustom);
                                        if (result.getStateCode() == InteractiveLivenessStateCode.ALL_ACTION_CORRECT) {
                                            FaceBitmapDataService faceBitmapDataService =
                                                    FaceBitmapDataService.getInstance();
                                            faceBitmapDataService.setFaceBitmap(result.getBitmap());
                                            Intent intentSuccess =
                                                    new Intent(
                                                            InteractiveLivenessCustomDetectionActivity.this,
                                                            InteractiveLivenessDetectionSuccessActivity.class);
                                            setResult(Activity.RESULT_OK, intentSuccess);
                                            intentSuccess.putExtra(IS_CUSTOM, isCustom);
                                            startActivity(intentSuccess);
                                        }
                                    }

                                    @Override
                                    public void onError(int error) {
                                        String failResult = null;
                                        switch (error) {
                                            case MLInteractiveLivenessCaptureError.CAMERA_NO_PERMISSION:
                                                failResult = "相机权限未获取";
                                                break;

                                            case MLInteractiveLivenessCaptureError.CAMERA_START_FAILED:
                                                failResult = "相机启动失败";
                                                break;

                                            case MLInteractiveLivenessCaptureError.DETECT_FACE_TIME_OUT:
                                                failResult = "人脸检测模块检测超时";
                                                break;

                                            case MLInteractiveLivenessCaptureError.USER_CANCEL:
                                            default:
                                                break;
                                        }
                                        if (failResult != null) {
                                            InteractiveLivenessCustomDetectionActivity.startUserActivity(
                                                    InteractiveLivenessCustomDetectionActivity.this,
                                                    InteractiveLivenessDetectionFailActivity.class,
                                                    isCustom,
                                                    failResult);
                                        }
                                    }
                                })
                        .build();

        mPreviewContainer.addView(mlInteractiveLivenessDetectView);
        mlInteractiveLivenessDetectView.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlInteractiveLivenessDetectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mlInteractiveLivenessDetectView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mlInteractiveLivenessDetectView.onResume();
    }

    /**
     * 界面初始化
     *
     * @author
     * @time 2021/11/8 14:58
     */
    @SuppressLint("ObsoleteSdkInt")
    private void init() {
        checkPermission();
        instance = this;
        DetectionInfo.getInstance().setScreenRecording(true);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.livenessbg));
        }
        if (InteractiveLivenessDetectionSuccessActivity.instance != null) {
            InteractiveLivenessDetectionSuccessActivity.instance.finish();
        }
        if (InteractiveLivenessDetectionFailActivity.instance != null) {
            InteractiveLivenessDetectionFailActivity.instance.finish();
        }
        Intent isCustomIntent = getIntent();
        if (isCustomIntent != null) {
            isCustom = isCustomIntent.getBooleanExtra(IS_CUSTOM, false);
        }
        GuideDetectionInfo.getInstance().firstGuideDetectionIsEnd = false;
        mPreviewContainer = findViewById(R.id.surface_layout);
        mTextView = findViewById(R.id.tipTextView);
        mSwitch = findViewById(R.id.masking_face_switchover);
        mMaskingFaceView = findViewById(R.id.masking_face_view);
        mBackImage = (ImageView) findViewById(R.id.img_back);
        mBackImage.setOnClickListener(this);
        mSwitch.setOnClickListener(this);

        DisplayMetrics outMetrics = new DisplayMetrics();
        int widthPixels = outMetrics.widthPixels;
        int frameWidth = widthPixels;

        frameRectLeft = dip2px(this, LEFT_HEIGHT);
        frameRectTop = dip2px(this, TOP_HEIGHT);
        frameRectRight = frameRectLeft + frameWidth;
        frameRectBottom = frameRectTop + dip2px(this, FRAME_HEIGHT);

        faceRectLeft = FACE_LEFT;
        faceRectTop = FACE_TOP;
        faceRectRight = FACE_RIGHT;
        faceRectBottom = FACE_BOTTOM;

        if (DetectionInfo.getInstance().getFaceMasking()) {
            mMaskingFaceView.setVisibility(View.VISIBLE);
            mSwitch.setChecked(true);
        } else {
            mMaskingFaceView.setVisibility(View.INVISIBLE);
            mSwitch.setChecked(false);
        }
    }

    /**
     * 权限检查
     *
     * @author
     * @time 2021/11/8 14:56
     */
    private void checkPermission() {
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {
        } else {
            String[] permissionsStringArray = mPermissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(
                    InteractiveLivenessCustomDetectionActivity.this, permissionsStringArray, PERMISSION_REQUEST);
        }
    }

    /**
     * dp转化
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return int
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Activity跳转静态方法
     *
     * @param context 上下文
     * @param cls 跳转对象activity
     * @param isCustom 是否是自定义页面
     * @param failResult 失败结果
     */
    public static void startUserActivity(Context context, Class cls, Boolean isCustom, String failResult) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(IS_CUSTOM, isCustom);
        intent.putExtra(DETECTION_RESULT, failResult);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startUserActivity(Context context, Class cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.masking_face_switchover:
                if (DetectionInfo.getInstance().getFaceMasking()) {
                    mMaskingFaceView.setVisibility(View.INVISIBLE);
                    mSwitch.setChecked(false);
                    DetectionInfo.getInstance().setFaceMasking(false);
                } else {
                    mMaskingFaceView.setVisibility(View.VISIBLE);
                    mSwitch.setChecked(true);
                    DetectionInfo.getInstance().setFaceMasking(true);
                }
                break;

            case R.id.img_back:
                onBackPressed();
                break;

            default:
                break;
        }
    }
}
