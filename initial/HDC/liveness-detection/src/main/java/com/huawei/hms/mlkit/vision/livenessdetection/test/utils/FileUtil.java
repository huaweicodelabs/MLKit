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

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件操作工具类
 *
 * @author fwx1079472
 * @since 2022-01-17
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 将Assets下的Log.zip文件复制文件到本地目录/data/data/包名/files/Log/Log.zip文件。
     *
     * @param context Context
     */
    public static void copySingleFile(Context context) {
        // 友情提示，线上此操作应该放在子线程
        Log.i(TAG, "copy start");
        // 日志目录：/data/data/包名/files/Log/
        String filesDir = context.getFilesDir() + File.separator + "Log";
        String oidFile =
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator
                        + "mlkit"
                        + File.separator
                        + "logs"
                        + File.separator
                        + "livenessZip";
        String newFile = context.getFilesDir() + File.separator + "Log" + File.separator + "simple.log";
        File dir = new File(filesDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "createReportFile mkdirs error.");
                return;
            }
        }
        File logFile = new File(filesDir, "simple.log");
        if (logFile.exists()) {
            deleteFile(logFile);
        }
        createFile(dir, logFile.getName());
        copyFile(oidFile, newFile);
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) { // 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 将Assets下的1.txt-7.txt文件复制文件到本地目录/data/data/包名/files/Log目录下。
     *
     * @param context Context
     */
    public static void copyListFile(Context context) {
        // 友情提示，线上此操作应该放在子线程
        Log.i(TAG, "copy start");
        // 日志目录：/data/data/包名/files/Log/
        String filesDir = context.getFilesDir() + File.separator + "Log";
        File dir = new File(filesDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "createReportFile mkdirs error.");
                return;
            }
        }
        for (int i = 1; i < 8; i++) {
            File logFile = new File(filesDir, i + "txt");
            if (logFile.exists()) {
                deleteFile(logFile);
            }
            createFile(dir, logFile.getName());
            copyAssetsToFile(context, logFile, logFile.getName());
        }
    }

    private static void copyAssetsToFile(Context context, File logFile, String assetFileName) {
        AssetManager manager = context.getAssets();
        if (manager == null) {
            return;
        }
        try (InputStream is = manager.open(assetFileName);
                FileOutputStream fos = new FileOutputStream(logFile)) {
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                // buffer字节
                fos.write(buffer, 0, byteCount);
            }
            Log.i(TAG, "copy finish");
        } catch (IOException e) {
            Log.e(TAG, "copy IOException");
        }
    }

    /**
     * 刪除文件
     *
     * @param f 文件
     * @return boolean
     */
    public static boolean deleteFile(File f) {
        return (f != null) && f.exists() && f.delete();
    }

    /**
     * 创建文件
     *
     * @param parent parent file
     * @param child  pathname
     * @return new file
     */
    public static File createFile(File parent, String child) {
        if (parent == null || TextUtils.isEmpty(child)) {
            Log.e(TAG, "createFile parent or child invalid.");
            return null;
        }
        return createNewFile(new File(parent, child));
    }

    /**
     * 创建新文件
     *
     * @param file new file
     * @return created file
     */
    public static File createNewFile(@NonNull File file) {
        try {
            if (file.exists()) {
                return file;
            }
            if (!file.createNewFile()) {
                Log.e(TAG, "createNewFile createFile error.");
                return null;
            }
            return file;
        } catch (IOException e) {
            Log.e(TAG, "createNewFile IOException.");
            return null;
        }
    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 压缩完成的Zip路径
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        // 创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));

        // 创建文件
        File file = new File(srcFileString);

        // 压缩
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);

        // 完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam)
            throws Exception {
        if (zipOutputSteam == null) {
            return;
        }
        File file = new File(folderString + fileString);

        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            // 文件夹
            String[] fileList = file.list();

            // 没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            // 子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
            }
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    private boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteSingleFile(delFile);
            } else {
                return deleteDirectory(delFile);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        fileSortByTime(files);
        if ((files != null) && (files.length > 100)) {
            for (int i = 0; i < files.length - 100; i++) {
                // 删除子文件
                File file = files[i];
                if (file.isFile()) {
                    flag = deleteSingleFile(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else if (file.isDirectory()) {
                    flag = deleteDirectory(file.getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            return false;
        }
    }

    // 对文件进行时间排序
    public static void fileSortByTime(File[] fileList) {
        Arrays.sort(
                fileList,
                new Comparator<File>() {
                    public int compare(File p1, File p2) {
                        if (p1.lastModified() < p2.lastModified()) {
                            return -1;
                        }
                        return 1;
                    }
                });
    }
}
