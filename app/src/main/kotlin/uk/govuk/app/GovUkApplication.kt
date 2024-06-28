package uk.govuk.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GovUkApplication: Application() {

    fun blah() {
        Log.d("Blah", "blah")
    }
}