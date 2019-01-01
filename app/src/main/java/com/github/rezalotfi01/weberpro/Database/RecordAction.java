package com.github.rezalotfi01.weberpro.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.rezalotfi01.weberpro.Utils.RecordUtils;
import com.github.rezalotfi01.weberpro.View.GridItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecordAction {
    private SQLiteDatabase database;
    private final RecordHelper helper;

    public RecordAction(Context context) {
        this.helper = new RecordHelper(context);
    }

    public void open(boolean rw) {
        if (rw) {
            database = helper.getWritableDatabase();
        } else {
            database = helper.getReadableDatabase();
        }
    }

    public void close() {
        helper.close();
    }

    public boolean addBookmark(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0L) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), record.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), record.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_TIME(), record.getTime());
        database.insert(RecordUtils.INSTANCE.getTABLE_BOOKMARKS(), null, values);

        return true;
    }

    public boolean addHistory(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0L) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), record.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), record.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_TIME(), record.getTime());
        database.insert(RecordUtils.INSTANCE.getTABLE_HISTORY(), null, values);

        return true;
    }

    public boolean addDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_DOMAIN(), domain.trim());
        database.insert(RecordUtils.INSTANCE.getTABLE_WHITELIST(), null, values);

        return true;
    }

    public boolean addGridItem(GridItem item) {
        if (item == null
                || item.getTitle() == null
                || item.getTitle().trim().isEmpty()
                || item.getURL() == null
                || item.getURL().trim().isEmpty()
                || item.getFilename() == null
                || item.getFilename().trim().isEmpty()
                || item.getOrdinal() < 0) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), item.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), item.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_FILENAME(), item.getFilename().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_ORDINAL(), item.getOrdinal());
        database.insert(RecordUtils.INSTANCE.getTABLE_GRID(), null, values);

        return true;
    }

    public boolean addDownload(Record record){
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getPath() == null
                || record.getPath().trim().isEmpty()
                || record.getFileName() == null
                || record.getFileName().trim().isEmpty()
                || record.getTime() < 0L) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), record.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), record.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_PATH(), record.getPath().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_FILENAME(), record.getFileName().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_TIME(), record.getTime());
        database.insert(RecordUtils.INSTANCE.getTABLE_DOWNLOADS(), null, values);

        return true;
    }

    public boolean updateBookmark(Record record) {
        if (record == null
                || record.getTitle() == null
                || record.getTitle().trim().isEmpty()
                || record.getURL() == null
                || record.getURL().trim().isEmpty()
                || record.getTime() < 0) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), record.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), record.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_TIME(), record.getTime());
        database.update(RecordUtils.INSTANCE.getTABLE_BOOKMARKS(), values, RecordUtils.INSTANCE.getCOLUMN_TIME() + "=?", new String[] {String.valueOf(record.getTime())});

        return true;
    }

    public boolean updateGridItem(GridItem item) {
        if (item == null
                || item.getTitle() == null
                || item.getTitle().trim().isEmpty()
                || item.getURL() == null
                || item.getURL().trim().isEmpty()
                || item.getFilename() == null
                || item.getFilename().trim().isEmpty()
                || item.getOrdinal() < 0) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RecordUtils.INSTANCE.getCOLUMN_TITLE(), item.getTitle().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_URL(), item.getURL().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_FILENAME(), item.getFilename().trim());
        values.put(RecordUtils.INSTANCE.getCOLUMN_ORDINAL(), item.getOrdinal());
        database.update(RecordUtils.INSTANCE.getTABLE_GRID(), values, RecordUtils.INSTANCE.getCOLUMN_URL() + "=?", new String[] {item.getURL()});

        return true;
    }

    public boolean checkBookmark(Record record) {
        if (record == null || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_BOOKMARKS(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_URL()},
                RecordUtils.INSTANCE.getCOLUMN_URL() + "=?",
                new String[] {record.getURL().trim()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean checkBookmark(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_BOOKMARKS(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_URL()},
                RecordUtils.INSTANCE.getCOLUMN_URL() + "=?",
                new String[] {url.trim()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean checkDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_WHITELIST(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_DOMAIN()},
                RecordUtils.INSTANCE.getCOLUMN_DOMAIN() + "=?",
                new String[] {domain.trim()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean checkGridItem(GridItem item) {
        if (item == null || item.getURL() == null || item.getURL().trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_GRID(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_URL()},
                RecordUtils.INSTANCE.getCOLUMN_URL() + "=?",
                new String[] {item.getURL().trim()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean checkGridItem(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_GRID(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_URL()},
                RecordUtils.INSTANCE.getCOLUMN_URL() + "=?",
                new String[] {url.trim()},
                null,
                null,
                null
        );

        if (cursor != null) {
            boolean result = false;
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();

            return result;
        }

        return false;
    }

    public boolean deleteBookmark(Record record) {
        if (record == null || record.getURL() == null || record.getURL().trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_BOOKMARKS() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_URL() + " = " + "\"" + record.getURL().trim() + "\"");
        return true;
    }

    public boolean deleteBookmark(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUtils.INSTANCE.getTABLE_BOOKMARKS() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_URL() + " = " + "\"" + url.trim() + "\"");
        return true;
    }

    public boolean deleteHistory(Record record) {
        if (record == null || record.getTime() <= 0) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUtils.INSTANCE.getTABLE_HISTORY() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_TIME() + " = " + record.getTime());
        return true;
    }

    public boolean deleteDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUtils.INSTANCE.getTABLE_WHITELIST() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_DOMAIN() + " = " + "\"" + domain.trim() + "\"");
        return true;
    }

    public boolean deleteGridItem(GridItem item) {
        if (item == null || item.getURL() == null || item.getURL().trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_GRID() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_URL() + " = " + "\"" + item.getURL().trim() + "\"");
        return true;
    }

    public boolean deleteGridItem(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        database.execSQL("DELETE FROM "+ RecordUtils.INSTANCE.getTABLE_GRID() + " WHERE " + RecordUtils.INSTANCE.getCOLUMN_URL() + " = " + "\"" + url.trim() + "\"");
        return true;
    }



    public void clearBookmarks() {
        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_BOOKMARKS());
    }

    public void clearHistory() {
        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_HISTORY());
    }

    public void clearDomains() {
        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_WHITELIST());
    }

    public void clearGrid() {
        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_GRID());
    }

    public void clearDownloads(){
        database.execSQL("DELETE FROM " + RecordUtils.INSTANCE.getTABLE_DOWNLOADS());
    }


    private Record getRecord(Cursor cursor) {
        Record record = new Record();
        record.setTitle(cursor.getString(0));
        record.setURL(cursor.getString(1));
        record.setTime(cursor.getLong(2));

        return record;
    }

    private GridItem getGridItem(Cursor cursor) {
        GridItem item = new GridItem();
        item.setTitle(cursor.getString(0));
        item.setURL(cursor.getString(1));
        item.setFilename(cursor.getString(2));
        item.setOrdinal(cursor.getInt(3));

        return item;
    }



    public List<Record> listBookmarks() {
        List<Record> list = new ArrayList<>();

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_BOOKMARKS(),
                new String[] {
                        RecordUtils.INSTANCE.getCOLUMN_TITLE(),
                        RecordUtils.INSTANCE.getCOLUMN_URL(),
                        RecordUtils.INSTANCE.getCOLUMN_TIME()
                },
                null,
                null,
                null,
                null,
                RecordUtils.INSTANCE.getCOLUMN_TIME() + " desc"
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<Record> listHistory() {
        List<Record> list = new ArrayList<>();

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_HISTORY(),
                new String[] {
                        RecordUtils.INSTANCE.getCOLUMN_TITLE(),
                        RecordUtils.INSTANCE.getCOLUMN_URL(),
                        RecordUtils.INSTANCE.getCOLUMN_TIME()
                },
                null,
                null,
                null,
                null,
                RecordUtils.INSTANCE.getCOLUMN_TIME() + " desc"
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<String> listDomains() {
        List<String> list = new ArrayList<>();

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_WHITELIST(),
                new String[] {RecordUtils.INSTANCE.getCOLUMN_DOMAIN()},
                null,
                null,
                null,
                null,
                RecordUtils.INSTANCE.getCOLUMN_DOMAIN()
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<GridItem> listGrid() {
        List<GridItem> list = new LinkedList<>();

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_GRID(),
                new String[] {
                        RecordUtils.INSTANCE.getCOLUMN_TITLE(),
                        RecordUtils.INSTANCE.getCOLUMN_URL(),
                        RecordUtils.INSTANCE.getCOLUMN_FILENAME(),
                        RecordUtils.INSTANCE.getCOLUMN_ORDINAL()
                },
                null,
                null,
                null,
                null,
                RecordUtils.INSTANCE.getCOLUMN_ORDINAL()
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getGridItem(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<Record> listDownloads() {
        List<Record> list = new ArrayList<>();

        Cursor cursor = database.query(
                RecordUtils.INSTANCE.getTABLE_DOWNLOADS(),
                new String[] {
                        RecordUtils.INSTANCE.getCOLUMN_TITLE(),
                        RecordUtils.INSTANCE.getCOLUMN_URL(),
                        RecordUtils.INSTANCE.getCOLUMN_FILENAME(),
                        RecordUtils.INSTANCE.getCOLUMN_PATH(),
                        RecordUtils.INSTANCE.getCOLUMN_TIME()
                },
                null,
                null,
                null,
                null,
                RecordUtils.INSTANCE.getCOLUMN_TIME() + " desc"
        );

        if (cursor == null) {
            return list;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(getRecord(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }


}
