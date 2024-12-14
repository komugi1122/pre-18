package com.example.memorycollection;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.PageAdapter;
import com.example.memorycollection.savon.PageData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MuseumActivity extends AppCompatActivity {

    private List<PageData> pageDataList; // 写真リスト
    private PageAdapter pageAdapter;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        // 写真リスト初期化
        pageDataList = new ArrayList<>();

        // ViewPager2 の設定
        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(pageDataList, this);
        viewPager.setAdapter(pageAdapter);

        // テスト用: 初期写真を追加
        addPhotoToMuseum(
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_1),
                2
        );
        addPhotoToMuseum(
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_2),
                1
        );
        addPhotoToMuseum(
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_3),
                8
        );

        // 戻るボタンの設定
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // ソートボタンの設定
        Button sortButton = findViewById(R.id.sortButton); // XML にボタンを追加
        sortButton.setOnClickListener(v -> sortPhotosByDate());
        Button sortCategoryButton = findViewById(R.id.sortCategoryButton); // カテゴリー順ソートボタンのIDを確認
        sortCategoryButton.setOnClickListener(v -> sortPhotosByCategory());

        // カテゴリー選択ボタン設定
        Button selectCategoryButton = findViewById(R.id.selectCategoryButton);
        selectCategoryButton.setOnClickListener(v -> showCategoryDialog());
    }

    /**
     * 写真をリストに追加して美術館に表示
     *
     * @param photoUri 写真のUri
     * @param category 写真のカテゴリー (0,1,2,3,4のいずれか)
     */
    public void addPhotoToMuseum(Uri photoUri, int category) {
        // 写真データを作成
        PageData newPageData = new PageData(photoUri, category);

        // リストに追加
        pageDataList.add(newPageData);

        // Adapter に通知
        pageAdapter.notifyDataSetChanged();
    }

    //撮影時期によってソートする
    public void sortPhotosByDate() {
        Collections.sort(pageDataList, new Comparator<PageData>() {
            @Override
            public int compare(PageData p1, PageData p2) {
                long date1 = p1.getImageDateTime(MuseumActivity.this);
                long date2 = p2.getImageDateTime(MuseumActivity.this);
                return Long.compare(date1, date2);
            }
        });

        // 撮影日時をログに出力
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        for (PageData pageData : pageDataList) {
            long dateTime = pageData.getImageDateTime(MuseumActivity.this);
            String formattedDate = dateFormat.format(dateTime);
            Log.d("SortPhotos", "Photo URI: " + pageData.getImageUri() + ", Date Taken: " + formattedDate);
        }

        // Adapter に変更を通知
        pageAdapter.notifyDataSetChanged();
    }

    //カテゴリーによってソートする
    public void sortPhotosByCategory() {
        // カテゴリー順にソート (0, 1, 2, 3, 4, 5, 未分類)
        pageDataList.sort((page1, page2) -> {
            int category1 = page1.getCategory();
            int category2 = page2.getCategory();

            // 未分類の場合、最後に並べる
            if (category1 == -1) return 1; // page1が未分類の場合、後ろに
            if (category2 == -1) return -1; // page2が未分類の場合、後ろに

            return Integer.compare(category1, category2); // 通常のカテゴリー順
        });

        // ソート後、Adapterに変更を通知
        pageAdapter.notifyDataSetChanged();
    }

    /**
     * カテゴリー選択ダイアログを表示
     */
    private void showCategoryDialog() {
        final String[] categories = {"人物", "風景・自然", "建物・都市", "料理 / ごはん", "物 / その他", "動物", "未分類"};
        final int[] selectedCategory = {0}; // 最初のカテゴリー（0）を選択

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("カテゴリーを選択")
                .setSingleChoiceItems(categories, selectedCategory[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategory[0] = which;
                    }
                })
                .setPositiveButton("選択", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 選択されたカテゴリーを反映
                        updatePhotoCategory(selectedCategory[0]);
                        Toast.makeText(MuseumActivity.this, "カテゴリーが更新されました", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .create()
                .show();
    }

    /**
     * 写真のカテゴリーを更新する
     * @param newCategory 選択されたカテゴリー
     */
    private void updatePhotoCategory(int newCategory) {
        // 選択されている写真のカテゴリーを更新
        if (viewPager.getCurrentItem() >= 0 && viewPager.getCurrentItem() < pageDataList.size()) {
            PageData currentPageData = pageDataList.get(viewPager.getCurrentItem());
            currentPageData.setCategory(newCategory);
            pageAdapter.notifyItemChanged(viewPager.getCurrentItem()); // 変更をアダプターに通知
        }
    }
}
