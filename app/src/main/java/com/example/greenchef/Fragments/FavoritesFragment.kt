package com.example.greenchef.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.RecipeAdapter
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoritesFragment : Fragment() {
    private lateinit var recipeRecyclerView: RecyclerView

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val userViewModel: UserViewModel = UserViewModel(GlobalVariables.currentUser!!.userId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recipeRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        recipeViewModel.setContextAndDB(requireContext())

        initRecipeRecyclerView()

        return view
    }

    private fun initRecipeRecyclerView() {
        recipeViewModel.getAllRecipes().observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNotEmpty()) {
                val filteredRecipes = recipes.filter { GlobalVariables.currentUser?.favoriteRecipeIds!!.contains(it.recipeId) }
                val adapter = RecipeAdapter(filteredRecipes, this)
                recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                recipeRecyclerView.adapter = adapter
            }
        }
    }
}