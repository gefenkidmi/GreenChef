package com.example.greenchef.Fragments

import GalleryHandler
import android.app.Activity
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.RecipeAdapter
import com.example.greenchef.DataClass.User
import com.example.greenchef.DialogFragments.EditDisplayNameDialogFragment
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UserViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(),
    EditDisplayNameDialogFragment.EditUsernameDialogListener {

    private lateinit var recipeRecyclerView: RecyclerView

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, null)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    userViewModel.updateUserPhoto(selectedImageUri)
                }
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

        recipeRecyclerView = view.findViewById(R.id.myRecipesRecyclerView)
        recipeViewModel.setContextAndDB(requireContext())

        observeUser(view)
        setClickListeners(view)
        initRecipeRecyclerView()

        return view
    }

    private fun setClickListeners(view: View) {
        val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
        val userPhotoImageView: ImageView = view.findViewById(R.id.userPhotoImageView)

        displayNameTextView.setOnClickListener {
            showEditUsernameDialog()
        }
        userPhotoImageView.setOnClickListener {
            GalleryHandler.getPhotoUriFromGallery(
                requireActivity(),
                pickImageLauncher,
                requestPermissionLauncher
            )
        }
    }

    private fun observeUser(view: View) {
        val emailTextView: TextView? = view.findViewById(R.id.emailTextView)

        userViewModel.userLiveData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                if (userData != GlobalVariables.currentUser) {
                    GlobalVariables.currentUser = userData
                }
                updateUI(userData)
            }
        }

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                emailTextView?.text = user.email
            }
        }
    }

    private fun showEditUsernameDialog() {
        val dialogFragment = EditDisplayNameDialogFragment()
        dialogFragment.show(childFragmentManager, "EditUsernameDialogFragment")
    }

    override fun onDisplayNameUpdated(displayName: String) {
        userViewModel.updateUserName(displayName)
        Log.d("NameUpdate", "Updated display name")
    }

    private fun updateUI(userData: User, thisView: View? = view) {
        val displayNameTextView: TextView? = thisView?.findViewById(R.id.displayNameTextView)
        val userPhotoImageView: ImageView? = thisView?.findViewById(R.id.userPhotoImageView)

        displayNameTextView?.text = userData.name

        // Load user photo using Picasso if available
        userData.photoUrl.takeIf { it.isNotEmpty() }?.let { url ->
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.baseline_add_photo_alternate_24) // Error image if loading fails
                .into(userPhotoImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        userPhotoImageView?.scaleType = ImageView.ScaleType.CENTER_CROP
                    }

                    override fun onError(e: Exception?) {
                        // Set your visibility to VISIBLE
                    }
                })
        } ?: run {
            // Load default placeholder image if user photo is not available
            userPhotoImageView?.setImageResource(R.drawable.baseline_person_24)
        }
    }

    private fun initRecipeRecyclerView() {
        recipeViewModel.getAllRecipes(lifecycleScope).observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNotEmpty()) {
                val filteredRecipes =
                    recipes.filter { it.ownerId == GlobalVariables.currentUser?.userId }
                val adapter = RecipeAdapter(filteredRecipes, this)
                recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                recipeRecyclerView.adapter = adapter
            }
        }
    }
}