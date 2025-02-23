package com.rats.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.TrainLineDetailViewModelFactory
import com.rats.ui.adapters.TrainLineReportAdapter
import com.rats.viewModels.TrainLineDetailViewModel

class TrainLineDetailActivity : AppCompatActivity() {
    private val trainLineDetailViewModel: TrainLineDetailViewModel by viewModels {
        TrainLineDetailViewModelFactory((application as RatsApp).trainLinesRepository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainLineName: TextView
    private lateinit var subscribeIcon: ImageView
    private lateinit var statusIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_train_line)

        recyclerView = findViewById(R.id.recyclerView)
        trainLineName = findViewById(R.id.tv_train_line_name)
        subscribeIcon = findViewById(R.id.subscribe_icon)
        statusIcon = findViewById(R.id.status_icon)

        recyclerView.layoutManager = LinearLayoutManager(this)

        trainLineDetailViewModel.reports.observe(this) { reports ->
            recyclerView.adapter = TrainLineReportAdapter(reports)
        }

        trainLineDetailViewModel.title.observe(this) { title ->
            trainLineName.text = title
        }

        trainLineDetailViewModel.subscribed.observe(this) { status ->
            subscribeIcon.visibility = View.VISIBLE
            if (status == true) {
                subscribeIcon.setImageResource(R.drawable.bookmark_filled_icon)
            } else if (status == false) {
                subscribeIcon.setImageResource(R.drawable.bookmark_transparent_icon)
            } else {
                subscribeIcon.visibility = View.GONE
            }
        }

        // TODO: mettre la vrai icone pour incident
        trainLineDetailViewModel.status.observe(this) { status ->
            subscribeIcon.visibility = View.VISIBLE
            if (status == "Sûr") {
                statusIcon.setImageResource(R.drawable.safe_notification)
            } else if (status == "Incident") {
                statusIcon.setImageResource(R.drawable.warning_notification)
            } else if (status == "Dangereux") {
                statusIcon.setImageResource(R.drawable.warning_notification)
            } else {
                statusIcon.visibility = View.GONE
            }
        }

        val lineId = intent.getIntExtra("id", -1)
        subscribeIcon.setOnClickListener {
            trainLineDetailViewModel.toggleSubscription(lineId)
        }
        if (lineId != -1) {
            trainLineDetailViewModel.fetchTrainLineById(lineId)
        } else {
            trainLineDetailViewModel.fetchSubscribedReports()
        }
    }
}
