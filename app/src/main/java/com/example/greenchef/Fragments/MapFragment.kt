package com.example.greenchef.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.greenchef.DataClass.Recipe
import com.example.greenchef.DataClass.User
import com.example.greenchef.InfoWindowsForMap.CustomInfoWindow
import com.example.greenchef.R
import com.example.greenchef.ViewModels.RecipeViewModel
import com.example.greenchef.ViewModels.UsersViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapFragment : Fragment() {
    private val recipeViewModel: RecipeViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private var recipes: List<Recipe> = listOf()
    private var users: List<User> = listOf()
    private var gotRecipes = false
    private var gotUsers = false
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        Configuration.getInstance().load(
            requireContext(),
            requireActivity().getSharedPreferences("map", AppCompatActivity.MODE_PRIVATE)
        )

        initializeMap(view)
        observeUsersAndRecipes()

        return view
    }

    private fun setUsersMarkers() {
        if (gotRecipes && gotUsers) {
            users.forEach { user ->
                user.GeoPoint?.let { geoPoint ->
                    val userRecipes = user.recipeIds.mapNotNull { recipeId ->
                        recipes.find { recipe -> recipe.recipeId == recipeId }
                    }
                    setMarker(
                        GeoPoint(geoPoint.latitude, geoPoint.longitude),
                        user.name + "'s Recipes",
                        userRecipes.joinToString("\n ------- \n") { recipe -> recipe.name }
                    )
                }
            }
        }
    }


    private fun observeUsersAndRecipes() {
        recipeViewModel.setContextAndDB(requireContext())

        recipeViewModel.getAllRecipes(lifecycleScope).observe(viewLifecycleOwner) { recipes ->
            this.recipes = recipes
            gotRecipes = true
            setUsersMarkers()
        }

        usersViewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            this.users = users
            gotUsers = true
            setUsersMarkers()
        }
    }

    private fun initializeMap(view: View) {
        this.mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.minZoomLevel = 4.0
        val mapController = mapView.controller
        mapController.setZoom(5.0)
        mapController.setCenter(mapView.boundingBox.center)
    }

    private fun setMarker(geoPoint: GeoPoint, title: String, snippet: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        marker.icon = resources.getDrawable(R.drawable.baseline_location_on_24)

        val customInfoWindow = CustomInfoWindow(mapView, title, snippet)
        marker.infoWindow = customInfoWindow
    }
}