package com.example.memorycollection.memory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.memorycollection.memory.CountManager;

public class MemoryManager {
    private final Context context;
    private final GetMemory getMemory;
    private final AnimationMemory animationMemory;
    private CountManager countManager;

    private static final int LANDSCAPE_WIDTH = 345;
    private static final int LANDSCAPE_HEIGHT = 195;
    private static final int PORTRAIT_WIDTH = 195;
    private static final int PORTRAIT_HEIGHT = 345;
    private static final int MAX_TAPS = 3;

    public MemoryManager(Context context) {
        this.context = context;
        this.getMemory = new GetMemory(context);
        this.animationMemory = new AnimationMemory(context);
    }

    public void createMemoryAtLocation(int x, int y, FrameLayout parentLayout) {
        Log.d("CountDebug", "MemoryManager - createMemoryAtLocation開始");

        if (!getMemory.hasStoragePermission()) {
            Toast.makeText(context, "ストレージへのアクセス権限が必要です", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri randomImageUri = getMemory.getRandomImageFromGallery();
        if (randomImageUri != null) {
            try {
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), randomImageUri);

                // 画像の向きを取得
                ExifInterface exif = new ExifInterface(getMemory.getRealPathFromURI(randomImageUri));
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // 画像の向きを補正
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }

                // 回転補正を適用
                originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                        originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

                // アスペクト比を計算
                float aspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();

                // 目標サイズを設定（固定値）
                int targetWidth, targetHeight;
                if (aspectRatio > 1.0f) {
                    // 横長画像
                    targetWidth = 592;  // 長辺
                    targetHeight = 333; // 短辺
                } else {
                    // 縦長画像
                    targetWidth = 333;  // 短辺
                    targetHeight = 592; // 長辺
                }

                // リサイズ
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

                // グレースケール処理
                Bitmap grayscaleBitmap = getMemory.applyGrayscaleFilter(resizedBitmap);

                // ImageButtonを作成
                ImageButton imageButton = new ImageButton(context);
                imageButton.setImageBitmap(grayscaleBitmap);
                imageButton.setBackgroundResource(android.R.color.transparent);
                imageButton.setZ(1000);

                // スクリーンの寸法を取得
                int screenWidth = parentLayout.getWidth();
                int screenHeight = parentLayout.getHeight();

                // X座標の補正（画面中央に配置）
                int adjustedX = Math.max(0, Math.min(x - (targetWidth / 2), screenWidth - targetWidth));

                // Y座標の補正（画面内に収める）
                int adjustedY = Math.max(0, Math.min(y - (targetHeight / 2), screenHeight - targetHeight));

                // ボタンの位置とサイズを設定
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        targetWidth, targetHeight
                );
                params.leftMargin = adjustedX;
                params.topMargin = adjustedY;
                imageButton.setLayoutParams(params);

                // アニメーションを設定（UriもAnimationMemoryに渡す）
                animationMemory.setupMemoryAnimation(imageButton, parentLayout, resizedBitmap, randomImageUri, adjustedX, adjustedY);

                parentLayout.addView(imageButton);
                Log.d("CountDebug", "MemoryManager - グレースケール画像の生成完了");

                // 元のビットマップを解放
                if (!originalBitmap.isRecycled()) {
                    originalBitmap.recycle();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "画像の処理中にエラーが発生しました", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public CountManager getCountManager() {
        return countManager;
    }

    public void setCountManager(CountManager countManager) {
        this.countManager = countManager;
        this.animationMemory.setCountManager(countManager);
    }

    // GetMemoryクラスに追加するメソッド
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    public AnimationMemory getAnimationMemory() {
        return animationMemory;
    }
}