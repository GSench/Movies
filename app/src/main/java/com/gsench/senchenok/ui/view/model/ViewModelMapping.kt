package com.gsench.senchenok.ui.view.model

import com.gsench.senchenok.domain.model.Country
import com.gsench.senchenok.domain.model.Genre
import com.gsench.senchenok.domain.model.KinopoiskMovie
import com.gsench.senchenok.domain.model.KinopoiskMovieDetails
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