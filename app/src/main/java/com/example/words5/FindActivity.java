package com.example.words5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FindActivity extends AppCompatActivity {

    static public  String WordText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        Intent intent = getIntent();
        WordText = intent.getStringExtra("text");
        Log.d("传递",WordText);

//        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//        editor.putString("word",WordText);
//        editor.apply();

        //Log.d("测试",WordText);
        FindTitleFragment findTitleFragment = new FindTitleFragment(WordText);
//        Bundle bundle = new Bundle();
//        bundle.putString("FindActivity",WordText);
//        findTitleFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.news_title_fragment,findTitleFragment);
        fragmentTransaction.commit();

    }
}
