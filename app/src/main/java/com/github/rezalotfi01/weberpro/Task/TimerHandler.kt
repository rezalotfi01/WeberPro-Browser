package com.github.rezalotfi01.weberpro.Task

/**
 * Created  on 11/08/2016.
 */
class TimerHandler {
    private var start: Long = 0
    private var end: Long = 0
    private var result: Long = 0

    init {
        start = 0
        end = 0
        result = -1
    }

    fun start() {
        start = System.currentTimeMillis()
    }

    fun endAndGetElapsedTime(): Long {
        end = System.currentTimeMillis()
        result = end - start

        return result / 1000
    }
}