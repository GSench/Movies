package com.gsench.senchenok.kinopoisk_api

data class KinopoiskMovie(
    val countries: List<Country>?,
    val filmId: Int?,
    val filmLength: String?,
    val genres: List<Genre>?,
    val isAfisha: Int?,
    val isRatingUp: String?,
    val nameEn: String?,
    val nameRu: String?,
    val posterUrl: String?,
    val posterUrlPreview: String?,
    val rating: String?,
    val ratingChange: String?,
    val ratingVoteCount: Int?,
    val year: String?
)