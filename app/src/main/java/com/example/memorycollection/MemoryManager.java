package com.example.memorycollection;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

public class MemoryManager {
    private final Context context;
    private final Random random = new Random();

    // 画像リソースの配列
    private final int[] memoryImages = {
            R.drawable.h_1,
            R.drawable.h_2,
            R.drawable.v_1,
            R.drawable.v_2
    };

    public MemoryManager(Context context) {
        this.context = context;
    }

    public void createMemoryAtLocation(View clickedView, FrameLayout parentLayout) {
        // クリックされた位置を取得
        float clickX = clickedView.getX()+150;
        float clickY = clickedView.getY()+150;

        // ランダムに画像を選択
        int randomImageResource = memoryImages[random.nextInt(memoryImages.length)];

        // ImageViewを作成
        ImageView memoryImage = new ImageView(context);
        memoryImage.setImageResource(randomImageResource);
        memoryImage.setScaleType(ImageView.ScaleType.FIT_XY);

        // LayoutParamsを設定
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(192, 108);
        params.leftMargin = (int) clickX;
        params.topMargin = (int) clickY;
        memoryImage.setLayoutParams(params);

        // レイアウトに追加
        parentLayout.addView(memoryImage);
    }
}