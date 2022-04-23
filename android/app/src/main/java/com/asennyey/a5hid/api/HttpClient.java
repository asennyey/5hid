package com.asennyey.a5hid.api;

import static java.net.HttpURLConnection.*;

import com.asennyey.a5hid.api.objects.read.Jwt;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//holds values from stream in apicontroller
abstract class HttpClientResponse{
    InputStream stream;
    int statusCode;
    public HttpClientResponse(InputStream stream, int statusCode) {
        this.stream = new BufferedInputStream(stream);
        this.statusCode = statusCode;
    }
}

// singleton,
public class HttpClient {
    static HttpClient instance = null;
    Executor executor = Executors.newFixedThreadPool(4);
    public class HttpSuccessResponse extends HttpClientResponse{
        public HttpSuccessResponse(InputStream stream, int statusCode) {
            super(stream, statusCode);
        }
    }

    public class HttpErrorResponse extends  HttpClientResponse{
        public HttpErrorResponse(InputStream stream, int statusCode) {
            super(stream, statusCode);
        }
    }

    private HttpClientResponse handleHttpResponse(HttpURLConnection connection) throws IOException {
        int responseStatusCode = connection.getResponseCode();
        System.out.println(responseStatusCode);
        if(responseStatusCode / 100 == 2){
            return new HttpSuccessResponse(connection.getInputStream(), responseStatusCode);
        }else{
            return new HttpErrorResponse(connection.getErrorStream(), responseStatusCode);
        }
    }

    private HttpURLConnection openConnection(URL url, Jwt jwt) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if(jwt != null){
            connection.setRequestProperty("Authorization", String.format("JWT %s", jwt.accessToken));
        }
        return connection;
    }

    // pass in url, pass in 2 callbacks,
    public void get(URL url, Jwt jwt, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError){
        executor.execute(()-> {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = openConnection(url, jwt);
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                HttpClientResponse res = handleHttpResponse(urlConnection);
                if(res instanceof HttpSuccessResponse) onSuccess.onResult(new Result<>((HttpSuccessResponse) res));
                else onError.onResult(new Result<>((HttpErrorResponse) res));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
        });
    }

    // pass in url, pass in 2 callbacks,
    public void get(URL url, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError){
        this.get(url, null, onSuccess, onError);
    }

    // url, data in strified json,
    public void post(URL url, String data, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError){
        this.post(url, data, null, onSuccess, onError);
    }

    public void post(URL url, String data, Jwt jwt, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError) {
        executor.execute(()-> {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = openConnection(url, jwt);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json"); // set content type
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();
                System.out.println(data);
                //Write
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.close();
                os.close();

                HttpClientResponse res = handleHttpResponse(urlConnection);
                if(res instanceof HttpSuccessResponse) onSuccess.onResult(new Result<>((HttpSuccessResponse) res));
                else onError.onResult(new Result<>((HttpErrorResponse) res));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
        });
    }

        public static HttpClient getInstance(){
        if(HttpClient.instance == null){
            HttpClient.instance = new HttpClient();
        }
        return HttpClient.instance;
    }
}
