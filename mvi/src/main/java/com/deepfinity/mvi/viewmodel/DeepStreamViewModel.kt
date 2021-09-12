package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.viewModelScope
import com.deepfinity.mvi.base.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

abstract class DeepStreamViewModel<STATE : State, INTENT : Intent, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<STATE, EFFECT, SIDE_EFFECT>(reducer, dispatcher) {

    protected fun initialIntent(): INTENT? = null

    protected abstract suspend fun handleIntent(intent: INTENT): Flow<EFFECT>

    fun dispatchInitialIntent() = initialIntent()?.also { dispatch(it) }

    fun dispatch(
        intent: INTENT
    ) = intent {
        viewModelScope.launch {
            withContext(dispatcher) {
                handleIntent(intent)
                    .map { effect -> reducer.reduce(this@DeepStreamViewModel.state, effect) }
                    .flowOn(dispatcher)
                    .catch { err ->
                        reporter(err)
                    }
                    .collect { result ->
                        try {
                            if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                            reduce {
                                result.state
                            }
                        } catch (err: Exception) {
                            reporter(err)
                        }
                    }
            }
        }
    }
}