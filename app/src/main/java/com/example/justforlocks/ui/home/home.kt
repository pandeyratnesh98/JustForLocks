package com.example.justforlocks.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.justforlocks.R
import com.example.justforlocks.databinding.HomeFragmentBinding
import com.example.justforlocks.model.user
import com.example.justforlocks.ui.auth.AuthViewModelFactory
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel
import com.example.justforlocks.utils.showDialog
import com.example.justforlocks.utils.toast
import com.squareup.okhttp.internal.Internal.instance
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class home : Fragment()  , KodeinAware,dataLoading {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: HomeViewModelFactory by instance()
var pd:Dialog?=null
var binding:HomeFragmentBinding?=null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        val  viewModal= ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)
        binding?.viewmodel=viewModal
        pd = context?.showDialog("please wait")
        val liveData : LiveData<user> = viewModal.userProfile
        viewModal.dataLoading=this
        viewModal.init()
        liveData.observe(viewLifecycleOwner, Observer {
            Log.d("data",it.toString())
           if (!it.name.isEmpty()){
             binding?.welcomemessage?.text="Welcome to Home Activity ${it.name}"
           }else{
               requireContext().toast("data not found")
           }
        })



        return binding?.root
    }

    override fun onStarted() {
    pd?.show()
    }

    override fun onFinished() {
        pd?.dismiss()
    }

    override fun onFailed(message: String) {
        pd?.dismiss()
        requireContext().toast(message)
    }


}