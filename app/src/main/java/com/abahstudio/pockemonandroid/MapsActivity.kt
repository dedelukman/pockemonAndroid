package com.abahstudio.pockemonandroid

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        LoadPockemon()
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


    }

    var ACCESSLOCATION=123
    fun checkPermission(){
        if (Build.VERSION.SDK_INT>=23){
            if (ActivityCompat
                            .checkSelfPermission(this, android
                                    .Manifest
                                    .permission
                                    .ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android
                        .Manifest
                        .permission
                        .ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return

            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this,"User location acces on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later
        var myLocation= MylocationListener()
        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)
        var mythread=myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            ACCESSLOCATION->{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot acces your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Get user location
    var location:Location?=null
    inner class MylocationListener:LocationListener{


        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.latitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
           location=p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
           // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
           // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
           // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation:Location?=null
    inner class myThread:Thread{
        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }

        override fun run() {
            while (true){
                try {
                    if (oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread(){
                        mMap!!.clear()
                        // show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14f))

                        // show pockemons

                        for (i in 0..listPockemons.size-1){
                            var newPockemon=listPockemons[i]
                            if (newPockemon.IsCatch==false){
                                val pockemoLoc = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                    .position(pockemoLoc)
                                    .title(newPockemon.name!!)
                                    .snippet(newPockemon.des!!+" power: "+newPockemon!!.power)
                                    .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!))
                                )
                                if (location!!.distanceTo(newPockemon.location)<2){
                                    newPockemon.IsCatch=true
                                    listPockemons[i]=newPockemon
                                    playerPower+=newPockemon.power!!
                                    Toast.makeText(applicationContext," You catch new pokemon, your new power is "
                                            +playerPower, Toast.LENGTH_LONG).show()
                                }

                            }

                        }
                    }
                        Thread.sleep(1000)

                }catch (ex:Exception){

                }
            }
//            super.run()
        }
    }

    var playerPower=0.0
    var listPockemons=ArrayList<Pockemon>()

    fun LoadPockemon(){
        listPockemons.add(Pockemon(R.drawable.charmander,
            "Charmander", "Charmander living in bukit asri", 55.0, -6.60465964295, 106.761366819 ))
        listPockemons.add(Pockemon(R.drawable.bulbasaur,
            "Bulbasaur", "Bulbasaur living in laladon", 90.5, -6.58515508446, 106.756211721))
        listPockemons.add(Pockemon(R.drawable.squirtle,
            "Squirtle", "Squirtle living in Kedunghalang", 33.5,  -6.55398517454, 106.813423286))
    }
}
