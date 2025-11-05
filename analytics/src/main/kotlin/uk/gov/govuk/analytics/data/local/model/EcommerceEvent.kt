package uk.gov.govuk.analytics.data.local.model

data class EcommerceEvent(
    val itemListId: String,
    val itemListName: String,
    val items: List<Item>,
    val totalItemCount: Int
) {
    data class Item(
        val itemName: String,
        val itemCategory: String? = null,
        val locationId: String
    )
}
