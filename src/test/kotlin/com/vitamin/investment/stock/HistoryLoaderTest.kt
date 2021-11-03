package com.vitamin.investment.stock

import com.vitamin.investment.calculate.Request
import com.vitamin.investment.calculate.WeightedStock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal.ONE
import java.math.BigDecimal.valueOf
import java.net.URI
import java.time.LocalDate

@SpringBootTest
class HistoryLoaderTest {
    lateinit var mockServer: MockRestServiceServer

    @Autowired
    lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setup(){
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun test_loading_of_example_cake_history(){
        // given
        prepareRestServiceForCake()
        val startDate = LocalDate.of(2021, 2, 1)
        val endDate = LocalDate.of(2021, 6, 30)

        val historyLoader = HistoryLoader(
                restTemplate = restTemplate,
                baseUrl = "http://localhost:8080",
                endPoint = "/api/v3/historical-price-full/",
                apiKey = "foo"
        )

        val request = Request(
                startDate = startDate,
                endDate = endDate,
                monthlyContribution = valueOf(400),
                loadCakePortfolio()
        )

        // when
        val historyService = historyLoader.load(request)

        // then
        val cakeEntry = historyService.findEntry("CAKE", startDate)
        assert(cakeEntry.date.isEqual(startDate))
    }

    private fun loadCakePortfolio(): List<WeightedStock> {
        return listOf(WeightedStock(weight = ONE, ticker = "CAKE"))
    }

    private fun prepareRestServiceForCake(){
        mockServer.expect(ExpectedCount.once(),
                MockRestRequestMatchers.requestTo(URI("http://localhost:8080/api/v3/historical-price-full/CAKE?from=2021-02-01&to=2021-06-30&apikey=foo")))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loadFile("CAKE.json"))
                )
    }

    private fun loadFile(fileName: String) =
            HistoryLoaderTest::class.java.getResource("/$fileName").readText()
}