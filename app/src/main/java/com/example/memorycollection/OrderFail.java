package com.example.memorycollection;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class OrderFail {
    private static final float SHAKE_DISTANCE = 10f;
    private static final long DURATION = 9;
    private static final int REPEAT_COUNT = 3;
    // 点滅エフェクトのパラメータ
    private static final int FLASH_DURATION = 350; // 点滅の周期
    private static final int FLASH_REPEAT_COUNT = 2; // 点滅回数
    private static final int BORDER_WIDTH = 15; // 赤枠の太さ（ピクセル）

    public static void shakeAnimation(View targetView) {
        AnimationSet animationSet = new AnimationSet(true);
        int currentOffset = 0;

        // 各方向への移動を順番に実行
        currentOffset = addDirectionalShake(animationSet, Direction.RIGHT, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.LEFT, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.LEFT, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.RIGHT, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.UP, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.DOWN, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.DOWN, currentOffset);
        currentOffset = addDirectionalShake(animationSet, Direction.UP, currentOffset);

        animationSet.setFillAfter(false);
        targetView.startAnimation(animationSet);
    }

    // 移動方向を表す列挙型
    private enum Direction {
        RIGHT, LEFT, UP, DOWN
    }

    // 指定方向への移動アニメーションを追加する関数
    private static int addDirectionalShake(AnimationSet animationSet, Direction direction, int startOffset) {
        int currentOffset = startOffset;

        for (int i = 0; i < REPEAT_COUNT; i++) {
            TranslateAnimation shake = createShakeAnimation(direction);
            shake.setDuration(DURATION);
            shake.setStartOffset(currentOffset);
            animationSet.addAnimation(shake);
            currentOffset += DURATION;
        }

        return currentOffset;
    }

    // 移動アニメーションを作成する関数
    private static TranslateAnimation createShakeAnimation(Direction direction) {
        float fromX = 0f;
        float toX = 0f;
        float fromY = 0f;
        float toY = 0f;

        switch (direction) {
            case RIGHT:
                toX = SHAKE_DISTANCE / 100f;
                break;
            case LEFT:
                toX = -SHAKE_DISTANCE / 100f;
                break;
            case UP:
                toY = -SHAKE_DISTANCE / 100f;
                break;
            case DOWN:
                toY = SHAKE_DISTANCE / 100f;
                break;
        }

        return new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX,
                Animation.RELATIVE_TO_SELF, toX,
                Animation.RELATIVE_TO_SELF, fromY,
                Animation.RELATIVE_TO_SELF, toY
        );
    }


    // 震えと点滅を組み合わせたアニメーション
    public static void playFailAnimation(View targetView) {
        Context context = targetView.getContext();
        ViewGroup rootView = (ViewGroup) targetView.getRootView();

        // 赤い枠のオーバーレイを作成
        View borderOverlay = createBorderOverlay(context);
        rootView.addView(borderOverlay);

        // 震えアニメーション
        shakeAnimation(targetView);

        // 点滅アニメーション
        AnimationSet flashAnimationSet = new AnimationSet(false); // アニメーションを順番に実行

        // メインの点滅アニメーション
        AlphaAnimation flash = new AlphaAnimation(0.0f, 1.0f);
        flash.setDuration(FLASH_DURATION);
        flash.setRepeatCount(FLASH_REPEAT_COUNT);
        flash.setRepeatMode(Animation.REVERSE);
        flashAnimationSet.addAnimation(flash);

        // 最後のフェードアウトアニメーション
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(FLASH_DURATION);
        fadeOut.setStartOffset((FLASH_REPEAT_COUNT + 1) * FLASH_DURATION); // 点滅後に開始

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

    // 赤い枠のオーバーレイを作成
    private static View createBorderOverlay(Context context) {
        View overlay = new View(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        overlay.setLayoutParams(params);

        // 赤い枠のドローアブルを作成
        GradientDrawable border = new GradientDrawable();
        border.setStroke(BORDER_WIDTH, Color.RED);
        border.setColor(Color.TRANSPARENT);
        overlay.setBackground(border);

        return overlay;
    }
}