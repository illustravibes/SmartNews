package com.task.smartnews

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso

class ArticleDetailsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }

    private lateinit var article: Article
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var editArticleLauncher: ActivityResultLauncher<Intent>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        databaseHelper = DatabaseHelper(this)
        article = intent.getParcelableExtra<Article>(EXTRA_ARTICLE)!!

        displayArticleDetails()
        editArticleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Refresh the current article after update
                val updatedArticle = result.data?.getParcelableExtra<Article>(ArticleDetailsActivity.EXTRA_ARTICLE)
                if (updatedArticle != null) {
                    article = updatedArticle
                    displayArticleDetails() // Update the displayed details
                }
            }
        }
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh the article details
            refreshArticleDetails()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun refreshArticleDetails() {
        val updatedArticle = databaseHelper.getArticleById(article.id)
        if (updatedArticle != null) {
            article = updatedArticle
            displayArticleDetails()
        }
    }

    private fun displayArticleDetails() {
        val textViewTitle: TextView = findViewById(R.id.textTitle)
        val textViewContent: TextView = findViewById(R.id.textContent)
        val imageViewArticle: ImageView = findViewById(R.id.imageArticle)

        textViewTitle.text = article.title
        textViewContent.text = article.content

        if (article.imageUri.isNullOrEmpty()) {
            imageViewArticle.visibility = View.GONE
        } else {
            imageViewArticle.visibility = View.VISIBLE
            Picasso.get().load(article.imageUri).into(imageViewArticle)
        }
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
            R.id.menu_delete -> {
                deleteArticle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editArticle() {
        val intent = Intent(this, AddEditArticleActivity::class.java)
        intent.putExtra("ARTICLE_ID", article.id)
        editArticleLauncher.launch(intent)
    }

    private fun deleteArticle() {
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    databaseHelper.deleteArticle(article)
                    Toast.makeText(this, "Article deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    // Do nothing
                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this article?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
