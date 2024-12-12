package com.example.memorycollection;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class MemoryManager {
    private final Context context;
    private final Random random = new Random();
    private static final int MEMORY_WIDTH = 192;
    private static final int MEMORY_HEIGHT = 108;

    public MemoryManager(Context context) {
        this.context = context;
    }

    public void createMemoryAtLocation(View clickedView, FrameLayout parentLayout) {
        if (!hasStoragePermission()) {
            Toast.makeText(context, "ストレージへのアクセス権限が必要です", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri randomImageUri = getRandomImageFromGallery();
        if (randomImageUri != null) {
            // ImageViewを作成
            ImageView memoryImage = new ImageView(context);
            memoryImage.setImageURI(randomImageUri);
            memoryImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 画面上部にランダムな位置を設定
            int parentWidth = parentLayout.getWidth();
            int randomX = random.nextInt(Math.max(1, parentWidth - MEMORY_WIDTH));

            // LayoutParamsを設定
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MEMORY_WIDTH, MEMORY_HEIGHT);
            params.gravity = Gravity.TOP;
            params.topMargin = 50 + random.nextInt(200); // 上部50-250pxの範囲でランダム
            params.leftMargin = randomX;
            memoryImage.setLayoutParams(params);

            // レイアウトに追加
            parentLayout.addView(memoryImage);
        } else {
            Toast.makeText(context, "画像が見つかりませんでした", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private Uri getRandomImageFromGallery() {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};

        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null)) {

            if (cursor != null && cursor.getCount() > 0) {
                int randomPosition = random.nextInt(cursor.getCount());
                cursor.moveToPosition(randomPosition);

                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long id = cursor.getLong(idColumnIndex);

                return ContentUris.withAppendedId(collection, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}