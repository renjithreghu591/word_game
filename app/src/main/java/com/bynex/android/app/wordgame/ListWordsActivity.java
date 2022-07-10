package com.bynex.android.app.wordgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListWordsActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_words);

        listView = findViewById(R.id.list_view);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("words", Context.MODE_PRIVATE);

        ArrayList<String> wordList = new ArrayList<>();
        ArrayList<String> meaningList = new ArrayList<>();

        Map<String , ?> map = sharedPreferences.getAll();
        Set<String> set = map.keySet();

        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            String str = iterator.next().toString();
            wordList.add(str);
            meaningList.add(sharedPreferences.getString(str, "null"));
        }

        Adapter adapter = new WordListAdapter(getApplicationContext(), R.layout.word_list_layout, wordList, meaningList);
        listView.setAdapter((ListAdapter) adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            int isSelected = 0;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListWordsActivity.this);

                View v = LayoutInflater.from(ListWordsActivity.this).inflate(R.layout.list_edit_delete, null);
                ImageButton edit = v.findViewById(R.id.listedit_imgbtn_edit);
                ImageButton delete = v.findViewById(R.id.listedit_imgbtn_delete);


                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edit.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                        delete.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
                        isSelected = 1;
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete.setBackgroundTintMode(PorterDuff.Mode.OVERLAY);
                        edit.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
                        isSelected = 2;
                    }
                });

                builder.setTitle("Options").setView(v).setNegativeButton("Back", null).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String wordStr = wordList.get((int) l);
                        String meaningStr = meaningList.get((int) l);
                        switch (isSelected) {
                            case 0:
                                break;

                            case 1:
                                View view1 = LayoutInflater.from(ListWordsActivity.this).inflate(R.layout.new_word_layout, null);
                                TextView tv = view1.findViewById(R.id.etxt_new_word);
                                TextView tv1 = view1.findViewById(R.id.etxt_new_word_meaning);

                                tv.setHint(wordStr);
                                tv1.setHint(meaningStr);

                                builder.setView(view1).setTitle("Edit").setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String tvStr = tv.getText().toString();
                                        String tv1Str = tv1.getText().toString();
                                        if (!tvStr.equals("") && !tv1Str.equals("")) {
                                            sharedPreferences.edit().remove(wordStr).commit();
                                            sharedPreferences.edit().putString(tvStr, tv1Str).commit();

                                            wordList.remove((int) l);
                                            wordList.add(tvStr);
                                            meaningList.remove((int) l);
                                            meaningList.add(tv1Str);
                                            ((WordListAdapter) adapter).notifyDataSetChanged();
                                        }
                                    }
                                });
                                builder.show();
                                Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_SHORT).show();
                                break;

                            case 2:

                                sharedPreferences.edit().remove(wordStr).commit();
                                wordList.remove((int) l);
                                meaningList.remove((int) l);
                                ((WordListAdapter) adapter).notifyDataSetChanged();
                                break;
                        }
                    }
                }).setCancelable(false);
                builder.show();
            }
        });
    }

    private class WordListAdapter extends ArrayAdapter<String> {

        private int resource;
        private List<String> wordList, meaningList;

        public WordListAdapter(@NonNull Context context, int resource, List<String> objects, List<String> meaning) {
            super(context, resource, objects);
            this.resource = resource;
            wordList = objects;
            meaningList = meaning;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder mainViewHolder = new ViewHolder();

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(resource, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.no = (TextView) convertView.findViewById(R.id.txt_list_count);
                viewHolder.word = (TextView) convertView.findViewById(R.id.txt_list_word);
                viewHolder.meaning = (TextView) convertView.findViewById(R.id.txt_list_word_meaning);

                viewHolder.no.setText(position + 1 + "");
                viewHolder.word.setText(wordList.get(position));
                viewHolder.meaning.setText(meaningList.get(position));
                convertView.setTag(viewHolder);
            } else {
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.word.setText((CharSequence) getItem(position));
                mainViewHolder.no.setText(position + 1 + "");
                mainViewHolder.meaning.setText(meaningList.get(position));
            }

            return convertView;
        }
    }

    public class ViewHolder {
        TextView no, word, meaning;
    }
}