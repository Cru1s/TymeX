package com.example.currencyconverter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.viewmodel.CurrencyViewModel
import com.example.currencyconverter.viewmodel.TravelEstimatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: CurrencyRepository = mock()
    private lateinit var viewModel : CurrencyViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }


    @Test
    fun `convertCurrency posts converted amount on success`() = runTest {
        val base = "USD"
        val target = "EUR"
        val amount = 100.0
        val exchangeRate = 0.85

        // Mock repository result
        whenever(repository.getExchangeRate(base, target)).thenReturn(Result.success(exchangeRate))

        // Variable to capture the LiveData value
        var actualConvertedAmount: String? = null
        viewModel.conversionResult.observeForever { actualConvertedAmount = it }

        // Call the function
        viewModel.convertCurrency(amount, base, target)

        advanceUntilIdle()

        // Assert conversion result
        val expectedConvertedAmount = String.format("%.5f", amount * exchangeRate)
        assertEquals(expectedConvertedAmount, actualConvertedAmount)
    }

    @Test
    fun `convertCurrency posts error message on failure`() = runTest {
        val base = "USD"
        val target = "INVALID"
        val amount = 100.0

        whenever(repository.getExchangeRate(base, target)).thenReturn(Result.failure(Exception("Unsupported currency")))

        var errorMessage: String? = null
        viewModel.errorMessage.observeForever { errorMessage = it }

        viewModel.convertCurrency(amount, base, target)

        advanceUntilIdle()

        // Assert error message
        assertEquals("Unsupported currency", errorMessage)
    }
}
