@file:Suppress("DEPRECATION")

package com.example.photoviewer.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.photoviewer.R

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = context.resources.getDimension(R.dimen._1sdp)
        centerRadius = context.resources.getDimension(R.dimen._5sdp)
        start()
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.ic_launcher_background)
//        .transform(CenterInside(),RoundedCorners(10))
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}


fun checkIsNetAvailable(con: Context): Boolean {
    val cm = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting == true
}


private fun checkTheNetworkAvailability(con: Context): Boolean {
    val myconnectivity = con
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val myinfo = myconnectivity.allNetworkInfo
    for (aMyinfo in myinfo) {
        if (aMyinfo.state == NetworkInfo.State.CONNECTED) {
            return true
        }
    }
    return false
}


fun isNetAvailable(con: Context) = checkIsNetAvailable(con) && checkTheNetworkAvailability(con)
