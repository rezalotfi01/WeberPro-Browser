package com.github.rezalotfi01.weberpro.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.github.rezalotfi01.weberpro.Utils.RecordUtils;

class RecordHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weber.db";
    private static final int DATABASE_VERSION = 1;

    public RecordHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RecordUtils.INSTANCE.getCREATE_BOOKMARKS());
        database.execSQL(RecordUtils.INSTANCE.getCREATE_HISTORY());
        database.execSQL(RecordUtils.INSTANCE.getCREATE_WHITELIST());
        database.execSQL(RecordUtils.INSTANCE.getCREATE_GRID());
        //database.execSQL(RecordUtils.CREATE_DOWNLOADS);
    }

    // UPGRADE ATTENTION!!!
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}

    // UPGRADE ATTENTION!!!
    private boolean isTableExist(@NonNull String tableName) {
        return false;
    }
}
