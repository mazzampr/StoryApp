package com.mazzampr.storyapps.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mazzampr.storyapps.data.remote.response.ListStoryItem
import com.mazzampr.storyapps.databinding.StoryItemBinding

class StoriesAdapter: PagingDataAdapter<ListStoryItem, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {
    lateinit var onItemClick: ((ListStoryItem)->Unit)
    inner class ViewHolder(val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

//    private val diffUtil: DiffUtil.ItemCallback<ListStoryItem> =
//        object : DiffUtil.ItemCallback<ListStoryItem>() {
//            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
//                return oldItem.id == newItem.id
//            }
//
//            @SuppressLint("DiffUtilEquals")
//            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
//                return oldItem == newItem
//            }
//        }
//
//    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            Glide.with(holder.itemView)
                .load(storyItem.photoUrl)
                .into(holder.binding.ivDetailPhoto)

            holder.itemView.setOnClickListener {
                    onItemClick.invoke(storyItem)
            }

            holder.binding.apply {
                tvItemName.text = storyItem.name
                tvDetailDescription.text = storyItem.description
            }
        }
        Log.d("Adapter, storyitem", storyItem.toString())
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}