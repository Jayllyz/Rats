package com.rats.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.TrainLinesViewModelFactory
import com.rats.ui.adapters.TrainLinesAdapter
import com.rats.viewModels.TrainLinesViewModel

class TrainLinesActivity : AppCompatActivity() {
    private val trainLinesViewModel: TrainLinesViewModel by viewModels {
        TrainLinesViewModelFactory((application as RatsApp).trainLinesRepository)
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transport_lines)
        setupViews()
        setupStateObservers()
        trainLinesViewModel.fetchTrainLines()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupStateObservers() {
        trainLinesViewModel.lines.observe(this) { trainLineStates ->
            recyclerView.adapter = TrainLinesAdapter(trainLineStates)
        }
    }
}
