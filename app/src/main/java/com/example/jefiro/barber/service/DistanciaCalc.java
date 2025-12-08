package com.example.jefiro.barber.service;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistanciaCalc {

    public static void getDistance(double lon1, double lat1, double lon2, double lat2, OnDistanceCallback callback) {
                if (lon1 == 0 || lat1 == 0 || lon2 == 0 || lat2 == 0) {
            callback.onError("Coordenadas inválidas (0.0)");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String urlString = String.format(Locale.US,
                        "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=false",
                        lat1, lon1, lat2, lon2
                );

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray routes = jsonResponse.getJSONArray("routes");

                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);
                        double distanciaMetros = route.getDouble("distance");

                        handler.post(() -> callback.onSucefull(distanciaMetros));
                    } else {
                        handler.post(() -> callback.onError("Nenhuma rota encontrada"));
                    }

                } else {
                    handler.post(() -> callback.onError("Erro API: " + responseCode));
                }

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}