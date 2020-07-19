package com.example.photoviewer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.photoviewer.R
import com.example.photoviewer.databinding.PhotoListItemBinding
import com.example.photoviewer.model.Photo
import com.example.photoviewer.utills.getProgressDrawable
import com.example.photoviewer.utills.loadImage

class PhotoListAdapter(private val photoList: ArrayList<Photo>, private var page: Int) :
    RecyclerView.Adapter<PhotoListAdapter.PhotoViewHolder>() {


    class PhotoViewHolder(var view: PhotoListItemBinding) : RecyclerView.ViewHolder(view.root)


    override fun getItemCount() = photoList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.view.photoImageView.loadImage(
            "https://farm${photoList[position].farm}.staticflickr.com/${photoList[position].server}/${photoList[position].id}_${photoList[position].secret}_q.jpg",
            getProgressDrawable(holder.view.photoImageView.context)
        )

        holder.view.root.setOnClickListener {
            Navigation.findNavController(it).navigate(
                PhotoListFragmentDirections.actionPhotoListFragmentToPhotoDetailFragment(
                    position.toLong(),
                    page.toLong(),
                    photoList.toTypedArray()
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PhotoViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.photo_list_item,
            parent,
            false
        )
    )

    fun updateList(tempList: ArrayList<Photo>, pageTemp: Int) {
        photoList.addAll(tempList)
        page = pageTemp
        tempList.clear()
        notifyDataSetChanged()
    }

    fun clearView() {
        photoList.clear()
        page = 1
        notifyDataSetChanged()
    }

}