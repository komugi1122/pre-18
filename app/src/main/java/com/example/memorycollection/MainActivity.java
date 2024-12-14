package com.example.memorycollection;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.ButtonSavonManager;
import com.example.memorycollection.savon.SavonManager;

public class MainActivity extends BaseActivity {

    private SavonManager savonManager;
    private ButtonSavonManager buttonSavonManager;
    private ScreenSlidePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // レイアウトリソースを指定
        int[] layouts = {R.layout.screen1_savon, R.layout.screen2_door};

        // SavonManagerとButtonSavonManagerのインスタンスを初期化
        savonManager = new SavonManager(this);
        buttonSavonManager = new ButtonSavonManager(this);

        // ViewPager2とアダプタを設定
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        adapter = new ScreenSlidePagerAdapter(this, layouts, savonManager, buttonSavonManager);
        viewPager.setAdapter(adapter);


        // ページ変更時のコールバックを設定
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                ViewPager2 viewPager = findViewById(R.id.viewPager);
                RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);

                // スクロール状態に応じて矢印の表示/非表示を切り替え
                int visibility = (state == ViewPager2.SCROLL_STATE_IDLE) ?
                        View.VISIBLE : View.INVISIBLE;

                // screen1のarrowを制御
                RecyclerView.ViewHolder holder1 = recyclerView.findViewHolderForAdapterPosition(0);
                if (holder1 != null) {
                    View screen1 = holder1.itemView;
                    ImageView arrowRight = screen1.findViewById(R.id.arrow_right);
                    if (arrowRight != null) {
                        arrowRight.setVisibility(visibility);
                    }
                }

                // screen2のarrowを制御
                RecyclerView.ViewHolder holder2 = recyclerView.findViewHolderForAdapterPosition(1);
                if (holder2 != null) {
                    View screen2 = holder2.itemView;
                    ImageView arrowLeft = screen2.findViewById(R.id.arrow_left);
                    if (arrowLeft != null) {
                        arrowLeft.setVisibility(visibility);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                // スクロール中は全てのarrowを非表示
                if (positionOffset > 0) {
                    ViewPager2 viewPager = findViewById(R.id.viewPager);
                    RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);

                    // screen1のarrowを非表示
                    RecyclerView.ViewHolder holder1 = recyclerView.findViewHolderForAdapterPosition(0);
                    if (holder1 != null) {
                        View screen1 = holder1.itemView;
                        ImageView arrowRight = screen1.findViewById(R.id.arrow_right);
                        if (arrowRight != null) {
                            arrowRight.setVisibility(View.INVISIBLE);
                        }
                    }

                    // screen2のarrowを非表示
                    RecyclerView.ViewHolder holder2 = recyclerView.findViewHolderForAdapterPosition(1);
                    if (holder2 != null) {
                        View screen2 = holder2.itemView;
                        ImageView arrowLeft = screen2.findViewById(R.id.arrow_left);
                        if (arrowLeft != null) {
                            arrowLeft.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // 現在のページのViewを取得
                View currentView = ((ViewPager2)findViewById(R.id.viewPager))
                        .getChildAt(0);

                if (position == 0) { // screen1_savonに移動した場合
                    // 通常のシャボン玉生成を開始（savon_s, savon_m, savon_l）
                    savonManager.startGeneratingSavon(findViewById(R.id.startLayout));

                    // 実行ボタンの状態を確認
                    ImageButton executeButton = currentView.findViewById(R.id.executeButton);
                    if (executeButton != null && executeButton.getVisibility() == View.GONE) {
                        // 実行ボタンが非表示（押された状態）の場合、insavonを再開
                        buttonSavonManager.startGeneratingSavon(findViewById(R.id.startLayout));
                    }

                    // arrow_rightのアニメーションをリセット
                    ImageView arrowRight = currentView.findViewById(R.id.arrow_right);
                    if (arrowRight != null) {
                        arrowRight.clearAnimation();
                        Animation blinkAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_blink);
                        arrowRight.startAnimation(blinkAnimation);
                    }
                } else { // screen2_doorに移動した場合
                    // 両方のシャボン玉生成を停止
                    savonManager.stopGeneratingSavon(findViewById(R.id.startLayout));
                    buttonSavonManager.stopGeneratingSavon(findViewById(R.id.startLayout));

                    // arrow_leftのアニメーションをリセット
                    ImageView arrowLeft = currentView.findViewById(R.id.arrow_left);
                    if (arrowLeft != null) {
                        arrowLeft.clearAnimation();
                        Animation blinkAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_blink);
                        arrowLeft.startAnimation(blinkAnimation);
                    }
                }
            }
        });


    }

}