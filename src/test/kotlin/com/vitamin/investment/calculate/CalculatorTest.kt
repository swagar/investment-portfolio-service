package com.vitamin.investment.calculate

import com.fasterxml.jackson.databind.ObjectMapper
import com.vitamin.investment.readValue
import com.vitamin.investment.stock.History
import com.vitamin.investment.stock.HistoryEntry
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
    fun `test a simple calculation with real world data`() {
        // given
        val request = Request(
                startDate = of(2021, 1, 31),
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

    @Test
    fun `test the calculation algo works as expected`() {
        // given
        val request = Request(
                startDate = of(2021, 1, 10),
                endDate = of(2021, 4, 30),
                monthlyContribution = valueOf(100),
                portfolio = listOf(
                        WeightedStock(weight = valueOf(0.1), ticker = "A"),
                        WeightedStock(weight = valueOf(0.9), ticker = "B"))
        )
        val calculator = Calculator(
                request = request,
                historyService = HistoryService(
                        histories = mapOf(
                                Pair("A", History(
                                        listOf(
                                                HistoryEntry(date = of(2021, 4, 30), close = valueOf(5)),
                                                HistoryEntry(date = of(2021, 4, 1), close = valueOf(5)),
                                                HistoryEntry(date = of(2021, 3, 1), close = valueOf(2)),
                                                HistoryEntry(date = of(2021, 2, 1), close = valueOf(1))

                                        ))),
                                Pair("B", History(
                                        listOf(
                                                HistoryEntry(date = of(2021, 4, 30), close = valueOf(10)),
                                                HistoryEntry(date = of(2021, 4, 1), close = valueOf(10)),
                                                HistoryEntry(date = of(2021, 3, 1), close = valueOf(9)),
                                                HistoryEntry(date = of(2021, 2, 1), close = valueOf(3))
                                        )))
                        )
                )
        )

        // when
        val result = calculator.calculateCurrentValue()

        // then
        assert(result.sumContribution == valueOf(300))
        assert(result.porfolioValue == valueOf(575).setScale(1))

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
            CalculatorTest::class.java.getResource("/$fileName").readText()
}