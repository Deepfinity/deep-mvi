package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.ViewModel
import com.deepfinity.mvi.base.Effect
import com.deepfinity.mvi.base.Reducer
import com.deepfinity.mvi.base.SideEffect
import com.deepfinity.mvi.base.State
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

abstract class DeepViewModel<STATE : State, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    val reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>
) : ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {

    val state: STATE get() = container.stateFlow.value

    override val container = container<STATE, SIDE_EFFECT>(this.initialState())
    abstract fun initialState(): STATE

}

