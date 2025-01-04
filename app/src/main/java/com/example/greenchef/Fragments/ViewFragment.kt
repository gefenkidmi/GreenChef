package com.example.greenchef.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UserViewModel
import com.squareup.picasso.Picasso

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val RECIPE_PARAM = "recipe"

class ViewFragment : Fragment() {

    private var recipe: Recipe? = null

    private lateinit var recipeNameTextView: TextView
    private lateinit var editRecipeButton: ImageButton
    private lateinit var recipeImageView: ImageView
    private lateinit var recipeDescriptionTextView: TextView
    private lateinit var recipeIngredientsTextView: TextView
    private lateinit var recipeProcedureTextView: TextView
    private lateinit var recipeRatingBar: RatingBar

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipe = it.getParcelable(RECIPE_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view, container, false)

        getViews(view)
        loadRecipeData()

        recipeViewModel.setContextAndDB(requireContext())



        return view
    }

    private fun loadRecipeData() {
        recipe?.let {
            recipeNameTextView.text = it.name
            recipeDescriptionTextView.text = it.description
            it.ingredients.forEach { ingredient ->
                recipeIngredientsTextView.append("$ingredient\n")
            }
            recipeProcedureTextView.text = it.procedure
            if (it.imageUri != "null") {
                Picasso.get()
                    .load(it.imageUri)
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
                recipeImageView.setImageResource(R.drawable.login_image)
                recipeImageView.scaleType = ImageView.ScaleType.FIT_XY
            }
            if (GlobalVariables.currentUser?.recipeIds?.contains(it.recipeId) == true) {
                editRecipeButton.visibility = View.VISIBLE
            }
        }
    }

    private fun getViews(view: View) {
        recipeNameTextView = view.findViewById(R.id.recipeNameTextView)
        editRecipeButton = view.findViewById(R.id.editRecipeButton)
        recipeImageView = view.findViewById(R.id.recipeImageView)
        recipeDescriptionTextView = view.findViewById(R.id.descriptionTextView)
        recipeIngredientsTextView = view.findViewById(R.id.ingredientsTextView)
        recipeProcedureTextView = view.findViewById(R.id.procedureTextView)
        recipeRatingBar = view.findViewById(R.id.recipeRatingBar)
    }
}