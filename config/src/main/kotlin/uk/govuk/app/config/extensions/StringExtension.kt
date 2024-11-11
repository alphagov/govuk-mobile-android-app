package uk.govuk.app.config.extensions

import kotlin.math.abs

fun String.isVersionLessThan(targetVersion: String): Boolean {
    val versionDelimiter = "."
    val versionList = this.split(versionDelimiter).toMutableList()
    val targetVersionList = targetVersion.split(versionDelimiter).toMutableList()
    val spareCount = versionList.size - targetVersionList.size
    if (spareCount == 0) {
        return this < targetVersion
    }
    val spareZeros = "0".repeat(abs(spareCount))
    val spareZerosList = spareZeros.split("").filter { it.isNotEmpty() }
    if (spareCount > 0) {
        targetVersionList.addAll(spareZerosList)
    } else {
        versionList.addAll(spareZerosList)
    }
    return versionList.joinToString(versionDelimiter) < targetVersionList.joinToString(
        versionDelimiter
    )
}
