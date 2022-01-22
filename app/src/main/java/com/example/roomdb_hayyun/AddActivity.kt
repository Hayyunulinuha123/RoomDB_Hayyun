package com.example.roomdb_hayyun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.roomdb_hayyun.room.Constant
import com.example.roomdb_hayyun.room.Movie
import com.example.roomdb_hayyun.room.MovieDb
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    val db by lazy { MovieDb(this) }
    private var movieId :  Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setupView()
        setupListener()
    }

    fun setupView(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val intentType= intent.getIntExtra("intent_type",0)
        when (intentType) {
            Constant.TYPE_CREATE -> {

            }
            Constant.TYPE_READ -> {
                btn_save.visibility = View.GONE
                btn_update.visibility = View.GONE
                getMovie()
            }
            Constant.TYPE_UPDATE -> {
                btn_save.visibility = View.GONE
                getMovie()
            }
        }
    }

    fun setupListener(){
        btn_save.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                db.movieDao().addMovie(
                    Movie(0,et_title.text.toString(),
                    et_description.text.toString())
                )

                finish()
            }
        }
        btn_update.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                db.movieDao().updateMovie(
                    Movie(movieId,et_title.text.toString(),
                        et_description.text.toString())
                )

                finish()
            }
        }
    }
    fun getMovie(){
        movieId = intent.getIntExtra("intent_id",0)
        CoroutineScope(Dispatchers.IO).launch {
            val movie =db.movieDao().getMovies(movieId)[0]
            et_title.setText(movie.title)
            et_description.setText(movie.title)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}