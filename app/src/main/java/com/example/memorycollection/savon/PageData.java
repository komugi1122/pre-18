package com.example.memorycollection.savon;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.Serializable;

public class PageData implements Serializable {
    private String imageUriString; // Uri を文字列で保持
    private int category;
    // PageData.java 内
    private float rotationAngle = 0f; // 回転角度を保存

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float angle) {
        this.rotationAngle = angle;
    }


    public PageData(Uri imageUri, int category) {
        this.imageUriString = imageUri.toString(); // Uri を文字列に変換して保存
        this.category = category;
    }

    public Uri getImageUri() {
        if(this.imageUriString == null){
            Log.d("getImageUri", "imageUriStringNULL");
            return Uri.EMPTY;
        }
        return Uri.parse(this.imageUriString); // 文字列から Uri に復元
    }

    public void setImageUri(Uri uri) {
        this.imageUriString = uri.toString(); // Uri を文字列に変換
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    // 撮影時期を返す
    public long getImageDateTime(Context context) {
        return getImageDateTime(context, getImageUri());
    }

    public long getImageDateTime(Context context, Uri imageUri) {
        if(imageUri == null){
            Log.e("getImageDataTime", "URI is null");
            return 0;
        }
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
}
