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

package com.huawei.hms.mlkit.vision.livenessdetection.test.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huawei.hms.mlsdk.interactiveliveness.R;

public class InteractiveCustomCircleProgressView extends View {
    // 圆弧的宽度的一半
    private float mBorderWidthHalf = 0f;

    // 圆弧最小宽度 直径
    private int mMinDiameter = -1;

    // 指定圆弧的外轮廓矩形区域 底色与进度
    private RectF mRectFCircle;

    // 背景矩形区域
    private RectF mRectBg;

    // 指定半圆弧的外轮廓矩形区域
    private RectF mHintRectFSemicircle;

    private Paint paintCurrent;

    public InteractiveCustomCircleProgressView(Context context) {
        super(context);
        init();
    }

    public InteractiveCustomCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InteractiveCustomCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initScale();
        drawHightLightArcProgress(canvas);
    }

    private void init() {
        paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND); // 连接线的样式
        paintCurrent.setStrokeCap(Paint.Cap.ROUND); // 字面意思线帽，就是给线的两端加了个帽子，我们可以指定形状（当然，只能用Paint中定义好的三种
        // 终点对应的角度和起始点对应的角度的夹角
        float mHightLightAngleLength = 90f;
        if (mHightLightAngleLength != 360) {
            paintCurrent.setStrokeCap(Paint.Cap.ROUND); // 圆角弧度
        }
        paintCurrent.setStyle(Paint.Style.STROKE); // 设置填充样式
        paintCurrent.setAntiAlias(true); // 抗锯齿功能
        // 圆弧的宽度
        float mBorderWidth = 18f;
        paintCurrent.setStrokeWidth(mBorderWidth); // 设置画笔宽度
        paintCurrent.setColor(getResources().getColor(R.color.progresscolorblue));
        mBorderWidthHalf = mBorderWidth / 2;
    }

    private void initScale() {
        int width = getWidth();
        if (mMinDiameter == -1) {
            mMinDiameter = width;
        }
        if (mRectBg == null) {
            mRectBg = new RectF(0, 0, width, width);
        }
        float rectRB = mMinDiameter - mBorderWidthHalf;
        float gapOffset = 10f; // 间隙偏移量
        if (mRectFCircle == null) {
            mRectFCircle = new RectF(mBorderWidthHalf, mBorderWidthHalf + 0, rectRB, rectRB);
        }
        if (mHintRectFSemicircle == null) {
            mHintRectFSemicircle =
                    new RectF(
                            mRectFCircle.left + mBorderWidthHalf + gapOffset,
                            mRectFCircle.top + mBorderWidthHalf + gapOffset,
                            mRectFCircle.right - mBorderWidthHalf - gapOffset,
                            mRectFCircle.bottom - mBorderWidthHalf - gapOffset);
        }
    }

    /**
     * 绘制当前进度圆弧
     *
     * @param canvas 绘制当前进度圆弧
     */
    private void drawHightLightArcProgress(Canvas canvas) {
        // 绘制当前圆弧canvas.clipRect(mRectFCircle, Region.Op.INTERSECT)
        // 高亮圓弧起始位置
        float mHightLightStartAngle = 90f;
        canvas.drawArc(mRectFCircle, mHightLightStartAngle, 90f, false, paintCurrent);
        // 绘制当前圆弧canvas.clipRect(mRectFCircle)
    }
}
