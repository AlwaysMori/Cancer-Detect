package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.adapter.NewsAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.api.NewsViewModel
import android.content.Intent
import com.dicoding.asclepius.R
import android.view.MenuItem
import android.net.Uri


class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var progressBar: ProgressBar
    private lateinit var newsAdapter: NewsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initBottomNavigationView()
        initRecyclerView()
        progressBar = findViewById(R.id.progressBar)
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsViewModel.fetchHealthNews()
        newsViewModel.newsList.observe(this, Observer { newsList ->
            newsAdapter.submitList(newsList)
            progressBar.visibility = View.GONE
        })
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.menuBar)
        newsRecyclerView = findViewById(R.id.rvNewsList)
    }

    private fun initBottomNavigationView() {
        bottomNavigationView.selectedItemId = R.id.news
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.news -> {
                    true
                }
                R.id.history_menu -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNewsList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun openNewsUrl(view: View) {
        val url = view.getTag(R.id.tvLink) as? String
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
