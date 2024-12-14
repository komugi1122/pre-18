package com.example.memorycollection.savon;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.memorycollection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SavonManager {

    private final Context context;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private boolean isRunning = false; // 実行状態を保持
    private final List<ImageView> savonList = new ArrayList<>(); // 現在生成されているsavonを保持

    public SavonManager(Context context) {
        this.context = context;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startGeneratingSavon(FrameLayout parentLayout) {
        if (isRunning) return;
        isRunning = true;
        generateSavon(parentLayout);
    }

    public void stopGeneratingSavon(FrameLayout parentLayout) {
        isRunning = false;
        handler.removeCallbacksAndMessages(null); // 全ての生成タスク���キャンセル

        // 全てのsavonを削除
        for (ImageView savon : savonList) {
            parentLayout.removeView(savon);
        }
        savonList.clear(); // リストをクリア
    }

    private void generateSavon(FrameLayout parentLayout) {
        if (!isRunning) return;

        // savonを生成
        createSavon(parentLayout);

        // 次の生成までの遅延時間をランダムに設定
        int delay = 250 + random.nextInt(150);
        handler.postDelayed(() -> generateSavon(parentLayout), delay);
    }

    private void createSavon(FrameLayout parentLayout) {
        ImageView savonImage = new ImageView(context);
        savonImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        savonImage.setZ(1);

        // savonのサイズとリソースを設定
        int resourceId;
        int size;
        int randomValue = random.nextInt(5);
        if (randomValue < 3) {
            resourceId = R.drawable.savon_s;
            size = 100;
        } else if (randomValue < 4) {
            resourceId = R.drawable.savon_m;
            size = 150;
        } else {
            resourceId = R.drawable.savon_l;
            size = 200;
        }
        savonImage.setImageResource(resourceId);

        // ランダムなX軸の位置を計算
        int parentWidth = parentLayout.getWidth();
        int randomX = random.nextInt(Math.max(1, parentWidth - size));

        // LayoutParamsで位置とサイズを指定
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = randomX;
        params.bottomMargin = 50;
        savonImage.setLayoutParams(params);

        // レイアウトに追加
        parentLayout.addView(savonImage);
        savonList.add(savonImage);

        // 初期位置の設定
        savonImage.setTranslationY(800f);

        // 通常シャボン玉のelevationを3fに設定（最背面）
        savonImage.setElevation(3f);

        SavonAnimationManager animationManager = new SavonAnimationManager();
        animationManager.startFloatingAnimation(savonImage, parentLayout, savonList);
    }

    public void resumeGeneratingSavon(FrameLayout parentLayout) {
        if (!isRunning) {
            isRunning = true;
            generateSavon(parentLayout);
        }
    }
}