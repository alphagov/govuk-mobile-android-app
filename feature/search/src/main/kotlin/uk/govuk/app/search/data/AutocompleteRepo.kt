package uk.govuk.app.search.data

import javax.inject.Inject

class AutocompleteRepo @Inject constructor() {
    fun performLookup(searchTerm: String): Result<List<String>> {
//        TODO: fetch autocomplete suggestions from API
        val wordList = arrayOf(
            "companies house",
            "household support fund",
            "council house",
            "moving house",
            "companies house login",
            "dog",
            "dog breeding",
            "category",
            "catch certificate",
            "cat"
        )

        val suggestions = wordList.filter {
            it.contains(searchTerm, ignoreCase = true)
        }

        return Result.success(suggestions)
    }
}
