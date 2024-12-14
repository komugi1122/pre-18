package com.example.memorycollection.savon;

import android.net.Uri;

public class PageData {
    private Uri imageUri;
    private int category;

    public PageData(Uri imageUri, int category) {
        this.imageUri = imageUri;
        this.category = category;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public int getCategory() {
        return category;
    }
}

