package com.example.myapplication.ui.notifications

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.Measurement
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.util.*


class NotificationsFragment : Fragment(), OnMapReadyCallback {

    private var mapView: MapView? = null
    private val model = NotificationsViewModel()
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

            Mapbox.getInstance(
                    requireContext(),
                    getString(R.string.access_token)
            )

// This contains the MapView in XML and needs to be called after the access token is configured.
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        mainActivity = activity as MainActivity
        return root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        var data: List<Measurement>
        GlobalScope.launch(Dispatchers.Main) {
            data = model.loadDataFromServer(mainActivity.connectionConfig.value!!)
            val routeCoordinates: ArrayList<Point> = data.map { item -> run { Point.fromLngLat(item.Longitude, item.Latitude, item.Altitude) } } as ArrayList<Point>
            mapboxMap.setStyle(if (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) Style.DARK else Style.LIGHT) { style ->


                try {

                    val features = Vector<Feature>()
                    for (item in routeCoordinates) {
                        val json = JsonObject()
                        json.addProperty("e", item.altitude())

                        features.addElement(Feature.fromGeometry(
                                Polygon.fromLngLats((mutableListOf(mutableListOf(
                                        Point.fromLngLat(item.longitude(), item.latitude()),
                                        Point.fromLngLat(item.longitude(), item.latitude() + 0.0001),
                                        Point.fromLngLat(item.longitude() + 0.0001, item.latitude() + 0.0001),
                                        Point.fromLngLat(item.longitude() + 0.0001, item.latitude()),
                                        Point.fromLngLat(item.longitude(), item.latitude()),
                                )))),
                                json
                        ))

                    }

                    style.addSource(GeoJsonSource("courseData", FeatureCollection.fromFeatures(features)))

//                 Add FillExtrusion layer to map using GeoJSON data
                    style.addLayer(
                            FillExtrusionLayer("course", "courseData").withProperties(
                                    fillExtrusionColor(Color.YELLOW),
                                    fillExtrusionOpacity(0.7f),
                                    fillExtrusionHeight(get("e"))
                            )
                    )
                } catch (exception: URISyntaxException) {

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

//    protected fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState!!)
//        mapView.onSaveInstanceState(outState)
//    }
}