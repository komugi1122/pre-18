package com.example.memorycollection;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderUrteil implements View.OnClickListener {
    private static final String TAG = "OrderUrteil";
    private Context context;
    private RelativeLayout rootLayout;
    private ImageButton targetButton;
    private ArrayList<Uri> selectedImages;  // 追加

    public OrderUrteil(Context context, RelativeLayout rootLayout, ImageButton targetButton, ArrayList<Uri> selectedImages) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.targetButton = targetButton;
        this.selectedImages = selectedImages;  // 追加
    }

    @Override
    public void onClick(View v) {
        ArrayList<UriPosition> uriList = new ArrayList<>();

        // 現在の画像位置を取得
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            if (child instanceof ImageView && child.getId() != R.id.remove_button) {
                ImageView imageView = (ImageView) child;
                Uri imageUri = (Uri) imageView.getTag();
                float yPosition = child.getY();

                uriList.add(new UriPosition(imageUri, yPosition));
            }
        }

        // Y座標でソート（下から上の順）
        Collections.sort(uriList, new Comparator<UriPosition>() {
            @Override
            public int compare(UriPosition o1, UriPosition o2) {
                // Y座標が大きい（画面下）ものが先頭（インデックス0）になるようにソート
                if (o1.y > o2.y) {
                    return -1;
                } else if (o1.y < o2.y) {
                    return 1;
                }
                return 0;
            }
        });

        // デバッグ用：ソート結果の確認
        Log.d(TAG, "ソート後の画像位置（下から上の順）:");
        for (int i = 0; i < uriList.size(); i++) {
            Log.d(TAG, String.format(
                    "インデックス %d: Y座標 = %.1f, Uri = %s",
                    i,
                    uriList.get(i).y,
                    uriList.get(i).uri.toString()
            ));
        }

        // 順番を比較して異なる画像を削除
        for (int i = 0; i < Math.min(selectedImages.size(), uriList.size()); i++) {
            Uri originalUri = selectedImages.get(i);
            Uri currentUri = uriList.get(i).uri;

            if (!originalUri.equals(currentUri)) {
                // 異なるUriの画像を見つけて削除
                for (int j = 0; j < rootLayout.getChildCount(); j++) {
                    View child = rootLayout.getChildAt(j);
                    if (child instanceof ImageView && child.getId() != R.id.remove_button) {
                        Uri childUri = (Uri) ((ImageView) child).getTag();
                        if (childUri.equals(currentUri)) {
                            rootLayout.removeView(child);
                            Log.d(TAG, "画像を削除: " + currentUri.toString());
                            break;
                        }
                    }
                }
            }
        }

        // デバッグ用ログ出力
        Log.d(TAG, "比較結果:");
        for (int i = 0; i < Math.min(selectedImages.size(), uriList.size()); i++) {
            Log.d(TAG, String.format(
                    "インデックス %d: \n元の画像: %s \n現在の画像: %s \n一致: %s",
                    i,
                    selectedImages.get(i).toString(),
                    uriList.get(i).uri.toString(),
                    selectedImages.get(i).equals(uriList.get(i).uri)
            ));
        }
    }

    private static class UriPosition {
        Uri uri;
        float y;

        UriPosition(Uri uri, float y) {
            this.uri = uri;
            this.y = y;
        }
    }
}