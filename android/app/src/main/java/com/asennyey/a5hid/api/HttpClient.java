package com.asennyey.a5hid.api;

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

abstract class HttpClientResponse{
    InputStream stream;
    int statusCode;
    public HttpClientResponse(InputStream stream, int statusCode) {
        this.stream = new BufferedInputStream(stream);
        this.statusCode = statusCode;
    }
}

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
        if (responseStatusCode != HttpURLConnection.HTTP_OK) {
            return new HttpErrorResponse(connection.getErrorStream(), responseStatusCode);
        } else {
            return new HttpSuccessResponse(connection.getInputStream(), responseStatusCode);
        }
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;
    }

    public void get(URL url, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError){
        executor.execute(()-> {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = openConnection(url);
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

    public void post(URL url, String data, Callback<HttpSuccessResponse> onSuccess, Callback<HttpErrorResponse> onError){
        executor.execute(()-> {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
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
