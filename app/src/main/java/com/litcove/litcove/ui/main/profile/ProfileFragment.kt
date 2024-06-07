package com.litcove.litcove.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.litcove.litcove.R
import com.litcove.litcove.databinding.FragmentProfileBinding
import com.litcove.litcove.ui.authentication.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageProfile = binding.imageProfile
        profileViewModel.imageProfile.observe(viewLifecycleOwner) {
            Glide.with(this)
                .load(it)
                .into(imageProfile)
        }

        val textUsername: TextView = binding.textUsername
        profileViewModel.textUsername.observe(viewLifecycleOwner) {
            textUsername.text = it
        }

        val textName: TextView = binding.textName
        profileViewModel.textName.observe(viewLifecycleOwner) {
            textName.text = it
        }

        val textJoinedSince: TextView = binding.textJoinedSince
        profileViewModel.textJoinedSince.observe(viewLifecycleOwner) {
            textJoinedSince.text = it
        }

        val buttonLogout = binding.buttonLogout
        buttonLogout.setOnClickListener {
            showLogoutDialog()
        }

        val buttonDeleteAccount = binding.buttonDeleteAccount
        buttonDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        profileViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> logout() }
            .setNegativeButton(getString(R.string.no), null)
            .setCancelable(true)
            .show()
    }

    private fun logout() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_account)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account_this_action_cannot_be_undone))
            .setPositiveButton(R.string.yes) { _, _ -> deleteAccount() }
            .setNegativeButton(R.string.no, null)
            .setCancelable(true)
            .show()
    }

    fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context,
                    getString(R.string.account_deleted_successfully), Toast.LENGTH_LONG).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                activity?.finish()
            } else {
                Toast.makeText(context,
                    getString(R.string.failed_to_delete_account), Toast.LENGTH_LONG).show()
            }
        }
    }
}