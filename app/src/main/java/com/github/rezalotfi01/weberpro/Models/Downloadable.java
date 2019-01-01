package com.github.rezalotfi01.weberpro.Models;

/**
 * Created  on 10/23/2016.
 */
public class Downloadable {
    private String fileURL;
    private String fileName;
    private String fileSavedAddress;

    private String speed;
    private String remainingTime;
    private String downloadedSize;
    private int imgStatusResource;
    private double percent;
    private int token;

    public Downloadable() {
    }

    public Downloadable(String fileName, String speed, String remainingTime, String downloadedSize, double percent, int imgStatusResource,int token , String URL , String savedAddress) {
        this.fileName = fileName;
        this.speed = speed;
        this.remainingTime = remainingTime;
        this.downloadedSize = downloadedSize;
        this.percent = percent;
        this.imgStatusResource = imgStatusResource;
        this.token = token;
        this.fileSavedAddress = savedAddress;
        this.fileURL = URL;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(String downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSavedAddress(String fileSavedAddress) {
        this.fileSavedAddress = fileSavedAddress;
    }

    public String getFileURL() {
        return fileURL;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSavedAddress() {
        return fileSavedAddress;
    }

    public int getImgStatusResource() {
        return imgStatusResource;
    }

    public void setImgStatusResource(int imgStatusResource) {
        this.imgStatusResource = imgStatusResource;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
