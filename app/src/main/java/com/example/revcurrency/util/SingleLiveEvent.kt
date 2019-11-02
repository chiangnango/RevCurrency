package com.example.revcurrency.util

import android.util.Log
import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class SingleLiveEvent<T> : MediatorLiveData<T>() {

    private val observers = ArraySet<SingleLiveEventObserverWrapper<T>>()

    override fun observeForever(observer: Observer<in T>) {
        val wrapper = SingleLiveEventObserverWrapper(observer = observer)
        observers.add(wrapper)
        super.observeForever(wrapper)
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (observers.find { it.owner == owner } != null) {
            Log.i(SingleLiveEvent::class.java.simpleName, "$owner had observed return it")
            return
        }

        val wrapper = SingleLiveEventObserverWrapper(owner, observer)
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val targetObserver = (observer as? SingleLiveEventObserverWrapper)?.observer ?: observer

        observers.find { it.observer == targetObserver }?.let {
            observers.remove(it)
            super.removeObserver(it)
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.newValue() }
        super.setValue(t)
    }

    private class SingleLiveEventObserverWrapper<T>(
        val owner: LifecycleOwner? = null, val observer: Observer<in T>
    ) : Observer<T> {

        private var pending = false

        override fun onChanged(t: T?) {
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending = true
        }
    }
}
