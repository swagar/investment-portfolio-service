package com.vitamin.investment.calculate

import com.fasterxml.jackson.databind.ObjectMapper
import com.vitamin.investment.InvestmentPortfolioServiceApplicationTests
import com.vitamin.investment.readValue
import com.vitamin.investment.stock.History
import com.vitamin.investment.stock.HistoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal.valueOf
import java.time.LocalDate.of

@SpringBootTest
class CalculatorTest {
    @Autowired
    lateinit var om: ObjectMapper

    @Test
    fun test_a_simple_calulation() {
        // given
        val request = Request(
                startDate = of(2021, 2, 1),
                endDate = of(2021, 6, 30),
                monthlyContribution = valueOf(400),
                loadPortfolio(2)
        )
        val calculator = Calculator(
                request = request,
                historyService = loadHistoryService()
        )

        // when
        val result = calculator.calculateCurrentValue()

        // then
        assert(result.sumContribution == valueOf(2000))

    }

    private fun loadHistoryService(): HistoryService {
        val cakeHistory: History = om.readValue(loadFile("CAKE.json"))
        val eatHistory: History = om.readValue(loadFile("EAT.json"))
        val pzzaHistory: History = om.readValue(loadFile("PZZA.json"))

        val histories = hashMapOf<String, History>()
        histories["CAKE"] = cakeHistory
        histories["EAT"] = eatHistory
        histories["PZZA"] = pzzaHistory

        return HistoryService(histories = histories)
    }

    private fun loadPortfolio(risk: Int): List<WeightedStock> {
        val portfolios: Map<Int, List<WeightedStock>> = om.readValue(loadFile("portfolios.json"))

        return portfolios[risk] ?: emptyList()
    }

    private fun loadFile(fileName: String) =
            InvestmentPortfolioServiceApplicationTests::class.java.getResource("/$fileName").readText()
}