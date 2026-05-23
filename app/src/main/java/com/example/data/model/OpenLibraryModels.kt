package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubjectResponse(
    @Json(name = "name") val name: String,
    @Json(name = "works") val works: List<Work>
)

@JsonClass(generateAdapter = true)
data class Work(
    @Json(name = "key") val key: String,
    @Json(name = "title") val title: String,
    @Json(name = "authors") val authors: List<WorkAuthor>?,
    @Json(name = "cover_id") val coverId: Long?,
    @Json(name = "first_publish_year") val firstPublishYear: Int?
) {
    fun getCoverUrl(): String? {
        return if (coverId != null && coverId > 0) {
            "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
        } else {
            null
        }
    }
}

@JsonClass(generateAdapter = true)
data class WorkAuthor(
    @Json(name = "name") val name: String
)
