package com.dicoding.asclepius.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import com.dicoding.asclepius.adapter.PredictionHistoryAdapter
import com.dicoding.asclepius.history.PredictionHistory
import kotlinx.coroutines.launch
import com.dicoding.asclepius.R
import android.widget.TextView
import com.dicoding.asclepius.history.AppDatabase
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.dicoding.asclepius.view.ResultActivity.Companion.REQUEST_HISTORY_UPDATE
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.GlobalScope


class HistoryActivity : AppCompatActivity(), PredictionHistoryAdapter.OnDeleteClickListener {

    // View components
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var predictionRecyclerView: RecyclerView
    private lateinit var tvNotFound: TextView

    // Data
    private var predictionList: MutableList<PredictionHistory> = mutableListOf()

    // Adapter
    private lateinit var predictionAdapter: PredictionHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize view components
        bottomNavigationView = findViewById(R.id.menuBar)
        predictionRecyclerView = findViewById(R.id.rvHistory)
        tvNotFound = findViewById(R.id.tvNotFound)

        // Set bottom navigation selected item and listener
        bottomNavigationView.selectedItemId = R.id.history_menu
        bottomNavigationView.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)

        // Initialize RecyclerView and adapter
        predictionAdapter = PredictionHistoryAdapter(predictionList)
        predictionAdapter.setOnDeleteClickListener(this)
        predictionRecyclerView.adapter = predictionAdapter
        predictionRecyclerView.layoutManager = LinearLayoutManager(this)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load prediction history from database
        loadPredictionHistoryFromDatabase()
    }

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.news -> startActivity(Intent(this, NewsActivity::class.java))
        }
        true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_HISTORY_UPDATE && resultCode == RESULT_OK) {
            loadPredictionHistoryFromDatabase()
        }
    }

    private fun loadPredictionHistoryFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val predictions = AppDatabase.getDatabase(this@HistoryActivity).predictionHistoryDao().getAllPredictions()
            Log.d(TAG, "Number of predictions: ${predictions.size}")
            predictionList.clear()
            predictionList.addAll(predictions)
            predictionAdapter.notifyDataSetChanged()
            showOrHideNoHistoryText()
        }
    }

    private fun showOrHideNoHistoryText() {
        if (predictionList.isEmpty()) {
            tvNotFound.visibility = View.VISIBLE
            predictionRecyclerView.visibility = View.GONE
        } else {
            tvNotFound.visibility = View.GONE
            predictionRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDeleteClick(position: Int) {
        val prediction = predictionList[position]
        if (prediction.result.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(this@HistoryActivity).predictionHistoryDao().deletePrediction(prediction)
            }
            predictionList.removeAt(position)
            predictionAdapter.notifyDataSetChanged()
            showOrHideNoHistoryText()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
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

    companion object {
        const val TAG = "historydata"
    }
}
