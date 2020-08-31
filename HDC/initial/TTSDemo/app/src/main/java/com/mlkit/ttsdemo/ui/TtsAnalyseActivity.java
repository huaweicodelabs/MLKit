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

package com.mlkit.ttsdemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;
import com.mlkit.ttsdemo.R;

public class TtsAnalyseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TtsAnalyseActivity.class.getSimpleName();
    private static final int HANDLE_CODE = 1;
    private static final String HANDLE_KEY = "text";
    private EditText mEditText;
    private TextView mTextView;
    private ImageView clear;

    public static final String API_KEY = "client/api_key";

    MLTtsEngine mlTtsEngine;
    MLTtsConfig mlConfigs;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case HANDLE_CODE:
                    String text = message.getData().getString(HANDLE_KEY);
                    mTextView.setText(text + "\n");
                    Log.e(TAG, text);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_voice_tts);
        this.findViewById(R.id.btn_speak).setOnClickListener(this);
        this.findViewById(R.id.btn_stop_speak).setOnClickListener(this);
        this.mEditText = this.findViewById(R.id.edit_input);
        this.mTextView = this.findViewById(R.id.textView);
        this.clear = findViewById(R.id.close);
        this.clear.setOnClickListener(this);
        // Set ApiKey.
        setApiKey();
        // Method 1: Use the default parameter settings to create a TTS engine.
        // In the default settings, the source language is Chinese, the Chinese female voice is used,
        // the voice speed is 1.0 (1x), and the volume is 1.0 (1x).
        // MLTtsConfig mlConfigs = new MLTtsConfig();
        // Method 2: Use customized parameter settings to create a TTS engine.
        // todo step 4 add on-device tts config

        // todo step 5 add on-device tts mlTtsEngine

        // todo step 6 add on-device tts callback

    }

    public void setApiKey(){
        // todo step 3: add on-device tts setApiKey

    }

    /**
     * TTS callback function.. If you want to use TTS,
     * you need to apply for an agconnect-services.json file in the developer
     * alliance(https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc),
     * replacing the sample-agconnect-services.json in the project.
     */
    MLTtsCallback callback = new MLTtsCallback() {
        @Override
        public void onError(String taskId, MLTtsError err) {
            // Processing logic for TTS failure.
            String str = "TaskID: " + taskId + ", error:" + err;
            displayResult(str);
        }

        @Override
        public void onWarn(String taskId, MLTtsWarn warn) {
            // Alarm handling without affecting service logic.
            String str = "TaskID: " + taskId + ", warn:" + warn;
            displayResult(str);
        }

        @Override
        public void onRangeStart(String taskId, int start, int end) {
            // Process the mapping between the currently played segment and text.
            String str = "TaskID: " + taskId + ", onRangeStart [" + start + "，" + end + "]";
            displayResult(str);
        }

        @Override
        public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment, int i, Pair<Integer, Integer> pair, Bundle bundle) {
            //  Audio stream callback API, which is used to return the synthesized audio data to the app.
            //  taskId: ID of an audio synthesis task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One audio synthesis task corresponds to an audio synthesis queue.
            //  range: text area where the audio segment to be transmitted is located; range.first (included): start position; range.second (excluded): end position.
        }

        @Override
        // Callback method of a TTS event. eventName: event name. The events are as follows:
        // MLTtsConstants.EVENT_PLAY_RESUME: playback resumption.
        // MLTtsConstants.EVENT_PLAY_PAUSE: playback pause.
        // MLTtsConstants.EVENT_PLAY_STOP: playback stop.
        public void onEvent(String taskId, int eventId, Bundle bundle) {
            String str = "TaskID: " + taskId + ", eventName:" + eventId;
            // Callback method of an audio synthesis event. eventId: event name.
            switch (eventId) {
                case MLTtsConstants.EVENT_PLAY_START:
                    //  Called when playback starts.
                    break;
                case MLTtsConstants.EVENT_PLAY_STOP:
                    // Called when playback stops.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED);
                    str += " " + isInterrupted;
                    break;
                case MLTtsConstants.EVENT_PLAY_RESUME:
                    //  Called when playback resumes.
                    break;
                case MLTtsConstants.EVENT_PLAY_PAUSE:
                    // Called when playback pauses.
                    break;

                /*//Pay attention to the following callback events when you focus on only synthesized audio data but do not use the internal player for playback:
                case MLTtsConstants.EVENT_SYNTHESIS_START:
                    //  Called when TTS starts.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_END:
                    // Called when TTS ends.
                    break;
                case MLTtsConstants.EVENT_SYNTHESIS_COMPLETE:
                    // TTS is complete. All synthesized audio streams are passed to the app.
                    boolean isInterrupted = bundle.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED);
                    break;*/
                default:
                    break;
            }

            displayResult(str);
        }
    };

    private void displayResult(String str) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(HANDLE_KEY, str);
        msg.setData(data);
        msg.what = HANDLE_CODE;
        handler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mlTtsEngine != null) {
            this.mlTtsEngine.shutdown();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                mEditText.setText("");
                break;
            case R.id.btn_speak:
                /**
                 *First parameter sourceText: text information to be synthesized. The value can contain a maximum of 500 characters.
                 *Second parameter indicating the synthesis mode: The format is configA | configB | configC.
                 *configA：
                 *    MLTtsEngine.QUEUE_APPEND：After an audio synthesis task is generated, the audio synthesis task is processed as follows: If playback is going on, the task is added to the queue for execution in sequence; if playback pauses, the playback is resumed and the task is added to the queue for execution in sequence; if there is no playback, the audio synthesis task is executed immediately.
                 *    MLTtsEngine.QUEUE_FLUSH：The ongoing audio synthesis task and playback are stopped immediately, all audio synthesis tasks in the queue are cleared, and the current audio synthesis task is executed immediately and played.
                 *configB：
                 *    MLTtsEngine.OPEN_STREAM：The synthesized audio data is output through onAudioAvailable.
                 *configC：
                 *    MLTtsEngine.EXTERNAL_PLAYBACK：external playback mode. The player provided by the SDK is shielded. You need to process the audio output by the onAudioAvailable callback API. In this case, the playback-related APIs in the callback APIs become invalid, and only the callback APIs related to audio synthesis can be listened.
                 */
                // Use the built-in player of the SDK to play speech in queuing mode.
                // todo step 7 add on-device tts speak

                if (sourceText.equals("")) {
                    displayResult("Please enter English text in the box above.");
                    break;
                }

                // In queuing mode, the synthesized audio stream is output through onAudioAvailable, and the built-in player of the SDK is used to play the speech.
                // String id = mlTtsEngine.speak(text, MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
                // In queuing mode, the synthesized audio stream is output through onAudioAvailable, and the audio stream is not played, but controlled by you.
                // String id = mlTtsEngine.speak(text, MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM | MLTtsEngine.EXTERNAL_PLAYBACK);
                displayResult("TaskID: " + id + " submit.");
                break;
            case R.id.btn_stop_speak:
                mlTtsEngine.stop();
                break;
            default:
                break;
        }
    }
}
