package com.felinkotech.flxsqlitemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.felinkotech.flxsqlitemanager.views.FlxSqliteViewerTableViewActivity;

public class FlxSqliteViewer {
    public static SQLiteDatabase database ;
    public void launchViewer(Activity activity,SQLiteDatabase database){
        this.database = database ;
        activity.startActivity(new Intent(activity.getApplicationContext(), FlxSqliteViewerTableViewActivity.class));
    }
}
