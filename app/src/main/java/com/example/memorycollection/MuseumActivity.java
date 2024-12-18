package com.example.memorycollection;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.memorycollection.savon.DataManager;
import com.example.memorycollection.savon.PageAdapter;
import com.example.memorycollection.savon.PageData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MuseumActivity extends AppCompatActivity {

    private List<PageData> pageDataList; // 写真リスト
    private PageAdapter pageAdapter;
    private ViewPager2 viewPager;
    private DataManager dataManager; // データ管理用
    private Random random = new Random();// Randomインスタンスを作成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        // 美術館用のBGM再生
        MusicManager.playBGM(this, R.raw.museum);

        // DataManagerを初期化
        dataManager = new DataManager(this);

        // 写真リストを復元
        pageDataList = dataManager.loadPageDataList();
        if(pageDataList == null){
            pageDataList = new ArrayList<>();
        }

        // ViewPager2 の設定
        viewPager = findViewById(R.id.viewPager);
        pageAdapter = new PageAdapter(pageDataList, this);
        viewPager.setAdapter(pageAdapter);

        // 戻るボタンの設定
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // MainActivity（ViewPager2を含む画面）に遷移し、screen2_door.xmlを表示
            Intent intent = new Intent(MuseumActivity.this, MainActivity.class);
            // ViewPager2でscreen2_door.xmlを表示するためのフラグを追加
            intent.putExtra("showScreen2", true);
            startActivity(intent);
            finish(); // 現在の画面を終了
        });

        // ソートボタンの設定
        Button sortButton = findViewById(R.id.sortButton); // XML にボタンを追加
        sortButton.setOnClickListener(v -> {
            sortPhotosByDate();
            Toast.makeText(this, "日時でソートしました", Toast.LENGTH_SHORT).show();
        });
        Button sortCategoryButton = findViewById(R.id.sortCategoryButton); // カテゴリー順ソートボタンのIDを確認
        sortCategoryButton.setOnClickListener(v -> {
            sortPhotosByCategory();
            Toast.makeText(this, "カテゴリーでソートしました", Toast.LENGTH_SHORT).show();
        });

        // カテゴリー選択ボタン設定
        Button selectCategoryButton = findViewById(R.id.selectCategoryButton);
        selectCategoryButton.setOnClickListener(v -> showCategoryDialog());

        // onCreateメソッドに消去ボタンの設定を追加
        ImageButton deleteButton = findViewById(R.id.deleteButton); // deleteButtonはXMLで定義してください
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        /*
        // デバッグ用
        // ランダム写真追加ボタン設定
        Button randomButton = findViewById(R.id.randomButton);
        randomButton.setOnClickListener(v -> addRandomPhotoToMuseum());
        */

        // 回転ボタンの設定
        Button rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(v -> rotateCurrentPhoto());
    }

    /**
     * 写真をリストに追加して美術館に表示
     *
     * @param photoUri 写真のUri
     * @param category 写真のカテゴリー (0,1,2,3,4のいずれか)
     */
    public void addPhotoToMuseum(Uri photoUri, int category) {
        if (photoUri == null) {
            Toast.makeText(this, "写真の URI が無効です", Toast.LENGTH_SHORT).show();
            return;
        }
        // 写真データを作成
        PageData newPageData = new PageData(photoUri, category);

        // リストに追加
        pageDataList.add(newPageData);

        // 保存
        dataManager.savePageDataList(pageDataList);

        //通知
        Log.d("addPhotoToMuseum", "Photo URI: " + photoUri);

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

        // ソート後にデータを保存
        dataManager.savePageDataList(pageDataList);

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

        // ソート後にデータを保存
        dataManager.savePageDataList(pageDataList);

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

            // 保存
            dataManager.savePageDataList(pageDataList);

            // Adapterに変更を通知
            pageAdapter.notifyItemChanged(viewPager.getCurrentItem()); // 変更をアダプターに通知
        }
    }

    // 写真消去
    // 最初の確認ダイアログ
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("写真を消去しますか？")
                .setPositiveButton("はい", (dialog, id) -> showSecondConfirmationDialog())  // 2番目のダイアログを表示
                .setNegativeButton("いいえ", null)
                .show();
    }

    // 2番目の確認ダイアログ
    private void showSecondConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("本当に消しますか？")
                .setPositiveButton("はい", (dialog, id) -> showThirdConfirmationDialog())  // 3番目のダイアログを表示
                .setNegativeButton("いいえ", null)
                .show();
    }

    // 3番目の確認ダイアログ
    private void showThirdConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("後悔しませんね？")
                .setPositiveButton("はい", (dialog, id) -> deletePhoto())  // 写真を消去
                .setNegativeButton("いいえ", null)
                .show();
    }

    // 写真を消去する処理
    private void deletePhoto() {
        // 現在表示されている写真のUriを取得
        int currentItemPosition = viewPager.getCurrentItem();
        if (currentItemPosition >= 0 && currentItemPosition < pageDataList.size()) {
            PageData pageData = pageDataList.get(currentItemPosition);
            pageDataList.remove(currentItemPosition); // 写真をリストから削除

            // 保存
            dataManager.savePageDataList(pageDataList);

            // Adapterに通知
            pageAdapter.notifyItemRemoved(currentItemPosition); // 変更をアダプターに通知

            // 通知
            Toast.makeText(MuseumActivity.this, "写真を消去しました", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ギャラリーからランダムに画像を取得して美術館に追加
     */
    public void addRandomPhotoToMuseum() {
        Uri randomImageUri = getRandomImageFromGallery();
        if (randomImageUri != null) {
            // ランダムに取得した画像を美術館に追加（カテゴリーは未設定）
            addPhotoToMuseum(randomImageUri, -1);
        } else {
            Toast.makeText(this, "ランダムに画像を取得できませんでした", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ギャラリーからランダムに画像のUriを取得
     */
    public Uri getRandomImageFromGallery() {
        Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};

        try (Cursor cursor = getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null)) {

            if (cursor != null && cursor.getCount() > 0) {
                int randomPosition = random.nextInt(cursor.getCount());
                cursor.moveToPosition(randomPosition);

                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long id = cursor.getLong(idColumnIndex);

                return ContentUris.withAppendedId(collection, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 現在表示中の写真を回転
     */
    private void rotateCurrentPhoto() {
        // 現在のアイテムの位置を取得
        int currentItemPosition = viewPager.getCurrentItem();

        // 現在のアイテムの View を取得
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(currentItemPosition);

        if (viewHolder instanceof PageAdapter.PageViewHolder) {
            // 回転を適用
            PageAdapter.PageViewHolder pageViewHolder = (PageAdapter.PageViewHolder) viewHolder;
            pageViewHolder.rotatePhoto(90f); // 90度回転
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // アクティビティが停止する際に元のBGMに切り替える
        MusicManager.playBGM(this, R.raw.mainmenu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 再度美術館用BGMを再生
        MusicManager.playBGM(this, R.raw.museum);
    }
}

