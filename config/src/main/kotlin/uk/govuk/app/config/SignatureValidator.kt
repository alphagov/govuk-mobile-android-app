package uk.govuk.app.config

import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class SignatureValidator {
    private val key =
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEI9ifhn/iLdu3PwCKMhzqICSNUTivwF78Z9ybmhyIDF1Nvv+BavPyvz1XICfgEQ8g6IvHapaALXHcTszv5tFFfg=="

    fun isValidSignature(
        signature: String = "",
        message: String = ""
    ): Boolean {
        if (signature.isEmpty() || message.isEmpty()) {
            return false
        }

        val signatureBytes = Base64.getDecoder().decode(signature)
        val messageBytes = message.toByteArray()
        val publicKey = getPublicKey()

        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initVerify(publicKey)
        sig.update(messageBytes)

        return sig.verify(signatureBytes)
    }

    private fun getPublicKey(): PublicKey {
        val keySpecPublic = X509EncodedKeySpec(Base64.getDecoder().decode(key))
        return KeyFactory.getInstance("EC").generatePublic(keySpecPublic)
    }
}
