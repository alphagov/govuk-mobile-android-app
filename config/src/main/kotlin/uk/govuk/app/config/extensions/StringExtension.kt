package uk.govuk.app.config.extensions

fun String.isVersionLessThan(targetVersion: String): Boolean {
    val versionDelimiter = "."
    val targetVersionList = targetVersion.split(versionDelimiter).map { it.toIntOrNull() }
    val versionList = this.split(versionDelimiter).map { it.toIntOrNull() }
    val smallestListSize = minOf(targetVersionList.size, versionList.size)

    for (i in 0 until smallestListSize) {
        val versionNumberSegment = versionList[i] ?: 0
        val targetVersionNumberSegment = targetVersionList[i] ?: 0
        return if (targetVersionNumberSegment > versionNumberSegment) {
            true
        } else if (targetVersionNumberSegment == versionNumberSegment) {
            continue
        } else {
            false
        }
    }
    return targetVersionList.size > versionList.size
}
