package com.github.rezalotfi01.weberpro.Database.DownloadsDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created  on 11/02/2016.
 */
class DownloadDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME    = "weber_downloads.db";
    private static final int    DATABASE_VERSION = 1;
    private Dao<DownloadEntity, Integer> mUserDao = null;

    public DownloadDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource,DownloadEntity.class);
        } catch (Exception e) {
            Log.e("Weber TAG", "onCreate SQL Table: "+e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DownloadEntity.class, true);
            onCreate(database, connectionSource);
        } catch (Exception e) {
            Log.e("Weber Tag", "onUpgrade Exception : "+e.toString());
        }
    }

    /* Downloads */

    public Dao<DownloadEntity, Integer> getUserDao() throws SQLException {
        if (mUserDao == null) {
            mUserDao = getDao(DownloadEntity.class);
        }
        return mUserDao;
    }

    @Override
    public void close() {
        mUserDao = null;
        super.close();
    }

}
