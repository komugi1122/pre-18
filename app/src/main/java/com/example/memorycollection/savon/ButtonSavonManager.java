package com.example.memorycollection.savon;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.memorycollection.MemoryManager;
import com.example.memorycollection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ButtonSavonManager {

    private final Context context;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private boolean isRunning = false; // 実行状態を保持
    private final List<ImageButton> savonList = new ArrayList<>(); // 現在生成されているsavonを保持
    private final MemoryManager memoryManager;

    public ButtonSavonManager(Context context) {
        this.context = context;
        this.memoryManager = new MemoryManager(context);
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
        handler.removeCallbacksAndMessages(null); // 全ての生成タスクをキャンセル

        // 全てのsavonボタンを削除
        for (ImageButton savon : savonList) {
            parentLayout.removeView(savon);
        }
        savonList.clear(); // リストをクリア
    }

    private void generateSavon(FrameLayout parentLayout) {
        if (!isRunning) return;

        // savonを生成
        createSavon(parentLayout);

        // 次の生成までの遅延時間（2～4秒）をランダムに設定
        int delay = 2000 + random.nextInt(2000);
        handler.postDelayed(() -> generateSavon(parentLayout), delay);
    }

    private void createSavon(FrameLayout parentLayout) {
        ImageButton savonImage = new ImageButton(context);
        savonImage.setImageResource(R.drawable.insavon);
        savonImage.setBackground(null);

        // ランダムなX軸の位置を計算　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
        int parentWidth = parentLayout.getWidth();
        int buttonWidth = 450; // ボタンの幅（デフォルト値）。必要に応じて正確な値に置き換え。
        int maxX = Math.max(1, parentWidth - buttonWidth);
        int randomX = random.nextInt(maxX);

        // LayoutParamsで位置を指定
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = randomX; // ランダムなX軸の位置を設定
        params.bottomMargin = 50; // 初期位置を画面下部に設定
        savonImage.setLayoutParams(params);

        // レイアウトに追加
        parentLayout.addView(savonImage);
        savonList.add(savonImage); // リストに追加

        // 初期位置の設定
        savonImage.setTranslationY(800f);

        //シャボンのアニメーション
        SavonAnimationManager animationManager = new SavonAnimationManager();
        animationManager.startFloatingAnimation(savonImage, parentLayout, savonList);

        // savonのクリックリスナー（アニメーション停止と削除）
        savonImage.setOnClickListener(v -> {
            animationManager.stopAnimation();
            
            // メモリーを作成
            memoryManager.createMemoryAtLocation(v, parentLayout);
            
            // シャボンを削除
            parentLayout.removeView(savonImage);
            savonList.remove(savonImage);
        });
    }
}