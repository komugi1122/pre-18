package com.example.memorycollection;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.PageAdapter;
import com.example.memorycollection.savon.PageData;

import java.util.ArrayList;
import java.util.List;

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
                0
        );
        addPhotoToMuseum(
                Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.photo_2),
                1
        );

        // 戻るボタンの設定
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
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
}
