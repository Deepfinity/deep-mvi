package com.deepfinity.mvi.base


abstract class Reducer<STATE : State, EFFECT : Effect, SIDE_EFFECT : SideEffect> {
    abstract suspend fun reduce(state: STATE, effect: EFFECT): ReduceResult<STATE, SIDE_EFFECT>
}

class ReduceResult<STATE : State, SIDE_EFFECT : SideEffect>(
    val state: STATE,
    val sideEffect: SIDE_EFFECT? = null
)
