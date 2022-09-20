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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志采集
 *
 * @author: fWX1079472
 * @date: 2022/1/13
 */
public class LogWriter {
    // 单例模式
    private static LogWriter mLogFile;

    private static String mPath;

    private static Writer mWriter;

    private static SimpleDateFormat df;

    private LogWriter(String file_path) {
        this.mPath = file_path;
        this.mWriter = null;
    }

    /* *调用open方法后会正式创建文件到指定路径，并初始化mWriter和创建日志文件的时间 */
    public static LogWriter open(String file_path) throws IOException {
        if (mLogFile == null) {
            mLogFile = new LogWriter(file_path);
        }
        File mFile = new File(mPath);
        mWriter = new BufferedWriter(new FileWriter(mPath, true), 2048);
        df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");

        return mLogFile;
    }

    public void close() throws IOException {
        mWriter.close();
    }

    public void print(String log) throws IOException {
        mWriter.write(df.format(new Date()));
        mWriter.write(log);
        mWriter.write("\r\n");
        mWriter.flush();
    }

    // 用于查看在哪个类
    public void print(Class cls, String log) throws IOException {
        mWriter.write(df.format(new Date()));
        mWriter.write(cls.getSimpleName() + " ");
        mWriter.write(log);
        mWriter.write("\r\n");
        mWriter.flush();
    }
}
