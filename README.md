# investment-portfolio-service
This is a spring-boot app written in Kotlin.

### You can run the app with the command:
`mvn spring-boot:run -Dspring-boot.run.arguments="--history.loader.baseUrl=https://financialmodelingprep.com --history.loader.apiKey=YOUR_API_KEX"`

### Example curl command for getting the portfolio with risk level 2:
`curl localhost:8080/users/me/investment-portfolio?riskLevel=2`

#### Result:
`[{"weight":0.20,"ticker":"CAKE"},{"weight":0.50,"ticker":"PZZA"},{"weight":0.30,"ticker":"EAT"}]
`
### Example curl command for getting the current value for risk level 2:
`curl "localhost:8080/users/me/investment-portfolio/current-value?riskLevel=2&from=2021-02-01&to=2021-06-01&monthlyContribution=200"`

#### Result:
`{"porfolioValue":973.49819475,"sumContribution":1000}`