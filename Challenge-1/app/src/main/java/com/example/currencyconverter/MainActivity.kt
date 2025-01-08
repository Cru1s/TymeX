package com.example.currencyconverter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.data.ApiClient
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.CurrencyViewModelFactory
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CurrencyViewModel by viewModels{
        CurrencyViewModelFactory(CurrencyRepository(ApiClient.api))
    }

    @SuppressLint("ClickableViewAccessibility")
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

        viewModel.errorMessage.observe(this){ error ->
            showErrorPopup(error)
        }

        binding.tietTarget.setOnFocusChangeListener{ _, hasFocus ->
            if (!hasFocus) {
                handleConversion()
            }
        }

        binding.btnSwap.setOnClickListener(){
            reverseCurrency()
        }

        textChangeListener()
        spinnerListener()


    }

    private fun textChangeListener(){
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
    }

    private fun spinnerListener(){
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

    private fun reverseCurrency(){
        val targetCurrency = binding.spTarget.selectedItem.toString()
        val resultCurrency = binding.spResult.selectedItem.toString()

        // Find the positions of these items in their respective arrays
        val targetPosition = (binding.spTarget.adapter as ArrayAdapter<String>).getPosition(resultCurrency)
        val resultPosition = (binding.spResult.adapter as ArrayAdapter<String>).getPosition(targetCurrency)

        binding.spTarget.setSelection(targetPosition)
        binding.spResult.setSelection(resultPosition)

        handleConversion()
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
                showErrorPopup("Invalid amount")
            }
        } else {
            binding.tietResult.setText("")
        }
    }

    private fun showErrorPopup(message: String) {
        val snack: Snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snack.animationMode = Snackbar.ANIMATION_MODE_FADE
        snack.show()
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

