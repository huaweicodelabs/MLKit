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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.FileUtil;
import com.huawei.hms.mlkit.vision.livenessdetection.test.utils.LogUtils;

/**
 * 多个日志文件回传Activity
 */
public class ListFileActionActivity extends Activity {
    private static final String TAG = ListFileActionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileUtil.copyListFile(this);
        // 获取调用的包名
        String callingPackage = getCallingPackage();
        // 会员中心会通过startActivityForResult的方式调用，所以这里能拿得到包名，如果拿不到包名就不能进行接下来的校验了。
        if (TextUtils.isEmpty(callingPackage)) {
            Log.i(TAG, "callingPackage is null: ");
            finish();
            return;
        }
        // 校验调用者是否是会员中心，里面的数据均为测试用的，上架众测的App请将里面的数据更改，注释有说明，找会员中心的获取。
        boolean verifyCaller = LogUtils.verifyCaller(this, callingPackage);
        Log.i(TAG, "verifyCaller: " + verifyCaller);
        if (!verifyCaller) {
            finish();
            return;
        }
        // 把日志文件路径映射到uri，回调给会员中心，核心的操作在fileToGrantUriList方法里。
        Intent intent = new Intent();
        LogUtils.fileToGrantUriList(this, intent);
        setResult(RESULT_OK, intent);
        finish();
    }
}
