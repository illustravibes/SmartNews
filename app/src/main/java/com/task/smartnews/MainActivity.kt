package com.task.smartnews
import ArticleAdapter
import android.content.Intent
import android.net.http.NetworkException
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DatabaseException

class MainActivity : AppCompatActivity(), ArticleAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var articleList: List<Article> = emptyList()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fabAddArticle: View
    private lateinit var addArticleLauncher: ActivityResultLauncher<Intent>


    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        fabAddArticle = findViewById(R.id.fabAddArticle)

        setupRecyclerView()
        setupDatabaseHelper()
        setupSwipeRefreshLayout()
        setupAddArticleButton()

        addArticleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadArticles()
            }
        }

        loadArticles()
    }

    @RequiresApi(34)
    override fun onResume() {
        super.onResume()
        loadArticles()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        articleAdapter = ArticleAdapter(articleList)
        recyclerView.adapter = articleAdapter
        articleAdapter.setOnItemClickListener(this)
    }

    private fun setupDatabaseHelper() {
        databaseHelper = DatabaseHelper(this)
    }

    @RequiresApi(34)
    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            loadArticles()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupAddArticleButton() {
        fabAddArticle.setOnClickListener {
            navigateToAddArticle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }

    override fun onItemClick(article: Article) {
        val intent = Intent(this, ArticleDetailsActivity::class.java)
        intent.putExtra(ArticleDetailsActivity.EXTRA_ARTICLE, article)
        addArticleLauncher.launch(intent)
    }


    @RequiresApi(34)
    private fun loadArticles() {
        try {
            articleList = databaseHelper.getAllArticles()
            articleAdapter.submitList(articleList)
            articleAdapter.notifyDataSetChanged() // Add this line to refresh the RecyclerView
        } catch (e: DatabaseException) {
            Toast.makeText(this, "Failed to load articles from the database", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: NetworkException) {
            Toast.makeText(this, "Failed to load articles due to network issues", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load articles", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun navigateToAddArticle() {
        val intent = Intent(this, AddEditArticleActivity::class.java)
        addArticleLauncher.launch(intent)
    }
}
