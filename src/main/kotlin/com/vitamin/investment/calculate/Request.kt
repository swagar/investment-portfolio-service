package com.vitamin.investment.calculate

import java.math.BigDecimal
import java.time.LocalDate

data class Request(val startDate: LocalDate, val endDate: LocalDate, val monthlyContribution: BigDecimal, val portfolio : List<WeightedStock>)
