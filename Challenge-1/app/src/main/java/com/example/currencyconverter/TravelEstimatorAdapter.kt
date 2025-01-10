package com.example.currencyconverter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.data.model.TravelEstimator
import com.example.currencyconverter.databinding.ItemTravelEstimatorDetailBinding

class TravelEstimatorAdapter(
    private val onCurrencySelected: (TravelEstimator, String) -> Unit,
    private val onCheckBoxClicked: (TravelEstimator) -> Unit
) : RecyclerView.Adapter<TravelEstimatorAdapter.EstimatorViewHolder>() {

    private var estimators: List<TravelEstimator> = emptyList()

    class EstimatorViewHolder(
        private val binding: ItemTravelEstimatorDetailBinding,
        private val onCurrencySelected: (TravelEstimator, String) -> Unit,
        private val onCheckBoxClicked: (TravelEstimator) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(estimator: TravelEstimator) {
            binding.apply {
                tvDestination.text = "Destination: ${estimator.destination}"
                tvDuration.text = "Duration: ${estimator.duration} days"
                tvDailyBudget.text = "Daily Budget: ${estimator.dailyBudget} ${estimator.baseCurrency}"
                val displayedTotal = String.format("%.2f", estimator.convertedTotalCost ?: estimator.totalCost)
                tvTotalNeed.text = "Total: $displayedTotal"

                cbTravelEstimatorDetail.isChecked = estimator.isChecked

                cbTravelEstimatorDetail.setOnCheckedChangeListener { _, isChecked ->
                    estimator.isChecked = isChecked
                    onCheckBoxClicked(estimator)
                }

                spTargetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                        val selectedCurrency = parent?.getItemAtPosition(position) as String
                        onCurrencySelected(estimator, selectedCurrency)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstimatorViewHolder {
        val binding = ItemTravelEstimatorDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EstimatorViewHolder(binding, onCurrencySelected, onCheckBoxClicked)
    }

    override fun onBindViewHolder(holder: EstimatorViewHolder, position: Int) {
        holder.bind(estimators[position])
    }

    override fun getItemCount() = estimators.size

    fun updateEstimators(newEstimators: List<TravelEstimator>) {
        estimators = newEstimators
        notifyDataSetChanged()
    }
}