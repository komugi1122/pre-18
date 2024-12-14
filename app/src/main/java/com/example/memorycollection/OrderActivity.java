package com.example.memorycollection;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.memorycollection.memory.MemoryManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class OrderActivity extends BaseActivity {
    private MemoryManager memoryManager;
    private ArrayList<Uri> selectedImages;  // フィールドとして追加

    private static final int long_len = 528;
    private static final int short_len = 297;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // ルートレイアウトを取得
        RelativeLayout rootLayout = findViewById(R.id.root_layout);

        // ボタンを取得
        ImageButton removeButton = findViewById(R.id.remove_button);

        // Intentから選択された画像のUriリストを取得
        selectedImages = getIntent().getParcelableArrayListExtra("selected_images");
        if (selectedImages == null || selectedImages.isEmpty()) {
            Toast.makeText(this, "選択された画像がありません", Toast.LENGTH_SHORT).show();
            return;
        }

        // ボタンのクリックリスナーを設定（selectedImagesを渡す）
        removeButton.setOnClickListener(new OrderUrteil(this, rootLayout, removeButton, selectedImages));

        // MemoryManagerインスタンスを作成
        memoryManager = new MemoryManager(this);

        // Intentから選択された画像のUriリストを取得
        ArrayList<Uri> selectedImages = getIntent().getParcelableArrayListExtra("selected_images");
        if (selectedImages == null || selectedImages.isEmpty()) {
            Toast.makeText(this, "選択された画像がありません", Toast.LENGTH_SHORT).show();
            return;
        }

// 表示用の新しいリストを作成してシャッフル
        ArrayList<Uri> displayImages = new ArrayList<>(selectedImages);
        Collections.shuffle(displayImages);

// レイアウトが描画された後に画像を配置
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // displayImagesを使用して画像を表示
                for (int i = 0; i < displayImages.size(); i++) {
                    Uri imageUri = displayImages.get(i);
                    try {
                        Bitmap processedImage = processImage(imageUri);

                        // 表示用のImageViewを作成
                        ImageView imageView = new ImageView(OrderActivity.this);
                        imageView.setImageBitmap(processedImage);
                        imageView.setTag(imageUri);  // UriをImageViewのタグとして保存

                        // レイアウトパラメータを設定（ランダム配置）
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.leftMargin = (int) (Math.random() * (rootLayout.getWidth() - processedImage.getWidth()));
                        params.topMargin = (int) (Math.random() * (rootLayout.getHeight() - processedImage.getHeight()));
                        imageView.setLayoutParams(params);

                        // ドラッグ機能を追加
                        imageView.setOnTouchListener(new OrderMove(OrderActivity.this));

                        // ImageViewをルートレイアウトに追加
                        rootLayout.addView(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(OrderActivity.this, "画像処理中にエラーが発生しました", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 選択された画像のUriリストをログ出力
        for (Uri uri : selectedImages) {
            Log.d("OrderActivity", "選択された画像Uri: " + uri.toString());
        }
        for (Uri uri : displayImages) {
            Log.d("OrderActivity", "シャッフルされた画像Uri: " + uri.toString());
        }
    }

    /**
     * 画像をリサイズするメソッド
     */
    private Bitmap processImage(Uri imageUri) throws IOException {
        // 画像を取得
        Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        // Exif情報を取得して向きを補正
        ExifInterface exif = new ExifInterface(memoryManager.getRealPathFromURI(imageUri));
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
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
        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

        // アスペクト比を計算
        float aspectRatio = (float) originalBitmap.getWidth() / originalBitmap.getHeight();

        // 目標サイズを設定（固定値）
        int targetWidth, targetHeight;
        if (aspectRatio > 1.0f) {
            // 横長画像
            targetWidth = long_len;
            targetHeight = short_len;
        } else {
            // 縦長画像
            targetWidth = short_len;
            targetHeight = long_len;
        }

        // リサイズ
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
    }
}