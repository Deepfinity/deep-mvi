package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.ReduceResult
import com.deepfinity.mvi.base.Reducer


class MainReducer(
) : Reducer<MainViewState, MainEffect, MainSideEffect>() {

    override suspend fun reduce(
        state: MainViewState,
        effect: MainEffect
    ): ReduceResult<MainViewState, MainSideEffect> =
        when (effect) {
            is MainEffect.ProcessedText -> {
                state.copy(text = effect.text)
            }
            MainEffect.Nothing -> {
                state
            }
        }.let { ReduceResult(it) }
}

