package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.viewModelScope
import com.deepfinity.mvi.base.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

abstract class DeepViewModel<STATE : State, EFFECT : Effect, SIDE_EFFECT : SideEffect>(
    reducer: Reducer<STATE, EFFECT, SIDE_EFFECT>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<STATE, EFFECT, SIDE_EFFECT>(reducer, dispatcher) {

    protected suspend fun dispatch(
        intent: suspend () -> EFFECT
    ): Unit = intent {
        viewModelScope.launch {
            withContext(dispatcher) {
                val result = reducer.reduce(this@DeepViewModel.state, intent())
                if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                reduce {
                    result.state
                }
            }
        }
    }

    @JvmName("dispatchFlow")
    protected suspend fun dispatch(
        intent: suspend () -> Flow<EFFECT>
    ): Unit = intent {
        viewModelScope.launch {
            withContext(dispatcher) {
                intent()
                    .map { effect -> reducer.reduce(this@DeepViewModel.state, effect) }
                    .catch { err ->
                        reporter(err)
                    }
                    .flowOn(dispatcher)
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

    @JvmName("dispatchSingle")
    protected fun dispatch(
        intent: () -> Single<EFFECT>
    ): Unit = intent {
        viewModelScope.launch {
            withContext(dispatcher) {
                val effect = intent()
                    .subscribeOn(Schedulers.io())
                    .doOnError { err ->
                        reporter(err)
                    }.await()

                try {
                    val result = reducer.reduce(this@DeepViewModel.state, effect)
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

    protected fun dispatch(
        intent: () -> Observable<EFFECT>
    ): Unit = intent {
        viewModelScope.launch {
            withContext(dispatcher) {
                intent()
                    .subscribeOn(Schedulers.io())
                    .asFlow()
                    .map { effect ->
                        reducer.reduce(this@DeepViewModel.state, effect)
                    }
                    .catch { err ->
                        reporter(err)
                    }
                    .flowOn(dispatcher)
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
