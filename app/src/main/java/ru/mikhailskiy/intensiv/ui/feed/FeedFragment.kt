package ru.mikhailskiy.intensiv.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.BuildConfig
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.MockRepository
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import ru.mikhailskiy.intensiv.ui.movie_details.ARG_ID
import ru.mikhailskiy.intensiv.ui.movie_details.ARG_TITLE
import ru.mikhailskiy.intensiv.ui.onTextChangedObservable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FeedFragment : Fragment() {

    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавляем recyclerView
        movies_recycler_view.layoutManager = LinearLayoutManager(context)
        movies_recycler_view.adapter = adapter.apply { addAll(listOf()) }

        search_toolbar
            .onTextChangedObservable
            .map { it.trim() }
            .debounce(500, TimeUnit.MILLISECONDS)
            .filter { it.isNotEmpty() }
            .subscribe (
                {
                    Timber.d(it.toString())
                    if (it.toString().length > 3) {
                        openSearch(it.toString())
                    }
                },
                {error ->
                    Timber.e(TAG, error.toString())
                }
            )

/*
        search_toolbar.search_edit_text.afterTextChanged {
            Timber.d(it.toString())
            if (it.toString().length > 3) {
                openSearch(it.toString())
            }
        }
*/
        fillNowPlayingMovies()

        fillUpcomingMovies()

        fillPopularMovies()
    }

    @SuppressLint("CheckResult")
    private fun fillNowPlayingMovies() {
        //получаем идущие фильмы
        val getNowPlayingMovies = MovieApiClient.apiClient.getNowPlayingMovies(API_KEY, "ru")

        getNowPlayingMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {response ->
                    val nowPlayingMovies = response.results

                    val nowPlayingMoviesList = listOf(
                        MainCardContainer(
                            R.string.now_playing,
                            nowPlayingMovies.map {
                                MovieItem(it) { movie ->
                                    openMovieDetails(movie)
                                }
                            }.toList()
                        )
                    )

                    movies_recycler_view.adapter = adapter.apply { addAll(nowPlayingMoviesList) }
                },
                { error ->
                    // Логируем ошибку
                    Timber.e(TAG, error.toString())
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun fillUpcomingMovies() {
        //получаем будущие фильмы
        val getUpcomingMovies = MovieApiClient.apiClient.getUpcomingMovies(API_KEY, "ru")

        getUpcomingMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {response ->
                    val upcomingMovies = response.results

                    val upcomingMoviesList = listOf(
                        MainCardContainer(
                            R.string.upcoming,
                            upcomingMovies.map {
                                MovieItem(it) { movie ->
                                    openMovieDetails(movie)
                                }
                            }.toList()
                        )
                    )

                    adapter.apply { addAll(upcomingMoviesList) }
                },
                { error ->
                    // Логируем ошибку
                    Timber.e(TAG, error.toString())
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun fillPopularMovies() {
        //получаем популярные фильмы
        val getPopularMovies = MovieApiClient.apiClient.getPopularMovies(API_KEY, "ru")

        getPopularMovies
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {response ->
                    val popularMovies = response.results

                    val popularMoviesList = listOf(
                        MainCardContainer(
                            R.string.popular,
                            popularMovies.map {
                                MovieItem(it) { movie ->
                                    openMovieDetails(movie)
                                }
                            }.toList()
                        )
                    )

                    adapter.apply { addAll(popularMoviesList) }
                },
                { error ->
                    // Логируем ошибку
                    Timber.e(TAG, error.toString())
                }
            )
    }

    private fun openMovieDetails(movie: Movie) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val bundle = Bundle()
        bundle.putString(ARG_TITLE, movie.title)
        bundle.putInt(ARG_ID, movie.id!!)
        findNavController().navigate(R.id.movie_details_fragment, bundle, options)
    }

    private fun openSearch(searchText: String) {
        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        val bundle = Bundle()
        bundle.putString("search", searchText)
        findNavController().navigate(R.id.search_dest, bundle, options)
    }

    override fun onStop() {
        super.onStop()
        search_toolbar.clear()
        adapter.clear()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    companion object {
        private val TAG = FeedFragment::class.java.simpleName

        const val API_KEY = BuildConfig.THE_MOVIE_DATABASE_API
    }
}