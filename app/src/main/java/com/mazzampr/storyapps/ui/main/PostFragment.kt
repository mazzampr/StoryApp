package com.mazzampr.storyapps.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.mazzampr.storyapps.R
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.databinding.FragmentPostBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.main.viewmodel.PostViewModel
import com.mazzampr.storyapps.utils.getImageUri
import com.mazzampr.storyapps.utils.reduceFileImage
import com.mazzampr.storyapps.utils.toast
import com.mazzampr.storyapps.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    private lateinit var client: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private var _binding: FragmentPostBinding? = null
    private val permissionCode = 101
    private val binding get() = _binding!!
    private val viewModel by viewModels<PostViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private var currentImageUri: Uri? = null
    private var lat = 0F
    private var lon = 0F
    private var tokenPost = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()
        setupAction()
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun getToken() {
        viewModel.getSession().observe(viewLifecycleOwner) {token ->
            if (!(token == null || token == "")) {
                tokenPost = "Bearer $token"
            }
        }
    }

    private fun observeUpload(token: String, image: MultipartBody.Part, desc: RequestBody, lat: Float, lon: Float) {
        viewModel.uploadStory(token, image, desc, lat, lon).observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.buttonAdd.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Upload Succeed!")
                        setMessage(getString(R.string.success_upload))
                        setPositiveButton(getString(R.string.ok)) { _, _ ->
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    binding.progressIndicator.isEnabled = false
                    toast(it.error)
                }
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun uploadImage() {
        currentImageUri?.let {uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            getToken()
            observeUpload(tokenPost, multipartBody, requestBody, lat, lon)
        } ?: toast("Gambar Kosong!")

    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun setupAction() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }
            cameraButton.setOnClickListener {
                startCamera()
            }
            buttonAdd.setOnClickListener {
                uploadImage()
            }
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun getCurrentLocation() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
        } else {
            val task = client.lastLocation
            task.addOnSuccessListener {location->
                if (location != null) {
                    currentLocation = location
                    lat = currentLocation.latitude.toFloat()
                    lon = currentLocation.longitude.toFloat()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}