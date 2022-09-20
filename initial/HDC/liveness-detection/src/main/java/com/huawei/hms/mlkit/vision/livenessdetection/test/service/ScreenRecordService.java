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

package com.huawei.hms.mlkit.vision.livenessdetection.test.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.DetectionInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenRecordService extends Service {
    private int resultCode;
    private Intent resultData = null;

    private MediaProjection mediaProjection = null;
    private MediaRecorder mediaRecorder = null;
    private VirtualDisplay virtualDisplay = null;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling startService(Intent),
     * providing the arguments it supplied and a unique integer token representing the start request.
     * Do not call this method directly.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            resultCode = intent.getIntExtra("resultCode", -1);
            resultData = intent.getParcelableExtra("resultData");
            mScreenWidth = intent.getIntExtra("mScreenWidth", 0);
            mScreenHeight = intent.getIntExtra("mScreenHeight", 0);
            mScreenDensity = intent.getIntExtra("mScreenDensity", 0);

            mediaProjection = createMediaProjection();
            mediaRecorder = createMediaRecorder();
            virtualDisplay = createVirtualDisplay();
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
    }

    // createMediaProjection
    public MediaProjection createMediaProjection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE))
                    .getMediaProjection(resultCode, resultData);
        }
        return null;
    }

    private MediaRecorder createMediaRecorder() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String filePath = Environment.getExternalStorageDirectory() + "/ScreenVideo/";
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        String filePathName = filePath + simpleDateFormat.format(new Date()) + ".mp4";
        DetectionInfo.getInstance().setFilePathName(filePathName);

        // Used to record audio and video. The recording control is based on a simple state machine.
        MediaRecorder mediaRecorder = new MediaRecorder();

        // Set the video source to be used for recording.
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        // Set the format of the output produced during recording.
        // 3GPP media file format
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        // Sets the video encoding bit rate for recording.
        // param:the video encoding bit rate in bits per second.
        mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);

        // Sets the video encoder to be used for recording.
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        // Sets the width and height of the video to be captured.
        mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);

        // Sets the frame rate of the video to be captured.
        mediaRecorder.setVideoFrameRate(60);

        try {
            // Pass in the file object to be written.
            mediaRecorder.setOutputFile(filePathName);

            // Prepares the recorder to begin capturing and encoding data.
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mediaProjection.createVirtualDisplay(
                    "mediaProjection",
                    mScreenWidth,
                    mScreenHeight,
                    mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mediaRecorder.getSurface(),
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder = null;
        }
        if (mediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.stop();
            }
            mediaProjection = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
