package com.example.justforlocks.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.justforlocks.repo.loginRepo
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory ( val repo: loginRepo): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}