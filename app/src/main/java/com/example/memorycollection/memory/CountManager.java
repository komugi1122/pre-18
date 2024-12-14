package com.example.memorycollection.memory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.memorycollection.R;

public class CountManager {
    // カウント表示の位置調整用の定数
    private static final int TOP_MARGIN = -550;   // 画面上部からの距離を30dpに変更
    private static final int RIGHT_MARGIN = 0; // 右端からの距離（dp）

    private int count = 0;
    private ImageView countImageView;
    private final Context context;
    private ViewGroup screen1ViewGroup; // screen1用
    private ViewGroup screen2ViewGroup; // screen2用

    public CountManager(Context context) {
        this.context = context;
    }

    public void setupCountView(ViewGroup parent) {
        setupViews(parent, null);
    }
    public void setupViews(ViewGroup screen1Parent, ViewGroup screen2Parent) {
        this.screen1ViewGroup = screen1Parent;
        this.screen2ViewGroup = screen2Parent;

        // カウント表示の設定（既存のsetupCountViewの内容）
        countImageView = new ImageView(context);
        countImageView.setVisibility(View.INVISIBLE);

        float density = context.getResources().getDisplayMetrics().density;
        int topMarginPx = (int) (TOP_MARGIN * density);
        int rightMarginPx = (int) (RIGHT_MARGIN * density);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.topMargin = topMarginPx;
        params.rightMargin = rightMarginPx;

        screen1Parent.addView(countImageView, params);
        updateCountDisplay();
    }
    public void setScreen2ViewGroup(ViewGroup screen2Parent) {
        this.screen2ViewGroup = screen2Parent;
        updateCountDisplay(); // 現在のカウントに応じて背景を更新
    }

    public void showCount() {
        if (countImageView != null) {
            countImageView.setVisibility(View.VISIBLE);
        }
    }

    public void hideCount() {
        if (countImageView != null) {
            count = 0;
            updateCountDisplay();
            countImageView.setVisibility(View.INVISIBLE);
        }
    }

    public int getCurrentCount() {
        return count;
    }

    public void incrementCount() {
        count++;
        updateCountDisplay();
    }

    private void updateCountDisplay() {
        if (countImageView != null) {
            int resourceId;
            int leftBackgroundId;
            int rightBackgroundId;

            switch (count) {
                case 1:
                    resourceId = R.drawable.count_1;
                    leftBackgroundId = R.drawable.left_background1;
                    rightBackgroundId = R.drawable.right_background1;
                    break;
                case 2:
                    resourceId = R.drawable.count_2;
                    leftBackgroundId = R.drawable.left_background2;
                    rightBackgroundId = R.drawable.right_background2;
                    break;
                case 3:
                    resourceId = R.drawable.count_3;
                    leftBackgroundId = R.drawable.left_background3;
                    rightBackgroundId = R.drawable.right_background3;
                    break;
                default:
                    resourceId = R.drawable.count_0;
                    leftBackgroundId = R.drawable.left_background0;
                    rightBackgroundId = R.drawable.right_background0;
                    break;
            }

            countImageView.setImageResource(resourceId);
            countImageView.setZ(10);

            // 両方の画面の背景を更新
            if (screen1ViewGroup != null) {
                screen1ViewGroup.setBackgroundResource(leftBackgroundId);
            }
            if (screen2ViewGroup != null) {
                screen2ViewGroup.setBackgroundResource(rightBackgroundId);
            }
        }
    }
}