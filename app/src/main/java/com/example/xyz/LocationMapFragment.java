package com.example.xyz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.xyz.Adapter.NearByItemRvAdapter;
import com.example.xyz.Interface.GoToMapListener;
import com.example.xyz.NearBy.NearByPlacesResponse;
import com.example.xyz.NearBy.Result;
import com.example.xyz.NearBy.RetrofitClientNearByST;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationMapFragment extends Fragment {
    private static final int REQUEST_AUTO_PLACEMENT_SEARCH_CODE_NEARBY = 300;
    private ArrayList<String> hotspots = new ArrayList<>();
    private ArrayList<String> radiouses = new ArrayList<>();
    private ArrayAdapter<String> hotspotsAdapter;
    private ArrayAdapter<String> radiousesAdapter;
    private Spinner hotspotSP, radiousSP;
    private Button findBT,seeOnMapBT,seeMoreBT;
    private RecyclerView rv;
    private LinearLayoutManager manager;
    private NearByPlacesResponse nearByPlacesResponse;
    private Context context;
    private double mLatitude;
    private double mLongitude;
    private String rradius;
    private String hhotspot;
    private String apiKeyNearBy;
    private List<Result> fullResult = new ArrayList<>();
    private GoToMapListener goToMapListener;
    private EditText mPlaceSearchET;


    public LocationMapFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        goToMapListener = (GoToMapListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getSpinnersInformations();
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("Latitude") && bundle.containsKey("Longitude")){
            mLatitude = bundle.getDouble("Latitude",0);
            mLongitude = bundle.getDouble("Longitude",0);
            Toast.makeText(context, "Latitude is "+mLatitude+" & Longitude is "+mLongitude, Toast.LENGTH_SHORT).show();
        }


        hotspotsAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,hotspots);
        radiousesAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,radiouses);
        View v = inflater.inflate(R.layout.fragment_location_map, container, false);
        hotspotSP = v.findViewById(R.id.hotspot_spinner_mgnbf);
        radiousSP = v.findViewById(R.id.radius_spinner_mgnbf);
        findBT = v.findViewById(R.id.find_bt_mgnbf);
        seeOnMapBT = v.findViewById(R.id.see_on_map_bt_mgnbf);
        seeMoreBT = v.findViewById(R.id.see_more_bt_mgnbf);
        mPlaceSearchET = v.findViewById(R.id.place_search_et_mgnbf);
        mPlaceSearchET.setText("");
        mPlaceSearchET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
                    startActivityForResult(intent,REQUEST_AUTO_PLACEMENT_SEARCH_CODE_NEARBY);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        hotspotSP.setAdapter(hotspotsAdapter);
        radiousSP.setAdapter(radiousesAdapter);
        rv = v.findViewById(R.id.rv_mgnbf);
        rv.setLayoutManager(manager);
        findBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rradius = radiousSP.getSelectedItem().toString();
                hhotspot = hotspotSP.getSelectedItem().toString();
                apiKeyNearBy = getString(R.string.api_key_nearby);
                String endUrl = String.format("place/nearbysearch/json?location=%f,%f&radius=%s&type=%s&key=%s",mLatitude,mLongitude,rradius,hhotspot,apiKeyNearBy);
                RetrofitClientNearByST.getService().getNearByService(endUrl).enqueue(new Callback<NearByPlacesResponse>() {
                    @Override
                    public void onResponse(Call<NearByPlacesResponse> call, Response<NearByPlacesResponse> response) {
                        fullResult.clear();
                        nearByPlacesResponse = response.body();
                        if (nearByPlacesResponse.getNextPageToken() != null){
                            seeMoreBT.setVisibility(View.VISIBLE);
                        }
                        List<Result> results = nearByPlacesResponse.getResults();
                        fullResult.addAll(results);
                        Toast.makeText(getActivity(), "Size is "+results.size(), Toast.LENGTH_SHORT).show();
                        NearByItemRvAdapter nearByItemRvAdapter = new NearByItemRvAdapter(getActivity(),results);
                        seeOnMapBT.setVisibility(View.VISIBLE);
                        rv.setAdapter(nearByItemRvAdapter);

                    }

                    @Override
                    public void onFailure(Call<NearByPlacesResponse> call, Throwable t) {

                    }
                });
            }
        });

        seeMoreBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endUrl = String.format("place/nearbysearch/json?pagetoken=%s&key=%s",nearByPlacesResponse.getNextPageToken(),apiKeyNearBy);
                RetrofitClientNearByST.getService().getNearByService(endUrl).enqueue(new Callback<NearByPlacesResponse>() {
                    @Override
                    public void onResponse(Call<NearByPlacesResponse> call, Response<NearByPlacesResponse> response) {
                        nearByPlacesResponse = response.body();
                        if (nearByPlacesResponse.getNextPageToken() == null){
                            seeMoreBT.setVisibility(View.GONE);
                        }
                        List<Result> results = nearByPlacesResponse.getResults();
                        fullResult.addAll(results);
                        Toast.makeText(getActivity(), "Size is "+results.size(), Toast.LENGTH_SHORT).show();
                        NearByItemRvAdapter nearByItemRvAdapter = new NearByItemRvAdapter(getActivity(),fullResult);
                        seeOnMapBT.setVisibility(View.VISIBLE);
                        rv.setAdapter(nearByItemRvAdapter);

                    }
                    @Override
                    public void onFailure(Call<NearByPlacesResponse> call, Throwable t) {

                    }
                });

            }
        });
        seeOnMapBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapListener.goToMapForNearByItems(fullResult,mLatitude,mLongitude);
            }
        });




        return v;

    }
    private void getSpinnersInformations(){
        hotspots.add("restaurant");
        hotspots.add("bank");
        hotspots.add("atm");
        hotspots.add("hospital");
        hotspots.add("shopping_mall");
        hotspots.add("mosque");
        hotspots.add("bus_station");
        hotspots.add("police");

        radiouses.add("500");
        radiouses.add("1000");
        radiouses.add("2000");
        radiouses.add("40000");
        radiouses.add("50000");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTO_PLACEMENT_SEARCH_CODE_NEARBY && resultCode == Activity.RESULT_OK){
            Toast.makeText(context, "Ok doing right thing", Toast.LENGTH_SHORT).show();
            Place place = PlaceAutocomplete.getPlace(getContext(),data);
            LatLng latLng = place.getLatLng();
            mLatitude = latLng.latitude;
            mLongitude = latLng.longitude;
            mPlaceSearchET.setText(place.getName());
            Toast.makeText(context, ""+mLongitude, Toast.LENGTH_SHORT).show();
        }
    }
}