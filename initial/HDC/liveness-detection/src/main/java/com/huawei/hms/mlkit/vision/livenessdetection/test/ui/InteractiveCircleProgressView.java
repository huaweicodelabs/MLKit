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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huawei.hms.mlsdk.interactiveliveness.R;

/**
 * TCircleProgressView自定义view：用于绘制人脸识别圆形进度框。
 *
 * @ignore
 * @since 2021-10-20
 */
public class InteractiveCircleProgressView extends View {
    private String TAG = "TCircleProgressView";

    private Context context;

    // 圆弧的宽度
    private float mBorderWidth = 18f;

    // 圆弧的宽度的一半
    private float mBorderWidthHalf = 0f;

    // 开始绘制圆弧的角度
    private float mStartAngle = 100f;

    // 终点对应的角度和起始点对应的角度的夹角
    private float mAngleLength = 340f;

    // 背景颜色
    private int mBackgroundColor;

    // 圆弧背景颜色
    private int mArcBackgroundColor;

    // 圆弧最小宽度 直径
    private int mMinDiameter = -1;

    // 指定圆弧的外轮廓矩形区域 底色与进度
    private RectF mRectFCircle;

    // 背景矩形区域
    private RectF mRectBg;

    // 是否显示半透明半圆以及文字
    private boolean mIsShowHint = true;

    // 指定半圆弧的外轮廓矩形区域
    private RectF mHintRectFSemicircle;

    // 半透明半圆覆盖比例
    private float mHintSemicircleRate = 0.3f;

    // 半透明半圆背景颜色
    private int mHintBackgroundColor;

    // 字体颜色
    private int mHintTextColor;

    // 字体大小
    private float mHintTextSize = -1;

    // 字体缩放倍数
    private float multipleText = 30f;

    // 直径半径比值
    private float half = 2f;

    // 文字
    private String mHintText = "";

    // 字距离顶部的偏移量
    private float mTextPadding = 0f;

    // 透明区域圆x轴位置
    private float mTransparentCircleCX;

    // 透明区域圆y轴位置
    private float mTransparentCircleCY;

    // 透明区域圆半径
    private float mTransparentCircleRadius;

    private int mTextWidth;

    private int mSqrt;

    private Rect mBoundsNumber;

    private TextPaint mVTextPaint;

    public InteractiveCircleProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public InteractiveCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InteractiveCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 设置圆弧起始的角度位置以及空白区域角度
     *
     * @param startAngle 起始位置的角度 左上右下 分别角度表示180 / 270 / 360 / 90
     * @param blankAngle 圆弧的空白区域夹角
     */
    public void setStartAngle(float startAngle, float blankAngle) {
        if (startAngle == 0 || startAngle > 360) {
            startAngle = 360;
        }
        this.mStartAngle = startAngle + blankAngle / 2;
        this.mAngleLength = 360 - blankAngle;
    }

    /**
     * 设置总进度
     *
     * @param totalProgress 总进度
     */
    public void setTotalProgress(float totalProgress) {}

    /**
     * 设置背景色
     *
     * @param backgroundColor 颜色值
     */
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    /**
     * 设置文字
     *
     * @param text 文字
     */
    public void setText(String text) {
        this.mHintText = text;
        invalidate();
    }

    /**
     * 是否显示提示文字以及半透明圆
     *
     * @param isShowHint 是否显示提示文字以及半透明圆
     */
    public void setIsShowHint(boolean isShowHint) {
        this.mIsShowHint = isShowHint;
    }

    /**
     * 设置半透明圆的覆盖比率
     *
     * @param mSemicircleRate 0.1F - 1F 默认
     */
    public void setSemicircleRate(float mSemicircleRate) {
        this.mHintSemicircleRate = mSemicircleRate;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initScale();
        drawbg(canvas);
        drawArcBase(canvas);
        if (mIsShowHint) {
            drawHint(canvas);
        }
    }

