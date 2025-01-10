package com.example.currencyconverter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.data.ApiClient
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.CurrencyViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ViewModel for handling currency conversion logic
    private val viewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory(CurrencyRepository(ApiClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Populate the currency dropdowns with predefined lists
        val currencies = resources.getStringArray(R.array.currencies)
        val currencies2 = resources.getStringArray(R.array.currencies2)

        val adapterBase = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapterBase.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val adapterTarget = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies2)
        adapterTarget.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spBase.adapter = adapterBase
        binding.spTarget.adapter = adapterTarget

        setupObservers() // Observe LiveData for UI updates
        setupListeners() // Handle user interactions
    }

    private fun setupObservers() {
        // Observe LiveData for conversion results and update the target field
        viewModel.conversionResult.observe(this) { result ->
            binding.tietTarget.setText(result)
        }

        // Observe LiveData for errors and show them as popups
        viewModel.errorMessage.observe(this) { error ->
            showErrorPopup(error)
        }
    }

    private fun setupListeners() {
        // Trigger currency conversion when the base amount loses focus
        binding.tietBase.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                handleConversion()
            }
        }

        // Swap the selected currencies when the swap button is clicked
        binding.btnSwap.setOnClickListener {
            reverseCurrency()
        }

        // Navigate to the Travel Estimator screen
        binding.btnTravelEstimator.setOnClickListener {
            val intent = Intent(this, TravelEstimatorListActivity::class.java)
            startActivity(intent)
        }

        // Add a text change listener to update conversion dynamically
        binding.tietBase.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleConversion()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Add listeners for currency dropdown selections
        val sharedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                handleConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }
        binding.spBase.onItemSelectedListener = sharedListener
        binding.spTarget.onItemSelectedListener = sharedListener
    }

    private fun reverseCurrency() {
        val baseCurrency = binding.spBase.selectedItem.toString()
        val targetCurrency = binding.spTarget.selectedItem.toString()

        val basePosition = (binding.spTarget.adapter as ArrayAdapter<String>).getPosition(baseCurrency)
        val targetPosition = (binding.spBase.adapter as ArrayAdapter<String>).getPosition(targetCurrency)

        binding.spBase.setSelection(targetPosition)
        binding.spTarget.setSelection(basePosition)

        handleConversion() // Trigger conversion after swapping
    }

    private fun handleConversion() {
        val amountText = binding.tietBase.text.toString()
        val fromCurrency = binding.spBase.selectedItem.toString()
        val toCurrency = binding.spTarget.selectedItem.toString()

        if (amountText.isNotEmpty()) {
            val amount = amountText.toDoubleOrNull()
            if (amount != null) {
                viewModel.convertCurrency(amount, fromCurrency, toCurrency)
            } else {
                showErrorPopup("Invalid amount")
            }
        } else {
            binding.tietTarget.setText("")
        }
    }

    private fun showErrorPopup(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).apply {
            setGravity(Gravity.TOP, 0, 50)
            show()
        }
    }

    // Hide the keyboard when the user taps outside of an EditText field
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
