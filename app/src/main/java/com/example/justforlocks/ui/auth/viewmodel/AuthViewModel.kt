package com.example.justforlocks.ui.auth.viewmodel

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justforlocks.repo.loginRepo
import com.example.justforlocks.ui.auth.listeners.authListener
import com.example.justforlocks.ui.auth.listeners.userInfoUpdationListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.tripplleat.trippleattcustomer.ui.auth.listeners.Otplistener
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel(private val repo: loginRepo):ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val userData = MutableLiveData<Int>()
    var _otpSend = MutableLiveData<String>()
    var uriImage = MutableLiveData<Uri>()
    var countdown = MutableLiveData<Long>()
    var phone = MutableLiveData<String>()
    var _token = MutableLiveData<PhoneAuthProvider.ForceResendingToken>()
    var contdownfinish = MutableLiveData<Boolean>()
    var phonenumber:String?=null
    var otp1:String?=null
    var otp2:String?=null
    var otp3:String?=null
    var otp4:String?=null
    var otp5:String?=null
    var otp6:String?=null
    var name:String?=null
    var email:String?=null
    var authListener : authListener? = null
    var userInfoUpdationListener : userInfoUpdationListener? = null
    var otplistener : Otplistener? = null


    public  fun sendVerifiactionCode(view: View){
        authListener?.onAuthStart()
        if (phonenumber.isNullOrEmpty()||phonenumber?.length!!<10){
            authListener?.onFailure("Invalid Phone Number")
            return
        }
        phone.value=phonenumber
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91$phonenumber",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val job = viewModelScope.launch {
                        repo.isUserRegistered(
                            userData,authListener!!
                        )
                        Log.i("databaseModel", userData.value.toString())
                        authListener?.onSuccess()
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    authListener?.onFailure(p0.toString())
                    contdownfinish.value=true
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    authListener?.onCodeSent()
                    _otpSend.value = p0
                    _token.value = p1
                    otplistener?.onOtpcountstarted()
                    contdownfinish.value=false
                    val timer = object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val sec = millisUntilFinished / 1000
                            countdown.value = sec
                        }

                        override fun onFinish() {
                            otplistener?.onOTPtimeout()
                            contdownfinish.value=true
                        }
                    }
                    timer.start()

                }
            })
    }

    fun verifyOtpClicked(view: View){
        if (otp1.isNullOrEmpty()||otp2.isNullOrEmpty()||otp3.isNullOrEmpty()||otp4.isNullOrEmpty()||otp5.isNullOrEmpty()||otp6.isNullOrEmpty()){
            authListener?.onFailure("Invalid OTP")
            return
        }
        val code = _otpSend.value.toString()
        Log.i("code", code)
        val credential = PhoneAuthProvider.getCredential(
            code,
            otp1 + otp2 + otp3 + otp4 + otp5 + otp6
        )
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                viewModelScope.launch {
                    repo.isUserRegistered(
                        userData,authListener!!
                    )
                }
                authListener?.onSuccess()
            }
            else{
                authListener?.onFailure("Verification failed")
                //Toast.makeText(application,"Otp verification failed ${it.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun resendCode(view: View){
        if (!contdownfinish.value!!){
            return
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91${phone.value}",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val job = viewModelScope.launch {
                        repo.isUserRegistered(
                            userData,authListener!!
                        )
                        Log.i("databaseModel", userData.value.toString())
                        authListener?.onSuccess()
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    authListener?.onFailure(p0.toString())
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    authListener?.onCodeSent()
                    _otpSend.value = p0
                    _token.value = p1
                    otplistener?.onOtpcountstarted()
                    contdownfinish.value=false
                    val timer = object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val sec = millisUntilFinished / 1000
                            countdown.value = sec
                        }

                        override fun onFinish() {
                            otplistener?.onOTPtimeout()
                            contdownfinish.value=true
                        }
                    }
                    timer.start()

                }
            },_token.value)
    }

    fun saveName(view: View){
        userInfoUpdationListener?.onUpdating()
        if (name.isNullOrEmpty()){
            userInfoUpdationListener?.onFailed("Enter your name")
            return
        }
        repo.saveName(name!!,userInfoUpdationListener!!)
    }
    fun saveEmail(view: View){
        userInfoUpdationListener?.onUpdating()
        if (email.isNullOrEmpty()|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userInfoUpdationListener?.onFailed("Invalid email")
            return
        }
        repo.saveEmail(email!!,userInfoUpdationListener!!)
    }
    fun savePicture(view: View){
        userInfoUpdationListener?.onUpdating()
        if (uriImage.value==null){
            userInfoUpdationListener?.onFailed("Upload your picture")
            return
        }
        viewModelScope.launch {
            repo.savePicture(uriImage.value!!,userInfoUpdationListener!!)
        }

    }


}