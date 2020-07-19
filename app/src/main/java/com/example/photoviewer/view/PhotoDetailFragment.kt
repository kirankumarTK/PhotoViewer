package com.example.photoviewer.view

import android.Manifest
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.photoviewer.R
import com.example.photoviewer.databinding.PhotoDetailFragmentBinding
import com.example.photoviewer.model.Photo
import com.example.photoviewer.utills.DownloadImage
import com.example.photoviewer.viewmodel.PhotoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PhotoDetailFragment : Fragment(), DownloadImage {
    private lateinit var dataBinding: PhotoDetailFragmentBinding
    private lateinit var photoList: ArrayList<Photo>
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var viewPagerAdapter: PhotoDetailAdapter
    private val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    var msg: String? = ""
    var lastMsg = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.photo_detail_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        arguments?.let {

            photoList =
                PhotoDetailFragmentArgs.fromBundle(it).photoModel.toList() as ArrayList<Photo>

            viewPagerAdapter = PhotoDetailAdapter(photoList, context, this)

            dataBinding.photoViewPager.apply {
                adapter = viewPagerAdapter
                currentItem = PhotoDetailFragmentArgs.fromBundle(it).position.toInt()
                addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(
                        position: Int, positionOffset: Float, positionOffsetPixels: Int
                    ) {

                    }

                    override fun onPageSelected(position: Int) {
                        if (position == photoList.size - 3) {
                            photoViewModel.fetchPhotos()
                        }
                    }

                })
            }


        }

        photoViewModel.photoList.observe(viewLifecycleOwner, Observer { photoList ->
            photoList?.let {
                viewPagerAdapter.updateList(photoList, photoViewModel.page)
            }
        })


    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(context)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos.")
                    .setPositiveButton("Accept") { dialog, id ->
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )

            }
        } else {
            downloadImage("https://farm${photoList[dataBinding.photoViewPager.currentItem].farm}.staticflickr.com/${photoList[dataBinding.photoViewPager.currentItem].server}/${photoList[dataBinding.photoViewPager.currentItem].id}_${photoList[dataBinding.photoViewPager.currentItem].secret}_.jpg"
                ,context,photoList[dataBinding.photoViewPager.currentItem].title)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            context?.let {
                downloadImage("https://farm${photoList[dataBinding.photoViewPager.currentItem].farm}.staticflickr.com/${photoList[dataBinding.photoViewPager.currentItem].server}/${photoList[dataBinding.photoViewPager.currentItem].id}_${photoList[dataBinding.photoViewPager.currentItem].secret}_.jpg"
                    , it,photoList[dataBinding.photoViewPager.currentItem].title)
            }

        }
    }

    override fun buttonClicked() {
        context?.let { askPermissions(it) }
    }


    fun downloadImage(url: String, context: Context, title: String) {

        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(title)
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    title
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        val job = CoroutineScope(Dispatchers.IO).launch {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }

                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }
    }


    private fun statusMessage(url: String, directory: File, status: Int): String? {

        return  when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
    }

}
