package com.example.photoviewer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.photoviewer.R
import com.example.photoviewer.databinding.PhotoDetailFragmentBinding
import com.example.photoviewer.model.Photo
import com.example.photoviewer.viewmodel.PhotoViewModel

class PhotoDetailFragment : Fragment() {
    private lateinit var dataBinding: PhotoDetailFragmentBinding
    private lateinit var photoList: ArrayList<Photo>
    private lateinit var photoViewModel: PhotoViewModel
    private lateinit var viewPagerAdapter: PhotoDetailAdapter

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
            dataBinding.photoViewPager.apply {
                viewPagerAdapter = PhotoDetailAdapter(photoList, context)
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


}
