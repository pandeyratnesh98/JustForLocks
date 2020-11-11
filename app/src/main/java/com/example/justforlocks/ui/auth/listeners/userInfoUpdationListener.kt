package com.example.justforlocks.ui.auth.listeners

interface userInfoUpdationListener {
    fun onUpdating()
    fun onNameUpdated()
    fun onEmailUpdated()
    fun onPictureUpdated()
    fun onFailed(message:String)
}