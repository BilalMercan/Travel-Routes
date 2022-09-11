package com.bm.travelroutes.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.AndroidRuntimeException
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.bm.travelroutes.R
import com.bm.travelroutes.databinding.ActivityMapsBinding
import com.bm.travelroutes.model.Places
import com.bm.travelroutes.roomdb.Dao
import com.bm.travelroutes.roomdb.RouteDB
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongtitude : Double? = null
    private lateinit var db : RouteDB
    private lateinit var Dao : Dao
    val compositeDisposable = CompositeDisposable()
    var placefromMain : Places? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPreferences = this.getSharedPreferences("com.bm.travelroutes", MODE_PRIVATE)
        trackBoolean = false
        selectedLatitude = 0.0
        selectedLongtitude = 0.0

        db = Room.databaseBuilder(applicationContext,RouteDB::class.java,"Places").build()

        Dao = db.placeDao()

        binding.savebtn.isEnabled = false

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        //latitude, lonitude

        val intent = intent
        val info = intent.getStringExtra("info")

        if ( info == "new") {

            binding.savebtn.visibility = View.VISIBLE
            binding.deletebtn.visibility = View.GONE

            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    trackBoolean = sharedPreferences.getBoolean("title",false)
                    if(trackBoolean == false){
                        val userLocation = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("title",true).apply()
                    }}
            }

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(binding.root,"Permission needed for Location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){

                        /* Request Permission  */
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()

                }else {
                    /* Request Permission  */
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

            } else {

                /* Permission Granted */
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
                val lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastlocation != null) {
                    val lastuserLocation = LatLng(lastlocation.latitude,lastlocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
                }
                mMap.isMyLocationEnabled = true
            }
        }
        else
        {
            mMap.clear()
            placefromMain = intent.getSerializableExtra("selectedPlace") as? Places

            placefromMain?.let {

                val latlng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))

                binding.Placetxt.setText(it.name)
                binding.savebtn.visibility = View.GONE
                binding.deletebtn.visibility = View.VISIBLE

            }


        }



        //Casting --->


    }

    private fun registerLauncher() {
    permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
        if (result) {

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                /* Permission Granted */
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0F,locationListener)
                val lastlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastlocation != null) {
                    val lastuserLocation = LatLng(lastlocation.latitude,lastlocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
                }
                mMap.isMyLocationEnabled = true
            }


        }else{
            /* Permission Denied */
            Toast.makeText(this@MapsActivity,"Permission Needed",Toast.LENGTH_LONG).show()
        }

    }

    }

    override fun onMapLongClick(p0: LatLng) {

        mMap.clear()

        mMap.addMarker(MarkerOptions().position(p0))
        selectedLatitude = p0.latitude
        selectedLongtitude = p0.longitude
        binding.savebtn.isEnabled = true


    }

    fun save (view: View) {

       val places = Places(binding.Placetxt.text.toString(),selectedLatitude!!,selectedLongtitude!!)
        compositeDisposable.add(
            Dao.insert(places)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(){
            val intent = Intent(this, MainActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

    }

    fun delete (view: View){

        placefromMain?.let {
            compositeDisposable.add(
                Dao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}

