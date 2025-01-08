package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.data.ApiClient
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.CurrencyViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels{
        CurrencyViewModelFactory(CurrencyRepository(ApiClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currencies = resources.getStringArray(R.array.currencies)
        val currencies2 = resources.getStringArray(R.array.currencies2)

        val adapterTarget = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapterTarget.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapterResult = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies2)
        adapterResult.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spTarget.adapter = adapterTarget
        binding.spResult.adapter = adapterResult

        viewModel.conversionResult.observe(this){ result ->
            binding.tietResult.setText(result)
        }

        binding.tietTarget.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                handleConversion()
            }
        }

        binding.tietTarget.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleConversion()
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })

        val sharedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                handleConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Do nothing
            }
        }

        binding.spTarget.onItemSelectedListener = sharedListener
        binding.spResult.onItemSelectedListener = sharedListener
    }

    private fun handleConversion() {
        val amountText = binding.tietTarget.text.toString()
        val fromCurrency = binding.spTarget.selectedItem.toString()
        val toCurrency = binding.spResult.selectedItem.toString()

        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                viewModel.convertCurrency(amount, fromCurrency, toCurrency)
            } else {
                binding.tietResult.setText("Invalid amount")
            }
        } else {
            binding.tietResult.setText("")
        }
    }

}

