package com.example.memorycollection;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.PageAdapter;
import com.example.memorycollection.savon.PageData;

import java.util.ArrayList;
import java.util.List;

public class MuseumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        // 歓迎画面の要素
        RelativeLayout welcomeScreen = findViewById(R.id.welcomeScreen);
        // 3秒後に歓迎画面を非表示にする
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            welcomeScreen.setVisibility(View.GONE); // 歓迎画面を非表示
        }, 3000);

        // ViewPager2の設定
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        List<PageData> pageDataList = new ArrayList<>();
        pageDataList.add(new PageData(R.drawable.photo_1));
        pageDataList.add(new PageData(R.drawable.photo_2));
        pageDataList.add(new PageData(R.drawable.photo_3));

        PageAdapter adapter = new PageAdapter(pageDataList, this::showImagePreview);
        viewPager.setAdapter(adapter);

        // 戻るボタンのクリックリスナーを設定
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // アクティビティを終了して前の画面に戻る
    }

    // 画像拡大表示用のダイアログ
    private void showImagePreview(int imageResId) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);

        ImageView expandedImage = dialog.findViewById(R.id.expandedImage);
        expandedImage.setImageResource(imageResId);

        // ダイアログをタップして閉じる
        expandedImage.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