    private void init(final Context context, AttributeSet attrs) {
        int startAngle = 90;
        int blankAngle = 30;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
            mBorderWidth =
                    typedArray.getDimension(R.styleable.CircleProgressView_tcpv_border_width, dipToPx(5f)); // 圆弧的宽度
            startAngle = typedArray.getInt(R.styleable.CircleProgressView_tcpv_start_angle, 90); // 圆弧起始角度
            blankAngle = typedArray.getInt(R.styleable.CircleProgressView_tcpv_blank_angle, 30); // 圆弧空白角度
            // 动画时长
            long mAnimationDuration =
                    typedArray.getInt(R.styleable.CircleProgressView_tcpv_animation_duration, 3) * 1000L; // 单位秒 动画持续时间
            mHintSemicircleRate =
                    typedArray.getFloat(
                            R.styleable.CircleProgressView_tcpv_hint_semicircle_rate, 0.3f); // 半圆覆盖比率 0.1f - 1f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_background_color,
                                context.getColor(R.color.livenessbg)); // 背景颜色   #FF181818
            } else {
                mBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_background_color, Color.parseColor("#FFBDBBBB"));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mArcBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_arc_background_color,
                                context.getColor(R.color.circlecolor)); // 圆弧背景颜色
            } else {
                mArcBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_arc_background_color, Color.parseColor("#cccccc"));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mHintBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_hint_background_color,
                                context.getColor(R.color.acrcolor)); // 半圆弧背景颜色
            } else {
                mHintBackgroundColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_hint_background_color,
                                Color.parseColor("#55000000"));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mHintTextColor =
                        typedArray.getColor(
                                R.styleable.CircleProgressView_tcpv_hint_text_color,
                                context.getColor(R.color.acrtextcolor)); // 字体颜色
            } else {
                mHintTextColor =
                        typedArray.getColor(R.styleable.CircleProgressView_tcpv_hint_text_color, Color.WHITE); // 字体颜色
            }
            mHintTextSize = typedArray.getDimension(R.styleable.CircleProgressView_tcpv_hint_text_size, -1f); // 字体大小
            mTextPadding = typedArray.getDimension(R.styleable.CircleProgressView_tcpv_hint_text_padding, 0); // 字体大小
            mHintText = typedArray.getString(R.styleable.CircleProgressView_tcpv_hint_text); // 文字
            mIsShowHint = typedArray.getBoolean(R.styleable.CircleProgressView_tcpv_hint_show, false); // 是否显示hint
            typedArray.recycle();
        }

        mBorderWidthHalf = mBorderWidth / 2;
        setStartAngle(startAngle, blankAngle);
    }

    private void initScale() {
        int width = getWidth();
        if (mMinDiameter == -1) {
            mMinDiameter = width;
        }
        if (mHintTextSize == -1) {
            mHintTextSize = dipToPx((float) (mMinDiameter / multipleText));
        }
        if (mRectBg == null) {
            mRectBg = new RectF(0, 0, width, width);
        }
        float rectRB = mMinDiameter - mBorderWidthHalf;
        float mMinDiameterHalf = (float) (mMinDiameter / half);
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
        mTransparentCircleCX = mMinDiameterHalf;
        mTransparentCircleCY = mTransparentCircleCX;
        mTransparentCircleRadius = mTransparentCircleCX - mBorderWidth - gapOffset;
    }

    /**
     * 半圆背景
     *
     * @param canvas 半圆
     */
    private void drawHint(Canvas canvas) {
        // 半圆背景
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mHintBackgroundColor);
        // 圆弧扫过的角度
        float sweepAngle = 180 * mHintSemicircleRate;
        float startAngle = 180 - 90 - sweepAngle;
        canvas.drawArc(mHintRectFSemicircle, startAngle, sweepAngle * 2, false, paint);
        // 提示文字
        mHintText = mHintText == null ? "" : mHintText;
        mVTextPaint = new TextPaint();
        mVTextPaint.setTextAlign(Paint.Align.CENTER);
        mVTextPaint.setAntiAlias(true); // 抗锯齿功能
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        mVTextPaint.setTypeface(font); // 字体风格
        mVTextPaint.setColor(mHintTextColor);
        mBoundsNumber = new Rect();
        getSqrtAndTxtWidth((int) mHintTextSize);

        if (mTextWidth > mSqrt) {
            getSqrtAndTxtWidth(dipToPx(12));
            if (mTextWidth > mSqrt) {
                StaticLayout layoutopen =
                        new StaticLayout(
                                mHintText,
                                (TextPaint) mVTextPaint,
                                mSqrt,
                                Layout.Alignment.ALIGN_NORMAL,
                                1.0F,
                                0.0F,
                                true);
                canvas.save();
                canvas.translate(mTransparentCircleCX, (mHintRectFSemicircle.bottom * 0.9f) - mTextPadding * 3);
                layoutopen.draw(canvas);
                canvas.restore();
                return;
            }
        }
        canvas.drawText(
                mHintText, mTransparentCircleCX, (mHintRectFSemicircle.bottom * 0.9f) - mTextPadding, mVTextPaint);
    }

    /**
     * 绘制矩形背景
     *
     * @param canvas 画笔
     */
    private void drawbg(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true); // 去锯齿
        paint.setColor(mBackgroundColor); // 设置颜色
        int color = paint.getColor();
        Log.i(TAG, "paintColor" + " " + color);
        canvas.drawRect(mRectBg, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawCircle(mTransparentCircleCX, mTransparentCircleCY, mTransparentCircleRadius, paint);
    }

    /**
     * 绘制底色圆弧
     *
     * @param canvas 画笔
     */
    private void drawArcBase(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mArcBackgroundColor);
        paint.setStrokeJoin(Paint.Join.ROUND); // 结合处为圆弧
        paint.setStrokeCap(Paint.Cap.ROUND); // 设置画笔的样式 Paint.Cap.Round ,Cap.SQUARE等分别为圆形、方形
        paint.setStyle(Paint.Style.STROKE); // 设置画笔的填充样式 Paint.Style.FILL  :填充内部
        paint.setAntiAlias(true); // 抗锯齿功能
        paint.setStrokeWidth(mBorderWidth); // 设置画笔宽度
        canvas.drawArc(mRectFCircle, mStartAngle, mAngleLength, false, paint);
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public void getSqrtAndTxtWidth(int hintTextSize) {
        mVTextPaint.setTextSize(hintTextSize);
        mVTextPaint.getTextBounds(mHintText, 0, mHintText.length(), mBoundsNumber);
        if (mRectBg != null && mBoundsNumber != null) {
            float diameter = mRectBg.width();
            float radiusAcr = (diameter / 2) - mBorderWidth;
            float textHeight = (mHintRectFSemicircle.bottom * 0.9f) - mTextPadding;
            float triangleHeight = radiusAcr - textHeight;
            double beveled = Math.pow(radiusAcr, 2);
            double triangle = Math.pow(triangleHeight, 2);
            mSqrt = (int) (Math.sqrt((beveled - triangle)) * 2);
            mTextWidth = mBoundsNumber.width();
        }
    }
}
