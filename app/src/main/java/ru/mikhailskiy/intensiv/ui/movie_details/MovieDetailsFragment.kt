package ru.mikhailskiy.intensiv.ui.movie_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.feed_fragment.*
import kotlinx.android.synthetic.main.feed_header.*
import kotlinx.android.synthetic.main.search_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.mikhailskiy.intensiv.BuildConfig
import ru.mikhailskiy.intensiv.R
import ru.mikhailskiy.intensiv.data.Movie
import ru.mikhailskiy.intensiv.data.MoviesResponse
import ru.mikhailskiy.intensiv.network.MovieApiClient
import ru.mikhailskiy.intensiv.ui.afterTextChanged
import ru.mikhailskiy.intensiv.ui.feed.FeedFragment
import ru.mikhailskiy.intensiv.ui.feed.MainCardContainer
import ru.mikhailskiy.intensiv.ui.feed.MovieItem
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

    private fun fillMovieDetails() {
        //получаем детали фильма
        val getMovie = MovieApiClient.apiClient.getMovie(mId!!, MovieDetailsFragment.API_KEY, "ru")

        getMovie.enqueue(object : Callback<Movie> {

            override fun onFailure(call: Call<Movie>, error: Throwable) {
                // Логируем ошибку
                Timber.e(MovieDetailsFragment.TAG, error.toString())
            }

            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>
            ) {

                val movieDetails = response.body()
/*
                Заполняем фрагмент данными

*/
            }
        })
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