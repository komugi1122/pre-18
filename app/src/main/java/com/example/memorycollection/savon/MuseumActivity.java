package com.example.memorycollection.savon;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memorycollection.R;

public class MuseumActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum); // 遷移先のレイアウト
    }
}