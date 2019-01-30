package com.amqo.forecastapp.internal

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Deferred

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, block: (T?) -> Unit) {
    liveData.observe(this, Observer(block))
}

suspend fun <T : Any, L : Deferred<LiveData<out T>>> LifecycleOwner.consume(
    deferred: L, block: (param: T) -> Unit) {
    deferred.await().observe(this, Observer {
        it?.let (block)
    })
}

suspend fun <T : Any, L : Deferred<LiveData<out T>>> LifecycleOwner.consume(
    deferred: L,  block: (param: T) -> Unit, blockError: () -> Unit = {}) {
    deferred.await().observe(this, Observer {
        it?.let(block) ?: blockError()
    })
}