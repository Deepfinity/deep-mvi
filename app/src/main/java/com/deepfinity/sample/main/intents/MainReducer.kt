package com.deepfinity.sample.main.intents

import com.deepfinity.mvi.base.ReduceResult
import com.deepfinity.mvi.base.Reducer


class MainReducer constructor(
) : Reducer<MainViewState, MainEffect, MainSideEffect>() {

    override suspend fun reduce(
        state: MainViewState,
        effect: MainEffect
    ): ReduceResult<MainViewState, MainSideEffect> =
        when (effect) {
            is MainEffect.Clear -> TODO()
            is MainEffect.FetchedData -> TODO()
            MainEffect.Nothing -> TODO()
        }.let { ReduceResult(it) }
}

