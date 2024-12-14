package com.example.memorycollection.savon;

import android.content.Context;
import android.graphics.BitmapFactory;
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

import java.io.InputStream;
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
    public void onBindViewHolder(PageViewHolder holder, int position) {
        PageData pageData = pageDataList.get(position);

        // 既存の写真をセット
        Picasso.get()
                .load(pageData.getImageUri())
                .resize(800, 800) // リサイズ
                .onlyScaleDown()
                .centerInside()
                .into(holder.photoImageView);

        // 額縁の選択（アスペクト比に応じて）
        int frameResId = isPortrait(pageData.getImageUri())
                ? R.drawable.frame_9_16
                : R.drawable.frame_16_9;


        ImageView frameImageView = holder.itemView.findViewById(R.id.frame);
        Picasso.get()
                .load(frameResId)
                .resize(800, 800) // リサイズ
                .onlyScaleDown()
                .centerInside()
                .into(frameImageView);

        // カテゴリ名を設定
        String categoryName = getCategoryName(pageData.getCategory());
        holder.categoryTextView.setText(categoryName);
    }

    /**
     * 写真が縦長か横長かを判定
     * @param uri 写真の URI
     * @return true: 縦長, false: 横長
     */
    private boolean isPortrait(Uri uri) {
        try {
            // 画像サイズを取得
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 実際にデコードしない
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);

            // 高さと幅で判定
            return options.outHeight > options.outWidth;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // デフォルトで横長と判定
        }
    }

    @Override
    public int getItemCount() {
        return (pageDataList != null) ? pageDataList.size() : 0; // pageDataListがnullの場合、0を返す
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