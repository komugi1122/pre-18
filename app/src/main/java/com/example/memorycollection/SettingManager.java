package com.example.memorycollection;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class SettingManager {
    private final Context context;

    public SettingManager(Context context) {
        this.context = context;
    }

    public void setupSettingButton(ImageButton settingButton) {
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 設定画面に遷移
                Intent intent = new Intent(context, SettingActivity.class);
                context.startActivity(intent);
            }
        });
    }
}