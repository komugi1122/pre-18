package com.example.memorycollection;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.ButtonSavonManager;
import com.example.memorycollection.savon.SavonManager;

public class MainActivity extends AppCompatActivity {

    private SavonManager savonManager;
    private ButtonSavonManager buttonSavonManager;
    private ScreenSlidePagerAdapter adapter;
    //githubのテストです！！byこむぎ

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
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 0) { // screen1_savonに戻った場合
                    FrameLayout startLayout = findViewById(R.id.startLayout);
                    adapter.resetScreen1(startLayout); // screen1を初期化
                } else { // screen2_doorに移動した場合
                    savonManager.stopGeneratingSavon(findViewById(R.id.startLayout));
                    buttonSavonManager.stopGeneratingSavon(findViewById(R.id.startLayout));
                }
            }
        });

    }

}