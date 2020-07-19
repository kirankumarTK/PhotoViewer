package com.example.photoviewer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoviewer.R
import com.example.photoviewer.databinding.PhotoListFragmentBinding
import com.example.photoviewer.model.Photo
import com.example.photoviewer.utills.SpacesItemDecoration
import com.example.photoviewer.utills.isNetAvailable
import com.example.photoviewer.viewmodel.PhotoViewModel

class PhotoListFragment : Fragment() {
    private lateinit var dataBinding: PhotoListFragmentBinding
    private lateinit var photoViewModel: PhotoViewModel
    private val photoAdapter = PhotoListAdapter(ArrayList<Photo>(), 1)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.photo_list_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoViewModel = ViewModelProvider(this).get(PhotoViewModel::class.java)
        dataBinding.photoRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(SpacesItemDecoration(10))
            adapter = photoAdapter
        }
        if (context?.let { isNetAvailable(it) }!!)
            photoViewModel.fetchPhotos()
        else {
            Toast.makeText(context, "Check you internet connectivity", Toast.LENGTH_SHORT).show()
            dataBinding.progressBar.visibility = View.GONE
            dataBinding.errorText.visibility = View.VISIBLE
            dataBinding.errorText.text = resources.getString(R.string.something_went_wrong)
            dataBinding.photoRecyclerView.visibility = View.GONE
        }
        photoViewModel.photoList.observe(viewLifecycleOwner, Observer { photoList ->
            photoList?.let {
                dataBinding.photoRecyclerView.visibility = View.VISIBLE
                dataBinding.errorText.visibility = View.GONE
                dataBinding.progressBar.visibility = View.GONE
                dataBinding.photoSwipeLayout.isRefreshing = false
                photoAdapter.updateList(photoList, photoViewModel.page)
            }
        })

        photoViewModel.isError.observe(viewLifecycleOwner, Observer { isError ->
            dataBinding.progressBar.visibility = View.GONE
            dataBinding.errorText.visibility = View.VISIBLE
            dataBinding.errorText.text = resources.getString(R.string.something_went_wrong)
            dataBinding.photoRecyclerView.visibility = View.GONE
        })
        dataBinding.photoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {

                    val layoutManager =
                        dataBinding.photoRecyclerView.layoutManager as GridLayoutManager
                    val visibleItemCount = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    if (visibleItemCount == layoutManager.itemCount && photoViewModel.total > layoutManager.itemCount) {
                        photoViewModel.fetchPhotos()
                    }


                }
            }
        })

        dataBinding.photoSwipeLayout.setOnRefreshListener {
            photoAdapter.clearView()
            photoViewModel.page = 1
            photoViewModel.fetchPhotos()
            dataBinding.progressBar.visibility = View.GONE
            dataBinding.photoRecyclerView.visibility = View.GONE
        }
        dataBinding.errorText.setOnClickListener {
            photoViewModel.fetchPhotos()
            dataBinding.errorText.visibility = View.GONE
            dataBinding.progressBar.visibility = View.VISIBLE
        }
    }
}