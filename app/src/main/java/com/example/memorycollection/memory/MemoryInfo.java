package com.example.memorycollection.memory;

import android.graphics.Bitmap;
import android.net.Uri;

public class MemoryInfo {
    private Bitmap bitmap;   // 画像データ
    private long dateTime;   // 画像が保存された日時
    private Uri imageUri;    // 画像のURI

    public MemoryInfo(Bitmap bitmap, long dateTime, Uri imageUri) {
        this.bitmap = bitmap;
        this.dateTime = dateTime;
        this.imageUri = imageUri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
