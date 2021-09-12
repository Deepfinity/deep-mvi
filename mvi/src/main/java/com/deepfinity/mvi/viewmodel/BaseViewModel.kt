package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.ViewModel
import com.deepfinity.mvi.base.Effect
import com.deepfinity.mvi.base.Reducer
import com.deepfinity.mvi.base.SideEffect
import com.deepfinity.mvi.base.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container


abstract class BaseViewModel<STATE : State, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    val reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>,
    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {

    val state: STATE get() = container.stateFlow.value
    protected abstract fun reporter(error: Throwable)

    override val container = container<STATE, SIDE_EFFECT>(this.initialState())
    protected abstract fun initialState(): STATE

}

