package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName
import uk.gov.govuk.chat.ui.model.SourceUi

data class Source(
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String
) {
    fun toSourceUi(): SourceUi {
        return SourceUi(
            url = url,
            title = title
        )
    }
}
