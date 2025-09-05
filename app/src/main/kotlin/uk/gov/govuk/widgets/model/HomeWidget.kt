package uk.gov.govuk.widgets.model

import uk.gov.govuk.config.data.remote.model.AlertBanner
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner

internal sealed interface HomeWidget {
    data class Alert(val alertBanner: AlertBanner) : HomeWidget
    data object Search : HomeWidget
    data object RecentActivity : HomeWidget
    data object Topics : HomeWidget
    data object Local : HomeWidget
    data class UserFeedback(val userFeedbackBanner: UserFeedbackBanner) : HomeWidget
}
