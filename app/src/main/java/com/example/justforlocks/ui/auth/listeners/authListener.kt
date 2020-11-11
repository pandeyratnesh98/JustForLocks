package com.example.justforlocks.ui.auth.listeners

interface authListener {
    fun onAuthStart()
    fun onCodeSent()
    fun onSuccess()
    fun onFailure(message : String)
}