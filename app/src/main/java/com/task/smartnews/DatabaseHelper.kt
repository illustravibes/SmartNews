package com.task.smartnews

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "smartnews.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ARTICLES = "articles"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_ARTICLES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_IMAGE_URL TEXT" +
                ")"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTICLES")
        onCreate(db)
    }

    fun createArticle(article: Article) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, article.title)
        values.put(COLUMN_CONTENT, article.content)
        values.put(COLUMN_IMAGE_URL, article.image_url)
        val id = db.insert(TABLE_ARTICLES, null, values)
        db.close()

        if (id != -1L) {
            showToast("Article created successfully")
        } else {
            showToast("Failed to create article")
        }
    }

    fun getArticleById(id: Int): Article? {
        val db = readableDatabase
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(TABLE_ARTICLES, null, selection, selectionArgs, null, null, null)

        var article: Article? = null
        if (cursor != null && cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            val imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL))
            article = Article(id, title, content, imageUrl)
        }
        cursor?.close()
        db.close()
        return article
    }

    fun getAllArticles(): List<Article> {
        val articles = mutableListOf<Article>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ARTICLES, null, null, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL))
                val article = Article(id, title, content, imageUrl)
                articles.add(article)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        db.close()
        return articles
    }

    fun updateArticle(article: Article) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, article.title)
        values.put(COLUMN_CONTENT, article.content)
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(article.id.toString())
        val rowsAffected = db.update(TABLE_ARTICLES, values, whereClause, whereArgs)
        db.close()

        if (rowsAffected > 0) {
            showToast("Article updated successfully")
        } else {
            showToast("Failed to update article")
        }
    }

    fun deleteArticle(article: Article) {
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(article.id.toString())
        val rowsAffected = db.delete(TABLE_ARTICLES, whereClause, whereArgs)
        db.close()

        if (rowsAffected > 0) {
            showToast("Article deleted successfully")
        } else {
            showToast("Failed to delete article")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
