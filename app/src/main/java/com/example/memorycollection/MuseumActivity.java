package com.example.memorycollection;

import android.app.Dialog;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.List;

public class MuseumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        // 歓迎画面の設定
        RelativeLayout welcomeScreen = findViewById(R.id.welcomeScreen);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            welcomeScreen.setVisibility(View.GONE);
        }, 3000);

        // Uriリストを作成
        List<Uri> photoUris = new ArrayList<>();
        photoUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_1));
        photoUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_2));
        photoUris.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_3));

        // ViewPager2の設定
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        PageAdapter adapter = new PageAdapter(photoUris);
        viewPager.setAdapter(adapter);

        // 戻るボタンの設定
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    // 画像拡大表示用のダイアログ
    private void showImagePreview(Uri uri) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_preview);

        ImageView expandedImage = dialog.findViewById(R.id.expandedImage);
        expandedImage.setImageURI(uri);

        // ダイアログをタップして閉じる
        expandedImage.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
