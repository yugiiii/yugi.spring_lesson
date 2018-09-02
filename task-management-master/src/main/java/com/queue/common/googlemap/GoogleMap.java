package com.queue.common.googlemap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GoogleMap {

    private static GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyDyCKXlrDWcyPWuyyOXvDBDFl3pFOkpdrs") // さっき取得したAPIキー
            .build();

    public static Map<String, Object> main(String address) throws InterruptedException, IOException {
    		Map<String, Object> result = new HashMap<>();
    		
        GeocodingResult[] results = getResults(address);
        if (results != null && results.length > 0) {
            LatLng latLng = results[0].geometry.location; // とりあえず一番上のデータを使う
            
            result.put("lat", latLng.lat);
            result.put("lng", latLng.lng);
            return result;
        } else {
        		result.put("lat", null);
        		result.put("lng", null);
            return result;
        }
    }

    public static GeocodingResult[] getResults(String address) throws InterruptedException, IOException {
        GeocodingApiRequest req = GeocodingApi.newRequest(context)
                .address(address)
                // .components(ComponentFilter.country("JP"))
                .language("ja");

        try {
            GeocodingResult[] results = req.await();
            if (results == null || results.length == 0) {
                // ZERO_RESULTSはresults.length==0の空配列がsuccessful扱いで返ってくるっぽい
                System.out.println("zero results.");
            }
            return results;
        } catch (ApiException e) {
            // ZERO_RESULTS以外のApiExceptionはこっちで
            System.out.println("geocode failed.");
            System.out.println(e);
            return null;
        } catch (Exception e) {
            System.out.println("error.");
            System.out.println(e);
            return null;
        }
    }

}