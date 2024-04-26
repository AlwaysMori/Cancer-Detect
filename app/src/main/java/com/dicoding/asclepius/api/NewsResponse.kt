package com.dicoding.asclepius.api

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<Article>
)

data class Article(
    @SerializedName("content") val content: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("source") val source: Source,
    @SerializedName("urlToImage") val urlToImage: String?
)

data class Source(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)
