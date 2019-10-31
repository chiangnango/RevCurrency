package com.example.revcurrency.architecture

import com.example.revcurrency.main.MainRepository
import com.example.revcurrency.main.MainViewModelFactory

object InjectorUtil {

    fun provideMainViewModelFactory(): MainViewModelFactory {
        return MainViewModelFactory(MainRepository())
    }
}