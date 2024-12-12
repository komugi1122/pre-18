package com.example.memorycollection.savon;

import com.example.memorycollection.R;

public class PageData {
    private final int backgroundImageResId;
    private final int photoResId;

    public PageData(int photoResId) {
        this.backgroundImageResId = R.drawable.background_museum;
        this.photoResId = photoResId;
    }

    public int getBackgroundImageResId() {
        return backgroundImageResId;
    }

    public int getPhotoResId() {
        return photoResId;
    }
}
