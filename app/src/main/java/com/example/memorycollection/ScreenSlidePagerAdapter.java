package com.example.memorycollection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorycollection.savon.SavonManager;

public class ScreenSlidePagerAdapter extends RecyclerView.Adapter<ScreenSlidePagerAdapter.ViewHolder> {

    private final int[] layouts;
    private final Context context;
    private final SavonManager savonManager;
    private final com.example.memorycollection.savon.ButtonSavonManager buttonSavonManager;
    private ImageButton executeButton;

    public ScreenSlidePagerAdapter(Context context, int[] layouts, com.example.memorycollection.savon.SavonManager savonManager, com.example.memorycollection.savon.ButtonSavonManager buttonSavonManager) {
        this.context = context;
        this.layouts = layouts;
        this.savonManager = savonManager;
        this.buttonSavonManager = buttonSavonManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) { // screen1_savon.xmlの場合
            executeButton = holder.itemView.findViewById(R.id.executeButton);
            FrameLayout startLayout = holder.itemView.findViewById(R.id.startLayout);

            // 実行ボタンのクリックリスナー
            executeButton.setOnClickListener(v -> {

                    savonManager.startGeneratingSavon(startLayout);
                    buttonSavonManager.startGeneratingSavon(startLayout);
                    executeButton.setVisibility(View.GONE);

            });
        }
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // screen1_savonの状態をリセット
    public void resetScreen1(FrameLayout startLayout) {
        savonManager.startGeneratingSavon(startLayout);
        buttonSavonManager.stopGeneratingSavon(startLayout);
        executeButton.setVisibility(View.VISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}