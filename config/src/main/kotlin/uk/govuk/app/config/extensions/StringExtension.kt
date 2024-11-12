package uk.govuk.app.config.extensions

fun String.isVersionLessThan(targetVersion: String): Boolean {
    val versionDelimiter = "."
    val targetVersionList = targetVersion.split(versionDelimiter).map { it.toIntOrNull() }
    val appVersionList = this.split(versionDelimiter).map { it.toIntOrNull() }
    val theSmallerListSize = minOf(targetVersionList.size, appVersionList.size)

    for (i in 0 until theSmallerListSize) {
        val appVersionNumberSegment = appVersionList[i] ?: 0
        val targetVersionNumberSegment = targetVersionList[i] ?: 0
        return if (targetVersionNumberSegment > appVersionNumberSegment) {
            true
        } else if (targetVersionNumberSegment == appVersionNumberSegment) {
            continue
        } else {
            false
        }
    }
    return targetVersionList.size > appVersionList.size
}
