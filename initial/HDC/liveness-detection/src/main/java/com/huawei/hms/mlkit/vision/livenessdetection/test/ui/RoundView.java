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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class RoundView extends View {
    private Paint paint;
    private int now = 180; // 当前进度
    private int max = 360; // 最大进度
    private Rect rect;
    private int rundwidth = 30; // 圆弧宽度
    private int measuredWidth;

    // 圆环底色
    public static final int COLOR_BG = Color.parseColor("#FFC3C6C9");

    public static final int COLOR_RR = Color.parseColor("#FF03A9F4");

    public RoundView(Context context) {
        this(context, null);
    }

    public RoundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 初始化
        initView();
    }

    private void initView() {
        paint = new Paint(); // 创建笔
        rect = new Rect(); // 创建矩形
        this.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        now--;
                        if (now <= 0) {
                            now = 0;
                        }
                        invalidate(); // 强制重绘
                    }
                });
    }

    public void setNowDraw(int now) {
        this.now = now;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth(); // 测量当前画布大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE); // 设置为空心圆
        paint.setStrokeWidth(rundwidth);
        paint.setColor(COLOR_BG);
        float x = measuredWidth >> 1;
        float y = measuredWidth >> 1;
        int rd = measuredWidth / 2 - rundwidth / 2;
        canvas.drawCircle(x, y, rd, paint);
        // 绘制圆弧
        int[] sweepGradientColors = new int[] {Color.GREEN, Color.GREEN, Color.BLUE, Color.RED, Color.RED};
        SweepGradient mColorShader = new SweepGradient(x, y, sweepGradientColors, null);
        RectF rectF =
                new RectF(rundwidth / 2, rundwidth / 2, measuredWidth - rundwidth / 2, measuredWidth - rundwidth / 2);
        paint.setColor(COLOR_RR);
        canvas.drawArc(rectF, 270, now * 360 / max, false, paint);
        // 设置当前文字
        String text = now * 100 / max + "%";
        paint.setStrokeWidth(0);

        Rect rect = new Rect();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(40);
        paint.getTextBounds(text, 0, text.length(), rect);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, measuredWidth / 2 - rect.width() / 2, measuredWidth / 2 + rect.height() / 2, paint);
    }
}
