package com.mazzampr.storyapps.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mazzampr.storyapps.R
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.databinding.FragmentRegisterBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.auth.viewmodel.RegisterViewModel
import com.mazzampr.storyapps.ui.main.MainActivity
import com.mazzampr.storyapps.utils.toast

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerValidation()
        playAnimation()

        binding.tvLogin.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeRegister(name: String, email: String, password : String) {
        viewModel.register(name, email, password).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnRegister.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Register")
                        setMessage(getString(R.string.register_succeed))
                        setPositiveButton(getString(R.string.continue_login)) { _, _ ->
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    binding.progressIndicator.hide()
                    binding.btnRegister.isEnabled = true
                    toast(it.error)
                }
            }
        }
    }

    private fun registerValidation() {

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            observeRegister(name, email, password)
        }
    }

    private fun playAnimation() {

        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.tlEmail, View.ALPHA, 1f).setDuration(100)
        val tvName = ObjectAnimator.ofFloat(binding.tlName, View.ALPHA, 1f).setDuration(100)
        val tvPass = ObjectAnimator.ofFloat(binding.tlPassword, View.ALPHA, 1f).setDuration(100)
        val tvWelcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(100)
        val tvWelcome2 = ObjectAnimator.ofFloat(binding.tvWelcome2, View.ALPHA, 1f).setDuration(100)
        val tvRegister = ObjectAnimator.ofFloat(binding.linearInfoLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(tvName, tvEmail, tvPass, tvWelcome, tvWelcome2, tvRegister, btnRegister)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}