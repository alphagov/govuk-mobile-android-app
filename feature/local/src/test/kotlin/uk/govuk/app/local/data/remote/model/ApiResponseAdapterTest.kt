package uk.govuk.app.local.data.remote.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ApiResponseAdapterTest {
    private lateinit var gson: Gson
    private lateinit var adapter: ApiResponseAdapter

    @Before
    fun setUp() {
        gson = Gson()
        adapter = ApiResponseAdapter(gson)
    }

    @Test
    fun `unitary localAuthority response`() {
        val json = """
        {
            "local_authority": {
                "name": "name",
                "homepage_url": "homepage_url",
                "tier": "unitary",
                "slug": "slug",
                "parent": null
            }
        }
        """.trimIndent()

        val expected = ApiResponse.LocalAuthorityResponse(
            localAuthority = LocalAuthority(
                name = "name",
                homePageUrl = "homepage_url",
                tier = "unitary",
                slug = "slug",
                parent = null
            )
        )

        val actual = adapter.fromJson(json)

        assertEquals(expected, actual)
    }

    @Test
    fun `two-tier localAuthority response`() {
        val json = """
        {
            "local_authority": {
                "name": "name",
                "homepage_url": "homepage_url",
                "tier": "district",
                "slug": "slug",
                "parent": {
                    "name": "parent name",
                    "homepage_url": "parentHomePageUrl",
                    "tier": "county",
                    "slug": "parentSlug"
                }
            }
        }
        """.trimIndent()

        val expected = ApiResponse.LocalAuthorityResponse(
            localAuthority = LocalAuthority(
                name = "name",
                homePageUrl = "homepage_url",
                tier = "district",
                slug = "slug",
                parent =
                    LocalAuthority(
                        name = "parent name",
                        homePageUrl = "parentHomePageUrl",
                        tier = "county",
                        slug = "parentSlug"
                    )
            )
        )

        val actual = adapter.fromJson(json)

        assertEquals(expected, actual)
    }

    @Test
    fun `ambiguous addressList response`() {
        val json = """
        {
            "addresses": [
                { "address": "address1", "slug": "slug1", "name": "name1" },
                { "address": "address2", "slug": "slug2", "name": "name2" }
            ]
        }
        """.trimIndent()

        val expected = ApiResponse.AddressListResponse(
            addresses = listOf(
                Address(
                    address = "address1",
                    slug = "slug1",
                    name = "name1"
                ),
                Address(
                    address = "address2",
                    slug = "slug2",
                    name = "name2"
                )
            )
        )

        val actual = adapter.fromJson(json)

        assertEquals(expected, actual)
    }

    @Test
    fun `message response`() {
        val json = """
            {
                "message": "Test Message"
            }
        """.trimIndent()

        val expected = ApiResponse.MessageResponse(message = "Test Message")

        val actual = adapter.fromJson(json)

        assertEquals(expected, actual)
    }

    @Test
    fun `empty response`() {
        val actual = adapter.fromJson("{}")

        assertNull(actual)
    }
}
