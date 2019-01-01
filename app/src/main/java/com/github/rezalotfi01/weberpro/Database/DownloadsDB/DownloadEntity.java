package com.github.rezalotfi01.weberpro.Database.DownloadsDB;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created  on 11/02/2016.
 */

@DatabaseTable(tableName = "downloads")
public class DownloadEntity {
    public static final String FIELD_NAME_ID     = "id";
    public static final String FIELD_NAME_NAME   = "name";
    public static final String FIELD_NAME_URL   = "url";
    private static final String FIELD_NAME_PATH = "path";
    private static final String FIELD_NAME_TIME = "time";
    public static final String FIELD_NAME_FINISHED = "finished";
    private static final String FIELD_NAME_DOWNLOADED_SIZE = "downloaded_size";
    private static final String FIELD_NAME_REMAINING_TIME = "remaining_time";
    private static final String FIELD_NAME_SPEED = "speed";
    private static final String FIELD_NAME_PERCENT = "percent";
    public static final String FIELD_NAME_STATUS = "status";
    private static final String FIELD_NAME_TOKEN = "token";

    public static final String FIELD_VALUE_STATUS_PAUSED = "paused";
    public static final String FIELD_VALUE_STATUS_IN_QUEUE = "queue";
    public static final String FIELD_VALUE_STATUS_RESUMING = "resuming";
    public static final String FIELD_VALUE_STATUS_ERROR = "error";
    public static final String FIELD_VALUE_STATUS_COMPLETED = "completed";
    public DownloadEntity() {
    }

   /* public DownloadEntity(int id, String fileName, String URL, String path, String time) {
        this.id = id;
        this.fileName = fileName;
        this.URL = URL;
        this.path = path;
        this.time = time;
    }

    public DownloadEntity(String fileName, String URL, String path, String time, boolean isFinished) {
        this.fileName = fileName;
        this.URL = URL;
        this.path = path;
        this.time = time;
        this.isFinished = isFinished;
    }

    */

    public DownloadEntity(String fileName, String URL, String path, String time, boolean isFinished, String downloadedSize, String remainingTime, String speed, double percent,String status,int token) {
        this.fileName = fileName;
        this.URL = URL;
        this.path = path;
        this.time = time;
        this.isFinished = isFinished;
        this.downloadedSize = downloadedSize;
        this.remainingTime = remainingTime;
        this.speed = speed;
        this.percent = percent;
        this.status = status;
        this.token = token;
    }

    @DatabaseField(generatedId = true , columnName = FIELD_NAME_ID)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String fileName;

    @DatabaseField(columnName = FIELD_NAME_URL)
    private String URL;

    @DatabaseField(columnName = FIELD_NAME_PATH)
    private String path;

    @DatabaseField(columnName = FIELD_NAME_TIME)
    private String time;

    @DatabaseField(columnName = FIELD_NAME_FINISHED)
    private boolean isFinished;

    @DatabaseField(columnName = FIELD_NAME_DOWNLOADED_SIZE)
    private String downloadedSize;

    @DatabaseField(columnName = FIELD_NAME_REMAINING_TIME)
    private String remainingTime;

    @DatabaseField(columnName = FIELD_NAME_SPEED)
    private String speed;

    @DatabaseField(columnName = FIELD_NAME_PERCENT)
    private double percent;

    @DatabaseField(columnName = FIELD_NAME_STATUS)
    private String status;

    @DatabaseField(columnName = FIELD_NAME_TOKEN)
    private int token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(String downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
