package uk.govuk.app.visited.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun localDateFormatter(millis: Long): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
    return LocalDate.ofEpochDay(millis).format(formatter)
}

fun capitaliseMonth(month: String): String {
    return month.lowercase().replaceFirstChar(Char::titlecaseChar)
}
