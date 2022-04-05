package com.qiet.modules.fragments.mapalarm

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.google.maps.android.SphericalUtil
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.Layer
import com.qiet.R
import com.qiet.models.Alarm
import com.qiet.modules.activities.base.BaseActivity
import com.qiet.utils.Application
import com.qiet.utils.ApplicationConstants
import com.qiet.utils.PrefsManager
import com.qiet.workflow.QietWorkflow
import kotlin.math.roundToInt

class MapAlarmFragment : Fragment(), PermissionsListener, MapAlarmView {
    private lateinit var prefsManager: PrefsManager
    private lateinit var mMapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var locationComponent: LocationComponent
    private var isARouteSelected: Boolean = false
    private lateinit var mapView: MapView
    private var markerViewManager: MarkerViewManager? = null
    private lateinit var currentSelectedChallengeMarker: ImageView
    private var previousSelectedChallengeMarker: ImageView? = null
    private var placeName: String = ""
    private lateinit var waterLayer: Layer
    private var listLatLng: ArrayList<LatLng> = ArrayList()
    private lateinit var presenter: MapAlarmPresenter
    private var userLocation: Location? = null
    private var workflow = QietWorkflow()
    private var hasbeeninitialized = false
    private lateinit var tvAlarmStatus: TextView
    private lateinit var tvSwitchLabel: TextView
    private lateinit var tvCoordinatesUser: TextView
    private lateinit var tvDistance: TextView
    private lateinit var switchAlarm: SwitchCompat
    private lateinit var tvModeAlarm: TextView
    private lateinit var switchModeAutoAlarm: SwitchCompat
    private var distanceBetweenUserAndAlarm = 0

    private lateinit var markerAlarmView: View
    private var alarm = Alarm()
    private lateinit var mView: View
    private lateinit var tvHelloFirstName: TextView
    private lateinit var imbLogoff: ImageButton
    private lateinit var baseActivity: BaseActivity

