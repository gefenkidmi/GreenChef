package com.example.greenchef.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.Adapters.RecipeAdapter
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.Objects.localDataRepository
import com.example.greenchef.R
import com.example.greenchef.ViewModels.RecipeViewModel
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private lateinit var categoryTabLayout: TabLayout
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var searchTextInput: EditText
    private val recipeViewModel: RecipeViewModel by viewModels()
    private var recipes = listOf<Recipe>()
    private val sortOptions = arrayOf("Name", "Rating", "Date")
    private var sortBy: String = sortOptions[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        categoryTabLayout = view.findViewById(R.id.categoryTabLayout)
        recipeRecyclerView = view.findViewById(R.id.recipesRecyclerView)
        searchTextInput = view.findViewById(R.id.searchTextInputLayout)
        recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeViewModel.setContextAndDB(requireContext())

        initCategoryTabs()
        initRecipeRecyclerView()
        setSearchRecipes()
        initSpinner(view)

        return view
    }

    private fun initRecipeRecyclerView() {
        recipeViewModel.getAllRecipes().observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNotEmpty()) {
                this.recipes = recipes
                updateAdapter()
            }
        }
    }

    private fun updateAdapter() {
        val category =
            categoryTabLayout.getTabAt(categoryTabLayout.selectedTabPosition)?.text.toString()
        val name = searchTextInput.text.toString()
        val filteredRecipes = recipes.filter { recipe ->
            if (category == "All") {
                recipe.name.contains(name, ignoreCase = true)
            } else {
                recipe.name.contains(name, ignoreCase = true) && recipe.category == category
            }
        }
        val adapter = RecipeAdapter(filteredRecipes, this, sortBy)
        recipeRecyclerView.adapter = adapter
    }

    private fun initCategoryTabs() {
        categoryTabLayout.addTab(categoryTabLayout.newTab().setText("All"))

        for (category in localDataRepository.categories) {
            categoryTabLayout.addTab(categoryTabLayout.newTab().setText(category))
        }

        categoryTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateAdapter()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

    private fun setSearchRecipes() {
        searchTextInput.addTextChangedListener(onTextChanged = { _, _, _, _ ->
            updateAdapter()
        })
    }

    private fun initSpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sortBy = sortOptions[position]
                updateAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                sortBy = "Name"
                updateAdapter()
            }
        }

    }
}