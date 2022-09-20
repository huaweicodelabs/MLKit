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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class InteractiveLivenessDetectionUtils {
    public static final String[] PERMISSIONS_STRING_ARRAY =
            new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final List<String> PERMISSIONS_LIST = new ArrayList<>();
    private static final int PERMISSION_REQUEST = 1;
    private static long lastClickTime = 0L;

    /**
     * 检查权限
     * @param context 上下文
     */
    public static void checkPermission(Context context) {
        PERMISSIONS_LIST.clear();
        for (String permission : PERMISSIONS_STRING_ARRAY) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_LIST.add(permission);
            }
        }
        if (PERMISSIONS_LIST.isEmpty()) {
        } else {
            String[] permissionsStringArray = PERMISSIONS_LIST.toArray(new String[0]);
            ActivityCompat.requestPermissions((Activity) context, permissionsStringArray, PERMISSION_REQUEST);
        }
    }

    /**
     * 检测是否连续快速点击
     * @return 布尔值
     */
    public static boolean isFastDoubleClick() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - lastClickTime < 500) {
            return true;
        }
        lastClickTime = nowTime;
        return false;
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
}
