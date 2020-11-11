package com.example.justforlocks.ui.auth

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.example.justforlocks.R
import com.example.justforlocks.databinding.EmailLayoutBinding
import com.example.justforlocks.databinding.FragmentUserinfoBinding
import com.example.justforlocks.databinding.NameLayoutBinding
import com.example.justforlocks.databinding.ProfilepicLayoutBinding
import com.example.justforlocks.ui.auth.listeners.userInfoUpdationListener
import com.example.justforlocks.ui.auth.viewmodel.AuthViewModel
import com.example.justforlocks.ui.home.homeActivity
import com.example.justforlocks.utils.showDialog
import com.example.justforlocks.utils.toast
import com.squareup.picasso.Picasso
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class userinfoFragment : Fragment() , KodeinAware,userInfoUpdationListener {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    var viewGroup:ViewGroup?=null
    var nameScene: Scene?=null
    var emailScene:Scene?=null
    var pictureScene:Scene?=null
    var viewModal: AuthViewModel?=null
    var binding: FragmentUserinfoBinding?=null
    var filePath: Uri?=null
    var pd: Dialog?=null
    private val PICK_IMAGE_REQUEST = 71
    var imagebutton:ImageButton?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_userinfo,
            container,
            false
        )
        viewModal = ViewModelProvider(requireActivity(), factory).get(AuthViewModel::class.java)
        binding?.viewmodel= viewModal
viewModal?.userInfoUpdationListener=this
        pd=context?.showDialog("Please wail we are uploading your data...")
        viewGroup=binding?.rootcontainer as ViewGroup
        nameScene= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.name_layout,
            requireContext()
        )
        TransitionManager.go(nameScene!!)
        val bind1:NameLayoutBinding = NameLayoutBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal

        return binding?.root
    }











    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.getData() != null )
        {
            Picasso.get().load(data.data).fit().into(imagebutton!!);
            viewModal?.uriImage?.value=data.data
        }

    }
    override fun onStart() {
        super.onStart()

    }

    override fun onUpdating() {
        pd?.show()
    }

    override fun onNameUpdated() {
        pd?.dismiss()
        emailScene= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.email_layout,
            requireContext()
        )
        emailScene?.enter()
        val bind1:EmailLayoutBinding = EmailLayoutBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal
    }

    override fun onEmailUpdated() {
        pd?.dismiss()

        pictureScene= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.profilepic_layout,
            requireContext()
        )
        pictureScene?.enter()
        val bind1:ProfilepicLayoutBinding = ProfilepicLayoutBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal
        imagebutton=bind1.imagebutton
        bind1.imagebutton.setOnClickListener(View.OnClickListener {
            val intent=Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        })
    }

    override fun onPictureUpdated() {
        pd?.dismiss()
        Intent(requireContext(), homeActivity::class.java).also {
            it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailed(message: String) {
        pd?.dismiss()
        requireContext().toast(message)
    }


}