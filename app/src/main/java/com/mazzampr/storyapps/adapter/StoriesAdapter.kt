package com.mazzampr.storyapps.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mazzampr.storyapps.data.remote.response.ListStoryItem
import com.mazzampr.storyapps.databinding.StoryItemBinding

class StoriesAdapter: RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    lateinit var onItemClick: ((ListStoryItem)->Unit)
    inner class ViewHolder(val binding: StoryItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private val diffUtil: DiffUtil.ItemCallback<ListStoryItem> =
        object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = differ.currentList[position]
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

}