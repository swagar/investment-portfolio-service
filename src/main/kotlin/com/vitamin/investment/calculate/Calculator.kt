package com.vitamin.investment.calculate

import com.vitamin.investment.stock.HistoryService
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.time.LocalDate

class Calculator (private val request: Request,
                  private val historyService: HistoryService) {

    var stockAccount = hashMapOf<String, BigDecimal>()
    var numInvestments : BigDecimal = ZERO

    fun calculateCurrentValue() : CurrentValue {
        var currentDate = firstInvestmentDate(request.startDate)
        while (!currentDate.isAfter(request.endDate)){

            invest(currentDate)

            currentDate = nextInvestmentDate(currentDate)
        }

        return calulateCurrentValue()
    }

    private fun nextInvestmentDate(currentDate: LocalDate) =
            currentDate.plusMonths(1).withDayOfMonth(1)

    private fun firstInvestmentDate(currentDate: LocalDate) =
            nextInvestmentDate(request.startDate.minusDays(1))

    private fun invest(currentDate: LocalDate){
        request.portfolio.forEach {
            val currentPrice = historyService.findEntry(it.ticker, currentDate, request.endDate)
            val numNewStocks = request.monthlyContribution * it.weight / currentPrice.close

            when (val numStocks = stockAccount[it.ticker]){
                null -> stockAccount[it.ticker] = numNewStocks
                else -> stockAccount[it.ticker] = numStocks + numNewStocks
            }
        }

        numInvestments++
    }

    private fun calulateCurrentValue(): CurrentValue{
        var porfolioValue = ZERO;

        stockAccount.forEach{
            val priceOnEndDate = historyService.findEntry(it.key, request.endDate, request.endDate)
            porfolioValue += (priceOnEndDate.close * it.value)
        }

        val sumContribution = request.monthlyContribution * numInvestments

        return CurrentValue( porfolioValue = porfolioValue, sumContribution = sumContribution)
    }

}