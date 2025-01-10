package com.example.currencyconverter

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.data.ApiClient
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.databinding.ActivityTravelEstimatorListBinding
import com.example.currencyconverter.viewmodel.TravelEstimatorViewModel
import com.example.currencyconverter.viewmodel.TravelEstimatorViewModelFactory

class TravelEstimatorListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTravelEstimatorListBinding
    private lateinit var adapter: TravelEstimatorAdapter

    private val viewModel: TravelEstimatorViewModel by viewModels {
        val repository = CurrencyRepository(ApiClient.api)
        TravelEstimatorViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelEstimatorListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = TravelEstimatorAdapter(
            onCurrencySelected = { estimator, targetCurrency ->
                viewModel.convertTotalCost(estimator, targetCurrency)
            },
            onCheckBoxClicked = { estimator ->
                viewModel.updateEstimator(estimator)
            }
        )

        binding.rvTravelEstimator.apply {
            layoutManager = LinearLayoutManager(this@TravelEstimatorListActivity)
            adapter = this@TravelEstimatorListActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnAdd.setOnClickListener {
                val destination = tietDestination.text.toString()
                val duration = tietDuration.text.toString().toIntOrNull()
                val dailyBudget = tietDailyBudget.text.toString().toDoubleOrNull()
                val baseCurrency = spBaseCurrency.selectedItem.toString()

                if (destination.isEmpty() || duration == null || dailyBudget == null) {
                    Toast.makeText(
                        this@TravelEstimatorListActivity,
                        "Please fill all fields correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.isCurrencySupported(baseCurrency) { isSupported ->
                    if (isSupported) {
                        viewModel.addEstimator(destination, duration, dailyBudget, baseCurrency)
                        runOnUiThread { clearInputs() }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@TravelEstimatorListActivity,
                                "Unsupported currency: $baseCurrency",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            btnDelete.setOnClickListener {
                viewModel.deleteSelectedEstimators()
            }

            btnBackToMain.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupObservers() {
        viewModel.estimators.observe(this) { estimators ->
            adapter.updateEstimators(estimators)
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        binding.apply {
            tietDestination.text?.clear()
            tietDuration.text?.clear()
            tietDailyBudget.text?.clear()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
