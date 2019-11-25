package com.example.words5;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FindTitleFragment extends Fragment {
    private boolean isTwoPane;
    private WordsDBHelper WdbHelper;
    String mess;
    FindTitleFragment(){

    }
    FindTitleFragment(String s){
        this.mess = s;
    }
//    FindTitleFragment(){
//
//    }
//    FindTitleFragment(String mess){
//        this.mess = mess;
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FindActivity findActivity = (FindActivity) getActivity();

//        Bundle bundle =this.getArguments();//得到从Activity传来的数据
//        String mess = null;
//        if(bundle!=null){
//            mess = bundle.getString("FindActivity");
//        }

//        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);

        WdbHelper = new WordsDBHelper(getContext(), "wordsdb", findActivity, 1);
        View view = inflater.inflate(R.layout.news_title_frag, container, false);
        RecyclerView newsTitleRecyclerView = (RecyclerView) view.findViewById(R.id.news_title_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        newsTitleRecyclerView.setLayoutManager(layoutManager);
        Bundle bundle = this.getArguments();//得到从Activity传来的数据

        if (bundle != null) {
            String text;
            text = bundle.getString("FindActivity");
            mess = text;
            Log.d("获取", mess);
        }
        FindTitleFragment.NewsAdapter adapter = new FindTitleFragment.NewsAdapter(getNews(mess));
        newsTitleRecyclerView.setAdapter(adapter);
        return view;
    }

    private List<Words> getNews(String find_word) {
        List<Words> wordsList = new ArrayList<>();
        SQLiteDatabase db = WdbHelper.getWritableDatabase();
//        String[] projection = {"id","word","meaning"};
//        String sortOrder = "word DESC";
//        String selection = "word like ?";
//        String[] selectionArgs = {"%"+find_word+""};
        Log.d("查询1", "1");
//        Cursor cursor = db.query("words",projection,selection,selectionArgs,null,null,sortOrder);
        String sql = "select * from words where word like ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + find_word + "%"});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
                Words words = new Words();
                words.setTitle(word);
                words.setContent(meaning);
                wordsList.add(words);
                Log.d("查询2", "2");
            }
            cursor.close();
        }
        Log.d("查询3",wordsList.toString());
        return wordsList;
    }

//    private String getRandomLengthContent(String content){
//        Random random = new Random();
//        int length = random.nextInt(20)+1;
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0;i < length;i++){
//            builder.append(content);
//        }
//        return builder.toString();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getActivity().findViewById(R.id.news_content_layout) != null) {
            isTwoPane = true;
        } else {
            isTwoPane = false;
        }
    }

    class NewsAdapter extends RecyclerView.Adapter<FindTitleFragment.NewsAdapter.ViewHolder> {
        private List<Words> mWordsList;

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView newsTitleText;

            public ViewHolder(View view) {
                super(view);
                newsTitleText = (TextView) view.findViewById(R.id.news_title);
            }
        }

        public NewsAdapter(List<Words> wordsList) {
            mWordsList = wordsList;
        }

        @Override
        public FindTitleFragment.NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            final FindTitleFragment.NewsAdapter.ViewHolder holder = new FindTitleFragment.NewsAdapter.ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Words words = mWordsList.get(holder.getAdapterPosition());
                    if (isTwoPane) {
                        NewsContentFragment newsContentFragment = (NewsContentFragment) getFragmentManager().findFragmentById(R.id.news_content_fragment);
                        newsContentFragment.refresh(words.getTitle(), words.getContent());
                    } else {
                        NewsContentActivity.actionStart(getActivity(), words.getTitle(), words.getContent());
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(FindTitleFragment.NewsAdapter.ViewHolder holder, int position) {
            Words words = mWordsList.get(position);
            holder.newsTitleText.setText(words.getTitle());
        }

        @Override
        public int getItemCount() {
            return mWordsList.size();
        }
    }
}
