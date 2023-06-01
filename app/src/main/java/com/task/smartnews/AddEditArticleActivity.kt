package com.task.smartnews

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.task.smartnews.databinding.ActivityAddEditArticleBinding

class AddEditArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditArticleBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var articleId: Int = -1

    companion object {
        private const val REQUEST_IMAGE_PICKER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        val extras = intent.extras
        if (extras != null) {
            articleId = extras.getInt("ARTICLE_ID", -1)
            if (articleId != -1) {
                supportActionBar?.title = "Edit Article"
                loadArticle()
            } else {
                supportActionBar?.title = "Add Article"
            }
        }

        binding.buttonSave.setOnClickListener {
            saveArticle()
        }

        binding.buttonUploadImage.setOnClickListener {
            openImagePicker()
        }
    }

    private fun loadArticle() {
        val article = databaseHelper.getArticleById(articleId)
        if (article != null) {
            binding.editTextTitle.setText(article.title)
            binding.editTextContent.setText(article.content)
            binding.textViewSelectedImage.text = article.imageUri
        }
    }

    private fun saveArticle() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        val imageUri = binding.textViewSelectedImage.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            return
        }

        val article = Article(id = articleId, title = title, content = content, imageUri = imageUri)

        if (articleId != -1) {
            databaseHelper.updateArticle(article)
            Toast.makeText(this, "Article updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            databaseHelper.createArticle(article)
            Toast.makeText(this, "Article saved successfully", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                binding.textViewSelectedImage.text = imageUri.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }
}
