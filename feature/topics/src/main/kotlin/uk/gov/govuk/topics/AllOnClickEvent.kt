package uk.gov.govuk.topics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.visited.Visited

internal fun onClick(
    analyticsClient: AnalyticsClient,
    viewModelScope: CoroutineScope,
    visited: Visited,
    section: String,
    text: String,
    url: String,
    selectedItemIndex: Int
) {
    analyticsClient.buttonClick(
        text = text,
        url = url,
        external = true,
        section = section
    )

    sendSelectItemEvent(
        analyticsClient = analyticsClient,
        title = text,
        section = section,
        text = text,
        url = url,
        selectedItemIndex = selectedItemIndex
    )

    viewModelScope.launch {
        visited.visitableItemClick(title = text, url = url)
    }
}

private fun sendSelectItemEvent(
    analyticsClient: AnalyticsClient,
    section: String,
    text: String,
    title: String?,
    url: String?,
    selectedItemIndex: Int
) {
    analyticsClient.selectItemEvent(
        ecommerceEvent = EcommerceEvent(
            itemListName = "Topics",
            itemListId = title ?: "",
            items = listOf(
                EcommerceEvent.Item(
                    itemName = text,
                    itemCategory = section,
                    locationId = url ?: ""
                )
            )
        ),
        selectedItemIndex = selectedItemIndex
    )
}
