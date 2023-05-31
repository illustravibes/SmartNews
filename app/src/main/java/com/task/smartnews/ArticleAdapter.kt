package com.task.smartnews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ArticleAdapter(private var articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = articles[position]
        holder.bind(currentArticle)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textContent: TextView = itemView.findViewById(R.id.textContent)
        private val imageArticle: ImageView = itemView.findViewById(R.id.imageArticle)

        fun bind(article: Article) {
            textTitle.text = article.title
            textContent.text = article.content

            // Set the article image if available
            if (!article.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(article.imageUrl).into(imageArticle)
            } else {
                imageArticle.visibility = View.GONE
            }

            itemView.setOnClickListener {
                // Invoke the onItemClick method of the listener and pass the clicked article
                listener?.onItemClick(article)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun submitList(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }
}
