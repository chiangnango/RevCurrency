# RevCurrency
This sample app contains only one page - currency rate list page.
Every 1 second will fetch latest rates data and update on UI. 
Edit the amount of first(base) currency will automatically update other currencies' amount value according to respective rate.
When click currency item, it will shift the item to top and set it as the base currency. 

RevCurrency adopts **Android Architecture Components** - **ViewModel & LiveData** to fulfill MVVM pattern.
It also utilize **Kotlin Coroutine** and **OkHttp3** to help to fetch data from network, and **Glide** to do *circleCorpTransformation*.
*MainActivity* will create *MainFragment* to display page. MainFragment create *MainViewModel* and inject *MainRepository* to handle fetch data.
MainFragment also observes LiveData in ViewModel to show loading and set currencyList to adapter to display on RecyclerView.

The mapping of currency abbreviation and currency name like *JPY* and *Japanese Yen* is from *https://docs.openexchangerates.org/docs/currencies-json* api.
And the currency icon is from *https://github.com/google/region-flags* .png and .svg.
