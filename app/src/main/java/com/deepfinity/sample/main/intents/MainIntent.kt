package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.Intent

sealed class MainIntent : Intent {
    object GetUserAccessLevel : MainIntent()

}