package com.vitamin.investment

import com.fasterxml.jackson.databind.ObjectMapper
import com.vitamin.investment.calculate.CurrentValue
import com.vitamin.investment.calculate.WeightedStock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.ExpectedCount.once
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal.valueOf
import java.net.URI
import java.time.LocalDate

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = ["history.loader.baseUrl=http://localhost:8080",
    "history.loader.apiKey=foo"])
class ControllerTest(
        @Autowired val testRestTemplate: TestRestTemplate,
        @Autowired val restTemplate: RestTemplate,
        @Autowired val objectMapper: ObjectMapper) {

    lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `Test that the given risk level returns the right portfolio`() {
        // given
        val riskLevel = 2

        // when
        val entity: String = testRestTemplate.getForObject("/users/me/investment-portfolio?riskLevel=$riskLevel")
        val portfolio: List<WeightedStock> = objectMapper.readValue(entity)

        // then
        assert(3 == portfolio.size)
        assert("CAKE" == portfolio[0].ticker)
        assert(valueOf(0.2).setScale(2) == portfolio[0].weight)
        assert("PZZA" == portfolio[1].ticker)
        assert(valueOf(0.5).setScale(2) == portfolio[1].weight)
        assert("EAT" == portfolio[2].ticker)
        assert(valueOf(0.3).setScale(2) == portfolio[2].weight)
    }

    @Test
    fun `Test that the given risk level returns the right calculation`() {
        // given
        prepareRestService()
        val riskLevel = 2
        val startDate = LocalDate.of(2021, 2, 1)
        val endDate = LocalDate.of(2021, 6, 30)
        val monthlyContribution = 200

        // when
        val entity: String = testRestTemplate.getForObject("/users/me/investment-portfolio/current-value?riskLevel=$riskLevel&from=$startDate&to=$endDate&monthlyContribution=$monthlyContribution")
        val currentValue: CurrentValue = objectMapper.readValue(entity)

        // then
        assert(valueOf(1000) == currentValue.sumContribution)
    }

    private fun prepareRestService() {
        prepareRestService("CAKE")
        prepareRestService("PZZA")
        prepareRestService("EAT")
    }

    private fun prepareRestService(ticker: String) {
        mockServer.expect(once(),
                requestTo(URI("http://localhost:8080/api/v3/historical-price-full/$ticker?from=2021-02-01&to=2021-06-30&apikey=foo")))
                .andExpect(method(GET))
                .andRespond(withStatus(OK)
                        .contentType(APPLICATION_JSON)
                        .body(loadFile("$ticker.json"))
                )
    }

    private fun loadFile(fileName: String) =
            ControllerTest::class.java.getResource("/$fileName").readText()

}


inline fun <reified T> TestRestTemplate.getForObject(url: String): T = getForObject(url, T::class.java)