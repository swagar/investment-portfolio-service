package de.wagar.investment.calculate

import de.wagar.investment.stock.HistoryService
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.time.LocalDate

class Calculator (private val request: Request,
                  private val historyService: HistoryService) {

    var stockAccount = hashMapOf<String, BigDecimal>()
    var numInvestments : BigDecimal = ZERO

    fun calculateCurrentValue() : CurrentValue {
        var currentDate = firstInvestmentDate()
        while (!currentDate.isAfter(request.endDate)){

            invest(currentDate)

            currentDate = nextInvestmentDate(currentDate)
        }

        return retrieveCurrentValue()
    }

    private fun nextInvestmentDate(currentDate: LocalDate) =
            currentDate.plusMonths(1).withDayOfMonth(1)

    private fun firstInvestmentDate() =
            nextInvestmentDate(request.startDate.minusDays(1))

    private fun invest(currentDate: LocalDate){
        request.portfolio.forEach {
            val currentPrice = historyService.findEntry(it.ticker, currentDate)
            val numNewStocks = request.monthlyContribution * it.weight / currentPrice.close

            when (val numStocks = stockAccount[it.ticker]){
                null -> stockAccount[it.ticker] = numNewStocks
                else -> stockAccount[it.ticker] = numStocks + numNewStocks
            }
        }

        numInvestments++
    }

    private fun retrieveCurrentValue(): CurrentValue {
        var porfolioValue = ZERO

        stockAccount.forEach{
            val priceOnEndDate = historyService.findEntry(it.key, request.endDate)
            porfolioValue += (priceOnEndDate.close * it.value)
        }

        val sumContribution = request.monthlyContribution * numInvestments

        return CurrentValue( porfolioValue = porfolioValue, sumContribution = sumContribution)
    }

}