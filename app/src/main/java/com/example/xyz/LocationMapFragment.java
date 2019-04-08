package com.example.xyz;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationMapFragment extends Fragment {

    private double mLatitude;
    private double mLongitude;
    private Location mLocation;
    private Geocoder mGeocoder;
    private String mCity;
    private Context mContext;
    private List<Address> mAddresses = new ArrayList<>();
    private List<Address> mSearchAddresses = new ArrayList<>();
    private FusedLocationProviderClient mClient;
    private String mAddressLine;
    public LocationMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (checkLocPermission()){
            mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        //Toast.makeText(MainActivity.this, "Location Got", Toast.LENGTH_SHORT).show();
                        mLocation = location;
                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        /*try {
                            mAddresses = mGeocoder.getFromLocation(mLatitude,mLongitude,1);
                            mAddressLine = mAddresses.get(0).getAddressLine(0);
                            mCity = mAddresses.get(0).getLocality();
                            if (mCity != null){
                                //Toast.makeText(MainActivity.this, mCity, Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/


                    }
                }
            });
        }
        return inflater.inflate(R.layout.fragment_location_map, container, false);
    }
    private boolean checkLocPermission(){
        if (getActivity()!=null){
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);
                return false;
            } else {
                return false;
            }
        }
        return true;
    }

}
