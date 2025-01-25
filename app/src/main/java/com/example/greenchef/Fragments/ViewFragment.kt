package com.example.greenchef.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.CommentAdapter
import com.example.greenchef.DataClass.Comment
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.Services.NutritionCalculatorService
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val RECIPE_PARAM = "recipe"

class ViewFragment : Fragment() {

    private var rating: Float? = null
    private lateinit var recipeNameTextView: TextView
    private lateinit var editRecipeButton: ImageButton
    private lateinit var deleteRecipeButton: ImageButton
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeDescriptionTextView: TextView
    private lateinit var recipeIngredientsTextView: TextView
    private lateinit var recipeProcedureTextView: TextView
    private lateinit var recipeRatingBar: RatingBar
    private lateinit var recipe: Recipe
    private lateinit var recipeCaloriesTextView: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var newCommentButton: Button
    private lateinit var newCommentText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var userImageView: ImageView
    private lateinit var userNameTextView: TextView

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view, container, false)
        recipeViewModel.setContextAndDB(requireContext())
        arguments?.let {
            val recipeParam = it.getParcelable<Recipe>(RECIPE_PARAM)
            if (recipeParam != null) {
                recipeViewModel.getById(recipeParam.recipeId)
                    .observe(viewLifecycleOwner) { recipeById ->
                        recipe = recipeById
                        Log.d("ViewFragment", recipe.toString())
                        setRating()

                        viewLifecycleOwner.lifecycleScope.launch {
                            loadRecipeData()
                        }
                        view.post {
                            Log.d("ViewFragment", recipe.comments.toString())
                            initCommentsRecyclerView(recipe.comments)
                        }
                    }
            }
        }

        getViews(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNewCommentButton()
    }

    private fun initCommentsRecyclerView(comments: List<Comment>) {
        val adapter = CommentAdapter(comments)
        commentRecyclerView = requireView().findViewById(R.id.commentsRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        commentRecyclerView.adapter = adapter
    }

    private suspend fun loadRecipeData() {
        recipe.let {
            recipeNameTextView.text = it.name
            recipeDescriptionTextView.text = it.description
            it.ingredients.forEach { ingredient ->
                recipeIngredientsTextView.append("$ingredient\n")
            }
            recipeProcedureTextView.text = it.procedure

            setUserInfo()
            setActionButtons()
            setRecipeImage()
            setCalories()
        }
    }

    private fun getViews(view: View) {
        recipeNameTextView = view.findViewById(R.id.recipeNameTextView)
        editRecipeButton = view.findViewById(R.id.editRecipeButton)
        deleteRecipeButton = view.findViewById(R.id.deleteRecipeButton)
        recipeImageView = view.findViewById(R.id.recipeImageView)
        recipeDescriptionTextView = view.findViewById(R.id.descriptionTextView)
        recipeIngredientsTextView = view.findViewById(R.id.ingredientsTextView)
        recipeProcedureTextView = view.findViewById(R.id.procedureTextView)
        recipeRatingBar = view.findViewById(R.id.recipeRatingBar)
        recipeCaloriesTextView = view.findViewById(R.id.caloriesNumberTextView)
        newCommentButton = view.findViewById(R.id.newCommentButton)
        newCommentText = view.findViewById(R.id.newCommentText)
        progressBar = view.findViewById(R.id.progress_loader)
        userImageView = view.findViewById(R.id.userImageView)
        userNameTextView = view.findViewById(R.id.userNameTextView)
    }

    private fun setRating() {
        this.rating = GlobalVariables.currentUser?.ratedRecipes?.get(recipe.recipeId)
        this.rating?.let {
            recipeRatingBar.rating = it
        }
        recipeRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            this.rating?.let {
                userViewModel.removeUserRatedRecipe(recipe.recipeId)
                recipeViewModel.removeRating(recipe, it, onSuccess = { recipeAfterRemove ->
                    userViewModel.addUserRatedRecipe(recipe.recipeId, rating)
                    recipeViewModel.addRating(
                        recipeAfterRemove,
                        rating,
                        onSuccess = { recipeAfterAdd ->
                            this.recipe = recipeAfterAdd
                            this.rating = rating
                        })
                })
            } ?: run {
                userViewModel.addUserRatedRecipe(recipe.recipeId, rating)
                recipeViewModel.addRating(recipe, rating, onSuccess = { recipeAfterAdd ->
                    this.recipe = recipeAfterAdd
                    this.rating = rating
                })
            }
        }
    }

    private fun setNewCommentButton() {
        newCommentButton.setOnClickListener {
            val commentText = newCommentText.text.toString()
            if (commentText.isNotEmpty()) {
                recipeViewModel.addCommentToRecipe(recipe, commentText, listener = {
                    recipe.comments = it.comments
                    val view = requireView()
                    view.post {
                        initCommentsRecyclerView(recipe.comments)
                    }
                    newCommentText.text.clear()
                })
            }
        }
    }

    private fun setUserInfo() {
        if (recipe.owner.userId != "") {
            userNameTextView.text = recipe.owner.name
            val photoUrl: String = recipe.owner.photoUrl
            if (photoUrl.isNotEmpty() && photoUrl.isNotBlank() && photoUrl != "null") {
                Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.progress_animation)
                    .into(userImageView)
            } else {
                userImageView.setImageResource(R.drawable.baseline_person_24)
            }
        }
    }

    private suspend fun setCalories() {
        if (recipe.ingredients.isNotEmpty() && recipeCaloriesTextView.text.isEmpty()) {
            progressBar.visibility = View.VISIBLE
            recipeCaloriesTextView.text =
                NutritionCalculatorService().getNutritionalValues(recipe.ingredients).toInt()
                    .toString()
            progressBar.visibility = View.INVISIBLE
        } else if (recipe.ingredients.isEmpty()) {
            recipeCaloriesTextView.text = "0"
        }
    }

    private fun setRecipeImage() {
        if (recipe.imageUri != "null") {
            Picasso.get()
                .load(recipe.imageUri)
                .placeholder(R.drawable.progress_animation)
                .into(recipeImageView, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        recipeImageView.scaleType = ImageView.ScaleType.FIT_XY
                    }

                    override fun onError(e: Exception?) {
                        // Set your visibility to VISIBLE
                    }
                })
        } else {
            recipeImageView.setImageResource(R.drawable.main_logo)
            recipeImageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun setActionButtons() {
        if (GlobalVariables.currentUser?.recipeIds?.contains(recipe.recipeId) == true) {
            editRecipeButton.visibility = View.VISIBLE
            editRecipeButton.setOnClickListener {
                val action = ViewFragmentDirections.actionNavigationViewToEditFragment(recipe)
                findNavController().navigate(action)
            }
            deleteRecipeButton.visibility = View.VISIBLE
            deleteRecipeButton.setOnClickListener {
                recipeViewModel.deleteRecipe(this.recipe.recipeId, onSuccess = {
                    userViewModel.removeUserRecipe(this.recipe.recipeId, onSuccess = {
                        findNavController().navigate(R.id.navigation_profile)
                    })
                })
            }
        }
    }
}