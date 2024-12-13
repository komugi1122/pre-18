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

    public CountManager(Context context) {
        this.context = context;
    }

    public void setupCountView(ViewGroup parent) {
        countImageView = new ImageView(context);
        countImageView.setVisibility(View.INVISIBLE);
        
        // dpをpxに変換
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
        
        parent.addView(countImageView, params);
        updateCountDisplay();
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

    public void incrementCount() {
        count++;
        updateCountDisplay();
    }

    private void updateCountDisplay() {
        if (countImageView != null) {
            int resourceId;
            switch (count) {
                case 1:
                    resourceId = R.drawable.count_1;
                    break;
                case 2:
                    resourceId = R.drawable.count_2;
                    break;
                case 3:
                    resourceId = R.drawable.count_3;
                    break;
                default:
                    resourceId = R.drawable.count_0;
                    break;
            }
            countImageView.setImageResource(resourceId);
        }
    }

    public int getCurrentCount() {
        return count;
    }
}