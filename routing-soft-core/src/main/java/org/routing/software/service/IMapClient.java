package org.routing.software.service;

import org.json.JSONObject;

import java.io.IOException;

public interface IMapClient {
    JSONObject fetchRouteObject(String logA, String latA, String logB, String latB) throws IOException, InterruptedException;
}
