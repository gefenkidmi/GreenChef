package com.example.greenchef.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.greenchef.DataClass.User
import com.example.greenchef.DialogFragments.EditDisplayNameDialogFragment
import com.example.greenchef.DialogFragments.EditProfileImageDialogFragment
import com.example.greenchef.Global.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel
import com.example.greenchef.ViewModels.UserViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment(),
    EditDisplayNameDialogFragment.EditUsernameDialogListener,
    EditProfileImageDialogFragment.EditProfileImageDialogListener {

    private var param1: String? = null
    private var param2: String? = null
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        GlobalVariables.currentUser?.let { user ->
            updateUI(user, view)
        }

        authViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                val emailTextView: TextView = view.findViewById(R.id.emailTextView)
                emailTextView.text = user.email
                // Initialize or update UserViewModel when the current user is available
                userViewModel = UserViewModel(user.uid)
                userViewModel.userLiveData.observe(viewLifecycleOwner, Observer { userData ->
                    userData?.let {
                        if (userData != GlobalVariables.currentUser){
                            GlobalVariables.currentUser = userData
                            updateUI(userData)
                        }
                    }
                })
            }
        })
        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

        displayNameTextView.setOnClickListener {
            showEditUsernameDialog()
        }
        userPhotoImageView.setOnClickListener {
            showEditProfileImageDialog()
        }

        return view
    }

    private fun showEditUsernameDialog() {
        val dialogFragment = EditDisplayNameDialogFragment()
        dialogFragment.show(childFragmentManager, "EditUsernameDialogFragment")
    }

    private fun showEditProfileImageDialog() {
        val dialogFragment = EditProfileImageDialogFragment()
        dialogFragment.show(childFragmentManager, "EditProfileImageDialogFragment")
    }

    override fun onDisplayNameUpdated(displayName: String) {
        userViewModel.updateUserName(displayName)
        Log.d("NameUpdate", "Updated display name")
    }

    override fun onImageUpdated(imageUri: String) {
        //authViewModel.updateUserPhoto(imageUri.toUri())

        // Update the photo URI in SharedPreferences
        sharedPreferences.edit {
            putString("photoUrl", imageUri)
        }

        val userPhotoImageView: ImageView? = view?.findViewById(R.id.userPhotoImageView)
        userPhotoImageView?.setImageURI(imageUri.toUri())
        Log.d("ImageUpdate", "Updated profile image")
    }

    private fun updateUI(userData: User, thisView: View? = view) {
        val displayNameTextView: TextView? = thisView?.findViewById(R.id.displayNameTextView)
        val emailTextView: TextView? = thisView?.findViewById(R.id.emailTextView)
        val userPhotoImageView: ImageView? = thisView?.findViewById(R.id.userPhotoImageView)

        displayNameTextView?.text = userData.name
        emailTextView?.text = authViewModel.currentUser.value?.email
        userData.photoUrl?.let { url ->
            if(url != "") {
                userPhotoImageView?.setImageURI(url.toUri())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}