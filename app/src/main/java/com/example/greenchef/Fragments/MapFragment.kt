package com.example.greenchef.Fragments

import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private var recipes: List<Recipe> = listOf()
    private var users: List<User> = listOf()
    private var gotRecipes = false
    private var gotUsers = false

    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

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

        if (hasLocationPermissions()) {
            enableMyLocationOverlayIfPermitted()
        } else {
            requestLocationPermissions()
        }

        return view
    }

    private fun initializeMap(view: View) {
        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.minZoomLevel = 4.0

        val mapController = mapView.controller
        mapController.setZoom(5.0)
        // Center on some default bounding box or location
        mapController.setCenter(GeoPoint(20.0, 0.0))
    }

    private fun enableMyLocationOverlayIfPermitted() {
        val gpsProvider = GpsMyLocationProvider(requireContext())
        locationOverlay = MyLocationNewOverlay(gpsProvider, mapView)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation() // if you want the map to track movement

        // Center on current location once we get a fix
        locationOverlay.runOnFirstFix {
            requireActivity().runOnUiThread {
                val myLocation = locationOverlay.myLocation
                myLocation?.let {
                    mapView.controller.setZoom(15.0)
                    mapView.controller.setCenter(myLocation)
                }
            }
        }

        mapView.overlays.add(locationOverlay)
    }

    private fun hasLocationPermissions(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return (fineLocation == PackageManager.PERMISSION_GRANTED &&
                coarseLocation == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions granted
            enableMyLocationOverlayIfPermitted()
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

    private fun setUsersMarkers() {
        if (gotRecipes && gotUsers) {
            users.forEach { user ->
                user.GeoPoint?.let { geoPoint ->
                    val userRecipes = user.recipeIds.mapNotNull { recipeId ->
                        recipes.find { recipe -> recipe.recipeId == recipeId }
                    }
                    setMarker(
                        GeoPoint(geoPoint.latitude, geoPoint.longitude),
                        "${user.name}'s Recipes",
                        userRecipes.joinToString("\n ------- \n") { it.name }
                    )
                }
            }
        }
    }

    private fun setMarker(geoPoint: GeoPoint, title: String, snippet: String) {
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        marker.icon = resources.getDrawable(R.drawable.baseline_location_on_24, null)

        val customInfoWindow = CustomInfoWindow(mapView, title, snippet)
        marker.infoWindow = customInfoWindow
    }
}