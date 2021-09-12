package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.Effect

sealed class MainEffect : Effect {
    data class ProcessedText(val text: String) : MainEffect()

    object Nothing : MainEffect()
}