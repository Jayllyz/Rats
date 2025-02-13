package com.rats.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.RatsApp
import com.rats.factories.TrainLinesViewModelFactory
import com.rats.ui.adapters.TrainLinesAdapter
import com.rats.viewModels.TrainLinesUiState
import com.rats.viewModels.TrainLinesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TrainLinesActivity : AppCompatActivity() {
    private val trainLinesViewModel: TrainLinesViewModel by viewModels {
        TrainLinesViewModelFactory((application as RatsApp).trainLinesRepository)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var filterSpinner: Spinner
    private lateinit var searchBar: SearchView

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

        filterSpinner = findViewById(R.id.filterTrainLine)
        filterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    trainLinesViewModel.setFilter(selectedItem)
                    trainLinesViewModel.fetchTrainLines()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        searchBar = findViewById(R.id.searchTrainLine)
        searchBar.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    trainLinesViewModel.setSearch(query ?: "")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    trainLinesViewModel.handleSearchQueryChange(newText)
                    return false
                }
            },
        )
    }

    private fun setupStateObservers() {
        lifecycleScope.launch {
            trainLinesViewModel.uiState.collectLatest { state ->
                when (state) {
                    is TrainLinesUiState.Loading -> {
                    }
                    is TrainLinesUiState.Success -> {
                        recyclerView.adapter = TrainLinesAdapter(state.lines)
                    }
                    is TrainLinesUiState.Error -> {
                    }
                }
            }
        }
    }
}
