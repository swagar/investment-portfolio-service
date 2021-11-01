package com.vitamin.investment.stock

import java.time.LocalDate

class HistoryService(private val histories: Map<String, History>) {

    fun findEntry(ticker: String, targetDate: LocalDate, endDate: LocalDate): HistoryEntry{
        val history = histories[ticker]!!.historical

        var targetOrAfterIdx = -1

        for (i in history.indices){
            val entry =  history[i]
            val date = entry.date

            if(date.isEqual(targetDate) || date.isAfter(targetDate))
                targetOrAfterIdx = i
        }

        return history[targetOrAfterIdx]
    }
}