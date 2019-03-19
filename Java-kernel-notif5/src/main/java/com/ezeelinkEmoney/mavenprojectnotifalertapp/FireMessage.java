/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ezeelinkEmoney.mavenprojectnotifalertapp;

//import com.mysql.cj.api.io.Protocol;
//import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.xml.ws.Response;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpResponse;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.Credentials;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.params.ConnRoutePNames;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 *
 * @author ezeelinkindonesia
 */
public class FireMessage 
{
private final String SERVER_KEY = "AAAAswz2Exo:APA91bHLuHT6zbZe-j8nChzubzsTrnwAGyXUWon7DiiPpWUBGzSTK9bSUHOgvsv-fb95_EqGgwfro57yN8bxE1JU955zuO79lvmwhSpM0JrLOf6HqthY9p5yXKMbxVRSYCZrzOiSNzCo";
private final String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
//private final String API_URL_FCM = "fcm.googleapis.com";
private JSONObject root;
private final String proxyHost="172.18.90.3";
private final int proxyPort=8080;
private final String proxyUser="teddy@ezeelink.co.id";
private final String proxyPassword="zugan418";

    public FireMessage(String title, String message) throws JSONException 
    {
        root = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject notif = new JSONObject();
        data.put("title", title);
        data.put("message", message);
        notif.put("title", title);
        notif.put("body", message);
        root.put("notification", notif);
        root.put("data", data);
    }


    public String sendToTopic(String topic) throws Exception { //SEND TO TOPIC
        System.out.println("Send to Topic");
        root.put("condition", "'"+topic+"' in topics");
        return sendPushNotification(true);
    }

    public String sendToGroup(JSONArray mobileTokens) throws Exception { // SEND TO GROUP OF PHONES - ARRAY OF TOKENS
        root.put("registration_ids", mobileTokens);
        return sendPushNotification(false);
    }

    public String sendToToken(String token) throws Exception {//SEND MESSAGE TO SINGLE MOBILE - TO TOKEN
        root.put("to", token);
        return sendPushNotification(false);
    }


    
    
    private HttpURLConnection createConnection(final String location) throws IOException 
    {

        URL url = new URL(location);
        HttpURLConnection conn;

        if (proxyHost != null) 
        {
//            if (proxyUser != null && proxyPassword != null) 
//            {
//                System.out.println("masuk1");
//                Authenticator authenticator = new Authenticator() {
//
//                    public PasswordAuthentication getPasswordAuthentication() {
//                        System.out.println("masuk2");
//                        return (new PasswordAuthentication("teddy@ezeelink.co.id","zugan418".toCharArray()));
//                    }
//                };
//                Authenticator.setDefault(authenticator);
//                
//            }
            
            Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", "8080");
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            
            conn = (HttpURLConnection) url.openConnection(proxy);
        } 
        else 
        {
            conn = (HttpURLConnection) url.openConnection();
        }
        
        return conn;
    }

    private String sendPushNotification(boolean toTopic)  throws Exception 
    {
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn = (HttpURLConnection) url.openConnection();
    
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);

        System.out.println(root.toString());

        try 
        {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(root.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader( (conn.getInputStream())));

            String output;
            StringBuilder builder = new StringBuilder();
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }
            System.out.println(builder);
            String result = builder.toString();

            JSONObject obj = new JSONObject(result);

            if(toTopic){
                if(obj.has("message_id")){
                    return  "SUCCESS";
                }
           } else {
            int success = Integer.parseInt(obj.getString("success"));
            if (success > 0) {
                return "SUCCESS";
            }
        }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
           return e.getMessage();
        }

    }
    
}