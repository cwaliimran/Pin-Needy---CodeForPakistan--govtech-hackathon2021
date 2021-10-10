package com.cwaliimran.pinneedy.activities.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.cwaliimran.pinneedy.R;
import com.cwaliimran.pinneedy.activities.DetailsActivity;
import com.cwaliimran.pinneedy.adapters.NearbyPlacesAdapter;
import com.cwaliimran.pinneedy.databinding.FragmentHomeBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GPSTracker;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "response";
    public GoogleApiClient googleApiClient;
    public FirebaseFirestore db;
    FragmentHomeBinding binding;
    MarkerOptions options;
    ArrayList<ModelNeedy> modelNeedies = new ArrayList<>();
    ArrayList<ModelNeedy> modelStoresin50KM = new ArrayList<>();
    ModelNeedy datum;
    List<Double> distanceList = new ArrayList<>();
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private LatLng latLng;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        db = FirebaseFirestore.getInstance();
        googleApiClient = new GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        options = new MarkerOptions();
        // Initialize the SDK
        Places.initialize(requireActivity(), getResources().getString(R.string.map_key));
        return binding.getRoot();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        TedPermission.with(requireActivity()).setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (GlobalClass.isLocationEnabled(requireActivity()) && GlobalClass.isOnline(requireActivity())) {
                    GPSTracker gps = new GPSTracker(requireActivity());
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        //Log.d(TAG, "myHandler: " + latitude + "=====" + longitude);
                        if (latitude != 0.0 || longitude != 0.0) {
//                            sharedPref.storeString("latitude", latitude + "");
//                            sharedPref.storeString("longitude", longitude + "");
                        } else {
//                            latitude = Double.valueOf(sharedPref.getString("latitude", "0.0"));
//                            longitude = Double.valueOf(sharedPref.getString("longitude", "0.0"));
                        }
                        latLng = new LatLng(latitude, longitude);
//                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        options.position(latLng);
                        options.anchor(0.5f, 1);
                        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        binding.myLocation.setOnClickListener(v -> {
                            latLng = new LatLng(latitude, longitude);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                            mMap.animateCamera(cameraUpdate);
                        });
                        getOutlets();
                    }

                } else {
                    requestGPSSettings();
                }
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
//                finish();
            }
        }).setDeniedMessage(getString(R.string.reject_massage))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    public void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    //Log.d("response", "All location settings are satisfied.");
                    Toast.makeText(getActivity(), "GPS is already enable", Toast.LENGTH_LONG).show();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i("response", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                    try {
                        status.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("Applicationsett", e.toString());
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                    Toast.makeText(getActivity(), "Location settings are inadequate, and cannot be fixed here", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }


    private void getOutlets() {
        modelNeedies.clear();
        showProgress(binding.progressBar, true);
        db.collection(AppConstants.TBL_NEEDIES).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        modelNeedies.add(document.toObject(ModelNeedy.class));
                    }
                    if (modelNeedies.size() == 0) {
                        Toast.makeText(requireActivity(), "No data found.", Toast.LENGTH_SHORT).show();
                    } else {
                        //distance work
                        for (int i = 0; i < modelNeedies.size(); i++) {
                            datum = modelNeedies.get(i);
                            Log.d(TAG, "initAdapter: latitude: " + latitude);
                            Log.d(TAG, "initAdapter: longitude: " + longitude);
                            Log.d(TAG, "initAdapter: datum.getLatitude(): " + datum.getLatitude());
                            Log.d(TAG, "initAdapter: datum.getLongitude(): " + datum.getLongitude());

                            //data is within 50km or 31 miles
                            if (distance(latitude, longitude, datum.getLatitude(), datum.getLongitude()) < 31.0686) {
                                modelStoresin50KM.add(modelNeedies.get(i));
                            }
                        }
                        Log.d(TAG, "onComplete: modelStoresin50KM size: " + modelStoresin50KM.size());
                        if (modelStoresin50KM.size() > 0) {
                            initAdapter(modelStoresin50KM);
                        } else {
                            Log.d(TAG, "onComplete: no stores found.");
                            latLng = new LatLng(latitude, longitude);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 8);
                            mMap.animateCamera(cameraUpdate);
                            Toast.makeText(getContext(), "No stores found in your area.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    showProgress(binding.progressBar, false);
                }
            }
        }).addOnFailureListener(e -> {
            showProgress(binding.progressBar, false);
            Toast.makeText(requireActivity(), "" + e, Toast.LENGTH_SHORT).show();
        });
    }


    private void initAdapter(ArrayList<ModelNeedy> modelStoresin50KM) {
        mMap.clear();
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.needy);
        Bitmap resours = getResizedBitmap(largeIcon, dpToPx(30));
        for (int i = 0; i < modelStoresin50KM.size(); i++) {
            datum = modelStoresin50KM.get(i);
            options = new MarkerOptions().title(datum.getNeedyName()).icon(BitmapDescriptorFactory.fromBitmap(resours))
                    .position(new LatLng(datum.getLatitude(), datum.getLongitude()));
            Objects.requireNonNull(mMap.addMarker(options)).setTag(datum);

            Double distance = distance(latitude, longitude, datum.getLatitude(), datum.getLongitude());
            distanceList.add(distance);
        }
        binding.recyclerView.setAdapter(new NearbyPlacesAdapter(modelStoresin50KM, requireActivity()));

        //      Log.d(TAG, "distanceList: " + distanceList);
        if (distanceList != null) {
            Double min = null;
            int minIndex = 0;
            for (int i = 0; i < distanceList.size(); i++) {
                min = distanceList.get(0);
                if (distanceList.get(i) < min) {
                    min = distanceList.get(i);
                    minIndex = i;
                }
            }
            //  Log.d(TAG, "min value: " + min);
            ModelNeedy modelNeedyVal = modelStoresin50KM.get(minIndex);
            latLng = new LatLng(modelNeedyVal.getLatitude(), modelNeedyVal.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 8);
            mMap.animateCamera(cameraUpdate);
        }


        //to user location

//        latLng = new LatLng(latitude, longitude);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//        mMap.animateCamera(cameraUpdate);

        showProgress(binding.progressBar, false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public View getInfoWindow(@NotNull Marker marker) {
                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.map_info_window, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(300), dpToPx(104)));

                TextView name = view.findViewById(R.id.name);
                ImageView image = view.findViewById(R.id.image);

                datum = (ModelNeedy) marker.getTag();

                try {
                    assert datum != null;
                    if (datum.getNeedyName() != null) {
                        name.setText(datum.getNeedyName());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return view;
            }

            @Override
            public View getInfoContents(@NotNull Marker marker) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(marker -> {
            datum = (ModelNeedy) marker.getTag();
            if (datum != null) {
                String outletId = datum.getId();
                Intent intent = new Intent(requireActivity(), DetailsActivity.class);
                intent.putExtra("store", datum);
                startActivity(intent);
            }

        });

    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    protected int dpToPx(int dp) {
        float density = getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }


    /**
     * calculates the distance between two locations in MILES
     */
    /*private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 31.0686; // in miles, equals to 50km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;
        Log.d(TAG, "distance: " + dist);
        return dist; // output distance, in MILES
    }*/
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        Log.d(TAG, "distance: in Miles: " + dist);
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void showProgress(ProgressBar progressBar, boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.GONE);
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

}