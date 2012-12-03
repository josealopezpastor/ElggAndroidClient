package org.development.network;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.DefaultRedirectStrategy;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkOperations {
	
	/**Change URL and strablish the appropiate*/
	public static final String MAIN_URL = "http://192.168.1.135/elgg/";
	
	private static NetworkOperations networkOp;
	private static DefaultHttpClient httpclient;
	private Context context;
	
	private String username;
	private String password;
	
	private NetworkOperations(){}

	
	public static NetworkOperations getNetworkOperations(Context context)
	{
		if(networkOp == null)
			networkOp = new NetworkOperations();

		networkOp.setContext(context);
		
		if(httpclient == null)
		{
			httpclient = new DefaultHttpClient();
			httpclient.log.enableDebug(true);
		}
		
		return networkOp;
	}

	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected() ) {
	        return true;
	    }
	    return false;
	}

	public boolean doLogin(){

		if(isOnline())
		{
			httpclient = new DefaultHttpClient();
			
			List<Cookie> cookies;
			
			try{
				
				HttpGet httpget = new HttpGet(MAIN_URL);

	            HttpResponse response = httpclient.execute(httpget);
	            HttpEntity entity = response.getEntity();

	            EntityUtils.consume(entity);

	            cookies = httpclient.getCookieStore().getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                	if(cookies.get(i).getName().contentEquals("elggperm"))
                		if(cookies.get(i).getExpiryDate().after( new Date()))
                			return true;
                }

	            System.out.println("Initial set of cookies:");
	            cookies = httpclient.getCookieStore().getCookies();
	            
	            if (cookies.isEmpty()) {
	                System.out.println("None");
	            } else {
	                for (int i = 0; i < cookies.size(); i++) {
	                    System.out.println("- " + cookies.get(i).toString());
	                }
	            }
	            
	            HttpPost httpPost = new HttpPost(MAIN_URL+"/action/login");

	            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	            nvps.add(new BasicNameValuePair("username", username));
	            nvps.add(new BasicNameValuePair("password", password));
	            nvps.add(new BasicNameValuePair("persistent", "true"));

	            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

	            response = httpclient.execute(httpPost);
	            entity = response.getEntity();
	            
	            EntityUtils.consume(entity);
		            
	            cookies = httpclient.getCookieStore().getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                	if(cookies.get(i).getName().contentEquals("elggperm"))
                		if(cookies.get(i).getExpiryDate().after( new Date()))
                			return true;
                }

                return false;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		return false;

	}
	
	public boolean doLogin(String username, String password)
	{
		this.username = username;
		this.password = password;

		return doLogin();
		
	}
	
	public boolean uploadFile(String pathfile)
	{
		String body, token, ts, guid;
		HttpGet httpget;
		HttpResponse response;
		HttpEntity entity;
		
		File file = new File(pathfile);
		
		if(!file.canRead())
			return false;
		
        try {
        	//C�digo para habilitar la redirecci�n
        	httpclient.setRedirectStrategy(new DefaultRedirectStrategy() {                
                public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
                    boolean isRedirect=false;
                    try {
                        isRedirect = super.isRedirected(request, response, context);
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    }
                    if (!isRedirect) {
                        int responseCode = response.getStatusLine().getStatusCode();
                        if (responseCode == 301 || responseCode == 302) {
                            return true;
                        }
                    }
                    return isRedirect;
                }
            });
        	
        	
    		httpget = new HttpGet(MAIN_URL+"/file/all");

            response = httpclient.execute(httpget);
            entity = response.getEntity();

            System.out.println("Get: " + response.getStatusLine());
            
            body = EntityUtils.toString(entity);
            guid = getGUID(body);
            
            EntityUtils.consume(entity);

            httpget = new HttpGet(MAIN_URL + "/file/add/"+guid);
            response = httpclient.execute(httpget);
            entity = response.getEntity();
            
            body = EntityUtils.toString(entity);
            token = getToken(body);
            ts = getTS(body);
             
            HttpPost httpPost = new HttpPost(MAIN_URL+"/action/file/upload");

            MultipartEntity mentity = new MultipartEntity();

            mentity.addPart("__elgg_token",new StringBody(token));
            mentity.addPart("__elgg_ts",new StringBody(ts));
            mentity.addPart("upload", new FileBody(file));
            mentity.addPart("title", new StringBody("Titulo"));
            mentity.addPart("description", new StringBody("Descripcion"));
            mentity.addPart("tags", new StringBody("tags"));
            mentity.addPart("access_id", new StringBody("1"));
            mentity.addPart("container_guid", new StringBody(guid));
            
 
            httpPost.setEntity(mentity);
            response = httpclient.execute(httpPost);

            HttpEntity httpEntity = response.getEntity();
            
            body = EntityUtils.toString(httpEntity);
            EntityUtils.consume(httpEntity);

            if(body.contains("elgg-message elgg-state-success"))
            	return true;
            else
            	return false;

        }
        catch (ClientProtocolException e) {
            Log.d("[Jin]", e.getMessage(),e);
        }
        catch (IOException e) {
            Log.d("[Jin]", e.getMessage(),e);
        }
 
		return false;
	}

	private void setContext(Context context) {
		this.context = context;
	}
	

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	private String getToken(String body) {
			
		int pos = body.indexOf("elgg.security.token.__elgg_token");
		int pos_endline = body.indexOf(';', pos);
		
		String sub = body.substring(pos, pos_endline-1);
		sub = sub.replace("elgg.security.token.__elgg_token = '", "");

		return sub;
		
	}

	private String getTS(String body) {
		
		int pos = body.indexOf("elgg.security.token.__elgg_ts");
		int pos_endline = body.indexOf(';', pos);
		
		String sub = body.substring(pos, pos_endline);
		sub = sub.replace("elgg.security.token.__elgg_ts = ", "");

		return sub;
		
	}

	private String getGUID(String body) {
		
		int pos = body.indexOf("\"guid\":");
		int pos_endline = body.indexOf(',', pos);
		
		String sub = body.substring(pos, pos_endline);
		sub = sub.replace("\"guid\":", "");

		return sub;
		
	}

}
