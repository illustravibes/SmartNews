package com.task.smartnews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var articleList: List<Article> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        articleAdapter = ArticleAdapter(articleList)
        recyclerView.adapter = articleAdapter

        databaseHelper = DatabaseHelper(this)
        databaseHelper.connectToDatabase()

        loadArticles()

        val fab: View = findViewById(R.id.fabAddArticle)
        fab.setOnClickListener {
            navigateToAddArticle()
        }
    }

    private fun loadArticles() {
        try {
            articleList = databaseHelper.getAllArticles()
            articleAdapter.submitList(articleList)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load articles", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }


    private fun navigateToAddArticle() {
        val intent = Intent(this, AddEditArticleActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.disconnectFromDatabase()
    }
}
