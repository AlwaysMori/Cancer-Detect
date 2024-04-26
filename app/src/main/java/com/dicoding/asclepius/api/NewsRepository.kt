package com.dicoding.asclepius.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


class NewsRepository {
    fun getHealthNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiClient.newsApiService.searchHealthNews("cancer", "health", "en", ApiConfig.API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        val newsList = articles.mapNotNull { article ->
                            if (!article.title.isNullOrEmpty() && !article.urlToImage.isNullOrEmpty() && !article.url.isNullOrEmpty()) {
                                NewsItem(article.title, article.urlToImage, article.url)
                            } else {
                                null
                            }
                        }
                        onSuccess(newsList)
                    } else {
                        onFailure("Gagal mengunduh berita")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    onFailure(t.message ?: "Error tidak diketahui")
                }
            })
    }
}
