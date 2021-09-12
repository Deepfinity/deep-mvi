package com.deepfinity.sample.main.viewmodels

import com.deepfinity.mvi.viewmodel.DeepStreamViewModel
import com.deepfinity.sample.main.domain.MainInteractor
import com.deepfinity.sample.main.intents.*
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.rx2.asFlow


class MainViewModel(private val interactor: MainInteractor) :
    DeepStreamViewModel<MainViewState, MainIntent, MainEffect, MainSideEffect>(
        MainReducer()
    ) {

    override fun initialState(): MainViewState = MainViewState()

    override suspend fun handleIntent(intent: MainIntent): Flow<MainEffect> {
        when (intent) {
            MainIntent.UpdateText -> interactor.processText(intent.)
            MainIntent.UpdateTextObservable -> interactor.processTextObservable(intent)
                .subscribeOn(Schedulers.io()).asFlow()
            MainIntent.UpdateTextSingle ->
                interactor.processTextSingle(intent.text)
                    .subscribeOn(Schedulers.io())
                    .toObservable().asFlow()
        }
    }

    override fun reporter(error: Throwable) = println(error)
}