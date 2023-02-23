package com.example.paging

import androidx.recyclerview.widget.RecyclerView
import com.example.paging.databinding.ItemViewBinding

class MyViewHolder (private val binding:ItemViewBinding) :RecyclerView.ViewHolder(binding.root) {
    fun bind(repo: Result){
        binding.apply {
            result=repo
        }
    }
}
