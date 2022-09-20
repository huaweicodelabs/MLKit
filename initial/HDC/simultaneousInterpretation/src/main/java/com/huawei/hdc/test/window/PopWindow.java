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

package com.huawei.hdc.test.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.hdc.test.R;
import com.huawei.hdc.test.adapter.PopAdapter;
import com.huawei.hdc.test.entry.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * PopWindow
 */
public class PopWindow extends PopupWindow implements AdapterView.OnItemClickListener {
    private PopAdapter popAdapter;
    private View mPopWindowLayout;
    private Activity mActivity;
    private ListView listView;
    private TextView btnCancle;
    private List<ItemBean> list = new ArrayList<>();
    private ItemChangeListerer itemChangeListener;
    private ArrayList<ItemBean> contentList;
    private ArrayList<ItemBean> voiceList;
    private int type = 0;


    private String[] timbreZhArray = {"", "zh-Hans-st-4", "zh-Hans-st-3"};
    private String[] timbreEnArray = {"", "en-US-st-4", "en-US-st-3"};

    public PopWindow(Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopWindowLayout = inflater.inflate(R.layout.popup_layout, null);
        this.setContentView(mPopWindowLayout);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true); // 取得焦点
        // 注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        this.setBackgroundDrawable(new BitmapDrawable());
        // 点击外部消失
        this.setOutsideTouchable(true);
        // 设置可以点击
        this.setTouchable(true);

        listView = mPopWindowLayout.findViewById(R.id.listView);
        btnCancle = mPopWindowLayout.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initData();
        initItemClick();
    }

    private void initData() {
        setInitContentList();
        voiceList = new ArrayList<>();
        String notBroadcast = mActivity.getResources().getString(R.string.notBroadcast);
        String englishMaleVoice = mActivity.getResources().getString(R.string.englishMaleVoice);
        String englishFemaleVoice = mActivity.getResources().getString(R.string.englishFemaleVoice);
        String[] strVoice = new String[]{notBroadcast, englishMaleVoice, englishFemaleVoice};
        for (int i = 0; i < strVoice.length; i++) {
            ItemBean itemBean = new ItemBean();
            itemBean.setName(strVoice[i]);
            itemBean.setId(timbreEnArray[i]);
            if (i == 0) {
                itemBean.setChecked(true);
            }
            voiceList.add(itemBean);
        }
    }

    private void setInitContentList() {
        contentList = new ArrayList<>();
        String asrText = mActivity.getResources().getString(R.string.asrText);
        String asrTextAndTranslation = mActivity.getResources().getString(R.string.asrTextAndTranslation);
        String[] str = new String[]{asrText, asrTextAndTranslation};
        for (int i = 0; i < str.length; i++) {
            ItemBean itemBean = new ItemBean();
            itemBean.setName(str[i]);
            itemBean.setId(i + "");
            if (i == 0) {
                itemBean.setChecked(true);
            }
            contentList.add(itemBean);
        }
    }

    private void initItemClick() {
        listView.setOnItemClickListener(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1f);
    }


    /**
     * 显示内容弹框
     */
    public void show() {
        popAdapter = new PopAdapter(mActivity, contentList);
        listView.setAdapter(popAdapter);
        backgroundAlpha(0.6f);
        showAtLocation(mPopWindowLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示声音弹框
     */
    public void showVoice() {
        popAdapter = new PopAdapter(mActivity, voiceList);
        listView.setAdapter(popAdapter);
        backgroundAlpha(0.6f);
        showAtLocation(mPopWindowLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
    }


    /**
     * setItemChangeListener
     *
     * @param itemChangeListener 回调所选数据
     */
    public void setItemChangeListener(ItemChangeListerer itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("meng", "onItemClick: ");
        if (itemChangeListener != null) {
            itemChangeListener.itemChange(type == 0 ? contentList.get(position) : voiceList.get(position));
            dismiss();
            // 处理选择数据
            if (type == 0) {
                for (int i = 0; i < contentList.size(); i++) {
                    if (position == i) {
                        contentList.get(i).setChecked(true);
                    } else {
                        contentList.get(i).setChecked(false);
                    }
                }
            } else {
                for (int i = 0; i < voiceList.size(); i++) {
                    if (position == i) {
                        voiceList.get(i).setChecked(true);
                    } else {
                        voiceList.get(i).setChecked(false);
                    }
                }
            }
        }
    }

    /**
     * 标识是文本弹框还是声音弹框
     *
     * @param type type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 目标语种类型区分
     *
     * @param languageType languageType
     */
    public void setLanguageType(String languageType) {
        setInitContentList();
        if ("zh".equals(languageType)) {
            voiceList = new ArrayList<>();
            String notBroadcast = mActivity.getResources().getString(R.string.notBroadcast);
            String chineseMaleVoice = mActivity.getResources().getString(R.string.chineseMaleVoice);
            String chineseFemaleVoice = mActivity.getResources().getString(R.string.chineseFemaleVoice);
            String[] strVoice = new String[]{notBroadcast, chineseMaleVoice, chineseFemaleVoice};
            for (int i = 0; i < strVoice.length; i++) {
                ItemBean itemBean = new ItemBean();
                itemBean.setName(strVoice[i]);
                itemBean.setId(timbreZhArray[i]);
                if (i == 0) {
                    itemBean.setChecked(true);
                }
                voiceList.add(itemBean);
            }
        } else {
            voiceList = new ArrayList<>();
            String notBroadcast = mActivity.getResources().getString(R.string.notBroadcast);
            String englishMaleVoice = mActivity.getResources().getString(R.string.englishMaleVoice);
            String englishFemaleVoice = mActivity.getResources().getString(R.string.englishFemaleVoice);
            String[] strVoice = new String[]{notBroadcast, englishMaleVoice, englishFemaleVoice};
            for (int i = 0; i < strVoice.length; i++) {
                ItemBean itemBean = new ItemBean();
                itemBean.setName(strVoice[i]);
                itemBean.setId(timbreEnArray[i]);
                if (i == 0) {
                    itemBean.setChecked(true);
                }
                voiceList.add(itemBean);
            }
        }
    }

    /**
     * 接口类，用于弹框选择回传数据
     */
    public interface ItemChangeListerer {
        void itemChange(ItemBean itemBean);
    }

}
