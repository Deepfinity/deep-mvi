package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.Intent
import com.deepfinity.sample.main.models.MainModel

sealed class MainIntent : Intent {
    data class UpdateText(val model: MainModel) : MainIntent()
    data class UpdateTextObservable(val model: MainModel) : MainIntent()
    data class UpdateTextSingle(val model: MainModel) : MainIntent()
}