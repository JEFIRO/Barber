package com.example.jefiro.barber.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupaBase {

    private static final String SUPABASE_URL = "https://gsdlqisexlujpbhmefau.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdzZGxxaXNleGx1anBiaG1lZmF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1MjUzMTgsImV4cCI6MjA4MDEwMTMxOH0._Vd5EzlFiTAjsKfzQwdiHVFe5fawUg8YTPXfrel9XxE";
    private static final String BUCKET_NAME = "ClientesProfileImage";

    public static String uploadImageToSupabase(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            byte[] imageBytes = toBytes(inputStream);

            inputStream.read(imageBytes);


            String url = SUPABASE_URL + "/storage/v1/object/"
                    + BUCKET_NAME + "/" + System.currentTimeMillis() + ".jpg";

            Log.d("SUPABASE", "URL destino: " + url);


            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SUPABASE", "Erro no upload: " + e.getMessage(), e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("SUPABASE", "Código de resposta: " + response.code());
                    Log.d("SUPABASE", "Resposta completa: " + response.body().string());
                }

            });

            return url;
        } catch (Exception e) {
            Log.e("SUPABASE", "Erro ao preparar imagem: " + e.getMessage(), e);
        }
        return null;

    }

    public static boolean deleteImg(String link) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(link)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static byte[] toBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

}
