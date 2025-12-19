package uk.gov.govuk.data.crypto

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.subtle.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TinkClient @Inject constructor(
    context: Context
) : CryptoProvider {
    private companion object {
        const val KEYSET_NAME = "data_key_set"
        const val PREF_FILENAME = "data_prefs"
    }

    private val packageName: String? = context.packageName

    private var aead: Aead

    init {
        AeadConfig.register()
        aead = getAead(context)
    }

    private fun getAead(context: Context): Aead {
        return AndroidKeysetManager.Builder()
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withSharedPref(
                context,
                "$packageName.$KEYSET_NAME",
                "$packageName.$PREF_FILENAME"
            )
            .withMasterKeyUri("android-keystore://tink_master_key")
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    override fun encrypt(input: ByteArray): Result<String> {
        return runCatching {
            val encrypted = aead.encrypt(input, null)
            Base64.encode(encrypted)
        }
    }

    override fun decrypt(inputEncrypted: String): Result<ByteArray> {
        return runCatching {
            val encrypted = Base64.decode(inputEncrypted)
            aead.decrypt(encrypted, null)
        }
    }
}
