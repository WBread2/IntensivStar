package ru.mikhailskiy.intensiv.ui.movie_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.movie_details_fragment.*
import kotlinx.android.synthetic.main.tv_shows_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.BuildConfig
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.feed.FeedFragment
import ru.mikhailskiy.intensiv.ui.tvshows.TVShowItem
import ru.mikhailskiy.intensiv.ui.tvshows.TvShowsFragment
import timber.log.Timber

const val ARG_TITLE = "title"
const val ARG_ID = "id"

class MovieDetailsFragment : Fragment() {

    private var mTitle: String? = null
    private var mId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mTitle = it.getString(ARG_TITLE)
            mId = it.getInt(ARG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillMovieDetails()
    }

    @SuppressLint("CheckResult")
    private fun fillMovieDetails() {
        //получаем детали фильма
        val getMovie = MovieApiClient.apiClient.getMovie(mId!!, API_KEY, "ru")

        getMovie
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {response ->
                    val movieDetails = response

                    tvTitle.setText(movieDetails?.title)

                    Picasso.get()
                        .load(movieDetails?.backdropPath)
                        .into(ivPoster)
/*
                Заполняем фрагмент данными

*/
                },
                { error ->
                    // Логируем ошибку
                    Timber.e(TAG, error.toString())
                }
            )
    }

    companion object {

        private val TAG = FeedFragment::class.java.simpleName

        const val API_KEY = BuildConfig.THE_MOVIE_DATABASE_API

        @JvmStatic
        fun newInstance(title: String, id: String) =
            MovieDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_ID, id)
                }
            }
    }
}