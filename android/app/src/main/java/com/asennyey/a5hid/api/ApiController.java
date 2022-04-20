package com.asennyey.a5hid.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.util.JsonReader;

import com.asennyey.a5hid.api.json.JsonHelper;
import com.asennyey.a5hid.api.json.PagedResponse;
import com.asennyey.a5hid.api.objects.Event;
import com.asennyey.a5hid.api.objects.Point;
import com.asennyey.a5hid.api.objects.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiController {
    HttpClient client = HttpClient.getInstance();
    Activity context;
    final String regex = "POINT ?\\((-?\\d+\\.\\d+) (-?\\d+\\.\\d+)\\)";

    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    public ApiController(Activity context){
        this.context = context;
    }

    private void runOnMainThread(Runnable callback){
        context.getMainExecutor().execute(callback);
    }

    public void getEvents(Callback<PagedResponse<Event>> onSuccess, Callback<Exception> onError){
        JsonHelper<Event> helper = new JsonHelper<>();
        try {
            client.get(
                    new URL(getApiUrl() + "/events"),
                    (res)->{
                        JsonReader reader = null;
                        try {
                            reader = new JsonReader(new InputStreamReader(res.result.stream,"UTF-8"));
                            PagedResponse<Event> currentPage = helper.readPage(reader, this::readEvent);
                            runOnMainThread(()->onSuccess.onResult(new Result<>(currentPage)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    (err)-> {
                        BufferedReader r = new BufferedReader(new InputStreamReader(err.result.stream));
                        StringBuilder total = new StringBuilder();
                        try {
                            for (String line; (line = r.readLine()) != null; ) {
                                total.append(line).append('\n');
                            }
                            System.out.println(total.toString());
                        }
                        catch(Exception e){
                                e.printStackTrace();
                            }
                        runOnMainThread(() -> onError.onResult(
                                new Result<>(
                                        new Exception(String.valueOf(err.result.statusCode))
                                )
                        ));
                    });
        } catch (MalformedURLException e) {
            e.printStackTrace();
            runOnMainThread(()->onError.onResult(new Result<>(e)));
        }
    }

    private Event readEvent(JsonReader reader){
        Event event = new Event();
        try {
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "description":
                        event.description = reader.nextString();
                        break;
                    case "location":
                        String geoData = reader.nextString();
                        System.out.println(geoData);
                        Matcher matcher = pattern.matcher(geoData);
                        System.out.println(matcher.find());
                        System.out.println("Full match: " + matcher.group(0));

                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            System.out.println("Group " + i + ": " + matcher.group(i));
                        }
                        if(matcher.groupCount() >= 2) {
                            Point point = new Point(
                                    Long.parseLong(matcher.group(1)),
                                    Long.parseLong(matcher.group(2))
                            );
                            event.location = point;
                        }else{
                            reader.skipValue();
                        }
                        break;
                    case "user":
                        reader.beginObject();
                        event.user = this.readUser(reader);
                        reader.endObject();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            return event;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private User readUser(JsonReader reader){
        User user = new User();
        try {
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "name":
                        user.name = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getApiUrl(){
        try {
            System.out.println(context.getPackageManager()
                    .getActivityInfo(context.getComponentName(), 0));
            return "http://10.0.2.2:8000/api";
            /*context.getPackageManager()
                    .getActivityInfo(context.getComponentName(), 0)
                    .metaData.getString("API_URL");*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
