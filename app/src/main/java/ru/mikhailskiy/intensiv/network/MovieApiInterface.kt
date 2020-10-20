package ru.mikhailskiy.intensiv.network

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MovieCreditsResponse
import ru.mikhailskiy.intensiv.data.MoviesResponse


interface MovieApiInterface {

    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>

    @GET("tv/popular")
    fun getPopularTV(@Query("api_key") apiKey: String, @Query("language") language: String): Single<MoviesResponse>

    @GET("movie/{movie_id}")
    fun getMovie(@Path("movie_id") movie_id: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Single<Movie>

    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(@Path("movie_id") movie_id: Int, @Query("api_key") apiKey: String): Single<MovieCreditsResponse>

}
