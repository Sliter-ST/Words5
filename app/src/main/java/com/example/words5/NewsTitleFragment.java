package com.example.words5;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NewsTitleFragment extends Fragment {
    private boolean isTwoPane;
    private WordsDBHelper WdbHelper;
    View view;
    RecyclerView newsTitleRecyclerView;

    String mess;
    public NewsTitleFragment(){

    }
    public NewsTitleFragment(String s){
        this.mess = s;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        WdbHelper = new WordsDBHelper(getContext(), "wordsdb", mainActivity, 1);

        view = inflater.inflate(R.layout.news_title_frag, container, false);



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsTitleRecyclerView = (RecyclerView)view.findViewById(R.id.news_title_recycler_view);
        newsTitleRecyclerView.setLayoutManager(layoutManager);
        NewsAdapter adapter = new NewsAdapter(getNews());
        newsTitleRecyclerView.setAdapter(adapter);

        if(mess == "add"){
            onResume();
//            NewsAdapter adapter3 = new NewsAdapter(getNews());
//            newsTitleRecyclerView.setAdapter(adapter3);
        }

        return view;
    }

    private List<Words> getNews() {
        List<Words> wordsList = new ArrayList<>();
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        Cursor cursor = db.query("words",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
                Words words = new Words();
                words.setTitle(word);
                words.setContent(meaning);
                wordsList.add(words);
                Log.d("适配器",word+"  "+meaning);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return wordsList;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity().findViewById(R.id.news_content_layout) != null){
            isTwoPane = true;
        } else {
            isTwoPane = false;
        }
    }

    class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private List<Words> mWordsList;
        private AdapterView.OnItemClickListener onItemClickListener;



        class ViewHolder extends RecyclerView.ViewHolder{

            TextView newsTitleText;

            public ViewHolder(View view){
                super(view);
                newsTitleText = (TextView)view.findViewById(R.id.news_title);
            }
        }

        public NewsAdapter(List<Words> wordsList){
            mWordsList = wordsList;
        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Words words = mWordsList.get(holder.getAdapterPosition());
                    if(isTwoPane){
                        NewsContentFragment newsContentFragment = (NewsContentFragment)getFragmentManager().findFragmentById(R.id.news_content_fragment);
                        newsContentFragment.refresh(words.getTitle(), words.getContent());
                    }else{
                        NewsContentActivity.actionStart(getActivity(), words.getTitle(), words.getContent());
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Words words = mWordsList.get(holder.getAdapterPosition());
//                    Toast.makeText(getActivity(),words.getTitle(),Toast.LENGTH_SHORT).show();

                    PopupMenu popupMenu = new PopupMenu(getActivity(),v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                    popupMenu.show();

                    //弹出式菜单的菜单项点击事件
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    Delete_Dialog(words.getTitle());

                                    return true;
                                case R.id.change:
                                    Change_Dialog(words.getTitle());

                                    break;
                                default:
                            }
                            return false;
                        }
                    });
                    return false;
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Words words = mWordsList.get(position);
            holder.newsTitleText.setText(words.getTitle());
        }

        @Override
        public int getItemCount() {
            return mWordsList.size();
        }


    }

    public void Delete_Dialog(final String word) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.delete_dialog,null)).setTitle("删除单词");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
            }
        });

        builder.setPositiveButton("删除",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
                String sql = "delete from words where word='"+word+"'";
                SQLiteDatabase db = WdbHelper.getReadableDatabase();
                db.execSQL(sql);
                NewsAdapter adapter1 = new NewsAdapter(getNews());
                newsTitleRecyclerView.setAdapter(adapter1);
            }
        });
        builder.show();
    }

    private void Change_Dialog(final String StrWord) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.change_dialog,null)).setTitle("修改单词");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
            }
        });

        builder.setPositiveButton("修改",new DialogInterface.OnClickListener(){
            @Override
            public void  onClick(DialogInterface dialog,int id){
                AlertDialog ad = (AlertDialog) dialog;
                EditText word = (EditText) ad.findViewById(R.id.change_word);
                String wordText = word.getText().toString();

                EditText meaning = (EditText) ad.findViewById(R.id.change_meaning);
                String meaningText = meaning.getText().toString();

                change(wordText,meaningText,StrWord);
                NewsAdapter adapter2 = new NewsAdapter(getNews());
                newsTitleRecyclerView.setAdapter(adapter2);
            }
        });
        builder.show();
    }

    private void change(String word, String meaning,String StrWord) {
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
        String sql = "update words set word=?,meaning=? where word=?";
        db.execSQL(sql,new String[]{word,meaning,StrWord});
    }
}
