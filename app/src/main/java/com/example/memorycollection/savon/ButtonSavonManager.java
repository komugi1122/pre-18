package com.example.memorycollection.savon;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.memorycollection.R;
import com.example.memorycollection.memory.MemoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ButtonSavonManager {
    private final Context context;
    private MemoryManager memoryManager;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private boolean isRunning = false;
    private final List<ImageButton> savonList = new ArrayList<>();

    public ButtonSavonManager(Context context) {
        this.context = context;
        this.memoryManager = new MemoryManager(context);
    }

    public void setMemoryManager(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
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
        handler.removeCallbacksAndMessages(null);

        for (ImageButton savon : savonList) {
            parentLayout.removeView(savon);
        }
        savonList.clear();
    }

    private void generateSavon(FrameLayout parentLayout) {
        if (!isRunning) return;

        createSavon(parentLayout);

        int delay = 2000 + random.nextInt(2000);
        handler.postDelayed(() -> generateSavon(parentLayout), delay);
    }

    private void createSavon(FrameLayout parentLayout) {
        Log.d("CountDebug", "シャボン玉生成開始");
        ImageButton savonImage = new ImageButton(context);
        savonImage.setImageResource(R.drawable.insavon);
        savonImage.setBackground(null);
        savonImage.setZ(100);

        int parentWidth = parentLayout.getWidth();
        int buttonWidth = 450;
        int maxX = Math.max(1, parentWidth - buttonWidth);
        int randomX = random.nextInt(maxX);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM;
        params.leftMargin = randomX;
        params.bottomMargin = 50;
        savonImage.setLayoutParams(params);

        parentLayout.addView(savonImage);
        savonList.add(savonImage);

        savonImage.setTranslationY(800f);

        SavonAnimationManager animationManager = new SavonAnimationManager();
        animationManager.startFloatingAnimation(savonImage, parentLayout, savonList);

        savonImage.setOnClickListener(v -> {
            Log.d("CountDebug", "シャボン玉をタップ - 現在のカウント: " + memoryManager.getCountManager().getCurrentCount());

            int[] viewLocation = new int[2];
            int[] parentLocation = new int[2];

            v.getLocationInWindow(viewLocation);
            parentLayout.getLocationInWindow(parentLocation);

            int x = viewLocation[0] - parentLocation[0];
            int y = viewLocation[1] - parentLocation[1];

            animationManager.stopAnimation();
            parentLayout.removeView(savonImage);
            savonList.remove(savonImage);

            Log.d("CountDebug", "グレースケール画像生成前のカウント: " + memoryManager.getCountManager().getCurrentCount());
            memoryManager.createMemoryAtLocation(x, y, parentLayout);
            Log.d("CountDebug", "グレースケール画像生成後のカウント: " + memoryManager.getCountManager().getCurrentCount());
        });

        savonImage.setElevation(10f);
    }

    public void resumeGeneratingSavon(FrameLayout parentLayout) {
        if (!isRunning) {
            isRunning = true;
            generateSavon(parentLayout);
        }
    }
}