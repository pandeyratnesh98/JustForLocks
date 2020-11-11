package com.example.justforlocks.ui.auth

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.justforlocks.R
import com.example.justforlocks.databinding.FragmentLoginBinding
import com.example.justforlocks.ui.auth.listeners.authListener
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel
import com.example.justforlocks.utils.showDialog
import com.example.justforlocks.utils.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class loginFragment : Fragment() , KodeinAware, authListener,TextWatcher {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    var pd : Dialog? = null
    var  binding: FragmentLoginBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val  viewModal= ViewModelProvider(requireActivity(), factory).get(AuthViewModel::class.java)
        binding?.viewmodel=viewModal
        viewModal.authListener = this
        pd = context?.showDialog("please wait")
binding?.phonenumber?.addTextChangedListener(this)
        val liveData : LiveData<String> = viewModal._otpSend

        liveData.observe(viewLifecycleOwner, Observer {
            view?.findNavController()?.navigate(R.id.otpFragment)
        })



        return binding?.root
    }

    override fun onAuthStart() {
        pd?.show()
    }

    override fun onCodeSent() {
        pd?.dismiss()
        requireContext().toast("code sent")

    }

    override fun onSuccess() {
        pd?.dismiss()
        requireContext().toast("Verification Syccessfull")
    }

    override fun onFailure(message : String) {
        pd?.dismiss()
        requireContext().toast(message)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    override fun afterTextChanged(s: Editable?) {
        if (s?.length==10){
            binding?.loginbutton?.visibility=View.VISIBLE

        }else{
            binding?.loginbutton?.visibility=View.GONE
        }
    }


}