package com.example.memorycollection.savon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorycollection.R;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {

    public interface OnImageClickListener {
        void onImageClick(int imageResId);
    }

    private final List<PageData> pageDataList;
    private final OnImageClickListener imageClickListener;

    public PageAdapter(List<PageData> pageDataList, OnImageClickListener listener) {
        this.pageDataList = pageDataList;
        this.imageClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PageData data = pageDataList.get(position);
        holder.backgroundImage.setImageResource(data.getBackgroundImageResId());
        holder.photo.setImageResource(data.getPhotoResId()); // 1枚のみ表示

        // 写真をタップした場合
        holder.photo.setOnClickListener(v -> imageClickListener.onImageClick(data.getPhotoResId()));
    }

    @Override
    public int getItemCount() {
        return pageDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView backgroundImage;
        ImageView photo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImage = itemView.findViewById(R.id.backgroundImage);
            photo = itemView.findViewById(R.id.photo);
        }
    }
}
