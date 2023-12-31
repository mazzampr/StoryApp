package com.mazzampr.storyapps.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.data.remote.response.ListStoryItem
import com.mazzampr.storyapps.databinding.FragmentDetailBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.main.viewmodel.DetailViewModel
import com.mazzampr.storyapps.utils.hide
import com.mazzampr.storyapps.utils.show

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getToken()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun getToken() {
        viewModel.getSession().observe(viewLifecycleOwner) {token ->
            if (!(token == null || token == "")) {
                observeDetailStory("Bearer $token")
            }
        }
    }

    private fun observeDetailStory(token: String) {
        val storyId = DetailFragmentArgs.fromBundle(arguments as Bundle).userDetail
        storyId?.let {
            viewModel.getDetailStory(token, storyId).observe(viewLifecycleOwner) {
                when(it) {
                    is Result.Loading -> {
                        binding.progressBar.show()
                    }
                    is Result.Success -> {
                        binding.progressBar.hide()
                        setupDetailPage(it.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.hide()
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

    }

    private fun setupDetailPage(data: ListStoryItem) {
        Glide.with(requireContext())
            .load(data.photoUrl)
            .into(binding.storyImgDetail)

        binding.apply {
            tvDetailArticleTitle.text = data.name
            tvDetailArticleDescription.text = data.description
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}