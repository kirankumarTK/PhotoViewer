package com.example.photoviewer.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.photoviewer.R
import com.example.photoviewer.model.Photo
import com.example.photoviewer.utills.DownloadImage
import com.example.photoviewer.utills.TouchImageView
import com.example.photoviewer.utills.getProgressDrawable
import com.example.photoviewer.utills.loadImage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class PhotoDetailAdapter(
    private val photoList: ArrayList<Photo>,
    private val context: Context?,
    val listener: DownloadImage
) :
    PagerAdapter() {

    override fun isViewFromObject(view: View, agr: Any) = view == agr


    override fun getCount() = photoList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            (context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.photo_detail_list_item,
                container,
                false
            )
        val photoView: TouchImageView = itemView.findViewById(R.id.photo_detail_image)
        val title: TextView = itemView.findViewById(R.id.titleTxt)
        val fab: FloatingActionButton = itemView.findViewById(R.id.floatingActionButton)

        title.text = photoList[position].title

        photoView.loadImage(
            "https://farm${photoList[position].farm}.staticflickr.com/${photoList[position].server}/${photoList[position].id}_${photoList[position].secret}_c.jpg",
            getProgressDrawable(photoView.context)
        )
        fab.setOnClickListener {
            listener.buttonClicked()
        }

        container.addView(itemView)
        return itemView
    }

    override fun getPageWidth(position: Int): Float {
        return 1.0f
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    fun updateList(photoListTemp: ArrayList<Photo>, pageTemp: Int) {
        photoList.addAll(photoListTemp)
        photoListTemp.clear()
        notifyDataSetChanged()
    }



}