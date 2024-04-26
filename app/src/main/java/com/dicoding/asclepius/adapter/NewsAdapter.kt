package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.api.NewsItem

class NewsAdapter : ListAdapter<NewsItem, NewsAdapter.NewsViewHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = getItem(position)
        holder.bind(newsItem)
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val newsImageView: ImageView = itemView.findViewById(R.id.imgNews)

        fun bind(newsItem: NewsItem) {
            titleTextView.text = newsItem.title
            itemView.findViewById<TextView>(R.id.tvLink).apply {
                text = "Baca Selengkapnya"
                setTag(R.id.tvLink, newsItem.url)
                visibility = if (newsItem.url != null) View.VISIBLE else View.GONE
            }
            if (!newsItem.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(newsItem.imageUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(newsImageView)
            } else {
                newsImageView.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_place_holder))
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<NewsItem>() {
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }
    }
}
