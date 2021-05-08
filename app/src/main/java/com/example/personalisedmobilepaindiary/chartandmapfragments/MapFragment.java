package com.example.personalisedmobilepaindiary.chartandmapfragments;

import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalisedmobilepaindiary.LocationViewModel;
import com.example.personalisedmobilepaindiary.MainActivity;
import com.example.personalisedmobilepaindiary.R;
import com.example.personalisedmobilepaindiary.databinding.MapFragmentBinding;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MapFragment extends Fragment {
    private MapFragmentBinding binding;
    private MapView map;
    private String userLocation;
    private Geocoder geocoder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_token));
        binding = MapFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        LocationViewModel model = new
                ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        model.getLocation().observe(getViewLifecycleOwner(), l -> {
            if (isAdded()){
                userLocation = l;
            }
        });
        map = binding.map;
        binding.map.onCreate(savedInstanceState);
        geocoder = new Geocoder(requireActivity(),new Locale.Builder().setLanguage("en").setRegion("AU").build());
        showCurrentLocation();
        binding.getRoot().setOnClickListener(v -> {
            ((MainActivity)requireActivity()).clearSoftKeyboard();
        });
        binding.showMap.setOnClickListener(v -> {
            ((InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(requireActivity().getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

            try {
                if (!binding.addressInput.getText().toString().equals("")){
                    List<Address> addresses = geocoder.getFromLocationName(binding.addressInput.getText().toString(), 1);
                    binding.addressMsg.setText("Search result: " + addresses.get(0).getAddressLine(0));
                    binding.map.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
                        symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(addresses.get(0).getLongitude(), addresses.get(0).getLatitude())));
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                                .zoom(13)
                                .build();
                        mapboxMap.setCameraPosition(position);
                        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/lohse/ckgop3hd11jdz19ju80smxy0g").
                                withImage("ICON", BitmapFactory.decodeResource(
                                        requireActivity().getResources(), R.drawable.mapbox_marker_icon_default)).withSource(new GeoJsonSource("SOURCE",
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList))).withLayer(new SymbolLayer("LAYER", "SOURCE")
                                .withProperties(
                                        iconImage("ICON"),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                )));
                    }));
                }
                else {
                    showCurrentLocation();
                }

            }catch (IOException e) {
                Log.d("sad","asv");
            }
        });

        return view;
    }
    public void showCurrentLocation(){

        binding.map.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
            symbolLayerIconFeatureList.add(Feature.fromGeometry(
                    Point.fromLngLat(Double.parseDouble(userLocation.split(",")[1]), Double.parseDouble(userLocation.split(",")[0]))));
            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/lohse/ckgop3hd11jdz19ju80smxy0g").
                    withImage("ICON", BitmapFactory.decodeResource(
                            requireActivity().getResources(), R.drawable.mapbox_marker_icon_default)).withSource(new GeoJsonSource("SOURCE",
                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList))).withLayer(new SymbolLayer("LAYER", "SOURCE")
                    .withProperties(
                            iconImage("ICON"),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                    )));
            try {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(userLocation.split(",")[0]), Double.parseDouble(userLocation.split(",")[1]), 1);
                binding.addressMsg.setText("You Current location: " + addresses.get(0).getAddressLine(0));
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                        .zoom(13)
                        .build();
                mapboxMap.setCameraPosition(position);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    @Override
    public void onStart() {
        super.onStart();
        map.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        map.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        map.onDestroy();
    }
}
