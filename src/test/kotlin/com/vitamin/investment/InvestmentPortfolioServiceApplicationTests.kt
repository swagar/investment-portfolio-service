package com.vitamin.investment

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.vitamin.investment.calculate.WeightedStock
import com.vitamin.investment.stock.History
import org.springframework.beans.factory.annotation.Autowired

inline fun <reified T> ObjectMapper.readValue(json: String): T = readValue(json, object : TypeReference<T>(){})

@SpringBootTest
class InvestmentPortfolioServiceApplicationTests {
	@Autowired
	lateinit var om: ObjectMapper

	@Test
	fun test_parsing_portfolios() {
		// given
		val json = loadFile("portfolios.json")

		// when
		val portfolios: Map<Int, List<WeightedStock>> = om.readValue(json)

		// then
		assert(null != portfolios)
		assert(5 == portfolios.size)
		assert(portfolios.containsKey(2))
	}

	@Test
	fun test_parsing_cake_stock_history(){
		// given
		val json = loadFile("CAKE.json")

		// when
		val cakeHistory: History = om.readValue(json)

		// then
		assert(null != cakeHistory)
		assert(191 == cakeHistory.historical.size)
	}

	private fun loadFile(fileName: String) =
			InvestmentPortfolioServiceApplicationTests::class.java.getResource("/$fileName").readText()

}
