package com.github.rezalotfi01.weberpro.Database.DownloadsDB;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created  on 11/02/2016.
 */
public class DownloadDBUtils {
    public DownloadDBUtils() {
    }

    public static boolean createOrUpdateInDatabase(Context ctx, String fileName, String fileURL , String downloadedSize, String remainingTime, String fileSaveAddress, String speed , double percent, boolean isFinished, String status , int token)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(ctx);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "createOrUpdateInDatabase getDao Exception : "+e.toString());
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String currentDateTime = dateFormat.format(cal.getTime());
        DownloadEntity downloadEntity = new DownloadEntity(fileName,fileURL,fileSaveAddress,currentDateTime,isFinished,downloadedSize,remainingTime,speed,percent,status,token);
        try {
            List<DownloadEntity> existing = downloadsDao.queryForEq(DownloadEntity.FIELD_NAME_URL,fileURL);
            if (existing.size() > 0){
                int id = existing.get(0).getId();
                downloadEntity.setId(id);
                downloadsDao.update(downloadEntity);
            }else {
                downloadsDao.create(downloadEntity);
            }
        } catch (Exception e) {
            Log.e(TAG, "createOrUpdateInDatabase downloadsDao.create() or .update() Exception : "+e.toString() );
            return false;
        }
        return true;
    }
    public static boolean createOrUpdateInDatabase(DownloadEntity entity , Context ctx)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(ctx);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "createOrUpdateInDatabase getDao Exception : "+e.toString());
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String currentDateTime = dateFormat.format(cal.getTime());
        DownloadEntity downloadEntity = entity;
        try {
            List<DownloadEntity> existing = downloadsDao.queryForEq(DownloadEntity.FIELD_NAME_URL,entity.getURL());
            if (existing.size() > 0){
                int id = existing.get(0).getId();
                downloadEntity.setId(id);
                downloadsDao.update(downloadEntity);
            }else {
                downloadsDao.create(downloadEntity);
            }
        } catch (Exception e) {
            Log.e(TAG, "createOrUpdateInDatabase downloadsDao.create() or .update() Exception : "+e.toString() );
            return false;
        }
        return true;
    }


    public static boolean deleteDownloadsFromDatabase(Context context , Collection<DownloadEntity> downloadEntities)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase getDao Exception : "+e.toString());
            return false;
        }
        try {
            downloadsDao.delete(downloadEntities);
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase Dao.delete Exception : "+e.toString());
            return false;
        }
        return true;
    }

    public static boolean deleteDownloadsWithIDsFromDatabase(Context context , Collection<Integer> IDs)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase getDao Exception : "+e.toString());
            return false;
        }
        try {
            downloadsDao.deleteIds(IDs);
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase Dao.delete Exception : "+e.toString());
            return false;
        }
        return true;
    }

    public static boolean deleteDownloadByURL(Context context , String URL)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase getDao Exception : "+e.toString());
            return false;
        }
        try {
            DeleteBuilder<DownloadEntity, Integer> deleteBuilder = downloadsDao.deleteBuilder();
            deleteBuilder.where().eq(DownloadEntity.FIELD_NAME_URL,URL);
            deleteBuilder.delete();
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase Exception : "+e.toString());
            return false;
        }
        return true;
    }

    public static List<DownloadEntity> getAllDownloadsFromDatabase(Context context)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase getDao Exception : "+e.toString());
        }
        List<DownloadEntity> allDownloads = null;
        try {
            allDownloads = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase Query Exception : "+e.toString());
        }
        return allDownloads;
    }

    public static boolean setAllNotFinishedDownloadsStatus(Context context, String status) {

        String TAG = "Weber Tag";
        try {
            List<DownloadEntity> downloads = getNotFinishedDownloads(context);
            for (int i = 0; i < downloads.size(); i++) {
                DownloadEntity curr = downloads.get(i);
                //curr.setStatus(status);
                createOrUpdateInDatabase(context,curr.getFileName(),curr.getURL(),curr.getDownloadedSize(),curr.getRemainingTime()
                ,curr.getPath(),curr.getSpeed(),curr.getPercent(),curr.isFinished(), status ,curr.getToken());
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteDownloadSFromDatabase Dao.delete Exception : "+e.toString());
            return false;
        }
        return true;
    }

    public static DownloadEntity getDownloadByURL(Context context , String URL)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL getDao Exception : "+e.toString());
        }
        List<DownloadEntity> downloadsList = null;
        try {
            downloadsList = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_URL,URL).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL Query Exception : "+e.toString());
        }
        return downloadsList.get(0);
    }

    public static List <DownloadEntity> getDownloadsListByURL(Context context , String URL)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL getDao Exception : "+e.toString());
        }
        List<DownloadEntity> downloadsList = null;
        try {
            downloadsList = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_URL,URL).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL Query Exception : "+e.toString());
        }
        return downloadsList;
    }

    public static List<DownloadEntity> getDownloadsByFileName(Context context , String fileName)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL getDao Exception : "+e.toString());
        }
        List<DownloadEntity> downloadsList = null;
        try {
            downloadsList = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_NAME,fileName).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsByURL Query Exception : "+e.toString());
        }
        return downloadsList;
    }

    public static List<DownloadEntity> getNotFinishedDownloads(Context context)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase getDao Exception : "+e.toString());
        }
        List<DownloadEntity> notFinishedDownloads = null;
        try {
            notFinishedDownloads = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_FINISHED,false).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase Query Exception : "+e.toString());
        }
        return notFinishedDownloads;
    }

    public static List<DownloadEntity> getFinishedDownloads(Context context){
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase getDao Exception : "+e.toString());
        }
        List<DownloadEntity> finishedDownloads = null;
        try {
            finishedDownloads = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_FINISHED,true).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase Query Exception : "+e.toString());
        }
        return finishedDownloads;
    }

    public static List<DownloadEntity> getDownloadsByStatus(Context context,String status)
    {
        String TAG = "Weber Tag";
        DownloadDatabaseHelper helper  = new DownloadDatabaseHelper(context);
        Dao<DownloadEntity, Integer> downloadsDao = null;
        try {
            downloadsDao = helper.getUserDao();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase getDao Exception : "+e.toString());
        }
        List<DownloadEntity> withStatusDownloads = null;
        try {
            withStatusDownloads = downloadsDao.queryBuilder().distinct().orderBy(DownloadEntity.FIELD_NAME_ID,true).where().eq(DownloadEntity.FIELD_NAME_STATUS,status).query();
        } catch (Exception e) {
            Log.e(TAG, "getDownloadsFromDatabase Query Exception : "+e.toString());
        }
        return withStatusDownloads;
    }

}
