package com.rats.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.rats.R
import com.rats.ui.activities.ChatActivity
import com.rats.ui.activities.HomeActivity
import com.rats.ui.activities.MoreMenuActivity
import com.rats.ui.activities.MyWagonActivity
import java.io.File

class BottomMenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstance: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        val reportLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_report)
        val mapLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_map)
        val wagonLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_wagon)
        val chatLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_chat)
        val moreLayout: LinearLayout = view.findViewById<LinearLayout>(R.id.menu_more)

        reportLayout.setOnClickListener {
            if (checkPermissionsCamera()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        mapLayout.setOnClickListener {
            if (activity !is HomeActivity) {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        wagonLayout.setOnClickListener {
            if (activity !is MyWagonActivity) {
                val intent = Intent(activity, MyWagonActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        chatLayout.setOnClickListener {
            if (activity !is ChatActivity) {
                val intent = Intent(activity, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        moreLayout.setOnClickListener {
            if (activity !is MoreMenuActivity) {
                val intent = Intent(activity, MoreMenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        return view
    }

    private var photoUri: Uri? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                photoUri?.let { uri ->
                    Toast.makeText(requireContext(), "Image saved at: $uri", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                openCamera()
            }
        }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile,
            )
        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir,
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}
