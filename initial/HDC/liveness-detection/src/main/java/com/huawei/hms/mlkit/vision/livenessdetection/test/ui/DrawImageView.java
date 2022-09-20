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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * 绘制检测结果图
 */
public class DrawImageView extends androidx.appcompat.widget.AppCompatImageView {
    private final Paint paint;
    private final Context context;
    private int mBackgroundColor;
    int startAngle = 0;

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs 构建参数
     */
    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        drawbg(canvas);
        int centerX = getWidth() / 2;
        float centerY = (float) ((float) getWidth() / 1.7);
        int radius = getWidth() / 3;
        int ringWidth = dip2px(context, 10);

        this.paint.setARGB(255, 138, 43, 226);
        this.paint.setStrokeWidth(2);
        canvas.drawCircle(centerX, centerY, radius, this.paint);

        this.paint.setARGB(255, 138, 43, 226);
        this.paint.setStrokeWidth(ringWidth);
        canvas.drawCircle(centerX, centerY, radius + 1 + ringWidth / 2, this.paint);

        this.paint.setARGB(255, 138, 43, 226);
        this.paint.setStrokeWidth(2);
        canvas.drawCircle(centerX, centerY, radius + ringWidth, this.paint);

        @SuppressLint("DrawAllocation")
        RectF rect2 =
                new RectF(
                        centerX - (radius + 1 + ringWidth / 2),
                        centerY - (radius + 1 + ringWidth / 2),
                        centerX + (radius + 1 + ringWidth / 2),
                        centerY + (radius + 1 + ringWidth / 2));

        this.paint.setARGB(30, 127, 255, 212);
        this.paint.setStrokeWidth(ringWidth + 2);
        this.paint.setColor(Color.GRAY);
        canvas.drawArc(rect2, 0 + startAngle, 270, false, paint);

        startAngle += 5;
        if (startAngle == 360) {
            startAngle = 0;
        }
        super.onDraw(canvas);
        if (isStart) {
            invalidate();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void drawbg(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int centerX = getWidth() / 2;
        int radius = getWidth() / 3;
        float centerY = (float) ((float) getWidth() / 1.7);
        int ringWidth = dip2px(context, 10);
        RectF mRectBg = new RectF(0, 0, width, height);
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true); // 去锯齿
        mPaint.setColor(Color.parseColor("#FFBDBBBB")); // 设置颜色
        canvas.drawRect(mRectBg, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawCircle(centerX, centerY, radius + ringWidth, paint);
    }

    private void drawFace(Canvas canvas) {
        int centerX = getWidth() / 2;
        float centerY = (float) ((float) getWidth() / 1.7);
        int radius = getWidth() / 3;
        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW); // 设置黄色
        yellowPaint.setAntiAlias(true); // 抗锯齿
        yellowPaint.setStrokeWidth(4); // 画笔粗细为4像素点
        int left = centerX - radius + 50;
        int top = (int) centerY - radius + 20;
        int right = centerX + radius - 50;
        int bottom = (int) centerY + radius - 20;
        Path path = new Path();
        path.moveTo(left + 50, top + 30);
        path.lineTo(right - 50, top + 30);
        path.lineTo(right - 20, top + 50);
        path.lineTo(right - 20, top + 100);
        path.lineTo(right - 70, bottom - 20);
        path.lineTo(left + 70, bottom - 20);
        path.lineTo(left + 20, top + 100);
        path.lineTo(left + 20, top + 50);
        path.lineTo(left + 50, top + 20);
        canvas.drawPath(path, yellowPaint);
    }

    private boolean isStart = false;
    Object mObject = new Object();

    /**
     * start方法
     */
    public void start() {
        synchronized (mObject) {
            isStart = true;
            invalidate();
        }
    }

    /**
     * stop方法
     */
    public void stop() {
        synchronized (mObject) {
            isStart = false;
            invalidate();
        }
    }

    /**
     * dp转化方法
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return int
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
