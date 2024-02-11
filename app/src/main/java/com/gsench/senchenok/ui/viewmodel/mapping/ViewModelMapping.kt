package com.gsench.senchenok.ui.viewmodel.mapping

import com.gsench.senchenok.kinopoisk_api.Country
import com.gsench.senchenok.kinopoisk_api.Genre
import com.gsench.senchenok.ui.viewmodel.MovieListItem
import com.gsench.senchenok.kinopoisk_api.KinopoiskMovie
import com.gsench.senchenok.kinopoisk_api.KinopoiskMovieDetails
import com.gsench.senchenok.ui.viewmodel.MovieDetails
import java.util.Locale

fun KinopoiskMovie.toMovieViewModel() = MovieListItem(
    id = filmId ?: -1,
    title = coalesce(nameRu, nameEn, "???"),
    genre = genres.toGenresString(limit = 3),
    year = year?:"",
    iconUrl = posterUrlPreview?:""
)

fun KinopoiskMovieDetails.toMovieDetailsViewModel() = MovieDetails (
    title = coalesce(nameRu, nameOriginal, nameEn, "???"),
    posterUrl = posterUrl ?: "",
    description = description ?: "",
    genres = genres.toGenresString(),
    countries = countries.toCountriesString(),
)

fun List<Genre?>?.toGenresString(limit: Int = this?.size ?: 0): String {
    if(this == null) return ""
    return this
        .filterNotNull()
        .mapNotNull { it.genre }
        .joinToString(limit = limit) { capitalize(it) }
}

fun List<Country?>?.toCountriesString(limit: Int = this?.size ?: 0): String {
    if(this == null) return ""
    return this
        .filterNotNull()
        .mapNotNull { it.country }
        .joinToString(limit = limit) { capitalize(it) }
}

private fun coalesce(vararg string: String?) = string.firstNotNullOf { it }

private fun capitalize(str: String) = str.replaceFirstChar {
    if (it.isLowerCase())
        it.titlecase(Locale.getDefault())
    else
        it.toString()
}