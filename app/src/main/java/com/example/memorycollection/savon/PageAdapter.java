package com.example.memorycollection.savon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
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
    private static List<PageData> pageDataList;
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

        try {
            // 画像を正しい向きで読み込む
            Bitmap correctedBitmap = loadCorrectedBitmap(pageData.getImageUri());

            // ビットマップをセット
            holder.photoImageView.setImageBitmap(correctedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PageAdapter", "Failed to load corrected bitmap.");
        }

        // 既存の写真をセット
        Picasso.get()
                .load(pageData.getImageUri())
                .resize(320, 320) // リサイズ
                .onlyScaleDown()
                .centerInside()
                .into(holder.photoImageView);

        /*
        // 額縁の選択（アスペクト比に応じて）
        int frameResId = isPortrait(pageData.getImageUri())
                ? R.drawable.frame_9_16
                : R.drawable.frame_16_9;
         */
        int frameResId = R.drawable.frame_1_1;


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

        // 保存された回転角度を適用
        holder.photoImageView.setRotation(pageData.getRotationAngle());
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
        float rotationAngle = 0f; // 回転角度を保持

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photo);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
        }

        // 回転を適用するメソッド
        public void rotatePhoto(float angle) {
            rotationAngle += angle; // 回転角度を加算
            photoImageView.setRotation(rotationAngle); // ImageView に回転を適用

            // 回転情報を保存
            pageDataList.get(getAdapterPosition()).setRotationAngle(rotationAngle);
        }
    }

    /**
     * 画像を正しい向きで読み込む
     * @param uri 画像の Uri
     * @return 回転を補正した Bitmap
     */
    private Bitmap loadCorrectedBitmap(Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            inputStream.close();
        }

        // Exif情報を取得して回転を補正
        InputStream exifInputStream = context.getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(exifInputStream);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        exifInputStream.close();

        int rotation = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }

        // 回転補正
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
