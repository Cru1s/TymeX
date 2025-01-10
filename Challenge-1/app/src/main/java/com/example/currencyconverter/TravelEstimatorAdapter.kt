package com.example.currencyconverter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.data.model.TravelEstimator
import com.example.currencyconverter.databinding.ItemTravelEstimatorDetailBinding

// Adapter for managing and displaying a list of travel estimators in a RecyclerView
class TravelEstimatorAdapter(
    private val onCurrencySelected: (TravelEstimator, String) -> Unit,
    private val onCheckBoxClicked: (TravelEstimator) -> Unit
) : RecyclerView.Adapter<TravelEstimatorAdapter.EstimatorViewHolder>() {

    // Holds the list of estimators to display
    private var estimators: List<TravelEstimator> = emptyList()

    // ViewHolder class to bind data to each item in the RecyclerView
    class EstimatorViewHolder(
        private val binding: ItemTravelEstimatorDetailBinding,
        private val onCurrencySelected: (TravelEstimator, String) -> Unit,
        private val onCheckBoxClicked: (TravelEstimator) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        // Bind data from a TravelEstimator object to the UI elements
        fun bind(estimator: TravelEstimator) {
            binding.apply {
                // Set text for destination, duration, and daily budget
                tvDestination.text = "Destination: ${estimator.destination}"
                tvDuration.text = "Duration: ${estimator.duration} days"
                tvDailyBudget.text = "Daily Budget: ${estimator.dailyBudget} ${estimator.baseCurrency}"

                // Display the total cost, using the converted cost if available
                val displayedTotal = String.format("%.2f", estimator.convertedTotalCost ?: estimator.totalCost)
                tvTotalNeed.text = "Total: $displayedTotal"

                // Set the checkbox state and handle its change listener
                cbTravelEstimatorDetail.isChecked = estimator.isChecked
                cbTravelEstimatorDetail.setOnCheckedChangeListener { _, isChecked ->
                    estimator.isChecked = isChecked // Update the model's state
                    onCheckBoxClicked(estimator)    // Trigger the callback
                }

                // Set up the dropdown for selecting a target currency
                spTargetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                        val selectedCurrency = parent?.getItemAtPosition(position) as String
                        onCurrencySelected(estimator, selectedCurrency) // Trigger the callback
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No action needed
                    }
                }
            }
        }
    }

    // Inflate the layout for each RecyclerView item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstimatorViewHolder {
        val binding = ItemTravelEstimatorDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EstimatorViewHolder(binding, onCurrencySelected, onCheckBoxClicked)
    }

    // Bind the ViewHolder to the specific data at the given position
    override fun onBindViewHolder(holder: EstimatorViewHolder, position: Int) {
        holder.bind(estimators[position])
    }

    // Return the total number of items in the list
    override fun getItemCount() = estimators.size

    // Update the list of estimators and refresh the RecyclerView
    fun updateEstimators(newEstimators: List<TravelEstimator>) {
        estimators = newEstimators
        notifyDataSetChanged() // Notify the RecyclerView that data has changed
    }
}
