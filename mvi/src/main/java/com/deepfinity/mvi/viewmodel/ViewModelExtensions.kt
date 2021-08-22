package com.deepfinity.mvi.viewmodel

import androidx.lifecycle.viewModelScope
import com.deepfinity.mvi.base.Effect
import com.deepfinity.mvi.base.SideEffect
import com.deepfinity.mvi.base.State
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce


suspend fun <STATE : State, EFFECT : Effect, SIDE_EFFECT
: SideEffect> DeepViewModel<STATE, EFFECT, SIDE_EFFECT>.handle(
    intent: suspend () -> EFFECT,
): Unit = intent {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val result = reducer.reduce(this@handle.state, intent())
            if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
            reduce {
                result.state
            }
        }
    }
}

@JvmName("handleFlow")
suspend fun <STATE : State, EFFECT : Effect, SIDE_EFFECT
: SideEffect> DeepViewModel<STATE, EFFECT, SIDE_EFFECT>.handle(
    intent: suspend () -> Flow<EFFECT>,
): Unit = intent {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            intent()
                .map { effect -> reducer.reduce(this@handle.state, effect) }
                .catch { err ->
                    println(err) // TODO
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    try {
                        if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                        reduce {
                            result.state
                        }
                    } catch (err: Exception) {
                        println(err) // TODO
                    }
                }
        }
    }
}

@JvmName("handleSingle")
fun <STATE : State, EFFECT : Effect, SIDE_EFFECT
: SideEffect> DeepViewModel<STATE, EFFECT, SIDE_EFFECT>.handle(
    intent: () -> Single<EFFECT>,
): Unit = intent {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val effect = intent()
                .subscribeOn(Schedulers.io())
                .doOnError { err ->
                    println(err) // TODO
                }.await()

            try {
                val result = reducer.reduce(this@handle.state, effect)
                if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                reduce {
                    result.state
                }
            } catch (err: Exception) {
                println(err) // TODO
            }
        }
    }
}

fun <STATE : State, EFFECT : Effect, SIDE_EFFECT
: SideEffect> DeepViewModel<STATE, EFFECT, SIDE_EFFECT>.handle(
    intent: () -> Observable<EFFECT>,
): Unit = intent {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            intent()
                .subscribeOn(Schedulers.io())
                .asFlow()
                .map { effect ->
                    reducer.reduce(this@handle.state, effect)
                }
                .catch { err ->
                    println(err) // TODO
                }
                .flowOn(Dispatchers.IO)
                .collect { result ->
                    try {
                        if (result.sideEffect != null) this@intent.postSideEffect(result.sideEffect)
                        reduce {
                            result.state
                        }
                    } catch (err: Exception) {
                        println(err) // TODO
                    }
                }
        }
    }
}