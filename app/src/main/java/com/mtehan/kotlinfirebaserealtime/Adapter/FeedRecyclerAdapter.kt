package com.mtehan.kotlinfirebaserealtime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtehan.kotlinfirebaserealtime.R
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private val emailArray: ArrayList<String>,private val commentArray: ArrayList<String>,private val imageArray: ArrayList<String>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_reyclerview, parent,false)
        return PostsHolder(view)
    }

    override fun getItemCount(): Int {
       return emailArray.size
    }

    override fun onBindViewHolder(holder: PostsHolder, position: Int) {
        holder.recyclerEmail?.text=emailArray[position]
        holder.recyclerComment?.text=commentArray[position]
        Picasso.get().load(imageArray[position]).into(holder.recyclerImageView);
    }

    class PostsHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recyclerEmail: TextView? = null
        var recyclerImageView: ImageView? = null
        var recyclerComment: TextView? = null

        init {
            recyclerEmail = view.findViewById(R.id.txtEmail)
            recyclerImageView = view.findViewById(R.id.imageView)
            recyclerComment = view.findViewById(R.id.txtComment)
        }
    }
}