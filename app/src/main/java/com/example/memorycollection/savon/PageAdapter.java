package com.example.memorycollection.savon;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorycollection.R;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private final List<Uri> uriList;

    public PageAdapter(List<Uri> uriList) {
        this.uriList = uriList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Uri uri = uriList.get(position);
        holder.photoImageView.setImageURI(uri);
        // 背景画像の設定は必要に応じてここに記載
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        ImageView backgroundImageView; // 必要なら背景画像もサポート

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_page.xml の ID と一致させること！
            photoImageView = itemView.findViewById(R.id.photo);
            backgroundImageView = itemView.findViewById(R.id.backgroundImage); // 必要なら取得
        }
    }
}
