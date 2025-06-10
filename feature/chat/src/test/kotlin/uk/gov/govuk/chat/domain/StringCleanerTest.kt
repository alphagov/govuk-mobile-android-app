package uk.gov.govuk.chat.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class StringCleanerTest {
    class RemoveInParagraphNewlinesTest {
        @Test
        fun `strips newlines between words without spaces`() {
            val actual = "one two three\nfour five six"
            val expected = "one two three four five six"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `strips newlines between words with spaces behind`() {
            val actual = "one two three\n four five six"
            val expected = "one two three four five six"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `strips newlines between words with multiple spaces behind`() {
            val actual = "one\n  two"
            val expected = "one two"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `strips newlines between words with spaces in front`() {
            val actual = "one two three \nfour five six"
            val expected = "one two three four five six"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `strips newlines between words with multiple spaces behind and brackets`() {
            val actual = "Animal and Plant Health Agency\n  (APHA)"
            val expected = "Animal and Plant Health Agency (APHA)"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `does not strip newlines after full stops and bullet points`() {
            val actual = "mark ([source][1]).\n* Obtaining"
            val expected = "mark ([source][1]).\n* Obtaining"

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }

        @Test
        fun `real response test`() {
            val actual = """
                Yes, you can keep a micropig, but you must follow specific regulations
                and requirements. If you keep a pig or ‘micropig’ as a pet, you are
                considered a pig keeper and must adhere to the same traceability and
                welfare rules as pig farmers. This includes:

                * Registering as a pig keeper with the Animal and Plant Health Agency
                  (APHA) to get a herd mark ([source][1]).
                * Obtaining a county parish holding (CPH) number from the Rural Payments
                  Agency (RPA) for the land and buildings where you’ll keep the pig
                  ([source][2]).
                * Ensuring the pig is identified with an ear tag, tattoo, or slap mark
                  before it moves from where it’s kept ([source][1]).
                * Following good hygiene and biosecurity standards to prevent the
                  introduction and spread of disease ([source][3]).

                For more detailed information on keeping a pet pig or micropig, visit
                the [GOV.UK page on keeping a pet pig or 'micropig'][3].
            """.trimIndent()

            val expected = """
                Yes, you can keep a micropig, but you must follow specific regulations and requirements. If you keep a pig or ‘micropig’ as a pet, you are considered a pig keeper and must adhere to the same traceability and welfare rules as pig farmers. This includes:

                * Registering as a pig keeper with the Animal and Plant Health Agency (APHA) to get a herd mark ([source][1]).
                * Obtaining a county parish holding (CPH) number from the Rural Payments Agency (RPA) for the land and buildings where you’ll keep the pig ([source][2]).
                * Ensuring the pig is identified with an ear tag, tattoo, or slap mark before it moves from where it’s kept ([source][1]).
                * Following good hygiene and biosecurity standards to prevent the introduction and spread of disease ([source][3]).

                For more detailed information on keeping a pet pig or micropig, visit the [GOV.UK page on keeping a pet pig or 'micropig'][3].
            """.trimIndent()

            assertEquals(StringCleaner.removeInParagraphNewlines(actual), expected)
        }
    }

    class IncludesPIITest {
        /*
         * Based on: https://github.com/alphagov/govuk-chat/blob/main/spec/validators/pii_validator_spec.rb
         */
        @Test
        fun `when the input does not contain any personal information`() {
            val question = "How much VAT do I have to pay?"
            assertFalse(StringCleaner.includesPII(question))
        }

        @Test
        fun `when the input contains an email address`() {
            val emailAddresses = listOf(
                "test@gmail.com",
                "test@localhost",
                "test.user@yahoo.co.uk",
            )

            emailAddresses.forEach { emailAddress ->
                val question = "My email address is $emailAddress"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `when the input contains a credit card number`() {
            val creditCardNumbers = listOf(
                1234567890123,
                12345678901234,
                123456789012345,
                1234567890123456,
            )

            creditCardNumbers.forEach { creditCardNumber ->
                val question = "My credit card number is $creditCardNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `returns true when the it contains a normal credit card number`() {
            val question = "My credit card number is 1234 5678 9012 3456"
            assertTrue(StringCleaner.includesPII(question))
        }

        @Test
        fun `returns true when the it contains an AMEX credit card number`() {
            val question = "My credit card number is 1234 567890 12345"
            assertTrue(StringCleaner.includesPII(question))
        }

        @Test
        fun `when the input contains a uk or international phone number`() {
            val phoneNumbers = listOf(
                "07555666777",
                "(01234)555666",
                "01234 555666",
                "(01234) 555666",
                "+441234567890",
                "+(44)1234567890",
                "+44 1234567890",
                "+(44) 1234567890",
                "+44 1234 567890",
                "+(44) 1234 567890",
                "+44 1234 567 890",
                "+(44) 1234 567 890",
                "+11234567",
                "+112345678",
                "+1123456789",
                "+11234567890",
                "+121234567890",
                "+1231234567890",
                "+(123)1234567890",
                "+1 1234567",
                "+1 12345678",
                "+1 123456789",
                "+1 1234567890",
                "+12 1234567890",
                "+123 1234567890",
                "+(123) 1234567890",
                "+1 123 4567890",
                "+(123) 123 4567",
                "+1-123-4567890",
                "+(123)-123-4567",
                "+1.123.4567890",
                "+(123).123.4567",
            )

            phoneNumbers.forEach { phoneNumber ->
                val question = "My phone number is $phoneNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }

        @Test
        fun `when the input contains a national insurance number`() {
            val niNumbers = listOf(
                "AB 12 34 56 A",
                "AB123456A",
                "AB 123 456 A",
                "AB 123 456A",
                "AB123456 A",
                "AB 123456A"
            )

            niNumbers.forEach { niNumber ->
                val question = "My ni number is $niNumber"
                assertTrue(StringCleaner.includesPII(question))
            }
        }
    }
}
