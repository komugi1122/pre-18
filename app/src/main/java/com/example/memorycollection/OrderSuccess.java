package com.example.memorycollection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Toast;

import java.util.List;

public class OrderSuccess {
    private Context context;
    // 点滅エフェクトのパラメータ
    private static final int FLASH_DURATION = 350;
    private static final int FLASH_REPEAT_COUNT = 2;
    private static final int BORDER_WIDTH = 15;

    public OrderSuccess(Context context) {
        this.context = context;
    }

    public void compareUriLists(List<Uri> list1, List<Uri> list2) {
        boolean allUrisMatch = true;

        if (list1.size() != list2.size()) {
            allUrisMatch = false;
        } else {
            for (int i = 0; i < list1.size(); i++) {
                if (!list1.get(i).toString().equals(list2.get(i).toString())) {
                    allUrisMatch = false;
                    break;
                }
            }
        }

        if (allUrisMatch) {
            // 成功時の緑の点滅アニメーションを実行
            playSuccessAnimation();
        } else {
            // URIが一致しない場合の処理
            Toast.makeText(context, "順序が正しくありません", Toast.LENGTH_SHORT).show();
        }
    }

    private void playSuccessAnimation() {
        ViewGroup rootView = (ViewGroup) ((android.app.Activity) context).getWindow().getDecorView().getRootView();

        // 緑の枠のオーバーレイを作成
        View borderOverlay = createBorderOverlay();
        rootView.addView(borderOverlay);

        // 点滅アニメーション
        AnimationSet flashAnimationSet = new AnimationSet(false);

        // メインの点滅アニメーション
        AlphaAnimation flash = new AlphaAnimation(0.0f, 1.0f);
        flash.setDuration(FLASH_DURATION);
        flash.setRepeatCount(FLASH_REPEAT_COUNT);
        flash.setRepeatMode(Animation.REVERSE);
        flashAnimationSet.addAnimation(flash);

        // 最後のフェードアウトアニメーション
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(FLASH_DURATION);
        fadeOut.setStartOffset((FLASH_REPEAT_COUNT + 1) * FLASH_DURATION);
        flashAnimationSet.addAnimation(fadeOut);

        // アニメーションの終了リスナー
        flashAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (borderOverlay.getParent() != null) {
                                rootView.removeView(borderOverlay);
                                // アニメーション終了後に画面遷移
                                Intent intent = new Intent(context, MuseumActivity.class);
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // アニメーションの開始
        borderOverlay.startAnimation(flashAnimationSet);
    }

    private View createBorderOverlay() {
        View overlay = new View(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        overlay.setLayoutParams(params);

        // 緑の枠のドローアブルを作成
        GradientDrawable border = new GradientDrawable();
        border.setStroke(BORDER_WIDTH, Color.GREEN);
        border.setColor(Color.TRANSPARENT);
        overlay.setBackground(border);

        return overlay;
    }
}