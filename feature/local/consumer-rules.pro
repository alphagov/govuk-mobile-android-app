# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class uk.gov.govuk.local.data.remote.model.Address
-keep class uk.gov.govuk.local.data.remote.model.ApiResponse
-keep class uk.gov.govuk.local.data.remote.model.ApiResponse.LocalResponse
-keep class uk.gov.govuk.local.data.remote.model.ApiResponse.AddressListResponse
-keep class uk.gov.govuk.local.data.remote.model.ApiResponse.MessageResponse
-keep class uk.gov.govuk.local.data.remote.model.LocalAuthority
