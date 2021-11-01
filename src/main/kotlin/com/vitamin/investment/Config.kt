package com.vitamin.investment

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.vitamin.investment.calculate.PortfolioService
import com.vitamin.investment.calculate.WeightedStock
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter




@Configuration
class Config {
    @Bean
    fun objectMapper(): ObjectMapper =
            ObjectMapper()
                    .registerModule(JavaTimeModule())
                    .registerModule(ParameterNamesModule())
                    .registerModule(Jdk8Module())
                    .registerModule(KotlinModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder, objectMapper: ObjectMapper): RestTemplate =
            restTemplateBuilder
                    .messageConverters(createConverter(objectMapper))
                    .build()

    @Bean
    fun portfolioService(objectMapper: ObjectMapper):PortfolioService{
        val portfolios: Map<Int, List<WeightedStock>> = objectMapper.readValue(loadFile("portfolios.json"))

        return PortfolioService(portfolios )
    }

    private fun createConverter(objectMapper: ObjectMapper): MappingJackson2HttpMessageConverter? {
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = objectMapper
        return converter
    }

    private fun loadFile(fileName: String) =
            Config::class.java.getResource("/$fileName").readText()
}

inline fun <reified T> ObjectMapper.readValue(json: String): T = readValue(json, object : TypeReference<T>(){})