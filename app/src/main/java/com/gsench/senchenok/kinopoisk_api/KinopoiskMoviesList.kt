package com.gsench.senchenok.kinopoisk_api

data class KinopoiskMoviesList(
    val films: List<KinopoiskMovie>,
    val pagesCount: Int,
)