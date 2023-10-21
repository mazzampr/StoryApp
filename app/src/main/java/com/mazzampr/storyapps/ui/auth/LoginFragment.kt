package com.mazzampr.storyapps.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mazzampr.storyapps.data.Result
import com.mazzampr.storyapps.databinding.FragmentLoginBinding
import com.mazzampr.storyapps.ui.ViewModelFactory
import com.mazzampr.storyapps.ui.auth.viewmodel.LoginViewModel
import com.mazzampr.storyapps.ui.main.MainActivity
import com.mazzampr.storyapps.utils.toast

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpAction()
        playAnimation()
    }

    private fun setUpAction() {
        binding.tvRegister.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            observeLogin(email, password)
        }
    }

    private fun observeLogin(email: String, password: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    saveUser(it.data)
                }
                is Result.Error -> {
                    binding.progressIndicator.hide()
                    binding.btnLogin.isEnabled = true
                    toast(it.error)
                }
                else -> false
            }
        }
    }

    private fun saveUser(token: String) {
        viewModel.saveSession(token)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE

        }.start()

        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.tlEmail, View.ALPHA, 1f).setDuration(100)
        val tvPass = ObjectAnimator.ofFloat(binding.tlPassword, View.ALPHA, 1f).setDuration(100)
        val tvWelcome = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(100)
        val tvRegister = ObjectAnimator.ofFloat(binding.linearInfoRegister, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(tvEmail, tvPass, tvWelcome, tvRegister, btnLogin)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}