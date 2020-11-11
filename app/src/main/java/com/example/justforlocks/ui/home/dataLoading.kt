package com.example.justforlocks.ui.home

interface dataLoading {
    fun onStarted()
    fun onFinished()
    fun onFailed(message:String)
}