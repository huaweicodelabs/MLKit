/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.hdc.test.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.huawei.hdc.test.R;

/**
 * 系统工具类
 */
public class SysUtils {
    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param activity activity
     */
    public static void setStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.activity_bg));  // 设置状态栏颜色

            if (activity.getApplicationContext().getResources().getConfiguration().uiMode == 0x21) {
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);  // 实现状态栏图标和文字颜色为暗色
            }
        }
    }


}
