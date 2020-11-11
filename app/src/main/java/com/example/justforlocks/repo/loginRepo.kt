package com.example.justforlocks.repo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.justforlocks.model.user
import com.example.justforlocks.ui.auth.listeners.authListener
import com.example.justforlocks.ui.auth.listeners.userInfoUpdationListener
import com.example.justforlocks.ui.home.dataLoading
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class loginRepo() {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val FIRESTOREREF = FirebaseFirestore.getInstance().collection("users")
    val FIRESTORAGEREF = FirebaseStorage.getInstance().getReference().child("users")
    suspend fun isUserRegistered(
        userData: MutableLiveData<Int>,
    listener:authListener
    ){
        var mode: Int = 0
        val job =  GlobalScope.launch {
            FIRESTOREREF.document(uid.toString())
                .get().addOnSuccessListener { document ->
                    if (document.exists()&&document.contains("name")
                        &&document.contains("email")&&document.contains("imageurl")) {
                        mode = 1
                        userData.value = mode;
                    } else {
                        mode = 2;
                        userData.value = mode;
                    }
                }.addOnFailureListener { exception ->
                  listener.onFailure(exception.message.toString())
                }

        }
        job.join()

    }
    fun saveName(name:String,userInfoUpdationListener: userInfoUpdationListener){
        val data :MutableMap<String, Any> = HashMap()
        data["name"] = name
        FIRESTOREREF.document(uid.toString()).set(data, SetOptions.merge())
            .addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful){
                    userInfoUpdationListener.onNameUpdated()
                }else{
                    userInfoUpdationListener.onFailed(it.exception?.message.toString())
                }
            }).addOnFailureListener(OnFailureListener {
                userInfoUpdationListener.onFailed(it.message.toString())
            })
    }
    fun saveEmail(email:String,userInfoUpdationListener: userInfoUpdationListener){
        val data :MutableMap<String, Any> = HashMap()
        data["email"] = email
        FIRESTOREREF.document(uid.toString()).set(data, SetOptions.merge())
            .addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful){
                    userInfoUpdationListener.onEmailUpdated()
                }else{
                    userInfoUpdationListener.onFailed(it.exception?.message.toString())
                }
            }).addOnFailureListener(OnFailureListener {
                userInfoUpdationListener.onFailed(it.message.toString())
            })
    }
    fun savePicture(picture: Uri, userInfoUpdationListener: userInfoUpdationListener){
        val ImageRef = FIRESTORAGEREF.child("prof_pic").child(uid.toString()).child(
            picture.getLastPathSegment().toString())
        ImageRef.putFile(picture).addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                ImageRef.downloadUrl.addOnSuccessListener {
                    val image: MutableMap<String, Any> = HashMap()
                    image["imageurl"] = it.toString()
                    FIRESTOREREF.document(uid.toString()).set(image, SetOptions.merge()).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful){
                                userInfoUpdationListener.onPictureUpdated()
                            }else{
                                userInfoUpdationListener.onFailed(it.exception
                                    ?.message.toString())
                            }
                        })
                }
            } else {
                userInfoUpdationListener.onFailed(it.exception?.message.toString())
            }
        })
    }
    suspend fun getProfileData( user1:MutableLiveData<user>,dataLoading: dataLoading) {
        Log.d("data","start")
        val job =  GlobalScope.launch {
           FIRESTOREREF.document(uid.toString()).get().addOnCompleteListener(OnCompleteListener {
             if (it.isSuccessful){
                 dataLoading.onFinished()
                 Log.d("data","midale data")
                 user1.value=it.result?.toObject(user::class.java)
             }
           }).addOnFailureListener(OnFailureListener {
               Log.d("data","error")
               dataLoading.onFailed(it.message.toString())
           })
        }
    job.join()

}}


