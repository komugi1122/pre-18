package com.example.memorycollection.savon;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

    public void setCategory(int category){
        this.category = category;
    }

    //撮影時期を返す
    public long getImageDateTime(Context context, Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DATE_TAKEN};
        try (Cursor cursor = context.getContentResolver().query(
                imageUri,
                projection,
                null,
                null,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                return cursor.getLong(dateColumn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // シンプルに利用するラッパー関数
    public long getImageDateTime(Context context){
        return getImageDateTime(context, this.imageUri);
    }
}

