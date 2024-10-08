package uk.govuk.app.topics.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsRealmProvider @Inject constructor(
    private val dataStore: TopicsDataStore
) {

    private companion object {
        private const val KEYSTORE_KEY_ALIAS = "realm_topics_key"
        private const val REALM_NAME = "topics"
    }

    suspend fun open(): Realm {
        val encryptedRealmKey = dataStore.getRealmTopicsKey()
        val realmIv = dataStore.getRealmTopicsIv()

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val realmKey =
            if (encryptedRealmKey != null && realmIv != null &&
                keyStore.containsAlias(KEYSTORE_KEY_ALIAS)) {
                val keystoreKey = getKeystoreKey(keyStore)
                decryptRealmKey(
                    encryptedKeyString = encryptedRealmKey,
                    ivString = realmIv,
                    keystoreKey = keystoreKey
                ).encoded
            } else {
                val keystoreKey = createKeystoreKey()
                val realmEncryptionKey = createRealmEncryptionKey()
                encryptAndSaveRealmKey(realmEncryptionKey, keystoreKey)
                realmEncryptionKey
            }

        val config = RealmConfiguration.Builder(schema = setOf(LocalTopicItem::class))
            .name(REALM_NAME)
            .encryptionKey(realmKey)
            .build()
        return Realm.open(config)
    }

    private fun getKeystoreKey(keyStore: KeyStore): SecretKey {
        val secretKeyEntry = keyStore.getEntry(KEYSTORE_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    private fun createKeystoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256) // Generate a 256-bit key
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun createRealmEncryptionKey(): ByteArray {
        val key = ByteArray(64) // 256 bits = 32 bytes
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(key) // Fill the byte array with random bytes
        return key
    }

    private suspend fun encryptAndSaveRealmKey(realmKey: ByteArray, keystoreKey: SecretKey) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey)

        val iv = cipher.iv
        val encryptedKey = cipher.doFinal(realmKey)

        val encodedKey = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
        val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)

        dataStore.saveRealmTopicsKey(encodedKey)
        dataStore.saveRealmTopicsIv(encodedIv)
    }

    private fun decryptRealmKey(
        encryptedKeyString: String,
        ivString: String,
        keystoreKey: SecretKey
    ): SecretKey {
        val encryptedKey = Base64.decode(encryptedKeyString, Base64.DEFAULT)
        val iv = Base64.decode(ivString, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, keystoreKey, GCMParameterSpec(128, iv)) // Using the stored IV

        val decryptedKeyBytes = cipher.doFinal(encryptedKey)
        return SecretKeySpec(decryptedKeyBytes, "AES") // Convert to SecretKey
    }

}