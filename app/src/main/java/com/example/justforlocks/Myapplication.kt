package com.example.justforlocks

import android.app.Application
import com.example.justforlocks.repo.loginRepo
import com.example.justforlocks.ui.auth.AuthViewModelFactory
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel
import com.example.justforlocks.ui.home.HomeViewModel
import com.example.justforlocks.ui.home.HomeViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Myapplication : Application(), KodeinAware {
    override val kodein= Kodein.lazy {
        import(androidXModule(this@Myapplication))
        bind() from singleton { AuthViewModel(instance()) }
        bind() from singleton { HomeViewModel(instance()) }
        bind() from singleton { loginRepo() }
        bind() from provider{ AuthViewModelFactory(instance()) }
        bind() from provider{ HomeViewModelFactory(instance()) }
    }
}