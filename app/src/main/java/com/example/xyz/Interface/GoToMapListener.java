package com.example.xyz.Interface;

import com.example.xyz.NearBy.Result;

import java.util.List;

public interface GoToMapListener {
    void goToMapForNearByItems(List<Result> fullResult, double latitude, double longitude);
    void goToMapWithItemDesc(Result result);
}
