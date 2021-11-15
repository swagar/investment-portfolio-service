package de.wagar.investment

import de.wagar.investment.stock.HistoryLoader
import de.wagar.investment.calculate.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@RestController
class Controller(
        val portfolioService: PortfolioService,
        val historyLoader: HistoryLoader) {


    @GetMapping("/users/me/investment-portfolio")
    fun getPortfolio(@RequestParam("riskLevel") riskLevel: Int): List<WeightedStock>{
        return portfolioService.getPortfolio(riskLevel)
    }

    @GetMapping("/users/me/investment-portfolio/current-value")
    fun getCurrentValue(
            @RequestParam("from")
            @DateTimeFormat(iso = DATE)
            startDate: LocalDate,
            @RequestParam("to")
            @DateTimeFormat(iso = DATE)
            endDate: LocalDate,
            @RequestParam("monthlyContribution") monthlyContribution: BigDecimal,
            @RequestParam("riskLevel") riskLevel: Int): CurrentValue {
        val portfolio = portfolioService.getPortfolio(riskLevel)
        val request = Request(
                startDate = startDate,
                endDate = endDate,
                monthlyContribution = monthlyContribution,
                portfolio = portfolio
        )
        val history = historyLoader.load(request)

        return Calculator(request, history).calculateCurrentValue()
    }
}