package com.example.justforlocks.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.justforlocks.R
import com.example.justforlocks.databinding.FragmentProfileBinding
import com.example.justforlocks.databinding.HomeFragmentBinding
import com.example.justforlocks.model.user
import com.example.justforlocks.utils.showDialog
import com.example.justforlocks.utils.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.name_layout.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class Profile : Fragment() , KodeinAware,dataLoading {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: HomeViewModelFactory by instance()
    var pd: Dialog?=null
    var binding: FragmentProfileBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val  viewModal= ViewModelProvider(requireActivity(), factory).get(HomeViewModel::class.java)
        binding?.viewmodel=viewModal
        viewModal.dataLoading=this
        pd = context?.showDialog("please wait")
        viewModal.init()
        val liveData : LiveData<user> = viewModal.userProfile

        liveData.observe(viewLifecycleOwner, Observer {
           binding?.name?.text=it.name
            binding?.email?.text=it.email
            Picasso.get().load(it.imageurl).into(binding?.profpic)
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