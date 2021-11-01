package com.vitamin.investment.stock

import com.vitamin.investment.calculate.Request
import org.springframework.web.client.RestTemplate

class HistoryLoader(
        private val restTemplate: RestTemplate,
        private val baseUrl: String,
        private val endPoint: String,
        private val apiKey: String
) {

    fun load(request: Request): HistoryService{
        val histories = hashMapOf<String, History>()

        request.portfolio.forEach {
            val ticker = it.ticker
            histories[ticker] = loadStock(ticker, request)
        }

        return HistoryService(histories = histories)
    }

    private fun loadStock(ticker: String, request: Request): History{
        val from = request.startDate.toString()
        val to = request.endDate.toString()

        return restTemplate.getForObject(baseUrl + endPoint + ticker +
                "?from=" + from + "&to=" + to + "&apikey=" + apiKey, History::class.java) ?: History(emptyList())
    }
}