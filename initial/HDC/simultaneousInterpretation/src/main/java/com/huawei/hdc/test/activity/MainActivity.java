/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.hdc.test.activity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hdc.test.R;
import com.huawei.hdc.test.adapter.ResultAdapter;
import com.huawei.hdc.test.dialog.CustomDialog;
import com.huawei.hdc.test.entry.ItemBean;
import com.huawei.hdc.test.entry.ResultBean;
import com.huawei.hdc.test.utils.SysUtils;
import com.huawei.hdc.test.window.PopWindow;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConfig;
import com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationConstants;
import com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationListener;
import com.huawei.hms.mlsdk.si.speech.MLSimultaneousInterpretationRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView contentList;
    private TextView voiceList;
    private TextView originLan;
    private ImageView switchBtn;
    private TextView targetLan;
    private TextView contentTitle;
    private TextView voiceTitle;
    private RelativeLayout relVoice;
    private TextView startBtn;
    private String contentId;
    private TextView hintTv;
    private ListView listView;
    private LinearLayout layout_content;
    private LinearLayout layout_switch;

    private List<ResultBean> listData = null;
    private boolean isRecognition;
    private ResultAdapter adapter;
    private boolean isTouch;
    private long actionUpTiem = 0L;
    private long actionDownTime = 0L;

    private TextView originFootTv;
    private TextView translateFootTv;

    private String original = "";
    private String translate = "";
    private boolean isGranted;

    private PopWindow popWindow;
    private boolean btnClickFlag;
    private boolean switchBtnClickFlag;

    // 动态参数声明
    private String timbre;
    private int stage;

    private long tempSn = 0L;
    private long temptranssn = 0L;

    private static int TOUCH_STATE = 109;
    private static int TOUCH_STATE_MSG = 110;

    private long startTime = 01L;

    private static final String[] ALL_PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};

    private String privacy = "同声传译服务需要存储及录音使用权限，" +
            "使用本服务您的个人信息不会上传至华为服务器，只有在提交反馈信息时，" +
            "您所提交的内容才会上传至华为服务器处理，用来帮助我们分析问题并改进服务体验。" +
            "点击“同意”，即表示您同意上述内容及《同声传译服务与隐私的声明》。";
    private MLSimultaneousInterpretationRecognizer mlSimultaneousInterpretationRecognizer;
    private MLSimultaneousInterpretationConfig config;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        SysUtils.setStatusBarColor(this);

        String apiKey= AGConnectInstance.getInstance().getOptions().getString("client/api_key");
        MLApplication.getInstance().setApiKey(apiKey);

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

        initView();
        popWindow = new PopWindow(MainActivity.this);
        switchBtn.setOnClickListener(this);
        contentList.setOnClickListener(this);
        voiceList.setOnClickListener(this);
        startBtn.setOnClickListener(this);

        // 隐私协议弹框
        SharedPreferences sharedPreferences = getSharedPreferences("localStorage", MODE_PRIVATE);
        boolean agree = sharedPreferences.getBoolean("agree", false);
        if (!agree) {
            showPrivacyDialog();
        }

        config = new MLSimultaneousInterpretationConfig
                .Factory()
                .setSourceLanguage(MLSimultaneousInterpretationConstants.LAN_ZH_CN)
                .setTargetLanguage(MLSimultaneousInterpretationConstants.LAN_EN_US)
                .setResultType(MLSimultaneousInterpretationConstants.RESULT_TYPE_TEXT)
                .setPerson(MLSimultaneousInterpretationConstants.EN_MALE_VOICE)
                .create();
        mlSimultaneousInterpretationRecognizer = MLSimultaneousInterpretationRecognizer.getInstance();
        mlSimultaneousInterpretationRecognizer.setMLSimultaneousInterpretationListener(new SIRecognitionListener());

    }

    protected class SIRecognitionListener implements MLSimultaneousInterpretationListener {
        @Override
        public void onStartListening() {
            // 录音器开始接收声音。
            Log.d(TAG, "onStartListening: ");
        }

        @Override
        public void onVoiceDataReceived(byte[] data) {
            // 返回给用户原始的PCM音频流和音频能量，该接口并非运行在主线程中，返回结果需要在子线程中处理。
            Log.d(TAG, "onVoiceDataReceived: ");
        }

        @Override
        public void onRecognizingResults(Bundle partialResults) {
            long sn = partialResults.getLong(MLSimultaneousInterpretationConstants.RESULTS_SN);
            long transsn = partialResults.getLong(MLSimultaneousInterpretationConstants.RESULTS_TRANS_SN);
            original = partialResults.getString(MLSimultaneousInterpretationConstants.RESULTS_RECOGNIZING);
            translate = partialResults.getString(MLSimultaneousInterpretationConstants.RESULTS_TRANS_RECOGNIZING);

            synchronized (SIRecognitionListener.class) {
                // 原始文本显示逻辑
                if (!TextUtils.isEmpty(original)) {
                    ResultBean resultBean = new ResultBean();
                    if (sn != 0) {
                        if (tempSn == sn) {
                            if (sn <= listData.get(listData.size() - 1).getNumText()) {
                                listData.get(listData.size() - 1).setText(original);
                            }
                        } else {
                            resultBean.setText(original);
                            resultBean.setNumText(sn);
                            listData.add(resultBean);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.setSelection(listData.size() - 1);

                    if (sn != 0) {
                        tempSn = sn;
                    }
                }
                // 翻译文本显示逻辑
                if (!TextUtils.isEmpty(translate)) {
                    ResultBean resultBean = new ResultBean();
                    if (temptranssn == transsn) {
                        if (transsn <= listData.get(listData.size() - 1).getNumText()) {
                            listData.get((int) (transsn - 1)).setTranslateText(translate);
                        }
                    } else {
                        if (transsn != 0) {
                            if (transsn <= listData.get(listData.size() - 1).getNumText()) {
                                listData.get(listData.size() - 1).setTranslateText(translate);
                            } else {
                                resultBean.setTranslateText(translate);
                                resultBean.setNumText(transsn);
                                listData.add(resultBean);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (transsn != 0) {
                        temptranssn = transsn;
                    }
                }
                // 从SIRecognitionListener接收到持续语音识别的文本。
                Log.d(TAG, "onRecognizingResults原始文本: " + partialResults.getString(MLSimultaneousInterpretationConstants.RESULTS_RECOGNIZING));
                Log.d(TAG, "onRecognizingResults翻译文本: " + partialResults.getString(MLSimultaneousInterpretationConstants.RESULTS_TRANS_RECOGNIZING));

            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults原始文本: " + results.getString(MLSimultaneousInterpretationConstants.RESULTS_RECOGNIZED));
            Log.d(TAG, "onResults翻译文本: " + results.getString(MLSimultaneousInterpretationConstants.RESULTS_TRANS_RECOGNIZED));

        }

        @Override
        public void onError(int error, String errorMessage) {
            // 识别发生错误后调用该接口。
            Log.d(TAG, "onError: " + error + ":" + errorMessage);
        }

        @Override
        public void onState(int state, Bundle params) {
            // 通知应用状态发生改变。
            Log.d(TAG, "onState() called with: state = [" + state + "], params = [" + params + "]");
        }
    }

    private void initView() {
        contentList = findViewById(R.id.contentList);
        voiceList = findViewById(R.id.voiceList);
        relVoice = findViewById(R.id.rel_voice);
        originLan = findViewById(R.id.originLan);
        switchBtn = findViewById(R.id.switchBtn);
        targetLan = findViewById(R.id.targetLan);
        startBtn = findViewById(R.id.startBtn);
        contentTitle = findViewById(R.id.contentTitle);
        voiceTitle = findViewById(R.id.voiceTitle);
        listView = findViewById(R.id.listView);
        hintTv = findViewById(R.id.hintTv);
        layout_content = findViewById(R.id.layout_content);
        layout_switch = findViewById(R.id.layout_switch);

        stage = 0;
        initAdapter();
    }

    private void initAdapter() {
        listView.setSelector(new ColorDrawable());
        listData = Collections.synchronizedList(new ArrayList<>());
        View footView = (View) getLayoutInflater().inflate(R.layout.footview_layou, null, false);
        originFootTv = footView.findViewById(R.id.originTv);
        translateFootTv = footView.findViewById(R.id.translateTv);
        listView.addFooterView(footView);
        adapter = new ResultAdapter(this, listData);
        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    isTouch = true;
                    actionDownTime = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    actionDownTime = System.currentTimeMillis();
                    isTouch = false;
                    long intervalTime = actionUpTiem - actionDownTime;
                    if (Math.abs(intervalTime) > 1000) {
                        handler.sendEmptyMessage(TOUCH_STATE);
                    }
                }
                return false;
            }
        });
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();

        for (String perm : getAllPermission()) {
            if (PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, perm)) {
                allNeededPermissions.add(perm);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, allNeededPermissions.toArray(new String[0]), 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contentList:
                popWindow.setType(0);
                popWindow.show();
                popWindow.setItemChangeListener(new PopWindow.ItemChangeListerer() {
                    @Override
                    public void itemChange(ItemBean itemBean) {
                        Log.e(TAG, "itemChangeconten: " + itemBean.getName() + "--" + itemBean.getId());
                        contentList.setText(itemBean.getName() + "");
                        contentId = itemBean.getId();
                        stage = Integer.parseInt(itemBean.getId());
                        if (stage == 1) {
                            if (!TextUtils.isEmpty(timbre)) {
                                stage = 2;
                            }
                        }
                        relVoice.setVisibility("0".equals(contentId) ? View.GONE : View.VISIBLE);
                        translateFootTv.setVisibility("0".equals(contentId) ? View.GONE : View.VISIBLE);
                        setConfig();
                    }
                });
                break;

            case R.id.voiceList:
                popWindow.setType(1);
                popWindow.showVoice();
                popWindow.setItemChangeListener(new PopWindow.ItemChangeListerer() {
                    @Override
                    public void itemChange(ItemBean itemBean) {
                        Log.e(TAG, "itemChangevoceList: " + itemBean.getName() + "--" + itemBean.getId());
                        voiceList.setText(itemBean.getName() + "");
                        timbre = itemBean.getId();
                        if (!TextUtils.isEmpty(timbre)) {
                            stage = 2;
                        } else {
                            timbre = null;
                            stage = 1;
                        }
                        setConfig();
                    }
                });
                break;

            case R.id.switchBtn:
                switchBtnClickFlag = !switchBtnClickFlag;
                originLan.setText(switchBtnClickFlag ? R.string.english : R.string.chinese);
                targetLan.setText(switchBtnClickFlag ? R.string.chinese : R.string.english);
                popWindow.setLanguageType(switchBtnClickFlag ? "zh" : "en");
                relVoice.setVisibility(View.GONE);
                contentList.setText(R.string.asrText);
                contentId = "0";
                stage = 0;
                voiceList.setText(R.string.notBroadcast);
                setConfig();
                break;

            case R.id.startBtn:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED) {
                } else {
                    if (isGranted) {
                        Toast.makeText(this, R.string.permissionStr, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!allPermissionsGranted()) {
                        getRuntimePermissions();
                    }
                    return;
                }
                btnClickFlag = !btnClickFlag;
                viewSwitch(btnClickFlag);
                isRecognition = !isRecognition;
                if (isRecognition) {
                    // 清空序列号 防止切换模式闪退
                    tempSn = 0L;
                    temptranssn = 0L;
                    if (listData != null) {
                        listData.clear();
                        adapter.notifyDataSetChanged();
                    }
                    mlSimultaneousInterpretationRecognizer.startRecognizing(config);
                    startTime = System.currentTimeMillis();
                    original = "";
                    translate = "";
                    Toast.makeText(this, "开始", Toast.LENGTH_SHORT).show();
                } else {
                    if (mlSimultaneousInterpretationRecognizer != null) {
                        mlSimultaneousInterpretationRecognizer.destroy();
                        originFootTv.setText("");
                        translateFootTv.setText("");
                        Toast.makeText(this, "关闭", Toast.LENGTH_SHORT).show();
                    }
                }
                originFootTv.setText("");
                translateFootTv.setText("");
                break;
        }
    }

    private void setConfig() {
        if (switchBtnClickFlag) {
            config = new MLSimultaneousInterpretationConfig
                    .Factory()
                    .setSourceLanguage(MLSimultaneousInterpretationConstants.LAN_EN_US)
                    .setTargetLanguage(MLSimultaneousInterpretationConstants.LAN_ZH_CN)
                    .setResultType(String.valueOf(stage))
                    .setPerson(timbre)
                    .create();
        } else {
            config = new MLSimultaneousInterpretationConfig
                    .Factory()
                    .setSourceLanguage(MLSimultaneousInterpretationConstants.LAN_ZH_CN)
                    .setTargetLanguage(MLSimultaneousInterpretationConstants.LAN_EN_US)
                    .setResultType(String.valueOf(stage))
                    .setPerson(timbre)
                    .create();
            Log.d(TAG, "contentId: " + contentId);
            Log.d(TAG, "stage: " + stage);
            Log.d(TAG, "setConfigtimbre: " + timbre);
        }
    }

    public static List<String> getAllPermission() {
        return Collections.unmodifiableList(Arrays.asList(ALL_PERMISSION));
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == TOUCH_STATE) {
                // 滚动到底部
                if (!isTouch) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setSelection(listData.size() - 1);
                                }
                            });
                        }
                    }, 0);
                }
            } else if (msg.what == TOUCH_STATE_MSG) {
                // 滚动到底部
                long time = System.currentTimeMillis() - actionDownTime;
                if (time > 3000) {
                    listView.setSelector(listData.size() - 1);
                } else {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listView.setSelection(listData.size() - 1);
                                }
                            });
                        }
                    }, time);
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void viewSwitch(boolean btnClickFlag) {
        startBtn.setText(btnClickFlag ? R.string.finishRecognitionBtn : R.string.startRecognitionBtn);
        startBtn.setBackground(btnClickFlag ? getResources().getDrawable(R.drawable.btn_circle__red_bg) : getResources().getDrawable(R.drawable.btn_circle_bg));
        switchBtn.setImageDrawable(btnClickFlag ? getResources().getDrawable(R.drawable.ic_switch_grey) : getResources().getDrawable(R.drawable.ic_switch));
        layout_content.setForeground(btnClickFlag ? getDrawable(R.drawable.layout_item_circle_grey_bg) : getDrawable(R.drawable.layout_item_circle_white_bg));
        layout_switch.setForeground(btnClickFlag ? getDrawable(R.drawable.layout_item_circle_grey_bg) : getDrawable(R.drawable.layout_item_circle_white_bg));
        // 设置布局不可点击置为灰色和白色可点击之间相互切换
        originLan.setEnabled(!btnClickFlag);
        switchBtn.setEnabled(!btnClickFlag);
        targetLan.setEnabled(!btnClickFlag);
        contentList.setEnabled(!btnClickFlag);
        voiceList.setEnabled(!btnClickFlag);
        hintTv.setVisibility(View.GONE);
        originLan.setTextColor(!btnClickFlag ? getResources().getColor(R.color.defaultColor) : getResources().getColor(R.color.clickColor));
        targetLan.setTextColor(!btnClickFlag ? getResources().getColor(R.color.defaultColor) : getResources().getColor(R.color.clickColor));
        contentTitle.setTextColor(!btnClickFlag ? getResources().getColor(R.color.defaultColor) : getResources().getColor(R.color.clickColor));
        voiceTitle.setTextColor(!btnClickFlag ? getResources().getColor(R.color.defaultColor) : getResources().getColor(R.color.clickColor));
    }

    /**
     * 弹出隐私框
     */
    private void showPrivacyDialog() {
        CustomDialog.Builder customDialogBuilder = new CustomDialog.Builder(MainActivity.this);
        customDialogBuilder.setMessage(privacy);
        customDialogBuilder.setTitle("同声传译服务");
        customDialogBuilder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            /**
             * onClick
             *
             * @param dialog  dialog
             * @param which which
             */
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        customDialogBuilder.setNegativeButton("同意",
                new DialogInterface.OnClickListener() {
                    /**
                     * onClick
                     *
                     * @param dialog dialog
                     * @param which which
                     */
                    public void onClick(DialogInterface dialog, int which) {
                        // 设置你的操作事项
                        SharedPreferences sharedPreferences = getSharedPreferences("localStorage", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putBoolean("agree", true);
                        edit.commit();
                        dialog.dismiss();
                    }
                });
        CustomDialog customDialog = customDialogBuilder.create();
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    finish();
                }
                return true;
            }
        });
        customDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        isGranted = true;
                    }
                }
            }
        }
    }


}
