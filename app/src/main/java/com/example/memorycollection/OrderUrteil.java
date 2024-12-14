package com.example.memorycollection;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderUrteil implements View.OnClickListener {
    private static final String TAG = "OrderUrteil";
    private Context context;
    private RelativeLayout rootLayout;
    private ImageButton targetButton;

    public OrderUrteil(Context context, RelativeLayout rootLayout, ImageButton targetButton) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.targetButton = targetButton;
    }

    @Override
    public void onClick(View v) {
        ArrayList<ImageViewPosition> imageViews = new ArrayList<>();

        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            if (child instanceof ImageView && child.getId() != R.id.remove_button) {
                ImageView imageView = (ImageView) child;
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

                // 画像の情報を取得
                int width = drawable.getBitmap().getWidth();
                int height = drawable.getBitmap().getHeight();
                String orientation = width > height ? "横長" : "縦長";

                imageViews.add(new ImageViewPosition(
                        imageView,
                        child.getY(),
                        orientation,
                        width,
                        height
                ));
            }
        }

        // Y座標で降順ソート（上から下の順）
        Collections.sort(imageViews, new Comparator<ImageViewPosition>() {
            @Override
            public int compare(ImageViewPosition o1, ImageViewPosition o2) {
                return Float.compare(o2.y, o1.y);
            }
        });

        // ログ出力
        Log.d(TAG, "画像の順番（上から下）:");
        for (int i = 0; i < imageViews.size(); i++) {
            ImageViewPosition img = imageViews.get(i);
            Log.d(TAG, String.format(
                    "位置 %d: Y座標 = %.1f, 種類: %s (幅: %d, 高さ: %d)",
                    (i + 1),
                    img.y,
                    img.orientation,
                    img.width,
                    img.height
            ));
        }
    }

    private static class ImageViewPosition {
        ImageView imageView;
        float y;
        String orientation;
        int width;
        int height;

        ImageViewPosition(ImageView imageView, float y, String orientation, int width, int height) {
            this.imageView = imageView;
            this.y = y;
            this.orientation = orientation;
            this.width = width;
            this.height = height;
        }
    }
}