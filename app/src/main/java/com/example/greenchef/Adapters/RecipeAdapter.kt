package com.example.greenchef.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Fragments.ProfileFragment
import com.example.greenchef.Fragments.ProfileFragmentDirections
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.UserViewModel
import com.squareup.picasso.Picasso

class RecipeAdapter(private var recipes: List<Recipe>,
                    private val fragmentContext: Fragment
) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)
    private var filteredRecipes: List<Recipe> = recipes

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editButton: Button = itemView.findViewById(R.id.buttonViewEdit)
        val imageViewRecipe: ImageView = itemView.findViewById(R.id.imageViewRecipe)
        val textViewRecipeName: TextView = itemView.findViewById(R.id.textViewRecipeName)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        val favoriteCheckBox: CheckBox = itemView.findViewById(R.id.favoriteCheckBox)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        //val buttonView: Button = itemView.findViewById(R.id.buttonView)
    }

    init {
        // Sort the recipes list alphabetically by name
        recipes = recipes.sortedWith(compareBy({ it.name }, { it.recipeId }))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_card_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Bind data to views
        holder.textViewRecipeName.text = recipe.name
        holder.textViewDescription.text = recipe.description
        holder.favoriteCheckBox.isChecked = GlobalVariables.currentUser!!.favoriteRecipeIds.contains(recipe.recipeId)
        holder.ratingBar.rating = recipe.rating

        if (fragmentContext is ProfileFragment) {
            holder.editButton.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.GONE
        }

        loadImageToView(holder, recipe)
        setClickListeners(holder, recipe)
    }

    private fun setClickListeners(holder: RecipeViewHolder, recipe: Recipe) {
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("recipe", recipe)
            fragmentContext.findNavController().navigate(R.id.viewFragment, bundle)
        }
        holder.favoriteCheckBox.setOnClickListener {
            if (holder.favoriteCheckBox.isChecked) {
                userViewModel.updateUserFavoriteRecipeId(recipe.recipeId)
            } else {
                userViewModel.removeUserFavoriteRecipeId(recipe.recipeId)
            }
        }

        holder.editButton.setOnClickListener {
            val action=ProfileFragmentDirections.actionNavigationProfileToEditFragment(recipe)
            fragmentContext.findNavController().navigate(action)
        }
    }

    private fun loadImageToView(holder: RecipeViewHolder, recipe: Recipe){
        if (recipe.imageUri != "null") {
            Picasso.get()
                .load(recipe.imageUri)
                .placeholder(R.drawable.progress_animation)
                .into(holder.imageViewRecipe)
        } else {
            holder.imageViewRecipe.setImageResource(R.drawable.main_logo)
        }
    }
    override fun getItemCount(): Int = recipes.size
}