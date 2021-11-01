package com.vitamin.investment.investment

import java.math.BigDecimal
import java.time.LocalDate

data class Request(val startDate: LocalDate, val endDate: LocalDate, val monthlyContribution: BigDecimal, val portfolio : List<WeightedStock>)
