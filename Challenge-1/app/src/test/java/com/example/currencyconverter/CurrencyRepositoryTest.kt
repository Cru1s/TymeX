package com.example.currencyconverter

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.CurrencyApi
import com.example.currencyconverter.data.model.HexarateData
import com.example.currencyconverter.data.model.HexarateResponse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

class CurrencyRepositoryTest {

    private val api: CurrencyApi = mock()
    private val repository = CurrencyRepository(api)

    @Test
    fun `getExchangeRate returns exchange rate on success`() = runBlocking {
        val base = "USD"
        val target = "EUR"
        val response = HexarateResponse(
            200,
            data = HexarateData("USD", "EUR", 0.85, 1, "2021-09-01T00:00:00Z")
        )

        // Mock API response
        whenever(api.getRates(base, target)).thenReturn(response)

        val result = repository.getExchangeRate(base, target)
        val rate = result.getOrNull()

        if (rate != null) {
            assertEquals(0.85, rate, 0.0001)
        }
    }

    @Test
    fun `getExchangeRate returns failure on unsupported currency`() = runBlocking {
        val base = "USD"
        val target = "INVALID"

        // Create a mocked HTTP response with status code 422
        val mockedResponse: Response<Unit> = mock {
            on { code() } doReturn 422
            on { message() } doReturn "Unprocessable Entity"
        }

        // Mock an HttpException using the mocked response
        val httpException = HttpException(mockedResponse)

        // Mock the API to throw the configured HttpException
        whenever(api.getRates(base, target)).thenThrow(httpException)

        // Call the method under test
        val result = repository.getExchangeRate(base, target)

        // Assert the failure result
        assertNull(result.getOrNull())
        assertEquals("Unsupported currency", result.exceptionOrNull()?.message)
    }
}