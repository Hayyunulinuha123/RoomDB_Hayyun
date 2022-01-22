package com.example.roomdb_hayyun

import android.content.DialogInterface
import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdb_hayyun.room.Constant
import com.example.roomdb_hayyun.room.Movie
import com.example.roomdb_hayyun.room.MovieDb
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.roomdb_hayyun.MovieAdapter as MovieAdapter

class MainActivity : AppCompatActivity() {
    val db by lazy { MovieDb(this) }
    lateinit var movieAdapter: MovieAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()


    }

    override fun onStart() {
        super.onStart()
        loadmovie()
    }

    fun loadmovie() {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.movieDao().getMovies()
            Log.d("MainActivity", "dbresponse: $movies")
            withContext(Dispatchers.Main) {
                movieAdapter.setData(movies)
            }
        }
    }

    fun setupListener() {
        add_movie.setOnClickListener {
            intentEdit(0, Constant.TYPE_CREATE)
        }
    }

    fun intentEdit(movieId: Int, intentType: Int) {
        startActivity(
            Intent(applicationContext, AddActivity::class.java)
                .putExtra("intent_id", movieId)
                .putExtra("intent_type", intentType)
        )
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(arrayListOf(), object : MovieAdapter.OnAdapterListener {
            override fun onClick(movie: Movie) {
                intentEdit(movie.id, Constant.TYPE_READ)
            }

            override fun onUpdate(movie: Movie) {
                intentEdit(movie.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(movie: Movie) {
                deleteDialog(movie)
            }

        })
        rv_movie.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = movieAdapter
        }
    }

    private fun deleteDialog(movie: Movie) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin mau hapus ${movie.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.movieDao().deleteMovie(movie)
                    loadmovie()
                }
            }
            alertDialog.show()
        }
    }
}