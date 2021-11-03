package com.vitamin.investment.calculate

import com.fasterxml.jackson.databind.ObjectMapper
import com.vitamin.investment.readValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PortfolioServiceTest {
    @Autowired
    lateinit var om: ObjectMapper

    @Test
    fun test_parsing_portfolios() {
        // given
        val portfolios: Map<Int, List<WeightedStock>> = om.readValue(loadFile("portfolios.json"))
        val portfolioService = PortfolioService(portfolios )

        // when
        val myPortfolio = portfolioService.getPortfolio(2)

        // then
        assert(3 == myPortfolio.size)
    }

    private fun loadFile(fileName: String) =
            PortfolioServiceTest::class.java.getResource("/$fileName").readText()
}