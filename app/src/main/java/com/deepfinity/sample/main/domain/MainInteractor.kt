package com.deepfinity.sample.main.domain

import com.deepfinity.sample.main.intents.MainEffect
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

class MainInteractor {

    suspend fun processText(text: String): MainEffect {
        delay(1000L)
        return MainEffect.ProcessedText(text.trim())
    }

    fun processTextFlow(text: String) =
        flowOf(MainEffect.ProcessedText(text.trim())).onEach { delay(100) }

    fun processTextObservable(text: String): Observable<String> =
        Observable.just(text.trim()).delay(100, TimeUnit.MILLISECONDS)

    fun processTextSingle(text: String): Single<String> =
        Single.just(text.trim()).delay(100, TimeUnit.MILLISECONDS)

}