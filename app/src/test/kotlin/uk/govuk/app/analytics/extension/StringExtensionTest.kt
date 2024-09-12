package uk.govuk.app.analytics.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionTest {

    companion object {
        private const val REDACTION_TEXT = "[REDACTED]"
    }

    @Test
    fun `Given a set of postcodes, when redacted, postcodes should be replaced`() {
        assertEquals(REDACTION_TEXT, "L1 8JQ".redactPii())
        assertEquals(REDACTION_TEXT, "W1A 0AX".redactPii())
        assertEquals(REDACTION_TEXT, "L10 9HL".redactPii())
        assertEquals(REDACTION_TEXT, "EH1 1YZ".redactPii())
        assertEquals(REDACTION_TEXT, "CF10 1AA".redactPii())
        assertEquals(REDACTION_TEXT, "EC1A 1BB".redactPii())

        assertEquals(REDACTION_TEXT, "L18JQ".redactPii())
        assertEquals(REDACTION_TEXT, "W1A0AX".redactPii())
        assertEquals(REDACTION_TEXT, "L109HL".redactPii())
        assertEquals(REDACTION_TEXT, "EH11YZ".redactPii())
        assertEquals(REDACTION_TEXT, "CF101AA".redactPii())
        assertEquals(REDACTION_TEXT, "EC1A1BB".redactPii())

        assertEquals(REDACTION_TEXT, "L1  8JQ".redactPii())
        assertEquals(REDACTION_TEXT, "W1A  0AX".redactPii())
        assertEquals(REDACTION_TEXT, "L10  9HL".redactPii())
        assertEquals(REDACTION_TEXT, "EH1  1YZ".redactPii())
        assertEquals(REDACTION_TEXT, "CF10  1AA".redactPii())
        assertEquals(REDACTION_TEXT, "EC1A  1BB".redactPii())

        val expected = "Universal credit $REDACTION_TEXT allowance"

        assertEquals(expected, "Universal credit L1 8JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A 0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L10 9HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH1 1YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF10 1AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A 1BB allowance".redactPii())

        assertEquals(expected, "Universal credit L18JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L109HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH11YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF101AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A1BB allowance".redactPii())

        assertEquals(expected, "Universal credit L1  8JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A  0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L10  9HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH1  1YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF10  1AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A  1BB allowance".redactPii())
    }

    @Test
    fun `Given a set of email addresses, when redacted, email addresses should be replaced`() {
        assertEquals(REDACTION_TEXT, "john.doe@example.com".redactPii())
        assertEquals(REDACTION_TEXT, "jane_doe123@company.co.uk".redactPii())
        assertEquals(REDACTION_TEXT, "user.name+tag@domain.org".redactPii())
        assertEquals(REDACTION_TEXT, "contact@sub.domain.net".redactPii())
        assertEquals(REDACTION_TEXT, "firstname.lastname@company.com".redactPii())
        assertEquals(REDACTION_TEXT, "user+mailbox@subdomain.example.com".redactPii())

        val expected = "Self assessment $REDACTION_TEXT tax return"

        assertEquals(expected, "Self assessment john.doe@example.com tax return".redactPii())
        assertEquals(expected, "Self assessment jane_doe123@company.co.uk tax return".redactPii())
        assertEquals(expected, "Self assessment user.name+tag@domain.org tax return".redactPii())
        assertEquals(expected, "Self assessment contact@sub.domain.net tax return".redactPii())
        assertEquals(expected, "Self assessment firstname.lastname@company.com tax return".redactPii())
        assertEquals(expected, "Self assessment user+mailbox@subdomain.example.com tax return".redactPii())
    }

    @Test
    fun `Given an NI number, when redacted, NI number should be replaced`() {
        assertEquals(REDACTION_TEXT, "CD987654A".redactPii())
        assertEquals(REDACTION_TEXT, "CD 98 76 54 A".redactPii())
        assertEquals(REDACTION_TEXT, "CD  98  76  54  A".redactPii())

        val expected = "Child benefit $REDACTION_TEXT allowance"
        assertEquals(expected, "Child benefit CD987654A allowance".redactPii())
        assertEquals(expected, "Child benefit CD 98 76 54 A allowance".redactPii())
        assertEquals(expected, "Child benefit CD  98  76  54  A allowance".redactPii())
    }
}