package com.mazzampr.storyapps.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mazzampr.storyapps.R
import com.mazzampr.storyapps.adapter.LoadingStateAdapter
import com.mazzampr.storyapps.adapter.StoriesAdapter
import com.mazzampr.storyapps.databinding.FragmentHomeBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.main.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding:FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(requireActivity()) }

//    private lateinit var adapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        adapter = StoriesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        getToken()
        setAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRv() {
        binding.rvListStory.layoutManager = LinearLayoutManager(requireContext())
    }

//    private fun setUpRvStories(token: String) {
//        adapter.onItemClick = {
//            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.id)
//            findNavController().navigate(action)
//        }
//        binding.rvListStory.adapter = adapter
//
//    }

    private fun getToken() {
        viewModel.getSession().observe(viewLifecycleOwner) {token ->
            if (!(token == null || token == "")) {
                observe("Bearer $token")
            }
        }
    }


    private fun observe(token: String) {
        val adapter = StoriesAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.getStories(token).observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitData(lifecycle, it)
            }

        }
        adapter.onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.id)
            findNavController().navigate(action)
        }

    }

    private fun setAction() {
        binding.topAppBar.setOnMenuItemClickListener {menuItem->
            when(menuItem.itemId) {
                R.id.action_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.action_logout -> {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Confirm Logout")
                        setMessage(getString(R.string.confirm_logout))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.logout()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        setNegativeButton(getString(R.string.no)) {dialog, which ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }

                    true
                }
                R.id.action_maps -> {
                   startActivity(Intent(requireContext(), MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        binding.btnAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}