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

package com.huawei.hms.mlkit.vision.livenessdetection.test.utils;

/**
 * 引导检测信息
 *
 * @author
 * @date: 2021/12/14
 */
public class DetectionInfo {
    public boolean checkPermissionEnd = false;

    public boolean faceMasking = true;

    public boolean screenRecording = false;

    public String filePathName;

    public String videoPath;

    public MediaRecordService mMediaRecordService;

    public boolean getFaceMasking() {
        return faceMasking;
    }

    public void setFaceMasking(boolean faceMasking) {
        this.faceMasking = faceMasking;
    }

    public boolean isScreenRecording() {
        return screenRecording;
    }

    public void setScreenRecording(boolean screenRecording) {
        this.screenRecording = screenRecording;
    }

    public String getFilePathName() {
        return filePathName;
    }

    public void setFilePathName(String filePathName) {
        this.filePathName = filePathName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public MediaRecordService getMediaRecordService() {
        return mMediaRecordService;
    }

    public void setMediaRecordService(MediaRecordService mMediaRecordService) {
        this.mMediaRecordService = mMediaRecordService;
    }

    private static DetectionInfo detectionInfo = new DetectionInfo();

    private DetectionInfo() {}

    public static DetectionInfo getInstance() {
        return detectionInfo;
    }
}
