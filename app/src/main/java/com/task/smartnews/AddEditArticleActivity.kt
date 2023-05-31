package com.task.smartnews

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditArticleActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var editTextImageUrl: EditText
    private lateinit var buttonSave: Button

    private var isEditMode = false
    private var articleId = 0
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_article)

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        editTextImageUrl = findViewById(R.id.editTextImageUrl)
        buttonSave = findViewById(R.id.buttonSave)

        // Check if it's in edit mode or add mode
        if (intent.hasExtra("ARTICLE_ID")) {
            isEditMode = true
            articleId = intent.getIntExtra("ARTICLE_ID", 0)
            supportActionBar?.title = "Edit Article"
            loadArticleData()
        } else {
            supportActionBar?.title = "Add Article"
        }

        databaseHelper = DatabaseHelper(this)
        databaseHelper.connectToDatabase()

        // Set click listener for the save button
        buttonSave.setOnClickListener {
            saveArticle()
        }
    }

    private fun loadArticleData() {
        val article = databaseHelper.getArticleById(articleId)
        if (article != null) {
            editTextTitle.setText(article.title)
            editTextContent.setText(article.content)
            editTextImageUrl.setText(article.imageUrl)
        }
    }

    private fun saveArticle() {
        val title = editTextTitle.text.toString().trim()
        val content = editTextContent.text.toString().trim()
        val imageUrl = editTextImageUrl.text.toString().trim()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            if (isEditMode) {
                // Update existing article
                val article = Article(articleId, title, content, imageUrl)
                databaseHelper.updateArticle(article)
                Toast.makeText(this, "Article updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                // Add new article
                val article = Article(0, title, content, imageUrl)
                databaseHelper.createArticle(article)
                Toast.makeText(this, "Article added successfully", Toast.LENGTH_SHORT).show()
            }

            finish() // Close the activity
        } else {
            Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.disconnectFromDatabase()
    }
}
