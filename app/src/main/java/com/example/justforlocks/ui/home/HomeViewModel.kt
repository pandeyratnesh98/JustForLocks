package com.example.justforlocks.ui.home

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justforlocks.model.user
import com.example.justforlocks.repo.loginRepo
import com.example.justforlocks.ui.splashActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel(val repo: loginRepo) : ViewModel() {
    var welcomemessage= MutableLiveData<String>()
    var dataLoading:dataLoading?=null
    val userProfile = MutableLiveData<user>()

  fun  init() {
        dataLoading?.onStarted()
        viewModelScope.launch {
           repo.getProfileData(userProfile,dataLoading!!)

        }
    }
fun logout(view:View){
    FirebaseAuth.getInstance().signOut()
    Intent(view.context, splashActivity::class.java).also {
        it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
        view.context.startActivity(it)
    }
}
}
