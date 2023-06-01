package com.task.smartnews

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "smartnews.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ARTICLES = "articles"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"

        private const val COLUMN_IMAGE_BLOB = "image_blob"

        private const val CREATE_TABLE_QUERY = "CREATE TABLE $TABLE_ARTICLES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_CONTENT TEXT," +
                "$COLUMN_IMAGE_BLOB BLOB" +
                ")"

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTICLES")
        onCreate(db)
    }

    fun createArticle(article: Article): Long {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, article.title)
            put(COLUMN_CONTENT, article.content)
            put(COLUMN_IMAGE_BLOB, article.imageBlob)
        }

        return writableDatabase.use { db ->
            db.insert(TABLE_ARTICLES, null, values)
        }
    }


    fun getArticleById(id: Int): Article? {
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())

        readableDatabase.use { db ->
            db.query(TABLE_ARTICLES, null, selection, selectionArgs, null, null, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                    val imageBlob = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_BLOB))
                    return Article(id, title, content, imageBlob)
                }
            }
        }

        return null
    }

    fun getAllArticles(): List<Article> {
        val articles = mutableListOf<Article>()

        readableDatabase.use { db ->
            db.query(TABLE_ARTICLES, null, null, null, null, null, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                    val imageBlob = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_BLOB))
                    articles.add(Article(id, title, content, imageBlob))
                }
            }
        }

        return articles
    }

    fun updateArticle(article: Article): Int {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, article.title)
            put(COLUMN_CONTENT, article.content)
            put(COLUMN_IMAGE_BLOB, article.imageBlob)
        }

        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(article.id.toString())

        return writableDatabase.use { db ->
            db.update(TABLE_ARTICLES, values, whereClause, whereArgs)
        }
    }

    fun deleteArticle(article: Article): Int {
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(article.id.toString())

        return writableDatabase.use { db ->
            db.delete(TABLE_ARTICLES, whereClause, whereArgs)
        }
    }
}
