package com.sepulveda.minutanutricional.accessibility

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent

object SttHelper {
    const val REQ_CODE = 101

    fun launch(activity: Activity, prompt: String = "Habla ahora") {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        activity.startActivityForResult(intent, REQ_CODE)
    }
}
