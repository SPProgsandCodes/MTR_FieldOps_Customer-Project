//package com.mtr.fieldopscust.RequestServiceScreen
//
//import android.Manifest
//import android.app.Activity
//import android.app.Dialog
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.provider.OpenableColumns
//import android.view.View
//import android.view.Window
//import android.view.WindowManager
//import android.widget.ArrayAdapter
//import android.widget.Spinner
//import android.widget.SpinnerAdapter
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.graphics.Insets
//import androidx.core.view.OnApplyWindowInsetsListener
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.fragment.app.FragmentManager
//import com.mtr.fieldopscust.DashboardScreen.Category
//import com.mtr.fieldopscust.DashboardScreen.ModelUserDashboard
//import com.mtr.fieldopscust.R
//import com.mtr.fieldopscust.Utils.ApplicationHelper
//import com.mtr.fieldopscust.Utils.ApplicationHelper.hideSystemUI
//import com.mtr.fieldopscust.Utils.Constants.Companion.DOMAIN_ID
//import com.mtr.fieldopscust.Utils.Constants.Companion.LOGIN_PREFS
//import com.mtr.fieldopscust.Utils.Constants.Companion.TOKEN_KEY
//import com.mtr.fieldopscust.Utils.Constants.Companion.USER_ID_KEY
//import com.mtr.fieldopscust.Utils.SessionPreferences
//import com.mtr.fieldopscust.databinding.ActivityRequestServicePageBinding
//import com.mtr.fieldopscust.network.request.RequestServiceResponse
//import com.mtr.fieldopscust.network.request.UploadFileResponse
//import com.mtr.fieldopsemp.network.ApiClientProxy
//import com.mtr.fieldopsemp.network.ApiClientProxy.uploadFile
//import io.reactivex.Observable
//import io.reactivex.disposables.Disposable
//import io.reactivex.schedulers.Schedulers
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import java.io.File
//import java.net.SocketTimeoutException
//import java.util.concurrent.TimeUnit
//
//
//class ActivityRequestSerPage : AppCompatActivity() {
//    lateinit var binding: ActivityRequestServicePageBinding
//    private val REQUEST_CODE = 123 // Define your request code
//    private var disposable: Disposable? = null
//    private var token: String? = null
//    private var sharedSesssionPrefs: SessionPreferences? = null
//    private var userId: Int = 0
//    private var categorySpinner: Spinner? = null
//    private var categoriesList: ArrayList<String>? = null
//    private var bitmap: Bitmap? = null
//    private var fileName: String? = "null"
//    private var filePath: String? = null
//    private var categories: List<Category> = emptyList()
//    private val selectedFileNames: MutableList<String> = mutableListOf()
//
//
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        binding = ActivityRequestServicePageBinding.inflate(getLayoutInflater())
////        this.enableEdgeToEdge()
////        hideSystemUI(this@ActivityRequestSerPage)
////        val coroutineScope = CoroutineScope(Dispatchers.Main)
////        coroutineScope.launch {
////            delay(2000)
////            hideSystemUI(this@ActivityRequestSerPage)
////        }
////        requestWindowFeature(Window.FEATURE_NO_TITLE)
////        this.getWindow().setFlags(
////            WindowManager.LayoutParams.FLAG_FULLSCREEN,
////            WindowManager.LayoutParams.FLAG_FULLSCREEN
////        )
////
////
////        setContentView(binding.getRoot())
////        categorySpinner = binding.spinner
////
////        try {
////            ViewCompat.setOnApplyWindowInsetsListener(
////                findViewById<View>(R.id.main),
////                OnApplyWindowInsetsListener { v: View, insets: WindowInsetsCompat ->
////                    val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
////                    v.setPadding(
////                        systemBars.left,
////                        systemBars.top,
////                        systemBars.right,
////                        systemBars.bottom
////                    )
////                    insets
////                })
////        } catch (e: NullPointerException) {
////            e.printStackTrace()
////        }
////        sharedSesssionPrefs = ApplicationHelper.getAppController()?.loginPrefs(LOGIN_PREFS)
////        if (sharedSesssionPrefs != null) {
////            token = sharedSesssionPrefs!!.getString(TOKEN_KEY, null)
////            userId = sharedSesssionPrefs!!.getInt(USER_ID_KEY, 0)
////        }
////
////
////
////        getUserDashboard()
////        binding.btnSendRequest.setOnClickListener{
////            if (bitmap!=null){
////                sendRequest()
////            } else {
////                uploadDetails()
////            }
////        }
////
////        binding.imageViewBackButton.setOnClickListener{
////            onClickBackButton()
////        }
////        binding.imgAddDocument.setOnClickListener(View.OnClickListener { requestStoragePermission() })
//////        binding.btnSendRequest.setOnClickListener(View.OnClickListener { showPopupWindow() })
////    }
//    private fun requestStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
//        } else {
//            openPdfPicker()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openPdfPicker()
//            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    private fun openPdfPicker() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "application/pdf"
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_FILE_REQUEST)
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
//            if (modelUserDashboard.isSuccess){
//                Toast.makeText(this, "Category Fetched", Toast.LENGTH_SHORT).show()
//
//                // Map the categories from the server to the Category data class
//                categories = modelUserDashboard.result.categories.map {
//                    Category(it.id, it.name, it.icon, it.createdBy, it.isActive, it.isDeleted, it.createdAt, it.updatedAt, it.requestCategoryCreatedBy)
//                }
//                val adapter = ArrayAdapter(
//                    this,
//                    android.R.layout.simple_spinner_item,
//                    modelUserDashboard.result.categories.map { it.name }
//                )
//
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                categorySpinner?.adapter = adapter
//            }
//        }
//
//
//
//
//    }
//
//    private fun onUserDashboardError(throwable: Throwable?) {
//        Toast.makeText(this, "Error Fetching Categories", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun showPopupWindow() {
//        val dialog = Dialog(this@ActivityRequestSerPage)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.request_sent_popup)
////        // Convert dp to pixels
////        val widthInPx = TypedValue.applyDimension(
////            TypedValue.COMPLEX_UNIT_DIP,
////            190f,
////            getResources().getDisplayMetrics()
////        ).toInt()
////        val heightInPx = TypedValue.applyDimension(
////            TypedValue.COMPLEX_UNIT_DIP,
////            158f,
////            getResources().getDisplayMetrics()
////        ).toInt()
////
////        // Set the size of the dialog
////        dialog.window!!.setLayout(widthInPx, heightInPx)
//
//        // Show the dialog
//        dialog.show()
//    }
//
//    private fun openGalleryForFile() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT) // Changed to ACTION_GET_CONTENT for broader file types
//        intent.type = "*/*"  // Allow all file types
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple file selection
//        startActivityForResult(intent, PICK_FILE_REQUEST)
//    }
//
//    private fun getBitmapFromUri(uri: Uri): Bitmap {
//        return MediaStore.Images.Media.getBitmap(contentResolver, uri)
//    }
//
//    companion object {
//        const val PICK_FILE_REQUEST = 1
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
//            data?.data?.let { uri ->
//                val pdfFile = getFileFromUri(uri)
//                fileName = pdfFile?.name
//                filePath?.let {
//                    uploadDocuments()
//                }
//            }
//        }
//    }
//
//    private fun onImageUploadSuccess(uploadFileResponse: UploadFileResponse?) {
//
//        if (uploadFileResponse!=null){
//            if (uploadFileResponse.isSuccess){
//                binding.requestServiceProgressBar.visibility = View.GONE
//                binding.txtLoadingDetailsServicePage.visibility = View.GONE
//                Toast.makeText(this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show()
//                uploadDetails()
//            }
//        } else {
//            binding.requestServiceProgressBar.visibility = View.GONE
//            binding.txtLoadingDetailsServicePage.visibility = View.GONE
//            Toast.makeText(this, "Failed to Upload File", Toast.LENGTH_SHORT).show()
//        }
//    }
//    private fun OnImageUploadFailure(throwable: Throwable?) {
//        binding.requestServiceProgressBar.visibility = View.GONE
//        binding.txtLoadingDetailsServicePage.visibility = View.GONE
//        Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show()
//    }
//
//    private fun isImageFile(fileName: String?): Boolean {
//        return fileName?.let {
//            it.endsWith(".jpg", true) || it.endsWith(".jpeg", true) || it.endsWith(".png", true)
//        } ?: false
//    }
//
//    private fun getFileFromUri(uri: Uri): File? {
//        val filePath = getFilePath(uri)
//        return filePath?.let { File(it) }
//    }
//
//    private fun getFilePath(uri: Uri): String? {
//        val cursor = contentResolver.query(uri, arrayOf(MediaStore.Files.FileColumns.DATA), null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                return it.getString(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
//            }
//        }
//        return null
//    }
//
//    private fun getFilePathFromUri(uri: Uri): String? {
//        val context = this // Use the appropriate context if not in an activity
//
//        return when {
//            // Check if the URI is a document URI (e.g., from the DocumentsProvider)
//            "content" == uri.scheme -> {
//                val cursor = context.contentResolver.query(uri, null, null, null, null)
//                cursor?.use {
//                    val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                    if (columnIndex != -1 && it.moveToFirst()) {
//                        return it.getString(columnIndex)
//                    }
//                }
//                // Handle case where data might not be available
//                null
//            }
//            // Check if the URI is a file URI
//            "file" == uri.scheme -> {
//                uri.path
//            }
//            else -> {
//                // Handle other URI schemes or unknown URIs
//                null
//            }
//        }
//    }
//
//
//
//
////    private fun sendRequest(){
////
////        uploadDocuments()
////
////    }
//
////    private fun uploadDocuments() {
////        binding.requestServiceProgressBar.visibility = View.VISIBLE
////        binding.txtLoadingDetailsServicePage.text = "Uploading Documents..."
////        binding.txtLoadingDetailsServicePage.visibility = View.VISIBLE
////        val token = "bearer $token"  // Replace with your token
////
////        if (bitmap != null && isImageFile(fileName)) {
////            val file = convertBitmapToFile(bitmap!!, fileName!!)
////            disposable = uploadFile(file, fileName!!, token, DOMAIN_ID)
////                .retryWhen { error ->
////                    error.zipWith(Observable.range(1, 3)) { error, retryCount ->
////                        if (error is SocketTimeoutException && retryCount < 3) {
////                            Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
////                        } else {
////                            Observable.error<Throwable>(error)
////                        }
////                    }.flatMap { it }
////                }
////                .subscribe(this::onImageUploadSuccess, this::OnImageUploadFailure)
////        } else if (filePath != null) {
////            val file = File(filePath!!)
////            val fileNameToUse = fileName ?: file.name
////            disposable = uploadFile(file, fileNameToUse, token, DOMAIN_ID)
////                .retryWhen { error ->
////                    error.zipWith(Observable.range(1, 3)) { error, retryCount ->
////                        if (error is SocketTimeoutException && retryCount < 3) {
////                            Observable.timer(2, TimeUnit.SECONDS) // Wait for 2 seconds before retrying
////                        } else {
////                            Observable.error<Throwable>(error)
////                        }
////                    }.flatMap { it }
////                }
////                .subscribe(this::onImageUploadSuccess, this::OnImageUploadFailure)
////        } else {
////            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
////            binding.requestServiceProgressBar.visibility = View.GONE
////            binding.txtLoadingDetailsServicePage.visibility = View.GONE
////        }
////    }
//
//
//    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
//        val file = File(cacheDir, fileName)
//        file.outputStream().use {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//            it.flush()
//        }
//        return file
//    }
//
//
//    private fun uploadDetails() {
//        binding.requestServiceProgressBar.visibility = View.VISIBLE
//        binding.txtLoadingDetailsServicePage.text = "Creating your Request..."
//        binding.txtLoadingDetailsServicePage.visibility = View.VISIBLE
//        val selectedPosition = categorySpinner?.selectedItemPosition
//        val selectedCategoryID = selectedPosition?.let {
//            categories[it].id
//        }
//        val serviceTitle = binding.editTextServiceTitle.text.toString()
//        val serviceDescription = binding.editTextServiceDescription.text.toString()
//        val price = Integer.parseInt(binding.editTextOfferPrice.text.toString())
//
//
//        if (serviceTitle.isEmpty()){
//            binding.editTextServiceTitle.error = "This Field is required"
//        } else if (selectedCategoryID == null){
//            binding.txtErrorFetchingCategories.visibility = View.VISIBLE
//        } else if (price==0){
//            binding.editTextOfferPrice.error = "This Field is required"
//        } else {
//            sendDataToServer(selectedCategoryID, serviceTitle, serviceDescription, price, selectedFileNames)
//        }
//    }
//
//    private fun sendDataToServer(
//        selectedCategoryId: Int,
//        serviceTitle: String,
//        serviceDescription: String,
//        price: Int,
//        selectedFileNames: MutableList<String>
//    ) {
//        val bearerToken = "bearer $token"
//        disposable = ApiClientProxy.requestService(DOMAIN_ID, serviceTitle, serviceDescription, price, selectedCategoryId, selectedFileNames, bearerToken)
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
//    private fun onRequestServiceSuccess(requestServiceResponse: RequestServiceResponse?) {
//        if (requestServiceResponse != null) {
//            if (requestServiceResponse.isSuccess){
//                binding.requestServiceProgressBar.visibility = View.GONE
//                binding.txtLoadingDetailsServicePage.visibility = View.GONE
//                Toast.makeText(this, "Task Created Successfully", Toast.LENGTH_SHORT).show()
//            } else {
//                binding.requestServiceProgressBar.visibility = View.GONE
//                binding.txtLoadingDetailsServicePage.visibility = View.GONE
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//    }
//
//    private fun onClickBackButton(){
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
//    private fun onRequestServiceFailure(throwable: Throwable?) {
//        binding.requestServiceProgressBar.visibility = View.GONE
//        binding.txtLoadingDetailsServicePage.visibility = View.GONE
//        Toast.makeText(this, "Task Failed", Toast.LENGTH_SHORT).show()
//    }
//
//}