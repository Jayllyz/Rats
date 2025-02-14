package com.rats.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.TrainLineDetailViewModelFactory
import com.rats.factories.TrainLinesViewModelFactory
import com.rats.ui.adapters.TrainLineReportAdapter
import com.rats.viewModels.TrainLineDetailViewModel
import com.rats.viewModels.TrainLinesViewModel

class TrainLineDetailActivity : AppCompatActivity() {
    private val trainLineDetailViewModel: TrainLineDetailViewModel by viewModels {
        TrainLineDetailViewModelFactory((application as RatsApp).trainLinesRepository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainLineName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_train_line)

        recyclerView = findViewById(R.id.recyclerView)
        trainLineName = findViewById(R.id.tv_train_line_name)

        recyclerView.layoutManager = LinearLayoutManager(this)

        trainLineDetailViewModel.reports.observe(this) { reports ->
            recyclerView.adapter = TrainLineReportAdapter(reports)
        }

        trainLineDetailViewModel.title.observe(this) { title ->
            trainLineName.text = title
        }

        val lineId = intent.getIntExtra("id", -1)
        trainLineDetailViewModel.fetchTrainLineById(lineId)
    }
}
