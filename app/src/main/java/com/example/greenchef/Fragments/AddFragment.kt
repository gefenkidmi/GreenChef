package com.example.greenchef.Fragments

import GalleryHandler
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.greenchef.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PICK_IMAGE_REQUEST = 1

class AddFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recipeNameEditText: EditText
    private lateinit var imageViewRecipe: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextDescription: EditText
    private lateinit var editTextIngredient: EditText
    private lateinit var editTextProcedure: EditText
    private lateinit var buttonAddIngredient: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, null)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                // Handle the selected image URI
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    imageViewRecipe.setImageURI(selectedImageUri)
                }
            }
        }

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
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Initialize views
        recipeNameEditText = view.findViewById(R.id.editTextRecipeName)
        imageViewRecipe = view.findViewById(R.id.imageViewRecipe)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextIngredient = view.findViewById(R.id.editTextIngredient)
        editTextProcedure = view.findViewById(R.id.editTextProcedure)
        buttonAddIngredient = view.findViewById(R.id.buttonAddIngredient)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonCancel = view.findViewById(R.id.buttonCancel)

        // Set onClickListener for the image view to pick an image from the gallery
        imageViewRecipe.setOnClickListener {
            GalleryHandler.getPhotoUriFromGallery(requireActivity(), pickImageLauncher, requestPermissionLauncher)
        }

        // Set onClickListener for the button to add more ingredients dynamically
        buttonAddIngredient.setOnClickListener {
            addIngredientField()
        }

        // Set onClickListener for the save button
        buttonSave.setOnClickListener {
            // Handle save button click here
        }

        // Set onClickListener for the cancel button
        buttonCancel.setOnClickListener {
            // Handle cancel button click here
        }

        return view
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageViewRecipe.setImageURI(imageUri)
        }
    }

    private fun addIngredientField() {
        val newIngredientEditText = EditText(requireContext())
        newIngredientEditText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        newIngredientEditText.hint = "Enter ingredient"
        // Add the new ingredient EditText to the existing layout
        (view?.findViewById<ViewGroup>(R.id.ingredientsLayout))?.addView(newIngredientEditText)
    }

    // Add any additional functionality as needed
}