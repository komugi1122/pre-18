package com.example.memorycollection.memory;

import android.graphics.Bitmap;
import android.net.Uri;

public class MemoryCompare implements Comparable<MemoryCompare> {
    private Bitmap bitmap;
    private long dateTime;
    private Uri uri;

    public MemoryCompare(Bitmap bitmap, long dateTime, Uri uri) {
        this.bitmap = bitmap;
        this.dateTime = dateTime;
        this.uri = uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public long getDateTime() {
        return dateTime;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public int compareTo(MemoryCompare other) {
        // 日時で比較（古い順）
        return Long.compare(this.dateTime, other.dateTime);
    }
}