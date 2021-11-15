package de.wagar.investment.calculate

class PortfolioService(val portfolios: Map<Int, List<WeightedStock>> ) {

    fun getPortfolio(risk: Int): List<WeightedStock>{
        return portfolios[risk] ?: emptyList()
    }
}