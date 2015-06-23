package com.jobs.ui;

import android.content.Context;
import android.graphics.Color;

import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.jobs.R;

/**
 * Created by Jondar on 6/23/2015.
 * A modified version of WelcomeCard from the MaterialList library provided by
 * dexafree on github
 */
public class ListItemCard extends SimpleCard {

    private String subtitle;
    private String buttonText;
    private OnButtonPressListener mListener;
    private int subtitleColor = -1;
    private int buttonTextColor = -1;

    public ListItemCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout(){
        return R.layout.listitem_card;
    }


    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(int subtitleId) {
        this.setSubtitle(this.getString(subtitleId));
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public OnButtonPressListener getOnButtonPressedListener() {
        return this.mListener;
    }

    public void setOnButtonPressedListener(OnButtonPressListener mListener) {
        this.mListener = mListener;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public int getSubtitleColor() {
        return this.subtitleColor;
    }

    public int getButtonTextColor() {
        return this.buttonTextColor;
    }

    public void setButtonText(int buttonTextId) {
        this.setButtonText(this.getString(buttonTextId));
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public void setSubtitleColorRes(int colorId) {
        this.setSubtitleColor(this.getResources().getColor(colorId));
    }

    public void setSubtitleColor(int color) {
        this.subtitleColor = color;
    }

    public void setSubtitleColor(String color) {
        this.setSubtitleColor(Color.parseColor(color));
    }

    public void setButtonTextColorRes(int colorId) {
        this.setButtonTextColor(this.getResources().getColor(colorId));
    }

    public void setButtonTextColor(int color) {
        this.buttonTextColor = color;
    }

    public void setButtonTextColor(String color) {
        this.setButtonTextColor(Color.parseColor(color));
    }


}
