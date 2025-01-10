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

    // Set up the ViewModel for managing data and actions
    private val viewModel: TravelEstimatorViewModel by viewModels {
        val repository = CurrencyRepository(ApiClient.api)
        TravelEstimatorViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelEstimatorListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView() // Prepare the list of travel estimators
        setupListeners()    // Set up button actions
        setupObservers()    // Watch for changes in the data
    }

    // Set up the RecyclerView to display the list of estimators
    private fun setupRecyclerView() {
        adapter = TravelEstimatorAdapter(
            // Handle currency conversion when the user selects a new currency
            onCurrencySelected = { estimator, targetCurrency ->
                viewModel.convertTotalCost(estimator, targetCurrency)
            },
            // Handle updates to an estimator when the checkbox is clicked
            onCheckBoxClicked = { estimator ->
                viewModel.updateEstimator(estimator)
            }
        )

        // Connect the RecyclerView to the adapter and set the layout
        binding.rvTravelEstimator.apply {
            layoutManager = LinearLayoutManager(this@TravelEstimatorListActivity)
            adapter = this@TravelEstimatorListActivity.adapter
        }
    }

    // Set up button actions like adding or deleting estimators
    private fun setupListeners() {
        binding.apply {
            btnAdd.setOnClickListener {
                // Get user input from the text fields and dropdown
                val destination = tietDestination.text.toString()
                val duration = tietDuration.text.toString().toIntOrNull()
                val dailyBudget = tietDailyBudget.text.toString().toDoubleOrNull()
                val baseCurrency = spBaseCurrency.selectedItem.toString()

                // Check if the input is valid
                if (destination.isEmpty() || duration == null || dailyBudget == null) {
                    Toast.makeText(this@TravelEstimatorListActivity,
                        "Please fill all fields correctly",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Check if the selected currency is supported
                viewModel.isCurrencySupported(baseCurrency) { isSupported ->
                    if (isSupported) {
                        // Add the estimator if the currency is supported
                        viewModel.addEstimator(destination, duration, dailyBudget, baseCurrency)
                        runOnUiThread { clearInputs() } // Clear the input fields
                    } else {
                        // Show an error message if the currency is not supported
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

            // Delete selected estimators when the delete button is clicked
            btnDelete.setOnClickListener {
                viewModel.deleteSelectedEstimators()
            }

            // Go back to the main screen when the back button is clicked
            btnBackToMain.setOnClickListener {
                finish()
            }
        }
    }

    // Observe LiveData from the ViewModel to update the UI
    private fun setupObservers() {
        // Update the list when the estimators change
        viewModel.estimators.observe(this) { estimators ->
            adapter.updateEstimators(estimators)
        }

        // Show error messages from the ViewModel
        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    // Clear all input fields after adding an estimator
    private fun clearInputs() {
        binding.apply {
            tietDestination.text?.clear()
            tietDuration.text?.clear()
            tietDailyBudget.text?.clear()
        }
    }

    // Hide the keyboard when the user taps outside an EditText field
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
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
