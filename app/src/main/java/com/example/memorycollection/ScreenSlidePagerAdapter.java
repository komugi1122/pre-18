package com.example.memorycollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorycollection.memory.CountManager;
import com.example.memorycollection.memory.ResetMemory;
import com.example.memorycollection.savon.ButtonSavonManager;
import com.example.memorycollection.savon.SavonManager;

public class ScreenSlidePagerAdapter extends RecyclerView.Adapter<ScreenSlidePagerAdapter.ViewHolder> {

    private final int[] layouts;
    private final Context context;
    private final SavonManager savonManager;
    private final ButtonSavonManager buttonSavonManager;
    private ImageButton executeButton;
    private CountManager countManager;

    public ScreenSlidePagerAdapter(Context context, int[] layouts,
                                   SavonManager savonManager, ButtonSavonManager buttonSavonManager) {
        this.context = context;
        this.layouts = layouts;
        this.savonManager = savonManager;
        this.buttonSavonManager = buttonSavonManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) { // screen1_savon.xmlの場合
            executeButton = holder.itemView.findViewById(R.id.executeButton);
            FrameLayout startLayout = holder.itemView.findViewById(R.id.startLayout);
            ImageButton resetButton = holder.itemView.findViewById(R.id.resetButton);

            resetButton.setVisibility(View.INVISIBLE);

            // CountManagerの初期化と設定
            countManager = new CountManager(context);
            countManager.setupCountView(startLayout);

            // ResetMemoryの初期化と設定
            ResetMemory resetMemory = new ResetMemory(
                    context,
                    savonManager,
                    buttonSavonManager,
                    countManager,
                    executeButton,
                    resetButton,
                    startLayout
            );
            resetMemory.setupResetButton();

            // 矢印のアニメーション設定
            ImageView arrowRight = holder.itemView.findViewById(R.id.arrow_right);
            Animation blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_blink);
            arrowRight.startAnimation(blinkAnimation);

            // 実行ボタンのクリックリスナー
            executeButton.setOnClickListener(v -> {
                countManager.showCount();
                savonManager.startGeneratingSavon(startLayout);
                buttonSavonManager.startGeneratingSavon(startLayout);
                executeButton.setVisibility(View.GONE);
                resetButton.setVisibility(View.VISIBLE);
            });

            // CountManagerをMemoryManagerに渡す
            if (buttonSavonManager != null) {
                buttonSavonManager.getMemoryManager().setCountManager(countManager);
            }

        } else if (position == 1) { // screen2_door.xmlの場合
            ImageView arrowLeft = holder.itemView.findViewById(R.id.arrow_left);
            Animation blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_blink);
            arrowLeft.startAnimation(blinkAnimation);
        }
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // screen1_savonの状態をリセット
    public void resetScreen1(FrameLayout startLayout) {
        savonManager.startGeneratingSavon(startLayout);
        buttonSavonManager.stopGeneratingSavon(startLayout);
        executeButton.setVisibility(View.VISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}