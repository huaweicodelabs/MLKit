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

import static com.huawei.hms.mlkit.vision.livenessdetection.test.activity.InteractiveLivenessCustomDetectionActivity.PERMISSION_REQUEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hms.mlkit.vision.livenessdetection.test.R;
import com.huawei.hms.mlkit.vision.livenessdetection.test.service.ScreenRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.ui.CustomDialog;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.DetectionInfo;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.InteractiveLivenessDetectionUtils;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.MediaRecordService;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static MainActivity instance = null;
    public static final String IS_CUSTOM = "isCustom";
    public static final int DARK_THEME_FLAG = 101;
    public static final int NORMAL_THEME_FLAG = 100;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private long exitTime = 0L;

    private Boolean isCustom = false;

    private TextView tittleTextView;
    private TextView customTextView;
    private Button mButtonDefault;
    private Button mButtonCustom;
    private Switch mSwitch;
    private ImageView mImageView;
    private LinearLayout defaultLinearLayout;
    private LinearLayout customLinearLayout;
    private CustomDialog.Builder customDialogBuilder;

    private String[] permissions =
            new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
    private List<String> mPermissionList = new ArrayList<>();

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
        setContentView(R.layout.activity_main);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        instance = this;
        getScreenBaseInfo();
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.livenessbg));
        }
        tittleTextView = (TextView) findViewById(R.id.action_detection_textView);
        customTextView = (TextView) findViewById(R.id.custom_textView);
        mButtonDefault = (Button) findViewById(R.id.default_button);
        mButtonCustom = (Button) findViewById(R.id.custom_button);
        mSwitch = (Switch) findViewById(R.id.switchover);
        mImageView = (ImageView) findViewById(R.id.demo_imageView);
        defaultLinearLayout = (LinearLayout) findViewById(R.id.default_linearLayout);
        customLinearLayout = (LinearLayout) findViewById(R.id.custom_linearLayout);

        mButtonDefault.setOnClickListener(new ClickListenerImpl());
        mButtonCustom.setOnClickListener(new ClickListenerImpl());
        mSwitch.setOnClickListener(new ClickListenerImpl());
        mImageView.setImageResource(R.mipmap.interactive_liveness_detection);

        if (InteractiveLivenessCustomDetectionActivity.instance != null) {
            InteractiveLivenessCustomDetectionActivity.instance.finish();
        }

        if (InteractiveLivenessDetectionSuccessActivity.instance != null) {
            InteractiveLivenessDetectionSuccessActivity.instance.finish();
        }

        if (InteractiveLivenessDetectionFailActivity.instance != null) {
            InteractiveLivenessDetectionFailActivity.instance.finish();
        }

        if (PrivacyActivity.instance != null) {
            PrivacyActivity.instance.finish();
        }
        Intent isCustomIntent = getIntent();
        isCustom = isCustomIntent.getBooleanExtra(IS_CUSTOM, false);
        mSwitch.setChecked(isCustom);
    }

    private class ClickListenerImpl implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.default_button:
                    if (InteractiveLivenessDetectionUtils.isFastDoubleClick()) {
                        return;
                    }
                    DetectionInfo.getInstance().setFaceMasking(false);
                    checkPermissionAndToActivity();
                    break;

                case R.id.custom_button:
                    if (InteractiveLivenessDetectionUtils.isFastDoubleClick()) {
                        return;
                    }
                    checkPermissionAndToActivity();
                    break;

                case R.id.switchover:
                    if (!isCustom) {
                        defaultLinearLayout.setVisibility(View.GONE);
                        customLinearLayout.setVisibility(View.VISIBLE);
                        isCustom = true;
                        mSwitch.setChecked(true);
                    } else {
                        defaultLinearLayout.setVisibility(View.VISIBLE);
                        customLinearLayout.setVisibility(View.GONE);
                        isCustom = false;
                        mSwitch.setChecked(false);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 获取屏幕基本信息
     */
    private void getScreenBaseInfo() {
        // A structure describing general information about a display, such as its size, density, and font scaling.
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;
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
                    mediaRecord.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isCustom) {
                    Intent intent0 = new Intent(MainActivity.this, InteractiveLivenessDetectionActivity.class);
                    intent0.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intent0);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, InteractiveLivenessCustomDetectionActivity.class);
                    intent1.putExtra(IS_CUSTOM, isCustom);
                    startActivity(intent1);
                }
            } else {
                Toast.makeText(this, "录屏失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // start screen record
    private void startScreenRecord() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            // Manages the retrieval of certain types of MediaProjection tokens.
            MediaProjectionManager mediaProjectionManager =
                    (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            // Returns an Intent that must passed to startActivityForResult() in order to start screen capture.
            Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(permissionIntent, 1000);
        }
    }

    // stop screen record.
    private void stopScreenRecord() {
        Intent service = new Intent(this, ScreenRecordService.class);
        stopService(service);
        Toast.makeText(this, "录屏成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long exitDuration = 2000L;
            if ((System.currentTimeMillis() - exitTime) > exitDuration) {
                Toast.makeText(getApplicationContext(), R.string.exit_tip, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkPermissionAndToActivity() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        if (mPermissionList.isEmpty()) {
            Intent intent;
            if (!isCustom) {
                intent = new Intent(MainActivity.this, InteractiveLivenessDetectionActivity.class);
            } else {
                intent = new Intent(MainActivity.this, InteractiveLivenessCustomDetectionActivity.class);
            }
            intent.putExtra(IS_CUSTOM, isCustom);
            startActivity(intent);
        } else {
            String[] permissionsStringArray = mPermissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false; // 有权限没有通过
        if (requestCode == PERMISSION_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            if (hasPermissionDismiss) {
            } else {
                Intent intent;
                if (!isCustom) {
                    intent = new Intent(MainActivity.this, InteractiveLivenessDetectionActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, InteractiveLivenessCustomDetectionActivity.class);
                }
                intent.putExtra(IS_CUSTOM, isCustom);
                startActivity(intent);
            }
        }
    }

    /**
     * 是否是暗主题
     *
     * @param context 上下文
     * @return 布尔值
     */
    public static boolean getDarkTheme(Context context) {
        boolean isDarkTheme = false;
        if (getCurentTheme(context.getApplicationContext(), com.huawei.hms.mlsdk.interactiveliveness.R.color.livenessbg)
                == DARK_THEME_FLAG) {
            isDarkTheme = true;
        }
        return isDarkTheme;
    }

    /**
     * 获取当前主题
     *
     * @param context 上下文
     * @param id      color的id
     * @return 主题
     */
    public static int getCurentTheme(Context context, int id) {
        if (context == null) {
            return 100;
        }
        if (context.getResources().getColor(id) == Color.parseColor("#FFFFFFFF")) {
            return NORMAL_THEME_FLAG;
        }
        if (context.getResources().getColor(id) == Color.parseColor("#000000")) {
            return DARK_THEME_FLAG;
        }
        return NORMAL_THEME_FLAG;
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
        if (DetectionInfo.getInstance().isScreenRecording()) {
            if (DetectionInfo.getInstance().getMediaRecordService() != null) {
                DetectionInfo.getInstance().getMediaRecordService().release();
                DetectionInfo.getInstance().setScreenRecording(false);
            }
        }
    }
}
