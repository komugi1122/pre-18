package com.example.memorycollection.savon;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
        //frameImageView.setImageResource(frameResId);

        // カテゴリ名を設定
        String categoryName = getCategoryName(pageData.getCategory());
        holder.categoryTextView.setText(categoryName);
    }

    /**
     * 写真に額縁を追加
     * @param photoImageView 写真を表示する ImageView
     * @param frameResId 額縁のリソース ID
     */
    private void addFrameToPhoto(ImageView photoImageView, int frameResId) {
        // 親ビューを取得
        ViewGroup parent = (ViewGroup) photoImageView.getParent();

        // すでに額縁がある場合は削除
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getTag() != null && "frame".equals(child.getTag())) {
                parent.removeView(child);
                break;
            }
        }

        // 新しい額縁を作成
        ImageView frameImageView = new ImageView(parent.getContext());
        frameImageView.setImageResource(frameResId);
        frameImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // 画像を中央に配置
        frameImageView.setTag("frame"); // 識別用タグを設定

        // 写真のサイズを取得
        int photoWidth = photoImageView.getWidth();
        int photoHeight = photoImageView.getHeight();

        // 額縁のサイズを写真より少し大きく設定（ここで調整）
        int frameWidth = (int) (photoWidth * 1.1);  // 写真の幅の1.1倍
        int frameHeight = (int) (photoHeight * 1.1); // 写真の高さの1.1倍

        // LayoutParamsで額縁のサイズを指定
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frameWidth, frameHeight);
        // 額縁を中央に配置
        params.leftMargin = (photoWidth - frameWidth) / 2;
        params.topMargin = (photoHeight - frameHeight) / 2;

        frameImageView.setLayoutParams(params);

        // 額縁を写真の背後に追加
        int photoIndex = parent.indexOfChild(photoImageView);
        parent.addView(frameImageView, photoIndex); // 写真の前に追加
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
