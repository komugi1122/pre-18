package com.example.memorycollection.savon;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

public class SavonAnimationManager {
    // アニメーションの定数
    private static final float START_Y = 500f;
    private static final float END_Y = -2200f;
    private static final float SWING_RANGE = 50f;
    private static final long RISE_DURATION = 9000;
    private static final long SWING_DURATION = 1000;

    private AnimatorSet animatorSet;  // クラス変数として追加

    // 上昇と横揺れの複合アニメーション
    public void startFloatingAnimation(View view, FrameLayout parentLayout, List<?> itemList) {
        // 既存のアニメーションがあればキャンセル
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        // 新しいAnimatorSetを作成
        animatorSet = new AnimatorSet();

        // 縦方向の移動アニメーション
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", START_Y, END_Y);
        translationY.setDuration(RISE_DURATION);

        // 横方向の揺れ (ふらふら効果)
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", -SWING_RANGE, SWING_RANGE);
        translationX.setDuration(SWING_DURATION);
        translationX.setRepeatCount(ObjectAnimator.INFINITE);
        translationX.setRepeatMode(ObjectAnimator.REVERSE);

        // アニメーション終了時の動作
        translationY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                parentLayout.removeView(view);
                itemList.remove(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        // アニメーションを同時実行
        animatorSet.playTogether(translationY, translationX);
        animatorSet.start();
    }

    // アニメーション停止用メソッド
    public void stopAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }
    }
}
