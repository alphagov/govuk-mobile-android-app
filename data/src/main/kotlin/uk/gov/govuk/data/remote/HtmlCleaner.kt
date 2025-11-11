package uk.gov.govuk.data.remote

import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.PolicyFactory

object HtmlCleaner {
    private val plainTextPolicy: PolicyFactory by lazy {
        HtmlPolicyBuilder().toFactory()
    }

    fun toPlainText(untrustedHtml: String): String {
        return plainTextPolicy.sanitize(untrustedHtml)
    }
}
