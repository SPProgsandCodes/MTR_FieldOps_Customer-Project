package com.mtr.fieldopscust.RequestServiceScreen

import android.Manifest
import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mtr.fieldopscust.DashboardScreen.Category
import com.mtr.fieldopscust.DashboardScreen.ModelUserDashboard
import com.mtr.fieldopscust.R
import com.mtr.fieldopscust.Utils.ApplicationHelper
import com.mtr.fieldopscust.Utils.ApplicationHelper.getFileFromUri
import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
import com.mtr.fieldopscust.Utils.SessionPreferences
import com.mtr.fieldopscust.databinding.ActivityRequestServicePageBinding
import io.reactivex.disposables.Disposable
import io.reactivex.internal.util.HalfSerializer.onComplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

//
class ActivityRequestServicePage : AppCompatActivity() {
    lateinit var binding: ActivityRequestServicePageBinding
    private val PICK_PDF_FILE: Int = 2
    private var disposable: Disposable? = null
    private var token: String? = null
    private var sharedSesssionPrefs: SessionPreferences? = null
    private var userId: Int = 0
    private var selectedPosition: Int? = null
    private var selectedCategory: Int? = null
    private var categorySpinner: Spinner? = null
    private var categories: List<Category> = emptyList()
    private val selectedItems = mutableListOf<Uri>()
    private val documentKeys = mutableListOf<String>()
    private lateinit var adapterFileNames: AdapterFileNames

    //
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServicePageBinding.inflate(getLayoutInflater())
        this.enableEdgeToEdge()
        hideSystemUI(this@ActivityRequestServicePage)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            delay(2000)
            hideSystemUI(this@ActivityRequestServicePage)
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//        selectedCategory = intent.getIntExtra("category_Id", 0)
        selectedCategory = intent.getIntExtra("category_Id", 0)

        binding.rvFileNames.layoutManager = LinearLayoutManager(this)
        adapterFileNames = AdapterFileNames(selectedItems, this)
        binding.rvFileNames.adapter = adapterFileNames

        setContentView(binding.getRoot())
        categorySpinner = binding.spinner


//        try {
//            ViewCompat.setOnApplyWindowInsetsListener(
//                findViewById<View>(R.id.main),
//                OnApplyWindowInsetsListener { v: View, insets: WindowInsetsCompat ->
//                    val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                    v.setPadding(
//                        systemBars.left,
//                        systemBars.top,
//                        systemBars.right,
//                        systemBars.bottom
//                    )
//                    insets
//                })
//        } catch (e: NullPointerException) {
//            e.printStackTrace()
//        }
        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
        if (sharedSesssionPrefs != null) {
            token = sharedSesssionPrefs!!.getString(TOKEN_KEY, null)
            userId = sharedSesssionPrefs!!.getInt(USER_ID_KEY, 0)
        }


//        getUserDashboard()

        binding.btnSendRequest.setOnClickListener {
//            sendFinalRequest()
        }

        binding.imageViewBackButton.setOnClickListener {
//            onClickBackButton()
        }
//        binding.imgAddDocument.setOnClickListener(View.OnClickListener { showSelectDocumentTypeDialog() })
//        binding.btnSendRequest.setOnClickListener(View.OnClickListener { showPopupWindow() })
    }
}

