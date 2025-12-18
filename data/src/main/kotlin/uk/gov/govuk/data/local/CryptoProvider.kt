package uk.gov.govuk.data.local

interface CryptoProvider {
    fun encrypt(input: ByteArray): Result<String>

    fun decrypt(inputEncrypted: String): Result<ByteArray>
}