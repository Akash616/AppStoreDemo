package com.akashgupta.appstoredemo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class AppScraper {
    fun scrapeApps(): List<App> {
        val url = "https://en.aptoide.com/group/applications/sub/communication"
        return try {
            val doc: Document = Jsoup.connect(url)
                .timeout(10000)
                .get()

            val appElements: Elements = doc.select("div.app-card__CardContainer-sc-12k8udz-0.egWriL")

            appElements.mapNotNull { element ->
                try {

                    val name = element.selectFirst("a.app-card__AppName-sc-12k8udz-2")?.text().orEmpty()
                    val iconUrl = element.selectFirst("div.app-card__AppCardIcon-sc-12k8udz-5 img")?.attr("src").orEmpty()
                    val rating = element.selectFirst("span.app-card__ScoreSpan-sc-12k8udz-7")?.text()?.toFloatOrNull() ?: 0f
                    val appUrl = element.selectFirst("a.app-card__AppName-sc-12k8udz-2")?.attr("href").orEmpty()

                    App(name, iconUrl, rating, appUrl, packageName = "", isInstalled = false)
                } catch (e: Exception) {
                    println("Error parsing app element: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            println("Error connecting to the URL: ${e.message}")
            emptyList()
        }
    }
}

data class App(
    val name: String,
    val iconUrl: String,
    val rating: Float,
    val appUrl: String,
    val packageName: String,
    val isInstalled: Boolean
)
