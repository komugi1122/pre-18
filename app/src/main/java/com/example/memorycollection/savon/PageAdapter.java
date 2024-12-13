package com.example.memorycollection.savon;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorycollection.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private final List<PageData> pageDataList;
    private final Context context;

    public PageAdapter(List<PageData> pageDataList, Context context) {
        this.pageDataList = pageDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PageData pageData = pageDataList.get(position);

        // Picasso を使って Uri から画像を読み込む
        Uri imageUri = pageData.getImageUri();
        Picasso.get()
                .load(imageUri)
                .resize(800, 800)
                .onlyScaleDown()
                .centerInside()
                .placeholder(R.drawable.placeholder_image) // 読み込み中のプレースホルダー
                .error(R.drawable.error_image) // エラー時の画像
                .into(holder.photoImageView);

        // カテゴリ名を取得して表示
        String categoryName = getCategoryName(pageData.getCategory());
        holder.categoryTextView.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return pageDataList.size();
    }

    // カテゴリ名を取得するメソッド
    private String getCategoryName(int category) {
        switch (category) {
            case 0: return "人物";
            case 1: return "風景・自然";
            case 2: return "建物・都市";
            case 3: return "料理 / ごはん";
            case 4: return "物 / その他";
            case 5: return "動物";
            default: return "未分類";
        }
    }

    // ViewHolder クラス
    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView categoryTextView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photo);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
        }
    }
}
