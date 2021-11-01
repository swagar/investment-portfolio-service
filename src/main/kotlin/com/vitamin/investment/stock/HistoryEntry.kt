package com.vitamin.investment.stock

import java.math.BigDecimal
import java.time.LocalDate

data class HistoryEntry(val date: LocalDate, val close: BigDecimal)