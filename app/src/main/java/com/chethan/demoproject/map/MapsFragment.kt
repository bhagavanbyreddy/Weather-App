package com.chethan.demoproject.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.chethan.demoproject.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    var mGoogleMap: GoogleMap? = null
    var mLocationRequest: LocationRequest? = null
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var sendSms: Button? = null
    var geocoder: Geocoder? = null
    var markerOptions: MarkerOptions? = null
    lateinit var navigationController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireContext().supportActionBar!!.title = "Google Map Sms"
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(
            requireContext(),
            Locale.getDefault()
        )
        markerOptions = MarkerOptions()
        navigationController = Navigation.findNavController(view)

        mapView.onCreate(savedInstanceState)

        mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /*fun sendingSms() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            "+254700000000",
            null,
            "https://www.google.com/maps/dir/?api=1&destination=lat,lng",
            null,
            null
        )
        Toast.makeText(
            applicationContext, "SMS SENT",
            Toast.LENGTH_LONG
        ).show()
    }*/

    public override fun onPause() {
        super.onPause()

        mapView.onPause()

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 120000 // two minute interval
        mLocationRequest!!.fastestInterval = 120000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    Looper.myLooper()
                )
                mGoogleMap!!.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
            mGoogleMap!!.isMyLocationEnabled = true
        }

        mGoogleMap!!.setOnMapClickListener(this)

    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }

                val latLng = LatLng(location.latitude, location.longitude)

                //Showing Current Location Marker on Map
                markerOptions!!.position(latLng)
                val locationManager =
                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = locationManager.getBestProvider(Criteria(), true)
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                val locations = locationManager.getLastKnownLocation(provider)
                val providerList = locationManager.allProviders
                if (null != locations && null != providerList && providerList.size > 0) {
                    val longitude = locations.longitude
                    val latitude = locations.latitude

                    try {
                        val listAddresses: List<Address>? = geocoder!!.getFromLocation(
                            latitude,
                            longitude, 1
                        )
                        if (null != listAddresses && listAddresses.size > 0) {
                            val state: String = listAddresses[0].getAdminArea()
                            val country: String = listAddresses[0].getCountryName()
                            //val subLocality: String = listAddresses[0].getSubLocality()
                            val city: String = listAddresses[0].locality
                            val featureName: String = listAddresses[0].featureName
                            val postalCode: String = listAddresses[0].postalCode
                            markerOptions!!.title(
                                "" + featureName + "," + postalCode + "," + city + "," + state + "," + country
                            )
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                markerOptions!!.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                mCurrLocationMarker = mGoogleMap!!.addMarker(markerOptions)
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                val cameraPosition =
                    CameraPosition.Builder().target(LatLng(latLng.latitude, latLng.longitude))
                        .zoom(16f).build()
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { dialogInterface, i -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        mFusedLocationClient!!.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback, Looper.myLooper()
                        )
                        mGoogleMap!!.isMyLocationEnabled = true
                    }
                } else {
                    // if not allow a permission, the application will exit
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    requireActivity().finish()
                    System.exit(0)
                }
            }
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        const val REQUEST_KEY = "MapsFragment_Request_Key"
    }

    override fun onMapClick(latLng: LatLng?) {

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        Log.e("onMapClick: ", latLng.toString())
        try {
            val addresses: List<Address> =
                geocoder!!.getFromLocation(latLng!!.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                mCurrLocationMarker = mGoogleMap!!.addMarker(
                    markerOptions!!
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                )

                var location = com.chethan.demoproject.model.Location(
                    name = streetAddress,
                    lat = latLng.latitude,
                    long = latLng.longitude
                )

                val handler = Handler()
                handler.postDelayed({
                    setNavigationResult(location, "resultKey")
                    navigationController.popBackStack(R.id.locationsListFragment, false)
                }, 1000)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun Fragment.setNavigationResult(result: com.chethan.demoproject.model.Location, key: String = "resultKey") {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}