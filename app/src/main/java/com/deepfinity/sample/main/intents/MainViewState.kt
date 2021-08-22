package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.State
import com.deepfinity.sample.main.models.MainModel

data class MainViewState(
    val someState: MainModel? = null
) : State