//    private fun uploadDocuments(
//        selectedCategoryID: Int,
//        serviceTitle: String,
//        serviceDescription: String,
//        price: Int,
//        selectedFileNames: List<String>
//    ) {
//        if (selectedItems.isEmpty()) {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            Toast.makeText(this, "No Documents Selected", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        var uploadsRemaining = selectedItems.size
//        Log.d("TAG", "Size of Selected Items: $uploadsRemaining")
//        for (itemUri in selectedItems) {
//            uploadItemToServer(itemUri) {
//                uploadsRemaining--
//                Log.d("TAG", "Size of Selected Items: $uploadsRemaining")
//
//                if (uploadsRemaining == 0) {
////                    sendFinalRequest()
//                    sendDataToServer(
//                        selectedCategoryID,
//                        serviceTitle,
//                        serviceDescription,
//                        price,
//                        selectedFileNames
//                    )
//                    Toast.makeText(this, "All Documents Uploaded", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    private fun sendFinalRequest() {
//        binding.requestServiceProgressBar.visibility = View.VISIBLE
//        binding.txtLoadingDetailsServicePage.text = "Creating your Request..."
//        binding.txtLoadingDetailsServicePage.visibility = View.VISIBLE
//        selectedPosition = categorySpinner?.selectedItemPosition
//        val selectedCategoryID = selectedPosition?.let {
//            categories[it].id
//        }
//        val serviceTitle = binding.editTextServiceTitle.text.toString()
//        val serviceDescription = binding.editTextServiceDescription.text.toString()
//        val price = binding.editTextOfferPrice.text.toString()
//
//
//        if (serviceTitle.isEmpty()) {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            binding.editTextServiceTitle.error = "This Field is required"
//        } else if (selectedCategoryID == null) {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            binding.txtErrorFetchingCategories.visibility = View.VISIBLE
//        } else if (price.isEmpty()) {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            binding.editTextOfferPrice.error = "This Field is required"
//        } else {
//            uploadDocuments(
//                selectedCategoryID,
//                serviceTitle,
//                serviceDescription,
//                Integer.parseInt(price),
//                documentKeys
//            )
//
//        }
//    }
//
//
//    private fun uploadItemToServer(itemUri: Uri, onComplete: () -> Unit) {
//        val file = getFileFromUri(itemUri)
//        if (file == null || file.length() > 10 * 1024 * 1024) {
//            // Check if file is null or exceeds 10 MB
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            Toast.makeText(this, "Documents cannot be more than 10 MB", Toast.LENGTH_SHORT).show()
////            onComplete() // Call onComplete to move to the next file
//            return
//        } else {
//            val requestFile = file.let { RequestBody.create("*/*".toMediaTypeOrNull(), it) }
//            val body = requestFile.let { MultipartBody.Part.createFormData("file", file.name, it) }
//
//            val bearerToken = "bearer $token"
//            val fileName = file.name
//
//            binding.requestServiceProgressBar.visibility = View.VISIBLE
//            binding.txtLoadingDetailsServicePage.text = "Documents Uploading"
//            binding.txtLoadingDetailsServicePage.visibility = View.VISIBLE
//            try {
//                disposable = body.let {
//                    uploadFile(it, fileName, bearerToken, DOMAIN_ID)
//                        .retryWhen { error ->
//                            error.zipWith(Observable.range(1, 3)) { error, retryCount ->
//                                if (error is SocketTimeoutException && retryCount < 3) {
//                                    Observable.timer(
//                                        2,
//                                        TimeUnit.SECONDS
//                                    ) // Wait for 2 seconds before retrying
//                                } else {
//                                    Observable.error<Throwable>(error)
//                                }
//                            }.flatMap { it }
//                        }
//                        .subscribe(
//                            { response ->
//                                // Handle the successful response
//                                onImageUploadSuccess(response)
//                            },
//                            { throwable ->
//                                // Handle the error
//                                OnImageUploadFailure(throwable)
//                            },
//                            {
//                                // Call onComplete callback
//                                onComplete()
//                            }
//                        )
//                }
//            } catch (e: UnsupportedOperationException) {
//                Toast.makeText(this, "Unsupported File Type", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//
//    }
//
//    private fun onImageUploadSuccess(uploadFileResponse: UploadFileResponse?) {
//        if (uploadFileResponse != null && uploadFileResponse.isSuccess) {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            // Handle the successful upload here
//            // Store the unique key returned from the server
//            val uniqueKey = uploadFileResponse.result.key
//            documentKeys.add(uniqueKey)
//
//            for (items in documentKeys){
//                Log.d("TAG", "Document Keys: $items")
//            }
//        } else {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.text = ""
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            Toast.makeText(this, "Document Upload Failed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun OnImageUploadFailure(throwable: Throwable?) {
//        binding.requestServiceProgressBar.visibility = View.GONE
//        binding.txtLoadingDetailsServicePage.text = ""
//        binding.txtLoadingDetailsServicePage.visibility = View.GONE
//        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
//            val selectedItemUri: Uri? = data?.data
//            selectedItemUri?.let {
////                val pdfFile = uriToFile(uri, this)
//                selectedItems.add(it)
//                adapterFileNames.notifyDataSetChanged()
//                Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show()
////                uploadPdfToServer(pdfFile!!)
//                Log.d("TAG", "Method Called")
//            }
//        } else if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
//
//            data?.data?.let {
//                selectedItems.add(it)
//                adapterFileNames.notifyDataSetChanged()
////                uploadPdfToServer(pdfFile!!)
//                Log.d("TAG", "Method Called")
//            }
//
//        }
//    }
//
//
//    private fun openPdfPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.type = "application/pdf"
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(intent, PICK_PDF_FILE)
//    }
//
//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_FILE_REQUEST)
//    }
//
//
//    private fun getUserDashboard() {
//        val bearerToken = "bearer $token"
//
//        disposable = ApiClientProxy.getUserDashboard(bearerToken, DOMAIN_ID)
//            .retryWhen { error ->
//                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
//                    if (error is SocketTimeoutException && retryCount < 3) {
//                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
//                    } else {
//                        Observable.error<Throwable>(error)
//                    }
//                }.flatMap { it }
//            }
//            .subscribe(this::onUserDashboardSuccess, this::onUserDashboardError)
//
//    }
//
//    private fun onUserDashboardSuccess(modelUserDashboard: ModelUserDashboard?) {
//        if (modelUserDashboard != null) {
//            if (modelUserDashboard.isSuccess) {
//                Toast.makeText(this, "Category Fetched", Toast.LENGTH_SHORT).show()
//
//                // Map the categories from the server to the Category data class
//                categories = modelUserDashboard.result.categories.map {
//                    Category(
//                        it.id,
//                        it.name,
//                        it.icon,
//                        it.createdBy,
//                        it.isActive,
//                        it.isDeleted,
//                        it.createdAt,
//                        it.updatedAt,
//                        it.requestCategoryCreatedBy
//                    )
//                }
//                val adapter = ArrayAdapter(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    modelUserDashboard.result.categories.map { it.name }
//                )
//
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                categorySpinner?.adapter = adapter
//
//                val customPosition = categories.indexOfFirst { it.id == selectedCategory }
//                if (customPosition != -1) {
//                    categorySpinner?.setSelection(customPosition)
//                }
//
//            }
//        }
//
//
//    }
//
//    private fun onUserDashboardError(throwable: Throwable?) {
//        Toast.makeText(this, "Error Fetching Categories", Toast.LENGTH_SHORT).show()
//    }
//
//
//    companion object {
//        const val PICK_FILE_REQUEST = 1
//    }
//
//
//    private fun onClickBackButton() {
//        val fragmentManager: FragmentManager = supportFragmentManager;
//
//        if (fragmentManager.backStackEntryCount > 0) {
//            // Navigate to the previous fragment
//            fragmentManager.popBackStack();
//        } else {
//            // If no fragments in the back stack, finish the activity or handle as needed
//            this.onBackPressed();
//        }
//    }
//
//    private fun showSelectDocumentTypeDialog() {
//        val options = arrayOf("Select File", "Select Image", "Cancel")
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Select Document Type")
//
//        builder.setItems(options) { dialog, which ->
//            when (which) {
//                0 -> {
//                    // Handle "Select File" button click
//                    openPdfPicker()
//                    dialog.dismiss()
//                }
//
//                1 -> {
//                    // Handle "Select Image" button click
//                    openGallery()
//                    dialog.dismiss()
//                }
//
//                2 -> dialog.dismiss() // Handle "Cancel" button click
//            }
//        }
//
//        builder.show()
//    }
//
//    private fun getFileFromUri(uri: Uri): File? {
//        return try {
//            // Create a temporary file in the cache directory
//            val inputStream = contentResolver.openInputStream(uri)
//            val file = File(cacheDir, "tempFile_${System.currentTimeMillis()}.tmp")
//            val outputStream = FileOutputStream(file)
//            inputStream?.use { input ->
//                outputStream.use { output ->
//                    input.copyTo(output)
//                }
//            }
//            file
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private fun sendDataToServer(
//        selectedCategoryID: Int,
//        serviceTitle: String,
//        serviceDescription: String,
//        price: Int,
//        selectedFileNames: List<String>
//    ) {
//        for(items in selectedFileNames){
//            Log.d("TAG", "Document Keys: $items")
//        }
//        val bearerToken = "bearer $token"
//        disposable = ApiClientProxy.requestService(
//            DOMAIN_ID,
//            serviceTitle,
//            serviceDescription,
//            price,
//            selectedCategoryID,
//            selectedFileNames,
//            bearerToken
//        )
//            .retryWhen { error ->
//                error.zipWith(Observable.range(1, 3)) { error, retryCount ->
//                    if (error is SocketTimeoutException && retryCount < 3) {
//                        Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
//                    } else {
//                        Observable.error<Throwable>(error)
//                    }
//                }.flatMap { it }
//            }.subscribe(this::onRequestServiceSuccess, this::onRequestServiceFailure)
//    }
//
//
//    private fun onRequestServiceSuccess(requestServiceResponse: RequestServiceResponse?) {
//        if (requestServiceResponse != null) {
//            if (requestServiceResponse.isSuccess) {
//                binding.requestServiceProgressBar.visibility = View.GONE
//                binding.txtLoadingDetailsServicePage.visibility = View.GONE
//                Toast.makeText(this, "Task Created Successfully", Toast.LENGTH_SHORT).show()
//            } else {
//                binding.requestServiceProgressBar.visibility = View.GONE
//                binding.txtLoadingDetailsServicePage.visibility = View.GONE
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
//
//        } else {
//            Toast.makeText(this, "Response is Null", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun onRequestServiceFailure(throwable: Throwable?) {
//        binding.requestServiceProgressBar.visibility = View.GONE
//        binding.txtLoadingDetailsServicePage.visibility = View.GONE
//        Toast.makeText(this, "Task Failed", Toast.LENGTH_SHORT).show()
//    }
//}



