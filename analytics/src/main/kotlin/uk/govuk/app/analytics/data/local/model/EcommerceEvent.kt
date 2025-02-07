package uk.govuk.app.analytics.data.local.model

data class EcommerceEvent(
    val itemListId: String,
    val itemListName: String,
    val items: List<Item>
) {
    data class Item(
        val itemName: String,
        val itemCategory: String,
        val locationId: String
    )
}
