package com.github.rezalotfi01.weberpro.Utils

import com.github.rezalotfi01.weberpro.Database.Record

object RecordUtils {
    val TABLE_BOOKMARKS = "BOOKMARKS"
    val TABLE_HISTORY = "HISTORY"
    val TABLE_WHITELIST = "WHITELIST"
    val TABLE_GRID = "GRID"
    val TABLE_DOWNLOADS = "DOWNLOADS"

    val COLUMN_TITLE = "TITLE"
    val COLUMN_URL = "URL"
    val COLUMN_TIME = "TIME"
    val COLUMN_DOMAIN = "DOMAIN"
    val COLUMN_FILENAME = "FILENAME"
    val COLUMN_ORDINAL = "ORDINAL"
    val COLUMN_PATH = "PATH"


    val CREATE_HISTORY = ("CREATE TABLE "
            + TABLE_HISTORY
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")")

    val CREATE_BOOKMARKS = ("CREATE TABLE "
            + TABLE_BOOKMARKS
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")")

    val CREATE_WHITELIST = ("CREATE TABLE "
            + TABLE_WHITELIST
            + " ("
            + " " + COLUMN_DOMAIN + " text"
            + ")")

    val CREATE_GRID = ("CREATE TABLE "
            + TABLE_GRID
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_FILENAME + " text,"
            + " " + COLUMN_ORDINAL + " integer"
            + ")")
    val CREATE_DOWNLOADS = ("CREATE TABLE "
            + TABLE_DOWNLOADS
            + " ("
            + " " + COLUMN_TITLE + " text,"
            + " " + COLUMN_URL + " text,"
            + " " + COLUMN_FILENAME + " text,"
            + " " + COLUMN_PATH + " text,"
            + " " + COLUMN_TIME + " integer"
            + ")")

    @set:Synchronized
    var holder: Record? = null
}
