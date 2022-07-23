package com.bynex.android.app.wordgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryActivity extends AppCompatActivity {

    ListView categoryListView;
    com.google.android.material.floatingactionbutton.FloatingActionButton addCategory;
    TextView title;
    Data data = new Data(CategoryActivity.this);

    int listViewPage = Values.PAGE_CATEGORIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryListView = findViewById(R.id.category_listview_category);
        addCategory = findViewById(R.id.floatingActionButton);
        title = findViewById(R.id.category_title);

        listViewPage = Values.PAGE_CATEGORIES;
        Adapter adapter = new CategoryComponentsAdapter(getApplicationContext(), R.layout.category_components_layout, data.getList(Values.CATEGORIES));
        categoryListView.setAdapter((ListAdapter) adapter);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(Values.WRONG_WORDS)) {
                    addCategory.setEnabled(false);
                } else {
                    addCategory.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String titleText = title.getText().toString();

                if (titleText.equals("Categories")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                    View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_add_layout, null);
                    com.google.android.material.textfield.TextInputEditText newCategoryName = v.findViewById(R.id.category_material_etxt);

                    builder.setView(v).setTitle("Add new category").setNegativeButton("Cancel", null).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String str = newCategoryName.getText().toString();
                            if (!str.equals("")) {

                                if (data.createCategory(str)) {
                                    Adapter adapter1 = categoryListView.getAdapter();
                                    ((CategoryComponentsAdapter) adapter1).categories.add(str);
                                    ((CategoryComponentsAdapter) adapter1).notifyDataSetChanged();
                                }
                            }
                        }
                    }).setCancelable(false);

                    builder.show();
                } else if (titleText.equals(Values.WRONG_WORDS)) {

                } else {
                    //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(titleText, Context.MODE_PRIVATE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                    View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.addword_current_category_layout, null);
                    com.google.android.material.textfield.TextInputEditText newWord = v.findViewById(R.id.addword_current_etxt_word);
                    com.google.android.material.textfield.TextInputEditText newWordMeaning = v.findViewById(R.id.addword_current_etxt_meaning);

                    builder.setView(v).setTitle("New word").setMessage("Add new word to " + titleText).setNegativeButton("Cancel", null).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String newWordStr = newWord.getText().toString();
                            String newWordMeaningStr = newWordMeaning.getText().toString();

                            if (!newWordStr.equals("") && !newWordMeaningStr.equals("")) {

                                if (data.add(titleText, newWordStr, newWordMeaningStr)) {
                                    Adapter adapter1 = categoryListView.getAdapter();
                                    ((CategoryComponentsAdapter) adapter1).categories.add(newWordStr);
                                    ((CategoryComponentsAdapter) adapter1).notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Please fill data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setCancelable(false);
                    builder.show();
                }
            }
        });

    }

    @Override
    public void onBackPressed(){

        switch (listViewPage) {
            case Values.PAGE_CATEGORIES:
                super.onBackPressed();
                break;

            case Values.PAGE_CATEGORY:

            case Values.PAGE_WRONG_CATEGORIES:
                title.setText("Categories");
                listViewPage = Values.PAGE_CATEGORIES;
                Adapter adapter = new CategoryComponentsAdapter(getApplicationContext(), R.layout.category_components_layout, data.getList(Values.CATEGORIES));
                categoryListView.setAdapter((ListAdapter) adapter);
                break;

            case Values.PAGE_WRONG_CATEGORY:
                title.setText(Values.WRONG_WORDS);
                listViewPage = Values.PAGE_WRONG_CATEGORIES;
                Adapter adapter1 = new WrongCategoryComponentsAdapter(getApplicationContext(), R.layout.wrong_category_components_layout, data.getList(Values.WRONG_WORDS));
                categoryListView.setAdapter((WrongCategoryComponentsAdapter) adapter1);
                break;
        }
    }

    private class CategoryComponentsAdapter extends ArrayAdapter<String> {

        private int resource;
        private List<String> categories;
        private Context context;

        public CategoryComponentsAdapter(@NonNull Context context, int resource, List<String> categories) {
            super(context, resource, categories);
            this.resource = resource;
            this.categories = categories;
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



            if (listViewPage == Values.PAGE_CATEGORIES) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).equals(Values.WRONG_WORDS)) {
                        categories.remove(i);
                        break;
                    }
                }

                Collections.sort(categories);
                categories.add(0, Values.WRONG_WORDS);



                CategoryComponentHolder categoryComponentHolder = new CategoryComponentHolder();

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(resource, parent , false);

                    CategoryComponentHolder categoryComponentHolder1 = new CategoryComponentHolder();
                    categoryComponentHolder1.category = (TextView) convertView.findViewById(R.id.category_txt_categoryname);
                    categoryComponentHolder1.edit = (ImageButton) convertView.findViewById(R.id.category_imgbtn_edit);
                    categoryComponentHolder1.delete = (ImageButton) convertView.findViewById(R.id.category_imgbtn_delete);
                    categoryComponentHolder1.progressBar = (ProgressBar) convertView.findViewById(R.id.category_progressBar);
                    categoryComponentHolder1.maxCount = (TextView) convertView.findViewById(R.id.category_txt_outof);
                    categoryComponentHolder1.progCount = (TextView) convertView.findViewById(R.id.category_txt_progcount);

                    if (categories.get(position).equals(Values.WRONG_WORDS)) {
                        categoryComponentHolder1.edit.setEnabled(false);
                        categoryComponentHolder1.delete.setEnabled(false);
                        categoryComponentHolder1.category.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        categoryComponentHolder1.category.setTextSize(categoryComponentHolder1.category.getAutoSizeMinTextSize());
                        categoryComponentHolder1.category.setTextColor(Color.RED);
                        categoryComponentHolder1.progressBar.setProgress(0);
                        categoryComponentHolder1.maxCount.setVisibility(View.INVISIBLE);
                        categoryComponentHolder1.progCount.setVisibility(View.INVISIBLE);

                        categoryComponentHolder1.category.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listViewPage = Values.PAGE_WRONG_CATEGORIES;
                                Adapter adapter = new WrongCategoryComponentsAdapter(context, R.layout.wrong_category_components_layout, data.getList(Values.WRONG_WORDS));
                                title.setText(Values.WRONG_WORDS);
                                categoryListView.setAdapter((WrongCategoryComponentsAdapter) adapter);
                            }
                        });

                    } else {

                        int size = data.getSize(categories.get(position));
                        int prog = size - data.getSize(categories.get(position) + " Wrong");

                        if (prog == 0) {
                            categoryComponentHolder1.progressBar.setProgress(prog);
                        } else {
                            categoryComponentHolder1.progressBar.setProgress((prog * 100) / size);
                        }

                        categoryComponentHolder1.progCount.setText(prog + "");
                        categoryComponentHolder1.maxCount.setText(size + "");

                        categoryComponentHolder1.edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                                View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_add_layout, null);
                                com.google.android.material.textfield.TextInputEditText textInputEditText = v.findViewById(R.id.category_material_etxt);
                                com.google.android.material.textfield.TextInputLayout textInputLayout = v.findViewById(R.id.category_material_inputlayout);
                                textInputLayout.setHint(categories.get(position));

                                builder.setTitle("Rename category").setView(v).setNegativeButton("Cancel", null).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String str = textInputEditText.getText().toString();
                                        if (!str.equals("")) {

                                            data.renameCategory(categories.get(position), str);
                                            categories.remove(position);
                                            categories.add(position, str);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }).setCancelable(false);

                                builder.show();
                            }
                        });

                        categoryComponentHolder1.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                data.deleteCategory(categories.get(position));
                                categories.remove(position);
                                notifyDataSetChanged();
                            }
                        });

                        categoryComponentHolder1.category.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listViewPage = Values.PAGE_CATEGORY;
                                Adapter adapter = new CategoryComponentsAdapter(context, R.layout.word_meaning_layout, data.getList(categoryComponentHolder1.category.getText().toString()));
                                title.setText(categoryComponentHolder1.category.getText());
                                categoryListView.setAdapter((CategoryComponentsAdapter) adapter);
                            }
                        });

                    }

                    categoryComponentHolder1.category.setText(categories.get(position));
                    convertView.setTag(categoryComponentHolder1);
                } else {
                    categoryComponentHolder = (CategoryComponentHolder) convertView.getTag();

                    categoryComponentHolder.category.setText(categories.get(position));
                    int size = data.getSize(categories.get(position));
                    int prog = size - data.getSize(categories.get(position) + " Wrong");

                    if (prog == 0) {
                        categoryComponentHolder.progressBar.setProgress(prog);
                    } else {
                        categoryComponentHolder.progressBar.setProgress((prog * 100) / size);
                    }

                    categoryComponentHolder.progCount.setText(prog + "");
                    categoryComponentHolder.maxCount.setText(size + "");
                }
            }  else {

                // word and meaning list view

                ListWordsHolder listWordsHolder = new ListWordsHolder();
                SharedPreferences category = getApplicationContext().getSharedPreferences(title.getText().toString(), Context.MODE_PRIVATE);

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(resource, parent, false);

                    ListWordsHolder listWordsHolder1 = new ListWordsHolder();
                    listWordsHolder1.word = (TextView) convertView.findViewById(R.id.category_word);
                    listWordsHolder1.meaning = (TextView) convertView.findViewById(R.id.category_meaning);
                    listWordsHolder1.no = (TextView) convertView.findViewById(R.id.category_no);

                    listWordsHolder1.word.setText(categories.get(position));
                    listWordsHolder1.no.setText((position + 1) + "");
                    listWordsHolder1.meaning.setText(category.getString(categories.get(position), "null"));

                    listWordsHolder1.word.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);

                            builder.setTitle(listWordsHolder1.word.getText().toString()).setNeutralButton("Back", null).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    data.deleteWordAndMeaning(title.getText().toString(), listWordsHolder1.word.getText().toString());
                                    categories.remove(position);
                                    notifyDataSetChanged();
                                }
                            }).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_add_layout, null);
                                    com.google.android.material.textfield.TextInputEditText textInputEditText = v.findViewById(R.id.category_material_etxt);
                                    com.google.android.material.textfield.TextInputLayout textInputLayout = v.findViewById(R.id.category_material_inputlayout);
                                    textInputEditText.setText(listWordsHolder1.word.getText().toString());
                                    textInputLayout.setHint( listWordsHolder1.word.getText().toString() + " to");

                                    builder.setView(v).setTitle("Rename").setNegativeButton("Cancel", null).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String str = textInputEditText.getText().toString();

                                            if (!str.equals(listWordsHolder1.word.getText().toString())) {
                                                data.renameWord(title.getText().toString(), listWordsHolder1.word.getText().toString(), str);
                                                categories.remove(position);
                                                categories.add(position, str);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }).setNeutralButton("", null).show();
                                }
                            }).show();
                        }
                    });

                    listWordsHolder1.meaning.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);

                            builder.setTitle(listWordsHolder1.meaning.getText().toString()).setNeutralButton("Back", null).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    data.deleteWordAndMeaning(title.getText().toString(), listWordsHolder1.word.getText().toString());
                                    categories.remove(position);
                                    notifyDataSetChanged();
                                }
                            }).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_add_layout, null);
                                    com.google.android.material.textfield.TextInputEditText textInputEditText = v.findViewById(R.id.category_material_etxt);
                                    com.google.android.material.textfield.TextInputLayout textInputLayout = v.findViewById(R.id.category_material_inputlayout);
                                    textInputEditText.setText(listWordsHolder1.meaning.getText().toString());
                                    textInputLayout.setHint( listWordsHolder1.meaning.getText().toString() + " to");

                                    builder.setView(v).setTitle("Rename").setNegativeButton("Cancel", null).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String newMeaning = textInputEditText.getText().toString();

                                            if (!newMeaning.equals(listWordsHolder1.meaning.getText().toString())) {
                                                data.renameMeaning(title.getText().toString(), categories.get(position), newMeaning);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }).setNeutralButton("", null).show();
                                }
                            }).show();
                        }
                    });

                    convertView.setTag(listWordsHolder1);
                } else {
                    listWordsHolder = (ListWordsHolder) convertView.getTag();

                    listWordsHolder.word.setText(categories.get(position));
                    listWordsHolder.no.setText((position + 1) + "");
                    listWordsHolder.meaning.setText(category.getString(categories.get(position), "null"));

                }


            }


            return convertView;
        }
    }

    private class WrongCategoryComponentsAdapter extends ArrayAdapter<String> {
        int resource;
        List<String> catecories;
        Context context;

        public WrongCategoryComponentsAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            this.resource = resource;
            catecories = objects;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (listViewPage == Values.PAGE_WRONG_CATEGORIES) {

                WrongCategoryHolder wrongCategoryHolder = new WrongCategoryHolder();

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(resource, parent, false);

                    WrongCategoryHolder wrongCategoryHolder1 = new WrongCategoryHolder();
                    wrongCategoryHolder1.category = (TextView) convertView.findViewById(R.id.wrong_catgory_component_textview);
                    wrongCategoryHolder1.count = (TextView) convertView.findViewById(R.id.wrong_catgory_component_count);

                    wrongCategoryHolder1.category.setText(catecories.get(position));
                    wrongCategoryHolder1.count.setText(data.getSize(wrongCategoryHolder1.category.getText().toString()) + "");

                    wrongCategoryHolder1.category.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listViewPage = Values.PAGE_WRONG_CATEGORY;
                            title.setText(wrongCategoryHolder1.category.getText().toString());
                            Adapter adapter = new WrongCategoryComponentsAdapter(context, R.layout.word_meaning_layout, data.getList(wrongCategoryHolder1.category.getText().toString()));
                            categoryListView.setAdapter((WrongCategoryComponentsAdapter) adapter);
                        }
                    });

                    convertView.setTag(wrongCategoryHolder1);

                } else {
                    wrongCategoryHolder = (WrongCategoryHolder) convertView.getTag();
                    wrongCategoryHolder.category.setText(catecories.get(position));
                    wrongCategoryHolder.count.setText(data.getSize(wrongCategoryHolder.category.getText().toString()) + "");
                }

            } else {

                ListWordsHolder listWordsHolder = new ListWordsHolder();
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(resource, parent, false);

                    ListWordsHolder listWordsHolder1 = new ListWordsHolder();
                    listWordsHolder1.no = convertView.findViewById(R.id.category_no);
                    listWordsHolder1.word = convertView.findViewById(R.id.category_word);
                    listWordsHolder1.meaning = convertView.findViewById(R.id.category_meaning);


                    listWordsHolder1.no.setText((position + 1) + "");
                    listWordsHolder1.word.setText(catecories.get(position));
                    listWordsHolder1.meaning.setText(data.getValue(title.getText().toString(), catecories.get(position)));

                    convertView.setTag(listWordsHolder1);
                } else {
                    listWordsHolder = (ListWordsHolder) convertView.getTag();
                    listWordsHolder.no.setText((position + 1) + "");
                    listWordsHolder.word.setText(catecories.get(position));
                    listWordsHolder.meaning.setText(data.getValue(title.getText().toString(), catecories.get(position)));
                }

            }

            return convertView;
        }
    }

    public class CategoryComponentHolder {
        TextView category, progCount, maxCount;
        ImageButton edit, delete;
        ProgressBar progressBar;
    }

    public class ListWordsHolder {
        TextView word, meaning, no;
    }

    public class WrongCategoryHolder {
        TextView category, count;
    }
}