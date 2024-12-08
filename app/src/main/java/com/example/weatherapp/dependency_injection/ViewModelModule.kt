package com.example.weatherapp.dependency_injection

import com.example.weatherapp.fragment.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {HomeViewModel (weatherDataRepository = get()) }
}