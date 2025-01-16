# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.govuk.app.search.data.remote.model.SearchResponse
-keep class uk.govuk.app.search.data.remote.model.SearchResult
