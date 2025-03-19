package com.example.greenchef.Fragments

import GalleryHandler
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.IngredientAdapter
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.Objects.localDataRepository
import com.example.greenchef.R
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso

class AddEditFragment : Fragment() {
    private val args: AddEditFragmentArgs by navArgs()
    private lateinit var recipeNameEditText: EditText
    private lateinit var imageViewRecipe: ImageView
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextDescription: EditText
    private lateinit var editTextProcedure: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button
    private lateinit var editTextFilter: TextInputEditText
    private lateinit var recyclerViewIngredients: RecyclerView
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var navController: NavController
    private lateinit var progressBar: ProgressBar

    private var imageUri: Uri? = null
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)
    private var recipe: Recipe? = null

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
                    imageViewRecipe.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageViewRecipe.setImageURI(selectedImageUri)
                    imageUri = selectedImageUri
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipe = args.recipe
        if (recipe != null) {
            recipeNameEditText.setText(recipe!!.name)
            imageViewRecipe.scaleType = ImageView.ScaleType.CENTER_CROP
            spinnerCategory.setSelection(localDataRepository.categories.indexOf(recipe!!.category))
            editTextDescription.setText(recipe!!.description)
            editTextProcedure.setText(recipe!!.procedure)
            initRecyclerViewIngredients(view)

            if (recipe!!.imageUri != "" && recipe!!.imageUri != "null") {
                setImage(Uri.parse(recipe!!.imageUri))
                imageUri = Uri.parse(recipe!!.imageUri)
            }
        } else {
            initRecyclerViewIngredients(view)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_edit, container, false)

        recipeViewModel.setContextAndDB(requireContext())
        navController = findNavController()

        // Initialize views
        recipeNameEditText = view.findViewById(R.id.editTextRecipeName)
        imageViewRecipe = view.findViewById(R.id.imageViewRecipe)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextProcedure = view.findViewById(R.id.editTextProcedure)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonCancel = view.findViewById(R.id.buttonCancel)
        progressBar = view.findViewById(R.id.progress_loader)

        initSpinnerCategory()
        initImageView()
        initButtons()
        initRecyclerViewIngredients(view)

        return view
    }

    private fun initSpinnerCategory() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spiner_item_layout, // Use the custom layout
            localDataRepository.categories
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerCategory.adapter = adapter
    }

    private fun initImageView() {
        // Set onClickListener for the image view to pick an image from the gallery
        imageViewRecipe.setOnClickListener {
            GalleryHandler.getPhotoUriFromGallery(
                requireActivity(),
                pickImageLauncher,
                requestPermissionLauncher
            )
        }
    }

    private fun initButtons() {
        // Set onClickListener for the save button
        buttonSave.setOnClickListener {
            uploadRecipe()

        }

        // Set onClickListener for the cancel button
        buttonCancel.setOnClickListener {
            // Handle cancel button click here
            navController.navigateUp()
        }
    }

    private fun initRecyclerViewIngredients(view: View) {
        editTextFilter = view.findViewById(R.id.editTextFilter)
        recyclerViewIngredients = view.findViewById(R.id.recyclerViewIngredients)

        // Initialize RecyclerView and Adapter
        recyclerViewIngredients.layoutManager = LinearLayoutManager(requireContext())
        ingredientAdapter =
            IngredientAdapter(localDataRepository.ingredients, recipe?.ingredients ?: emptyList())
        recyclerViewIngredients.adapter = ingredientAdapter

        // Set up text change listener for filtering
        editTextFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterIngredients(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                // Do nothing
            }
        })
    }

    private fun uploadRecipe() {
        progressBar.visibility = View.VISIBLE
        val recipeName = recipeNameEditText.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val description = editTextDescription.text.toString()
        val procedure = editTextProcedure.text.toString()
        val checkedIngredients = ingredientAdapter.getCheckedItems().toList()

        // Use a null-safe conversion for the image URI
        val imageUriString = imageUri?.toString() ?: ""

        val recipe = Recipe(
            recipeId = recipe?.recipeId ?: "",
            name = recipeName,
            category = category,
            description = description,
            imageUri = imageUriString,  // Updated here
            ingredients = checkedIngredients,
            procedure = procedure,
            rating = recipe?.rating ?: 0.0f,
            numberOfRatings = recipe?.numberOfRatings ?: 0,
            ownerId = GlobalVariables.currentUser!!.userId,
            lastUpdated = System.currentTimeMillis()
        )

        if (recipe.recipeId != "") {
            // Call the updateRecipe method in RecipeViewModel
            recipeViewModel.updateRecipe(recipe) { recipeWithId ->
                // After a successful creation, update the user's recipeIds
                userViewModel.updateUserRecipeIds(listOf(recipeWithId.recipeId), onSuccess = {
                    // After a successful update, navigate back to the previous fragment
                    progressBar.visibility = View.GONE
                    navController.navigateUp()
                }, onFailure = {
                    // Handle failure
                })
            }
        } else {
            // Call the createRecipe method in RecipeViewModel
            recipeViewModel.createRecipe(recipe) { recipeWithId ->
                // After a successful creation, update the user's recipeIds
                userViewModel.updateUserRecipeIds(listOf(recipeWithId.recipeId), onSuccess = {
                    // After a successful update, navigate back to the previous fragment
                    progressBar.visibility = View.GONE
                    navController.navigateUp()
                }, onFailure = {
                    // Handle failure
                })
            }
        }
    }

    private fun filterIngredients(query: String) {
        ingredientAdapter.filter.filter(query)
    }

    private fun setImage(uri: Uri) {
        Picasso.get()
            .load(uri)
            .placeholder(R.drawable.progress_animation)
            .into(imageViewRecipe, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    imageViewRecipe.scaleType = ImageView.ScaleType.FIT_XY
                }

                override fun onError(e: Exception?) {
                    // Set your visibility to VISIBLE
                }
            })
    }
}