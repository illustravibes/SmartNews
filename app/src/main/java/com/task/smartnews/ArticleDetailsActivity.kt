package com.task.smartnews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArticleDetailsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }

    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        article = intent.getParcelableExtra(EXTRA_ARTICLE)!!
        displayArticleDetails()
    }

    private fun displayArticleDetails() {
        val textViewTitle: TextView = findViewById(R.id.textTitle)
        val textViewContent: TextView = findViewById(R.id.textContent)

        textViewTitle.text = article.title
        textViewContent.text = article.content
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                editArticle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editArticle() {
        val intent = Intent(this, AddEditArticleActivity::class.java)
        intent.putExtra("ARTICLE_ID", article.id)
        startActivity(intent)
    }
}
