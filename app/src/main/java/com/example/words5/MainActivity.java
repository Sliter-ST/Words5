package com.example.words5;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WordsDBHelper WdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WdbHelper = new WordsDBHelper(this, "wordsdb", this, 1);
        NewsTitleFragment newsTitleFragment = new NewsTitleFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.news_title_fragment,newsTitleFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        WdbHelper.close();
    }

    public boolean onCreateOptionsMenu(Menu menu){      //创建菜单
        //获取MenuInflater
        MenuInflater inflater = getMenuInflater();
        //加载Menu资源
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){        //监听器
        switch (item.getItemId()) {
            case R.id.add:
                Add_Dialog();
                return true;
            case R.id.select:
                Find_Dialog();
            case R.id.help:
                Help_Dialog();
                //Toast.makeText(this,"成功",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void Add_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.add_dialog,null)).setTitle("增加单词");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
            }
        });

        builder.setPositiveButton("增加",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
                AlertDialog ad = (AlertDialog) dialog;
                EditText word = (EditText) ad.findViewById(R.id.new_word);
                String wordText = word.getText().toString();

                EditText meaning = (EditText) ad.findViewById(R.id.meaning);
                String meaningText = meaning.getText().toString();

                add(wordText,meaningText);

                NewsTitleFragment newsTitleFragment = new NewsTitleFragment("add");

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.news_title_fragment,newsTitleFragment);
                fragmentTransaction.commit();
            }
        });
        builder.show();
    }

    public void Find_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.find_dialog,null)).setTitle("查找单词");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
            }
        });

        builder.setPositiveButton("查找",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
                AlertDialog ad = (AlertDialog) dialog;
                EditText word = (EditText) ad.findViewById(R.id.find_word);
                String wordText = word.getText().toString();
                Log.d("输入",wordText);

                Intent intent = new Intent(MainActivity.this,FindActivity.class);
                intent.putExtra("text",wordText);
                startActivity(intent);
            }
        });
        builder.show();
    }

    public void Help_Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.help_dialog,null)).setTitle("帮助");
        builder.setNegativeButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
            }
        });
        builder.show();
    }


    //使用insert方法增加单词
    private void add(String word, String meaning) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        String sql = "insert into words(word,meaning)values(?,?)";
        db.execSQL(sql,new String[]{word,meaning});
    }

    private List<Words> find(String find_word) {
        List<Words> wordsList = new ArrayList<>();
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
//        String[] projection = {"id","word","meaning"};
//        String sortOrder = "word DESC";
//        String selection = "word like ?";
//        String[] selectionArgs = {"%"+find_word+""};
//        Cursor cursor = db.query("words",projection,selection,selectionArgs,null,null,sortOrder);
        String sql = "select * from words where word like ?";
        Cursor cursor = db.rawQuery(sql,new String[]{"%"+find_word+"%"});
        if(cursor.moveToFirst()){
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
                Words words = new Words();
                words.setTitle(word);
                words.setContent(meaning);
                wordsList.add(words);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return wordsList;
    }
}
