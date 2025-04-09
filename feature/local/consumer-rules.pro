# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.govuk.app.local.data.remote.model.Address
-keep class uk.govuk.app.local.data.remote.model.ApiResponse
-keep class uk.govuk.app.local.data.remote.model.LocalAuthority
