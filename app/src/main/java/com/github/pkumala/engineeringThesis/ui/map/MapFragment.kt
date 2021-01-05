package com.github.pkumala.engineeringThesis.ui.map

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.github.engineeringThesis.R
import com.github.pkumala.engineeringThesis.MainActivity
import com.github.pkumala.engineeringThesis.shared.Measurement
import com.github.pkumala.engineeringThesis.ui.mapSettings.MapSettingsFragment
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback, MapSettingsFragment.NoticeDialogListener {

    private var mapView: MapView? = null
    private val model = MapViewModel()
    private lateinit var mainActivity: MainActivity

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        mapView?.getMapAsync(this)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(
            requireContext(),
            getString(R.string.access_token)
        )

        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = root.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        mainActivity = activity as MainActivity
        val fab: View = root.findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            val newDialog = MapSettingsFragment()
            newDialog.show(childFragmentManager, "dialog")
        }
        return root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        var data: List<Measurement>
        GlobalScope.launch(Dispatchers.Main) {
            data = model.loadDataFromServer(mainActivity.connectionConfig.value!!)
            mapboxMap.cameraPosition =
                CameraPosition.Builder().target(LatLng(
                    data.sortedBy { it.Latitude }.map { it.Latitude }
                        .let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 },
                    data.sortedBy { it.Longitude }.map { it.Longitude }
                        .let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }
                )).build()
            mapboxMap.setStyle(if (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) Style.DARK else Style.LIGHT) { style ->


                try {

                    val features = Vector<Feature>()
                    for (item in data) {
                        val json = JsonObject()
                        json.addProperty("height", item.Altitude)
                        json.addProperty(
                            "color", when (mainActivity.mapConfig.value?.SpinnerSelection) {
                                0 -> item.PM1
                                1 -> item.PM25
                                2 -> item.PM10
                                3 -> item.Temperature
                                4 -> item.RelativeHumidity
                                5 -> item.AtmosphericPressure
                                else -> item.PM1
                            }
                        )

                        features.addElement(
                            Feature.fromGeometry(
                                Polygon.fromLngLats(
                                    (mutableListOf(
                                        mutableListOf(
                                            Point.fromLngLat(item.Longitude, item.Latitude),
                                            Point.fromLngLat(
                                                item.Longitude,
                                                item.Latitude + 0.0001
                                            ),
                                            Point.fromLngLat(
                                                item.Longitude + 0.0001,
                                                item.Latitude + 0.0001
                                            ),
                                            Point.fromLngLat(
                                                item.Longitude + 0.0001,
                                                item.Latitude
                                            ),
                                            Point.fromLngLat(item.Longitude, item.Latitude),
                                        )
                                    ))
                                ),
                                json
                            )
                        )
                    }

                    style.addSource(
                        GeoJsonSource(
                            "courseData", FeatureCollection.fromFeatures(
                                features
                            )
                        )
                    )

//                 Add FillExtrusion layer to map using GeoJSON data
                    val colorBreakpoint =
                            if (mainActivity.mapConfig.value!!.ColorBreakpoint != 0) mainActivity.mapConfig.value!!.ColorBreakpoint else 90
                    style.addLayer(
                            FillExtrusionLayer("course", "courseData").withProperties(
                                    fillExtrusionColor(
                                            interpolate(
                                                    linear(),
                                                    get("color"),
                                                    stop(colorBreakpoint - 1, color(Color.GREEN)),
                                                    stop(colorBreakpoint, color(Color.RED))
                                            )
                                    ),
                                    fillExtrusionOpacity(0.7f),
                                    fillExtrusionHeight(get("height"))
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