package com.example.justforlocks.ui.auth

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.justforlocks.R
import com.example.justforlocks.databinding.FragmentOtpBinding
import com.example.justforlocks.ui.auth.listeners.authListener
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel
import com.example.justforlocks.ui.home.homeActivity
import com.example.justforlocks.utils.showDialog
import com.example.justforlocks.utils.toast
import com.tripplleat.trippleattcustomer.ui.auth.listeners.Otplistener
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class otpFragment : Fragment() , authListener, KodeinAware, TextWatcher, Otplistener {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    var pd : Dialog? = null
    var otp1: EditText?=null
    var otp2: EditText?=null
    var otp3: EditText?=null
    var otp4: EditText?=null
    var otp5: EditText?=null
    var otp6: EditText?=null
    var counter: TextView?=null
    var resendcode: TextView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentOtpBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_otp,container,false)
        val viewModal = ViewModelProvider(requireActivity(),factory).get(AuthViewModel::class.java)
        binding.viewmodel=viewModal
        viewModal.authListener = this
        viewModal.otplistener=this
        otp1=binding.otp1
        otp2=binding.otp2
        otp3=binding.otp3
        otp4=binding.otp4
        otp5=binding.otp5
        otp6=binding.otp6
        counter=binding.countdown
        resendcode=binding.resendCode
        otp1?.addTextChangedListener(this)
        otp2?.addTextChangedListener(this)
        otp3?.addTextChangedListener(this)
        otp4?.addTextChangedListener(this)
        otp5?.addTextChangedListener(this)
        otp6?.addTextChangedListener(this)
        pd = context?.showDialog("please wait")

        val liveData : LiveData<Int> = viewModal.userData
        val otpcountdown : LiveData<Long> = viewModal.countdown
        liveData.observe(viewLifecycleOwner, Observer {mode ->
            if(mode == 2)
                view?.findNavController()?.navigate(R.id.userinfoFragment)
            else{
                requireContext().toast("user existed");
               Intent(context, homeActivity::class.java).also {
                    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }

        })
        otpcountdown.observe(viewLifecycleOwner, Observer {
            binding.countdown.text=it?.toString()
        })

        return binding.root
    }

    override fun onAuthStart() {
        pd?.show()
    }
    override fun onCodeSent() {
        pd?.dismiss()
        requireContext().toast("code sent");
    }

    override fun onSuccess() {
        pd?.dismiss()
        requireContext().toast("Verification Syccessfull");
    }

    override fun onFailure(message : String) {
        pd?.dismiss()
        requireContext().toast(message);

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        if (p0?.length==1){
            if (otp1?.length() == 1) {
                otp2?.requestFocus()

            }
            if (otp2?.length() == 1) {
                otp3?.requestFocus()

            }
            if (otp3?.length() == 1) {
                otp4?.requestFocus()

            }
            if (otp4?.length() == 1) {
                otp5?.requestFocus()

            }
            if (otp5?.length() == 1) {
                otp6?.requestFocus()


            }
        }else if (p0?.length == 0) {
//            if user tries to delete numbers
            if (otp6?.length() == 0) {
                otp5?.requestFocus()
            }
            if (otp5?.length() == 0) {
                otp4?.requestFocus()

            }
            if (otp4?.length() == 0) {
                otp3?.requestFocus()
            }
            if (otp3?.length() == 0) {
                otp2?.requestFocus()
            }
            if (otp2?.length() == 0) {
                otp1?.requestFocus()
            }
        }
        if (otp1?.length()==0){
            otp1?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp1?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp2?.length()==0){
            otp2?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp2?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp3?.length()==0){
            otp3?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp3?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp4?.length()==0){
            otp4?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp4?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp5?.length()==0){
            otp5?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp5?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp6?.length()==0){
            otp6?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp6?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
    }

    override fun onOtpcountstarted() {
        resendcode?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        resendcode?.isClickable=false
    }

    override fun onOTPtimeout() {
      try {
          counter?.text=""
          resendcode?.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
          resendcode?.isClickable=true
      }catch (e:Exception){
          e.printStackTrace()
      }
    }

}