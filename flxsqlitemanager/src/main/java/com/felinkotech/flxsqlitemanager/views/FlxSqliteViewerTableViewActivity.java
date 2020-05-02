package com.felinkotech.flxsqlitemanager.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.felinkotech.flxsqlitemanager.R;
import com.felinkotech.flxsqlitemanager.baseviews.BaseView;
import com.felinkotech.flxsqlitemanager.customviews.HistoryViewScrollable;
import com.felinkotech.flxsqlitemanager.models.FieldView;
import com.felinkotech.flxsqlitemanager.models.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlxSqliteViewerTableViewActivity extends BaseView implements HistoryViewScrollable.onActionButtonClickedListener {
    public static final String TABLE_MODEL_KEY = "table_model_key" ;
    private Toolbar toolbar ;
    private HistoryViewScrollable historyViewScrollable;
    private TableViewAdapter tableViewAdapter ;
    private List<FieldView> tables = new ArrayList<>() ;
    private SQLiteDatabase database ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_view_content_layout);
        toolbar = findViewById(R.id.toolbar) ;
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true) ;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras() ;
        if(extras!=null && extras.containsKey(TABLE_MODEL_KEY)){
            Table table = extras.getParcelable(TABLE_MODEL_KEY) ;
            if(table!=null) {
                init(table);
            }
        }
    }

    private void init(Table table){
        setTitle(table.getTableName()+" ("+ table.getNumberOfRecords() +")");

        historyViewScrollable = findViewById(R.id.tableview) ;
        historyViewScrollable.setData(getFieldViews(table))
                .setOnActionButtonClickedListener(this)
                .initializeComponent();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home) finish();
        return true ;
    }

    private String getQuery(Table table){
        String query = "" ;
        try {
            Cursor cursor = database.rawQuery("PRAGMA table_info(" + table.getTableName() + ")",null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    int counter = 0;
                    while (!cursor.isAfterLast()) {
                        query +="substr(`"+ cursor.getString(1) +"`, 1, 100) AS "+cursor.getString(1) ;
                        if(counter<cursor.getCount()-1){
                            query += "," ;
                        }
                        cursor.moveToNext();
                        counter++;
                    }
                }
                cursor.close();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        if(!query.equals("")) {
            return "SELECT " + query + " FROM " + table.getTableName();
        }else return null ;
    }

    private List<List<String>> getFieldViews(Table table){
        List<List<String>> fieldViews = new ArrayList<>() ;
        String query = getQuery(table) ;
        if(query==null){
            return fieldViews ;
        }
        try{
            Cursor cursor = database.rawQuery(query,null) ;
            if(cursor!=null){
                if(cursor.getCount()>0){
                    cursor.moveToFirst() ;
                    int counter = 0 ;
                    while (!cursor.isAfterLast()) {
                        List<String> fieldValues = new ArrayList<>() ;
                        try {
                            Cursor cursor1 = database.rawQuery("PRAGMA table_info(" + table.getTableName() + ")",null);
                            if (cursor1 != null) {
                                if (cursor1.getCount() > 0) {
                                    cursor1.moveToFirst();
                                    while (!cursor1.isAfterLast()) {
                                        if(counter==0){
                                            fieldValues.add(cursor1.getString(1)) ;
                                        }else{
                                            fieldValues.add(cursor.getString(cursor.getColumnIndex(cursor1.getString(1)))) ;
                                        }
                                        /*fieldValues += "<span style='color:#004D40;'>" + cursor1.getString(1) + " | " + cursor1.getString(2) + "</span><br> <b><span style='color:#000000'>" + cursor.getString(cursor.getColumnIndex(cursor1.getString(1))) + "</span></b>";
                                        if (counter < cursor1.getCount() - 1) {
                                            fieldValues += "<br><br>";
                                        }*/
                                        cursor1.moveToNext();
                                    }
                                }
                                cursor1.close();
                            }
                        }catch (SQLiteException e){
                            e.printStackTrace();
                        }
                        fieldViews.add(fieldValues) ;
                        cursor.moveToNext() ;
                        counter++ ;
                    }
                }
                cursor.close();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return fieldViews ;
    }

    @Override
    public void onActionButtonClicked(List<String> allRowData) {
        StringBuilder builder = new StringBuilder() ;
        for(String string:allRowData){
            builder.append(string).append("\n") ;
        }
        Toast.makeText(this,builder.toString(),Toast.LENGTH_LONG).show() ;
    }

    @Override
    public void onTextClicked(List<String> allRowData, String clickedValue) {
        Toast.makeText(this,clickedValue,Toast.LENGTH_LONG).show() ;
    }

    private class TableViewAdapter extends RecyclerView.Adapter<TableViewAdapter.TableViewViewHolder>{
        Context context ;
        List<String> fieldViews ;
        TableViewAdapter(Context context,List<String> fieldViews){
            this.context = context ;
            this.fieldViews = fieldViews ;
        }
        @NonNull
        @Override
        public TableViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View row = LayoutInflater.from(context).inflate(R.layout.field_view_single_row,parent,false) ;
            return new TableViewViewHolder(row) ;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TableViewViewHolder holder, int position) {
            String fieldView = fieldViews.get(holder.getAdapterPosition()) ;
            holder.fieldInfo.setText(Html.fromHtml(fieldView+"")) ;
        }

        @Override
        public int getItemViewType(int position) {
            return position ;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public int getItemCount() {
            return fieldViews.size();
        }

        class TableViewViewHolder extends RecyclerView.ViewHolder{
            TextView fieldInfo ;
            TableViewViewHolder(@NonNull View itemView) {
                super(itemView);
                this.fieldInfo = itemView.findViewById(R.id.field_info);
            }
        }
    }
}
