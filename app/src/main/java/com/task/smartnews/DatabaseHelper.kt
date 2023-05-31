package com.task.smartnews

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class DatabaseHelper(private val context: Context) {
    private val host = "10.113.86.187"
    private val port = "3306"
    private val databaseName = "smartnews"
    private val username = "root" // Replace with your MySQL username
    private val password = ""     // Replace with your MySQL password

    private lateinit var connection: Connection
    private var isConnected = false

    fun connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver")
            val url = "jdbc:mysql://$host:$port/$databaseName"
            connection = DriverManager.getConnection(url, username, password)
            isConnected = true
            showToast("Connected to the database server")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            isConnected = false
            showToast("Failed to connect to the database server")
        } catch (e: SQLException) {
            e.printStackTrace()
            isConnected = false
            showToast("Failed to connect to the database server")
        }
    }

    fun disconnectFromDatabase() {
        try {
            if (::connection.isInitialized && !connection.isClosed) {
                connection.close()
                isConnected = false
                showToast("Disconnected from the database server")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun createArticle(article: Article) {
        GlobalScope.launch(Dispatchers.IO) {
            connectToDatabase()

            if (isConnected) {
                val query = "INSERT INTO articles (title, content, image_url) VALUES (?, ?, ?)"

                val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                preparedStatement.setString(1, article.title)
                preparedStatement.setString(2, article.content)
                preparedStatement.setString(3, article.imageUrl)
                preparedStatement.executeUpdate()

                showToast("Article created successfully")
            } else {
                showToast("Failed to create article. Not connected to the database")
            }

            disconnectFromDatabase()
        }
    }

    fun getArticleById(id: Int): Article? {
        connectToDatabase()

        val query = "SELECT * FROM articles WHERE id = ?"

        var article: Article? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, id)
            resultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val articleId = resultSet.getInt("id")
                val title = resultSet.getString("title")
                val content = resultSet.getString("content")
                val imageUrl = resultSet.getString("image_url")

                article = Article(articleId, title, content, imageUrl)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            showToast("Failed to retrieve article from the database")
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            disconnectFromDatabase()
        }

        return article
    }


    fun getAllArticles(): List<Article> {
        val articles = mutableListOf<Article>()

        try {
            connectToDatabase()
            val statement = connection.createStatement()
            val query = "SELECT * FROM articles"
            val resultSet = statement.executeQuery(query)

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val title = resultSet.getString("title")
                val content = resultSet.getString("content")
                val imageUrl = resultSet.getString("image_url")
                val article = Article(id, title, content, imageUrl)
                articles.add(article)
            }

            resultSet.close()
            statement.close()
            disconnectFromDatabase()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return articles
    }

    fun updateArticle(article: Article) {
        connectToDatabase()

        val query = "UPDATE articles SET title = ?, content = ? WHERE id = ?"

        val preparedStatement: PreparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, article.title)
        preparedStatement.setString(2, article.content)
        preparedStatement.setInt(3, article.id)
        preparedStatement.executeUpdate()

        disconnectFromDatabase()
    }

    fun deleteArticle(article: Article) {
        connectToDatabase()

        val query = "DELETE FROM articles WHERE id = ?"

        val preparedStatement: PreparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, article.id)
        preparedStatement.executeUpdate()

        disconnectFromDatabase()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
