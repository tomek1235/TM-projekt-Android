package com.example.projekt_android;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private static final String BASE_URL = "http://192.168.1.11:8080";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();

    public interface ApiCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public interface ProductsCallback {
        void onSuccess(ArrayList<Product> products);
        void onError(String error);
    }
    public static void login(String login, String haslo, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("login", login);
            json.put("haslo", haslo);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/users/login")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnMainThread(() -> callback.onError(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    runOnMainThread(() -> callback.onSuccess(result));
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
    public static void register(String login, String haslo, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("login", login);
            json.put("haslo", haslo);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/users/register")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnMainThread(() -> callback.onError(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    runOnMainThread(() -> callback.onSuccess(result));
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
    public static void getProducts(int userId, ProductsCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/products?userId=" + userId)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnMainThread(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();

                    JSONArray jsonArray = new JSONArray(result);
                    ArrayList<Product> products = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        products.add(new Product(
                                obj.getInt("id"),
                                obj.getString("nazwa"),
                                obj.getString("opis"),
                                obj.getDouble("cena"),
                                obj.getInt("iloscKupionychSztuk"),
                                obj.getString("dataDodania")
                        ));
                    }

                    runOnMainThread(() -> callback.onSuccess(products));
                } catch (Exception e) {
                    runOnMainThread(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }
    public static void addProduct(String nazwa, String opis, double cena, int userId, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("nazwa", nazwa);
            json.put("opis", opis);
            json.put("cena", cena);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/products?userId=" + userId)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnMainThread(() -> callback.onError(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    runOnMainThread(() -> callback.onSuccess(result));
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
    public static void updateProduct(int productId, String nazwa, String opis, double cena, int userId, ApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("nazwa", nazwa);
            json.put("opis", opis);
            json.put("cena", cena);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "/products/" + productId + "?userId=" + userId)
                    .put(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnMainThread(() -> callback.onError(e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    runOnMainThread(() -> callback.onSuccess(result));
                }
            });
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
    public static void deleteProduct(int productId, int userId, ApiCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/products/" + productId + "?userId=" + userId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnMainThread(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnMainThread(() -> callback.onSuccess("OK"));
            }
        });
    }

    public static void buyProduct(int productId, ApiCallback callback) {
        RequestBody body = RequestBody.create("", JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "/products/" + productId + "/buy")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnMainThread(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                runOnMainThread(() -> callback.onSuccess(result));
            }
        });
    }
    private static void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}