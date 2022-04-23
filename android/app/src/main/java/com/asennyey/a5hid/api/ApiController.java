package com.asennyey.a5hid.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.JsonReader;

import com.asennyey.a5hid.api.json.JsonHelper;
import com.asennyey.a5hid.api.json.PagedResponse;
import com.asennyey.a5hid.api.objects.read.Event;
import com.asennyey.a5hid.api.objects.read.Jwt;
import com.asennyey.a5hid.api.objects.read.LeaderboardUser;
import com.asennyey.a5hid.api.objects.read.User;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
public class ApiController {
    private static ApiController instance;
    HttpClient client = HttpClient.getInstance();
    AuthenticationController auth;
    Activity context;
    final String regex = "POINT ?\\((-?\\d+\\.?\\d*) (-?\\d+\\.?\\d*)\\)";

    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    public ApiController(Activity context){
        this.context = context;
        auth = AuthenticationController.getInstance(context);
    }

    private void runOnMainThread(Runnable callback){
        context.getMainExecutor().execute(callback);
    }

    //
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

    public void getLeaderboard(Callback<PagedResponse<LeaderboardUser>> onSuccess, Callback<Exception> onError){
        JsonHelper<LeaderboardUser> helper = new JsonHelper<>();
        try {
            client.get(
                    new URL(getApiUrl() + "/leaderboard"),
                    (res)->{
                        JsonReader reader = null;
                        try {
                            reader = new JsonReader(new InputStreamReader(res.result.stream,"UTF-8"));
                            PagedResponse<LeaderboardUser> currentPage = helper.readPage(reader, this::readLeaderboardUser);
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

    //reads each json response and parses it
    private Event readEvent(JsonReader reader){
        Event event = new Event();
        try {
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "description":
                        event.description = reader.nextString();
                        break;
                    case "location": // regex for lat, long, makes latlong obj for google map
                        String geoData = reader.nextString();
                        Matcher matcher = pattern.matcher(geoData);
                        if(matcher.find()) {
                            if (matcher.groupCount() >= 2) {
                                event.location = new LatLng(
                                        Double.parseDouble(matcher.group(1)),
                                        Double.parseDouble(matcher.group(2))
                                );
                            }
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

    private LeaderboardUser readLeaderboardUser(JsonReader reader){
        LeaderboardUser user = new LeaderboardUser();
        try {
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "name":
                        user.name = reader.nextString();
                        break;
                    case "overall_score":
                        user.score = reader.nextInt();
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

    public void login(com.asennyey.a5hid.api.objects.write.User user, Callback<Boolean> onSuccess, Callback<Exception> onError){
        System.out.println(auth.getJwt());
        try {
            client.post(
                    new URL(getApiUrl() + "/auth/jwt/create/"),
                    user.toString(),
                    auth.getJwt(),
                    (res)->{
                        JsonReader reader = null;
                        try {
                            reader = new JsonReader(new InputStreamReader(res.result.stream,"UTF-8"));
                            Jwt jwt = readJwt(reader);
                            auth.setJwt(jwt);
                            runOnMainThread(()->onSuccess.onResult(new Result<>(true)));
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

    private Jwt readJwt(JsonReader reader){
        Jwt jwt = new Jwt();
        try {
            reader.beginObject();
            while(reader.hasNext()){
                switch(reader.nextName()){
                    case "access":
                        jwt.accessToken = reader.nextString();
                        break;
                    case "refresh":
                        jwt.refreshToken = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
            return jwt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createEvent(com.asennyey.a5hid.api.objects.write.Event event, Callback<Boolean> onSuccess, Callback<Exception> onError){
        System.out.println(auth.getJwt());
        try {
            client.post(
                    new URL(getApiUrl() + "/events/"),
                    event.toString(),
                    auth.getJwt(),
                    (res)->{
                        runOnMainThread(()->onSuccess.onResult(new Result<>(true)));
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

    //TODO: dont hardcode
    private String getApiUrl(){
        try {
            System.out.println(context.getPackageManager()
                    .getActivityInfo(context.getComponentName(), 0));
            return "https://api-omytizdchq-uc.a.run.app/api";
            /*context.getPackageManager()
                    .getActivityInfo(context.getComponentName(), 0)
                    .metaData.getString("API_URL");*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ApiController getInstance(Activity context){
        if(instance == null){
            instance = new ApiController(context);
        }
        return instance;
    }
}
