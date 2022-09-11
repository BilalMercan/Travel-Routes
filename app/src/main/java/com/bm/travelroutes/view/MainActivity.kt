package com.bm.travelroutes.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bm.travelroutes.R
import com.bm.travelroutes.adapter.PlacesAdapter
import com.bm.travelroutes.databinding.ActivityMainBinding
import com.bm.travelroutes.model.Places
import com.bm.travelroutes.roomdb.RouteDB
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {



private lateinit var binding: ActivityMainBinding
private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db = Room.databaseBuilder(applicationContext,RouteDB::class.java,"Places").build()
        val Dao = db.placeDao()

        compositeDisposable.add(
            Dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponsable)
        )

    }

    private fun handleResponsable (placeList: List<Places>){

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = PlacesAdapter(placeList)
        binding.recyclerview.adapter = adapter


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_place){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)

        }

        //Another Activity Transaction
        if (item.itemId == R.id.go_set){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)

        }
        if (item.itemId == R.id.go_about){
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)

        }



        return super.onOptionsItemSelected(item)
    }




}