package com.example.greenchef.DialogFragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.greenchef.R

class EditProfileImageDialogFragment : DialogFragment() {

    interface EditProfileImageDialogListener {
        fun onImageUpdated(imageUri: String)
    }

    private lateinit var listener: EditProfileImageDialogListener

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri? = result.data?.data
            selectedImage?.let { uri ->
                listener.onImageUpdated(uri.toString())
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as EditProfileImageDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement EditProfileImageDialogListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndOpenGalleryIfPermissionGranted()
    }

    private fun checkAndOpenGalleryIfPermissionGranted() {
        // Check if the permission is granted
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, open the gallery
            openGallery()
        } else {
            // Permission not granted, request it
            requestReadExternalStoragePermission()
        }
    }

    private fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open the gallery
                    openGallery()
                } else {
                    // Permission denied, handle accordingly (show a message, take appropriate action)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()
        // Check permissions again when the user returns to the dialog
        checkAndOpenGalleryIfPermissionGranted()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    override fun onStart() {
        super.onStart()
        // Adjust the dialog size if needed
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_profile_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageViewProfile: ImageView = view.findViewById(R.id.imageViewProfile)

        // Set up your UI and listeners as needed
    }

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 123
    }
}