package com.rats.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rats.R

class LoadingErrorFragment : Fragment(R.layout.fragment_loading_error) {
    private lateinit var loading: ProgressBar
    private lateinit var errorMessage: TextView

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        loading = view.findViewById(R.id.loading)
        errorMessage = view.findViewById(R.id.errorMessage)
    }

    fun showLoading() {
        loading.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
    }

    fun hideLoading() {
        loading.visibility = View.GONE
    }

    fun showError(message: String) {
        errorMessage.visibility = View.VISIBLE
        errorMessage.text = message
        loading.visibility = View.GONE
    }

    fun hideError() {
        errorMessage.visibility = View.GONE
    }
}
