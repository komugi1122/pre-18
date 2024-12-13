package com.example.memorycollection.memory;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.memorycollection.savon.ButtonSavonManager;
import com.example.memorycollection.savon.SavonManager;

public class ResetMemory {
    private final Context context;
    private final SavonManager savonManager;
    private final ButtonSavonManager buttonSavonManager;
    private final CountManager countManager;
    private final ImageButton executeButton;
    private final ImageButton resetButton;
    private final FrameLayout startLayout;
    private final MemoryManager memoryManager;

    public ResetMemory(Context context, SavonManager savonManager,
                       ButtonSavonManager buttonSavonManager, CountManager countManager,
                       ImageButton executeButton, ImageButton resetButton, FrameLayout startLayout) {
        this.context = context;
        this.savonManager = savonManager;
        this.buttonSavonManager = buttonSavonManager;
        this.countManager = countManager;
        this.executeButton = executeButton;
        this.resetButton = resetButton;
        this.startLayout = startLayout;
        this.memoryManager = buttonSavonManager.getMemoryManager();
    }

    public void setupResetButton() {
        resetButton.setOnClickListener(v -> resetScreen());
    }

    private void resetScreen() {
        // メモリーマネージャーを通じてアニメーションメモリーをクリア
        if (memoryManager != null) {
            memoryManager.getAnimationMemory().clearAllMemories(startLayout);
        }

        // カウントをリセット
        countManager.hideCount();

        // シャボン玉生成を停止
        buttonSavonManager.stopGeneratingSavon(startLayout);

        // 通常のシャボン玉を再開
        savonManager.resumeGeneratingSavon(startLayout);

        // ボタンの表示状態を初期状態に戻す
        executeButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

        // MemoryManagerのカウントもリセット
        if (buttonSavonManager.getMemoryManager() != null) {
            buttonSavonManager.getMemoryManager().setCountManager(countManager);
        }
    }
}