    private var addreQiet = "29 rue des Pyramides, 75001 Paris"
    private var latQiet = 48.866241
    private var longQiet =  2.33379
    private var enabledQiet = false
    private var iconQiet = R.layout.marker_view_alarm
    private var precisionQiet = 20

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        baseActivity = activity as BaseActivity
        Mapbox.getInstance(requireActivity(), ApplicationConstants.QIET_TOKEN_PUBLIC_MAP_BOX)
        Mapbox.setAccessToken(ApplicationConstants.QIET_TOKEN_PUBLIC_MAP_BOX)
        mView = inflater.inflate(R.layout.fragment_map_alarm, container, false)
        workflow = Application.getQietWorkflow()
        presenter = MapAlarmPresenter(this, workflow)
        initAlarm()
        initViews()
        fillViews()
        initPrefs()
        initListeners()
        return mView
    }

    private fun initPrefs() {
        ButterKnife.bind(requireActivity())
        prefsManager = PrefsManager(requireContext())
    }

    private fun initAlarm() {
        workflow.alarm = Alarm()
        workflow.alarm.apply {
            address = addreQiet
            longitude = longQiet
            latitude = latQiet
            precision = precisionQiet
            enabled = enabledQiet
            icon = iconQiet
        }
        Application.updateQietWorkflow(workflow)
        workflow = Application.getQietWorkflow()
    }

    private fun initListeners() {
        switchAlarm.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> refreshInfos(
                    R.color.colorQietPrimary,
                    isChecked,
                    "Alarme active",
                    distanceBetweenUserAndAlarm.toString()
                )
                false -> refreshInfos(
                    R.color.colorRedError,
                    isChecked,
                    "Alarme inactive",
                    distanceBetweenUserAndAlarm.toString()
                )
            }
        }

        switchModeAutoAlarm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.mode_auto_msg),
                    Toast.LENGTH_LONG
                ).show()
            }
            initAlarmManagement()
            initDistanceRules()
        }
        imbLogoff.setOnClickListener { Application.logout(baseActivity, prefsManager) }
    }

    private fun initViews() {
        mapView = mView.findViewById(R.id.mapView_alarm)
        tvAlarmStatus = mView.findViewById(R.id.tv_alarm_status)
        tvCoordinatesUser = mView.findViewById(R.id.tv_coordonates)
        tvDistance = mView.findViewById(R.id.tv_distance)
        tvSwitchLabel = mView.findViewById(R.id.tv_switch)
        switchAlarm = mView.findViewById(R.id.switch_status_value)
        tvModeAlarm = mView.findViewById(R.id.tv_switch_mode_alarm)
        switchModeAutoAlarm = mView.findViewById(R.id.switch_mode_alarm_value)
        tvHelloFirstName = mView.findViewById(R.id.tv_name)
        imbLogoff = mView.findViewById(R.id.imb_logoff)
    }

    fun fillViews() {
        tvHelloFirstName.text = "Hello " + workflow.user.firstname
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapAsync()
    }

    fun mapAsync() {
        mapView.getMapAsync { mapboxMap ->
            mMapboxMap = mapboxMap
            markerViewManager = MarkerViewManager(mapView, mMapboxMap)
            //SYNC MAP
            mMapboxMap.uiSettings.isCompassEnabled = false
            mMapboxMap.uiSettings.setCompassMargins(0, 50, 0, 0)
            mMapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
                if (style.isFullyLoaded) {
                    waterLayer = style.getLayer("water")!!
                    enableLocationComponent()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) =
        Toast.makeText(requireActivity(), "onExplanationNeeded", Toast.LENGTH_LONG).show()


    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            lastPositionUser()
            mMapboxMap.getStyle { style ->
                enableLocationComponent()
            }
        } else {
            val toast =
                Toast.makeText(requireActivity(), "Permission not granted", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            requireParentFragment().requireActivity().finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() =
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {
            init()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this.activity)
        }

    @SuppressLint("MissingPermission")
    fun init() {
        val options = LocationComponentOptions.builder(requireActivity())
            .foregroundDrawable(R.drawable.ic_arrow_head) //marker user
            .bearingTintColor(R.color.colorRedError)
            .accuracyAlpha(0f)
            .foregroundDrawable(R.drawable.ic_arrow_head) //marker user
            .build()
        locationComponent = mMapboxMap.locationComponent
        locationComponent.activateLocationComponent(requireActivity(), mMapboxMap.style!!, options)


        locationComponent.apply {
            isLocationComponentEnabled = true
            cameraMode = CameraMode.TRACKING_GPS_NORTH
            renderMode = RenderMode.GPS
        }
        locationComponent = mMapboxMap.locationComponent
        lastPositionUser()
    }

    //ADD NEW MARKER
    @SuppressLint("MissingPermission")
    fun createCustomMarker(
        placeName: String,
        latitude: Double,
        longitude: Double,
        mMarker: Int
    ): View {
        val customView: View = createCustomAnimationView(placeName, mMarker)
        val marker = MarkerView(LatLng(latitude, longitude), customView)
        markerAlarmView = customView.findViewById<View>(R.id.imv_marker) as ImageView
        marker.let {
            markerViewManager?.addMarker(it)
        }
        return customView
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("MissingPermission")
    private fun createCustomAnimationView(
        placeName: String,
        marker: Int
    ): View {
        val customView = LayoutInflater.from(requireActivity()).inflate(marker, null)
        customView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val animationRelativeLayout: RelativeLayout =
            customView.findViewById<View>(R.id.layout_marker) as RelativeLayout
        val animationImageView: ImageView =
            customView.findViewById<View>(R.id.imv_marker) as ImageView
        this.placeName = placeName

        animationRelativeLayout.setOnClickListener {

            currentSelectedChallengeMarker = animationImageView

            this.placeName = placeName
            val anim = ValueAnimator.ofInt(animationRelativeLayout.measuredWidth, 350)
            val visibility: Int = View.VISIBLE

            anim.addUpdateListener {
                val `val` = it.animatedValue as Int
                val layoutParams = animationRelativeLayout.layoutParams
                layoutParams.width = `val`
                animationRelativeLayout.layoutParams = layoutParams
                animationRelativeLayout.visibility = visibility

            }
            anim.duration = 1000
        }
        return customView
    }

    @SuppressLint("MissingPermission")
    fun lastPositionUser() {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createCustomMarker(
            workflow.alarm.address,
            workflow.alarm.latitude,
            workflow.alarm.longitude,
            workflow.alarm.icon
        )
        val locationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                userLocation = location
                val position: CameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            userLocation!!.latitude,
                            userLocation!!.longitude
                        )
                    ) // Sets the new camera position
                    .zoom(16.0) // Sets the zoom
                    .bearing(0.0) // Rotate the camera
                    .tilt(60.0) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

                mMapboxMap.moveCamera(
                    CameraUpdateFactory
                        .newCameraPosition(position)
                )
                initDistanceRules()
                initAlarmManagement()
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    fun initDistanceRules() {
        distanceBetweenUserAndAlarm = SphericalUtil.computeDistanceBetween(
            com.google.android.gms.maps.model.LatLng(
                userLocation!!.latitude, userLocation!!.longitude
            ),
            com.google.android.gms.maps.model.LatLng(
                workflow.alarm.latitude,
                workflow.alarm.longitude
            )
        ).roundToInt()

        //SI LE MODE GESTION ALARME EST EN AUTO, L'ALARME S'ACTIVE/DESACTIVE AUTOMATIQUEMENT
        if (switchModeAutoAlarm.isChecked) {
            //SI L'USAGER N'EST PAS LE DERNIER PARTANT ET QU'IL S'ELOIGNE A PLUS DE 20 L'ALARME RESTE INACTIVE (ROUGE)
            if (distanceBetweenUserAndAlarm > workflow.alarm.precision && workflow.user.isFirst) {
                refreshInfos(
                    R.color.colorRedError,
                    false,
                    context!!.getString(R.string.msg_alarm_off),
                    distanceBetweenUserAndAlarm.toString()
                )
            }
            //SI L'USAGER N'EST PAS LE DERNIER PARTANT ET QU'IL S'ELOIGNE A PLUS DE 20 L'ALARME RESTE INACTIVE (VERT)
            else if (distanceBetweenUserAndAlarm > workflow.alarm.precision && !workflow.user.isFirst) {
                refreshInfos(
                    R.color.colorQietPrimary,
                    true,
                    context!!.getString(R.string.msg_alarm_on),
                    distanceBetweenUserAndAlarm.toString()
                )
            }
            //SI L'USAGER EST A MOINS DE 20M de l'ALARME, ET QUE L'UTILISATEUR N'A PAS ACTIVE MANUELLEMENT L'ALARME (ROUGE)
            else if (distanceBetweenUserAndAlarm < workflow.alarm.precision) {
                refreshInfos(
                    R.color.colorRedError,
                    false,
                    context!!.getString(R.string.msg_alarm_off),
                    distanceBetweenUserAndAlarm.toString()
                )
            }
        }
        //SI LE MODE GESTION ALARME EST EN MANUEL, L'ALARME NE S'ACTIVE/DESACTIVE PLUS AUTOMATIQUEMENT
        else if (!switchModeAutoAlarm.isChecked) {

            when (switchAlarm.isChecked) {
                true -> {
                    refreshInfos(
                        R.color.colorQietPrimary,
                        true,
                        baseActivity.getString(R.string.msg_alarm_on),
                        distanceBetweenUserAndAlarm.toString()
                    )
                }
                false -> {
                    refreshInfos(
                        R.color.colorRedError,
                        false,
                        baseActivity.getString(R.string.msg_alarm_off),
                        distanceBetweenUserAndAlarm.toString()
                    )
                }
            }
        }
    }

    fun initAlarmManagement() {
        when (switchModeAutoAlarm.isChecked) {
            true -> switchAlarm.isClickable = false
            false -> switchAlarm.isClickable = true
        }
    }

    fun refreshInfos(icon: Int, enabled: Boolean, status: String, distance: String) {
        alarm.icon = icon
        alarm.enabled = enabled
        switchAlarm.isChecked = enabled
        markerAlarmView.background.setTint(ContextCompat.getColor(baseActivity, icon))
        tvAlarmStatus.text = status
        tvSwitchLabel.setTextColor(ContextCompat.getColor(baseActivity, icon))
        tvAlarmStatus.setTextColor(ContextCompat.getColor(baseActivity, icon))
        tvModeAlarm.setTextColor(ContextCompat.getColor(baseActivity, icon))
        tvCoordinatesUser.text = baseActivity.getString(
            R.string.text_lat,
            userLocation!!.latitude
        ) + ", " + baseActivity.getString(R.string.text_long, userLocation!!.longitude)
        tvCoordinatesUser.setTextColor(ContextCompat.getColor(baseActivity, icon))
        tvDistance.text = baseActivity.getString(R.string.text_distance, distance)
        tvDistance.setTextColor(ContextCompat.getColor(baseActivity, icon))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

}