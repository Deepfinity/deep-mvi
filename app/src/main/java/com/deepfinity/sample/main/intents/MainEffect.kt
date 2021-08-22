package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.Effect
import com.deepfinity.sample.main.models.MainModel

sealed class MainEffect : Effect {
    data class FetchedData(val data: MainModel) : MainEffect()
    data class Clear(val id: String) : MainEffect()

    object Nothing : MainEffect()
}