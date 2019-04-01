package me.mariocmflys.nmc.launcher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Mojang {
	
	public static String getJSON(String link, String body) throws IOException {
        /**StringEntity entity = new StringEntity(body,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);
        latestStatus = response.getStatusLine().getStatusCode();
        //response.getEntity().getContent().
        
        if(response.getEntity() == null) {
        	return null;
        }
        else {
        	return EntityUtils.toString(response.getEntity());
        }
        **/
        
		// new code: http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
        URL url;
        HttpURLConnection connection = null;  
        try {
          //Create connection
          url = new URL(link);
          connection = (HttpURLConnection)url.openConnection();
          connection.setRequestMethod("POST");
          connection.setRequestProperty("Content-Type", 
               "application/json");
    			
          connection.setRequestProperty("Content-Length", "" + 
                   Integer.toString(body.getBytes().length));
          connection.setRequestProperty("Content-Language", "en-US");  
    			
          connection.setUseCaches (false);
          connection.setDoInput(true);
          connection.setDoOutput(true);

          //Send request
          DataOutputStream wr = new DataOutputStream (
                      connection.getOutputStream ());
          wr.writeBytes (body);
          wr.flush ();
          wr.close ();

          //Get Response	
          InputStream is = connection.getInputStream();
          BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          String line;
          StringBuffer response = new StringBuffer(); 
          while((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\n');
          }
          rd.close();
          return response.toString();

        } catch (Exception e) {

          e.printStackTrace();
          return null;

        } finally {

          if(connection != null) {
            connection.disconnect(); 
          }
        }
	}
	
	/**
	 * Retrieve account information and tokens 
	 * @param username Account username / E-Mail
	 * @param password Account password
	 * @return JSONObject of API response
	 */
	public static JSONObject generateToken(String username, String password) {
		// https://authserver.mojang.com/authenticate
		String body = "{\"agent\": {\"name\": \"Minecraft\",\"version\": 1},\"username\": \""+username+"\",\"password\": \""+password+"\"}";
		try {
			String json = getJSON("https://authserver.mojang.com/authenticate", body);
			JSONObject b = new JSONObject(json);
			return b;
		} catch(Exception e) {
			return null;
		}
		
	}
	
	/**
	 * Checks if access token is valid
	 * @param token Access token
	 * @return True if valid access token 
	 */
	public static boolean validateToken(String token) {
		String body = "{\"accessToken\": \""+token+"\"}";
		try {
			if(getJSON("https://authserver.mojang.com/validate", body) == null) {return false;}			
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Get new access token from valid client token
	 * @param token Old access token
	 * @param client Client token
	 * @return New access token
	 */
	public static String refreshToken(String token, String client) {
		String body = "{\"accessToken\": \""+token+"\", \"clientToken\": \""+client+"\"}"; // this should work, but has not been tested ( same w validateToken above^)
		try {
			JSONObject j = new JSONObject(getJSON("https://authserver.mojang.com/refresh", body));
			return j.getString("accessToken");
		} catch(Exception e) {
			return null;
		}
		
	}
}