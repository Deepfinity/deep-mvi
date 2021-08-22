package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepfinity.mvi.base.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

abstract class DeepStreamViewModel<STATE : State, INTENT : Intent, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    val reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>
) : ContainerHost<STATE, SIDE_EFFECT>, ViewModel() {

    val state: STATE get() = container.stateFlow.value

    override val container = container<STATE, SIDE_EFFECT>(this.initialState())
    abstract fun initialState(): STATE
    abstract fun initialIntent(): INTENT

    abstract suspend fun handleIntent(intent: INTENT): Flow<EFFECT>

    fun dispatchInitialIntent() = dispatch(initialIntent())

    fun dispatch(
        intent: INTENT,
    ) = intent {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                handleIntent(intent)
                    .map { effect -> reducer.reduce(this@DeepStreamViewModel.state, effect) }
                    .flowOn(Dispatchers.IO)
                    .catch { err ->
                        println(err)
                    }
                    .collect { result ->
                        try {
                            if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                            reduce {
                                result.state
                            }
                        } catch (err: Exception) {
                            println(err)
                        }
                    }
            }
        }
    }
}