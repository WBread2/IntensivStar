package ru.mikhailskiy.intensiv.ui.tvshows

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import kotlinx.android.synthetic.main.tv_shows_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.BuildConfig
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import ru.mikhailskiy.intensiv.ui.feed.FeedFragment
import ru.mikhailskiy.intensiv.ui.feed.MainCardContainer
import ru.mikhailskiy.intensiv.ui.feed.MovieItem
import timber.log.Timber

class TvShowsFragment : Fragment() {
    private val adapter by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tv_shows_fragment, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Добавляем recyclerView
        tv_shows_recycler_view.layoutManager = LinearLayoutManager(context)
        tv_shows_recycler_view.adapter = adapter.apply { addAll(listOf()) }

        //получаем идущие телешоу
        val getPopularTV = MovieApiClient.apiClient.getPopularTV(API_KEY, "ru")

        getPopularTV
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {response ->
                    val popularTVs = response.results

                    val popularTVsList1 =
                        popularTVs.map {
                            TVShowItem(it) { movie ->
//                        openMovieDetails(movie)
                            }
                        }.toList()

                    tv_shows_recycler_view.adapter = adapter.apply {
                        addAll(popularTVsList1)
                    }
                },
                { error ->
                    // Логируем ошибку
                    Timber.e(TAG, error.toString())
                }
            )
    }

    override fun onStop() {
        super.onStop()
        adapter.clear()
    }


    companion object {
        private val TAG = TvShowsFragment::class.java.simpleName

        const val API_KEY = BuildConfig.THE_MOVIE_DATABASE_API
    }
}