package com.example.currencyconverter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.viewmodel.TravelEstimatorViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class TravelEstimatorViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val repository: CurrencyRepository = mock()
    private lateinit var viewModel : TravelEstimatorViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        // Set the Main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = TravelEstimatorViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `addEstimator adds new estimator to the list`() {
        val viewModel = TravelEstimatorViewModel(mock())
        viewModel.addEstimator("Paris", 5, 200.0, "USD")

        val estimators = viewModel.estimators.value
        assertEquals(1, estimators?.size)
        assertEquals("Paris", estimators?.first()?.destination)
    }

    @Test
    fun `deleteSelectedEstimators removes checked estimators`() {
        val viewModel = TravelEstimatorViewModel(mock())

        // Add estimators
        viewModel.addEstimator("Paris", 5, 200.0, "USD")
        viewModel.addEstimator("London", 3, 150.0, "USD")

        // Mark the first estimator as checked
        val estimatorList = viewModel.estimators.value?.toMutableList()!!
        estimatorList[0].isChecked = true
        viewModel.updateEstimator(estimatorList[0])

        // Delete checked estimators
        viewModel.deleteSelectedEstimators()

        // Assert only unchecked estimators remain
        val remainingEstimators = viewModel.estimators.value
        assertEquals(1, remainingEstimators?.size)
        assertEquals("London", remainingEstimators?.first()?.destination)
    }

    @Test
    fun `convertTotalCost updates convertedTotalCost when rate is valid`() = runTest {
        val repository = mock<CurrencyRepository>()
        val viewModel = TravelEstimatorViewModel(repository)

        // Add estimator
        viewModel.addEstimator("Paris", 5, 200.0, "USD")
        val estimator = viewModel.estimators.value!!.first()

        // Mock repository result
        whenever(repository.getExchangeRate("USD", "EUR")).thenReturn(Result.success(0.85))

        viewModel.convertTotalCost(estimator, "EUR")

        // Assert converted total cost
        estimator.convertedTotalCost?.let { assertEquals(850.0, it, 0.01) }
    }


}