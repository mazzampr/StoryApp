package com.mazzampr.storyapps.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.mazzampr.storyapps.R
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.databinding.ActivityMapsBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.main.viewmodel.MapsViewModel
import com.mazzampr.storyapps.utils.hide
import com.mazzampr.storyapps.utils.show
import com.mazzampr.storyapps.utils.toast

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel by viewModels<MapsViewModel> { ViewModelFactory.getInstance(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        observe()
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun observe() {
        viewModel.getStoryFromDb().observe(this) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    it.data.forEach{data->
                        val latLng = LatLng(data.lat!!, data.lon!!)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(data.name)
                                .snippet(data.description)
                        )
                        boundsBuilder.include(latLng)
                    }
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }
    }
}