package com.task.smartnews

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditArticleActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var imageUrlEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var databaseHelper: DatabaseHelper

    private var isEditMode: Boolean = false
    private var editedArticle: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_article)

        titleEditText = findViewById(R.id.editTextTitle)
        contentEditText = findViewById(R.id.editTextContent)
        imageUrlEditText = findViewById(R.id.editTextImageUrl)
        saveButton = findViewById(R.id.buttonSave)

        databaseHelper = DatabaseHelper(this)

        isEditMode = intent.getBooleanExtra("editMode", false)
        if (isEditMode) {
            title = "Edit Article"
            editedArticle = intent.getSerializableExtra("article") as? Article
            populateFieldsWithArticleData()
        } else {
            title = "Add Article"
        }

        saveButton.setOnClickListener {
            saveArticle()
        }
    }

    private fun populateFieldsWithArticleData() {
        editedArticle?.let {
            titleEditText.setText(it.title)
            contentEditText.setText(it.content)
            imageUrlEditText.setText(it.image_url)
        }
    }

    private fun saveArticle() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()
        val image_url = imageUrlEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            showToast("Title and content cannot be empty")
            return
        }

        val article = Article(
            id = editedArticle?.id ?: 0,
            title = title,
            content = content,
            image_url = image_url
        )

        if (isEditMode) {
            databaseHelper.updateArticle(article)
            showToast("Article updated successfully")
        } else {
            databaseHelper.createArticle(article)
            showToast("Article created successfully")
        }

        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
