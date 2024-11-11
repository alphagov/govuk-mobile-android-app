package uk.govuk.app.visited.data

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun localDateFormatter(millis: Long): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy")
    return LocalDateTime.ofEpochSecond(millis, 0, ZoneOffset.UTC).format(formatter)
}

fun capitaliseMonth(month: String): String {
    return month.lowercase().replaceFirstChar(Char::titlecaseChar)
}
