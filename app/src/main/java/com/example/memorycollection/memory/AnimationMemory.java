package com.example.memorycollection.memory;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.memorycollection.OrderActivity;
import com.example.memorycollection.savon.DataManager;
import com.example.memorycollection.savon.PageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationMemory {
    // 落下アニメーションの初期速度
    private static final float INITIAL_FALLING_SPEED = 1.5f;

    // 落下時の加速度
    private static final float ACCELERATION = 0.5f;

    // アニメーションの更新間隔（ミリ秒）
    private static final int ANIMATION_INTERVAL = 16;

    // ゆっくりとした落下の持続時間（ミリ秒）
    private static final int SLOW_FALL_DURATION = 1000;

    // タップ時の拡大スケール
    private static final float ZOOM_SCALE = 1.3f;

    // 拡大アニメーションの持続時間（ミリ秒）
    private static final int ZOOM_DURATION = 400;

    // 縮小アニメーションの持続時間（ミリ秒）
    private static final int ZOOM_BACK_DURATION = 400;

    // フェードアニメーションの持続時間（ミリ秒）
    private static final int FADE_DURATION = 400;

    // アニメーション全体の遅延時間（ミリ秒）
    private static final int TOTAL_ANIMATION_DELAY = 1000;

    // 最大メモリー（画像）数
    private static final int MAX_MEMORIES = 3;

    // 画面遷移までの遅延時間（ミリ秒）
    private static final int TRANSITION_DELAY = 1000;

    private Runnable currentAnimation;
    private CountManager countManager;
    private final Context context;
    private DataManager dataManager;
    private final ArrayList<MemoryInfo> colorMemories = new ArrayList<>();
    private final GetMemory getMemory;
    private static final String TAG = "MemoryArrayDebug";  // タグ名を変更
    private final List<ImageButton> activeMemories = new ArrayList<>();  // 追加：アクティブな画像を管理
    private final List<AnimatorSet> activeAnimations = new ArrayList<>();  // 追加：実行中のアニメーションを管理
    private final Handler handler = new Handler();  // Handlerを追加

    public AnimationMemory(Context context) {
        this.context = context;
        this.dataManager = new DataManager(context);
        this.getMemory = new GetMemory(context);  // GetMemoryを初期化
    }

    public void setCountManager(CountManager countManager) {
        this.countManager = countManager;
    }

    public void setupMemoryAnimation(ImageButton imageButton, FrameLayout parentLayout, Bitmap colorBitmap, Uri imageUri, int startX, int startY) {
        imageButton.setX(startX - (imageButton.getWidth() / 2));
        imageButton.setY(startY - (imageButton.getHeight() / 2));

        // アクティブなメモリーリストに追加
        activeMemories.add(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            private boolean isGrayscale = true;

            @Override
            public void onClick(View v) {
                if (!isGrayscale) {
                    Log.d("CountDebug", "既にカラー画像なのでカウントしません");
                    return;
                }

                if (countManager == null) {
                    Log.d("CountDebug", "CountManagerがnullです");
                    return;
                }

                if (countManager.getCurrentCount() >= MAX_MEMORIES) {
                    Log.d("CountDebug", "カウント上限に達しています: " + countManager.getCurrentCount());
                    Toast.makeText(v.getContext(), "グレースケール画像のタップ回数の制限に達しました", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("CountDebug", "グレースケール画像をタップ - カウント前: " + countManager.getCurrentCount());

                // カラー画像に変更
                imageButton.setImageBitmap(colorBitmap);
                isGrayscale = false;

                addToMuseum(imageUri);

                // 日時情報を取得して保存
                long dateTime = getMemory.getImageDateTime(imageUri);
                MemoryInfo memoryInfo = new MemoryInfo(
                        colorBitmap.copy(colorBitmap.getConfig(), true),
                        dateTime,
                        imageUri
                );
                colorMemories.add(memoryInfo);



// 日時順に並び替え
                Collections.sort(colorMemories, (m1, m2) -> Long.compare(m1.getDateTime(), m2.getDateTime()));

// ソート後のリストの状態をログ出力
                Log.d("MemoryListDebug", "=== ソート後のメモリーリスト ===");
                for (int i = 0; i < colorMemories.size(); i++) {
                    MemoryInfo memory = colorMemories.get(i);
                    Log.d("MemoryListDebug", String.format("メモリー[%d]: 日時=%d, Uri=%s",
                            i, memory.getDateTime(), memory.getImageUri()));
                }

// カウントを増やす
                countManager.incrementCount();
                Log.d("CountDebug", "カウント後: " + countManager.getCurrentCount());

// OrderActivityへの遷移時にUriリストを渡す部分
                if (countManager.getCurrentCount() >= MAX_MEMORIES) {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(context, OrderActivity.class);

                        // UriリストをIntentに渡す
                        ArrayList<Uri> selectedImageUris = new ArrayList<>();
                        Log.d("MemoryListDebug", "=== OrderActivityに渡すUriリスト ===");
                        for (MemoryInfo info : colorMemories) {
                            Uri uri = info.getImageUri();
                            Log.d("MemoryListDebug", "Uri追加: " + uri.toString());
                            selectedImageUris.add(uri);
                        }
                        intent.putParcelableArrayListExtra("selected_images", selectedImageUris);

                        context.startActivity(intent);
                    }, TRANSITION_DELAY);
                }

                // 現在のアニメーションを停止
                if (currentAnimation != null) {
                    imageButton.removeCallbacks(currentAnimation);
                }

                // タップ後のアニメーションを開始
                startTapAnimation(imageButton, parentLayout);

                // アニメーション開始時にリストに追加
                AnimatorSet fullAnimation = new AnimatorSet();
                activeAnimations.add(fullAnimation);  // リストに追加

                // フェードアウトアニメーションを作成
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageButton, "alpha", 1.0f, 0.0f);
                fadeOut.setDuration(FADE_DURATION);

                // アニメーション終了時の処理
                fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        parentLayout.removeView(imageButton);
                        activeMemories.remove(imageButton);
                        activeAnimations.remove(fullAnimation);
                    }
                });

                fullAnimation.start();
            }
        });

        startFallingAnimation(imageButton, parentLayout);
    }

    private void startTapAnimation(ImageButton imageButton, FrameLayout parentLayout) {
        // ズームイン
        ObjectAnimator scaleXIn = ObjectAnimator.ofFloat(imageButton, "scaleX", 1.0f, ZOOM_SCALE);
        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(imageButton, "scaleY", 1.0f, ZOOM_SCALE);

        // ズームアウト（元のサイズに戻る）
        ObjectAnimator scaleXOut = ObjectAnimator.ofFloat(imageButton, "scaleX", ZOOM_SCALE, 1.0f);
        ObjectAnimator scaleYOut = ObjectAnimator.ofFloat(imageButton, "scaleY", ZOOM_SCALE, 1.0f);

        // フェードアウト
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(imageButton, "alpha", 1.0f, 0.0f);

        // アニメーションの設定
        AnimatorSet zoomInSet = new AnimatorSet();
        zoomInSet.playTogether(scaleXIn, scaleYIn);
        zoomInSet.setDuration(ZOOM_DURATION);
        zoomInSet.setInterpolator(new OvershootInterpolator());

        AnimatorSet zoomOutSet = new AnimatorSet();
        zoomOutSet.playTogether(scaleXOut, scaleYOut);
        zoomOutSet.setDuration(ZOOM_BACK_DURATION);

        fadeOut.setDuration(FADE_DURATION);

        // アニメーションの順序を設定
        AnimatorSet fullAnimation = new AnimatorSet();
        fullAnimation.play(zoomInSet)
                .before(zoomOutSet);
        fullAnimation.play(fadeOut)
                .after(TOTAL_ANIMATION_DELAY);

        // アニメーション終了時の処理
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                parentLayout.removeView(imageButton);
            }
        });

        // アニメーション開始
        fullAnimation.start();
    }

    private void startFallingAnimation(final ImageButton imageButton, final FrameLayout parentLayout) {
        final float[] currentY = {imageButton.getY()};
        final float[] speed = {INITIAL_FALLING_SPEED};
        final boolean[] isSlowFall = {true};

        currentAnimation = new Runnable() {
            private long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                if (isSlowFall[0] && (currentTime - startTime) >= SLOW_FALL_DURATION) {
                    isSlowFall[0] = false; // ゆっくり落下終了
                }

                if (!isSlowFall[0]) {
                    // 加速落下
                    currentY[0] += speed[0];
                    speed[0] += ACCELERATION;
                } else {
                    // ゆっくり落下
                    currentY[0] += INITIAL_FALLING_SPEED;
                }

                imageButton.setY(currentY[0]);

                if (currentY[0] < parentLayout.getHeight()) {
                    imageButton.postDelayed(this, ANIMATION_INTERVAL);
                } else {
                    parentLayout.removeView(imageButton);
                }
            }
        };

        imageButton.post(currentAnimation);
    }

    // 並び替えられた画像を取得
    public ArrayList<MemoryInfo> getColorMemories() {
        return new ArrayList<>(colorMemories);
    }

    public void clearColorMemories() {
        colorMemories.clear();
    }

    // 全てのアニメーションと表示をクリアするメソッド
    public void clearAllMemories(FrameLayout parentLayout) {
        // 実行中のアニメーションをすべて停止
        for (AnimatorSet animation : activeAnimations) {
            if (animation != null) {
                animation.cancel();
            }
        }
        activeAnimations.clear();

        // すべてのアクティブなメモリー（ImageButton）を削除
        for (ImageButton memory : activeMemories) {
            parentLayout.removeView(memory);
        }
        activeMemories.clear();

        // カラーメモリーをクリア
        colorMemories.clear();

        // 現在のアニメーションを停止
        if (currentAnimation != null) {
            handler.removeCallbacks(currentAnimation);
            currentAnimation = null;
        }
    }

    // 美術館に登録するメソッド
    private void addToMuseum(Uri imageUri) {
        // 現在の保存データを取得
        List<PageData> pageDataList = dataManager.loadPageDataList();

        // 新しいPageDataを作成（カテゴリーは未分類の-1）
        PageData newPageData = new PageData(imageUri, -1);

        // リストに追加
        pageDataList.add(newPageData);

        // 保存
        dataManager.savePageDataList(pageDataList);

    }
}