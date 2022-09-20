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

package com.huawei.hdc.test.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hdc.test.R;
import com.huawei.hdc.test.activity.PrivacyActivity;

/**
 * CustomDialog 自定义弹窗
 */
public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
    }

    /**
     * @param context context
     * @param theme theme
     */
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Builder
     */
    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message message
         * @return return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title title
         * @return return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title title
         * @return return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * setContentView
         *
         * @param view view
         * @return return
         */
        public Builder setContentView(View view) {
            this.contentView = view;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText positiveButtonText
         * @param listener listener
         * @return return
         */
        public Builder setPositiveButton(int positiveButtonText,
            OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * setPositiveButton
         *
         * @param positiveButtonText positiveButtonText
         * @param listener listener
         * @return return
         */
        public Builder setPositiveButton(String positiveButtonText,
            OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * setNegativeButton
         *
         * @param negativeButtonText negativeButtonText
         * @param listener listener
         * @return return
         */
        public Builder setNegativeButton(int negativeButtonText,
            OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * setNegativeButton
         *
         * @param negativeButtonText negativeButtonText
         * @param listener listener
         * @return return
         */
        public Builder setNegativeButton(String negativeButtonText,
            OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * create
         *
         * @return return
         */
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_custom_layout, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((MediumBoldTextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveTextView))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveTextView))
                            .setOnClickListener(new View.OnClickListener() {
                                /**
                                 * onClick
                                 *
                                 * @param view view
                                 */
                                public void onClick(View view) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveTextView).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeTextView))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeTextView))
                            .setOnClickListener(new View.OnClickListener() {
                                /**
                                 * 点击事件
                                 *
                                 * @param view view
                                 */
                                public void onClick(View view) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeTextView).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                TextView tv = ((TextView) layout.findViewById(R.id.message));
                tv.setText(message);
                final SpannableStringBuilder style = new SpannableStringBuilder();

                style.append(message);

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        context.startActivity(new Intent(context, PrivacyActivity.class));
                    }
                };

                String privacy = "同声传译服务需要存储及录音使用权限，" +
                        "使用本服务您的个人信息不会上传至华为服务器，只有在提交反馈信息时，" +
                        "您所提交的内容才会上传至华为服务器处理，用来帮助我们分析问题并改进服务体验。" +
                        "点击“同意”，即表示您同意上述内容及《同声传译服务与隐私的声明》。";
                if (message.equals(privacy)) {
                    style.setSpan(clickableSpan, message.length() - 15, message.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(style);

                    // 改变部分文字颜色
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
                    style.setSpan(foregroundColorSpan, message.length() - 15, message.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(style);
                }
            } else if (contentView != null) {
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(false);
            // 设置dialog四个圆角
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dl_default);

            return dialog;
        }
    }
}
