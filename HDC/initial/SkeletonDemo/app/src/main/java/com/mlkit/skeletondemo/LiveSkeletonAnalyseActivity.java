/*
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

package com.mlkit.skeletondemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLJoint;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.mlkit.skeletondemo.camera.GraphicOverlay;
import com.mlkit.skeletondemo.camera.LensEnginePreview;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LiveSkeletonAnalyseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LiveSkeletonAnalyseActivity.class.getSimpleName();

    public static final int UPDATE_VIEW = 101;

    private static final int CAMERA_PERMISSION_CODE = 1;

    private Handler mHandler = new MsgHandler(this);

    private MLSkeletonAnalyzer analyzer;

    private LensEngine mLensEngine;

    private LensEnginePreview mPreview;

    private GraphicOverlay graphicOverlay;
    private ImageView templateImgView;
    private TextView similarityTxt;

    private int lensType = LensEngine.BACK_LENS;

    private boolean isFront = false;

    private List<MLSkeleton> templateList;

    // coordinates for the bones of the image template
    final static float[][] TMP_SKELETONS = {{434.98978f,305.05008f},{396.73953f,505.9227f},{358.5737f,651.8988f}
            ,{ 672.13837f,305.0012f},{677.9236f,546.8901f},{715.7734f,699.4107f}
            ,{479.137f,700.27637f},{ 479.0433f,949.52075f},{479.11667f,1150.6168f}
            ,{600.6858f,706.8922f},{633.7828f,949.70874f},{639.37054f,1198.9513f}
            ,{556.0345f,155.2215f},{556.1129f,297.47336f}};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_live_skeleton_analyse);
        if (savedInstanceState != null) {
            this.lensType = savedInstanceState.getInt("lensType");
        }
        this.mPreview = this.findViewById(R.id.skeleton_preview);
        this.graphicOverlay = this.findViewById(R.id.skeleton_overlay);
        templateImgView = this.findViewById(R.id.template_imgView);
        templateImgView.setImageResource(R.mipmap.skeleton_template);
        similarityTxt = this.findViewById(R.id.similarity_txt);
        this.createSkeletonAnalyzer();
        Button facingSwitchBtn= this.findViewById(R.id.skeleton_facingSwitch);
        if (Camera.getNumberOfCameras() == 1) {
            facingSwitchBtn.setVisibility(View.GONE);
        }
        facingSwitchBtn.setOnClickListener(this);

        initTemplateData();

        // Checking Camera Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.createLensEngine();
        } else {
            this.requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    private void createSkeletonAnalyzer() {
        // todo step 2: add on-device skeleton analyzer

        // todo step 3: set transactor of result

    }

    private void createLensEngine() {
        // todo step 4: add on-device lens engine

    }

    private void startLensEngine() {
        if (this.mLensEngine != null) {
            try {
                this.mPreview.start(this.mLensEngine, this.graphicOverlay);
            } catch (IOException e) {
                Log.e(LiveSkeletonAnalyseActivity.TAG, "Failed to start lens engine.", e);
                this.mLensEngine.release();
                this.mLensEngine = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        if (this.analyzer != null) {
            try {
                this.analyzer.stop();
            } catch (IOException e) {
                Log.e(LiveSkeletonAnalyseActivity.TAG, "Stop failed: " + e.getMessage());
            }
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[] {Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, LiveSkeletonAnalyseActivity.CAMERA_PERMISSION_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode != LiveSkeletonAnalyseActivity.CAMERA_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.createLensEngine();
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("lensType", this.lensType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        this.isFront = !this.isFront;
        if (this.isFront) {
            this.lensType = LensEngine.FRONT_LENS;
        } else {
            this.lensType = LensEngine.BACK_LENS;
        }
        if (this.mLensEngine != null) {
            this.mLensEngine.close();
        }
        this.createLensEngine();
        this.startLensEngine();
    }

    private void initTemplateData() {
        if(templateList != null) {
            return;
        }
        List<MLJoint> mlJointList = new ArrayList<>();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.skeleton_template);
        int type = 100;
        for(int i = 0; i < TMP_SKELETONS.length; i++) {
            type++;
            MLJoint mlJoint = new MLJoint(bitmap.getWidth() * TMP_SKELETONS[i][0], bitmap.getHeight() * TMP_SKELETONS[i][1], type);
            mlJointList.add(mlJoint);
        }

        templateList = new ArrayList<>();
        templateList.add(new MLSkeleton(mlJointList));
    }

    /**
     * Compute Similarity
     * @param skeletons
     */
    private void compareSimilarity(List<MLSkeleton> skeletons) {
        if (templateList == null) {
            return;
        }

        float similarity = 0f;
        float result = analyzer.caluteSimilarity(skeletons, templateList);
        if (result > similarity) {
            similarity = result;
        }

        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putFloat("similarity", similarity);
        msg.setData(bundle);
        msg.what = this.UPDATE_VIEW;
        mHandler.sendMessage(msg);
    }


    private static class MsgHandler extends Handler {
        WeakReference<LiveSkeletonAnalyseActivity> mMainActivityWeakReference;

        MsgHandler(LiveSkeletonAnalyseActivity mainActivity) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LiveSkeletonAnalyseActivity mainActivity = mMainActivityWeakReference.get();
            if (mainActivity == null || mainActivity.isFinishing()) {
                return;
            }
            if (msg.what == UPDATE_VIEW) {
                Bundle bundle = msg.getData();
                float result = bundle.getFloat("similarity");
                mainActivity.similarityTxt.setVisibility(View.VISIBLE);
                mainActivity.similarityTxt.setText("similarity:" + (int) (result * 100) + "%");
            }
        }
    }


    private static class SkeletonAnalyzerTransactor implements MLAnalyzer.MLTransactor<MLSkeleton>{
        private GraphicOverlay mGraphicOverlay;

        WeakReference<LiveSkeletonAnalyseActivity> mMainActivityWeakReference;
        SkeletonAnalyzerTransactor(LiveSkeletonAnalyseActivity mainActivity, GraphicOverlay ocrGraphicOverlay) {
            mMainActivityWeakReference = new WeakReference<>(mainActivity);
            this.mGraphicOverlay = ocrGraphicOverlay;
        }

        @Override
        public void transactResult(MLAnalyzer.Result<MLSkeleton> result) {
            this.mGraphicOverlay.clear();

            SparseArray<MLSkeleton> skeletonSparseArray = result.getAnalyseList();
            List<MLSkeleton> list = new ArrayList<>();
            for (int i = 0; i < skeletonSparseArray.size(); i++) {
                list.add(skeletonSparseArray.valueAt(i));
            }
            // todo step 5: add on-device skeleton graphic


            LiveSkeletonAnalyseActivity mainActivity = mMainActivityWeakReference.get();
            if(mainActivity != null && !mainActivity.isFinishing()) {
                mainActivity.compareSimilarity(list);
            }
        }

        @Override
        public void destroy() {
            this.mGraphicOverlay.clear();
        }

    }

}
