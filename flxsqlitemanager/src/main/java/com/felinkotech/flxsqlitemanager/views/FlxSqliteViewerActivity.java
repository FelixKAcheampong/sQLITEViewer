package com.felinkotech.flxsqlitemanager.views;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.felinkotech.flxsqlitemanager.R;
import com.felinkotech.flxsqlitemanager.baseviews.BaseView;
import com.felinkotech.flxsqlitemanager.models.Table;

import java.util.ArrayList;
import java.util.List;

import static com.felinkotech.flxsqlitemanager.views.FlxSqliteViewerTableViewActivity.TABLE_MODEL_KEY;

public class FlxSqliteViewerActivity extends BaseView {
    private RecyclerView recyclerView ;
    private TablesAdapter tablesAdapter ;
    private List<Table> tables = new ArrayList<>() ;
    private List<Table> tablesBk = new ArrayList<>() ;
    private SQLiteDatabase database ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_lists_layout);

        Toolbar toolbar = findViewById(R.id.toolbar) ;
        setSupportActionBar(toolbar);

        tables = getTables() ;
        tablesBk = getTables() ;

        recyclerView = findViewById(R.id.tables) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false)) ;
        recyclerView.hasFixedSize() ;

        tablesAdapter = new TablesAdapter(this,tables) ;
        recyclerView.setAdapter(tablesAdapter) ;

        ((EditText)findViewById(R.id.search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tables.clear();
                for(Table table:tablesBk){
                    if(table.getTableName().toLowerCase().contains(editable.toString().toLowerCase())) tables.add(table) ;
                }
                if(tablesAdapter!=null){
                    tablesAdapter.notifyDataSetChanged();
                }else{
                    tablesAdapter = new TablesAdapter(FlxSqliteViewerActivity.this,tables) ;
                    recyclerView.setAdapter(tablesAdapter) ;
                }
            }
        });
    }

    private List<Table> getTables(){
        List<Table> tables = new ArrayList<>() ;
        try {
            Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%' order by name asc;",null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        int numberOfItems = 0;
                        try {
                            Cursor cursor1 = database.rawQuery("SELECT COUNT(*) FROM " + cursor.getString(0),null);
                            if (cursor1 != null) {
                                cursor1.moveToFirst();
                                numberOfItems = cursor1.getInt(0);
                                cursor1.close();
                            }
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                        final int totalRecords = numberOfItems;
                        tables.add(new Table() {{
                            setTableName(cursor.getString(0));
                            setNumberOfRecords(totalRecords);
                        }});
                        cursor.moveToNext();
                    }
                }
                cursor.close();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return tables ;
    }

    private class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.TablesViewHolder>{
        List<Table> tables ;
        Context context ;
        TablesAdapter(Context context,List<Table> tables){
            this.context = context ;
            this.tables = tables ;
        }
        @NonNull
        @Override
        public TablesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View row = LayoutInflater.from(context).inflate(R.layout.tables_single_row,parent,false) ;

            return new TablesViewHolder(row) ;
        }

        @Override
        public void onBindViewHolder(@NonNull TablesViewHolder holder, int position) {
            Table table = tables.get(holder.getAdapterPosition()) ;
            holder.tableName.setText(table.getTableName()) ;
            holder.numberOfRecords.setText(String.valueOf(table.getNumberOfRecords()));
            holder.root.setOnClickListener(v->{
                Intent intent = new Intent(FlxSqliteViewerActivity.this, FlxSqliteViewerTableViewActivity.class) ;
                intent.putExtra(TABLE_MODEL_KEY,table) ;
                startActivity(intent) ;
            });
        }

        @Override
        public int getItemCount() {
            return tables.size();
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public int getItemViewType(int position) {
            return position ;
        }

        private class TablesViewHolder extends RecyclerView.ViewHolder{
            TextView tableName,numberOfRecords ;
            RelativeLayout root ;
            TablesViewHolder(@NonNull View itemView) {
                super(itemView);
                this.tableName = itemView.findViewById(R.id.table_name) ;
                this.numberOfRecords = itemView.findViewById(R.id.number_of_records) ;
                this.root= itemView.findViewById(R.id.root) ;
            }
        }
    }
}
