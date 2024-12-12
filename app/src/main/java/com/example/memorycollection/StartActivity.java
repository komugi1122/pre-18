package com.example.memorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // レイアウト全体をタップ可能に設定
        RelativeLayout layout = findViewById(R.id.startLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // メイン画面に遷移
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // アニメーションの適用
        TextView tapText = findViewById(R.id.tapText);
        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_blink);
        tapText.startAnimation(blinkAnimation);
    }
}