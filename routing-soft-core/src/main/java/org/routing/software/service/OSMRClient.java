package org.routing.software.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@NoArgsConstructor
public class OSMRClient implements IMapClient {

    @Override
    public JSONObject fetchRouteObject(String logA, String latA, String logB, String latB) throws IOException, InterruptedException {
    String url = "http://router.project-osrm.org/route/v1/driving/"
                + latA + "," + logA + ";"
                + latB + "," + logB
                + "?overview=full&geometries=geojson";

    HttpClient httpClient = HttpClient.newHttpClient();
    HttpRequest httpRequest = HttpRequest
            .newBuilder()
            .uri(URI.create(url)).build();

    HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    String responseBody = httpResponse.body();
    JSONObject json = new JSONObject(responseBody);
    JSONObject route = json.getJSONArray("routes").getJSONObject(0);
    double duration = route.getDouble("duration");
    double distance = route.getDouble("distance");
    JSONArray coordinates = route.getJSONObject("geometry").getJSONArray("coordinates");


    //TODO REMOVE
        System.out.println("Duration (sec): "+duration);
        System.out.println("Distance (m): "+distance);
        System.out.println("Path coordinates: "+coordinates.toString());
        return json;
    }
}
