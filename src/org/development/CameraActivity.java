package org.development;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.development.network.NetworkOperations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity{
	
	ImageView iv;
	Button button_upload;
	private String name = "";
	private static int TAKE_PICTURE = 100;
	public static Intent j;
	
	private NetworkOperations networkOperations = NetworkOperations.getNetworkOperations(this);
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraactivity);
        
        iv = (ImageView)findViewById(R.id.imgView);
        button_upload = (Button) findViewById(R.id.button_upload);
        
        /**Cuando pulsemos el botón de tomar foto*/
		Calendar cal = new GregorianCalendar();
	    Date date = cal.getTime();

	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	    String formatteDate = df.format(date);
		 
        name = Environment.getExternalStorageDirectory() + "/genx/genx"+formatteDate+".jpg";
        
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
					
		Uri output = Uri.fromFile(new File(name));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
	    
	    startActivityForResult(intent, TAKE_PICTURE);
	}
		
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(requestCode==TAKE_PICTURE){
			if(resultCode==RESULT_OK){
				
				ImageView iv = (ImageView)findViewById(R.id.imgView);
				iv.setImageBitmap(BitmapFactory.decodeFile(name));				
			}
		}
		else{
			Toast.makeText(CameraActivity.this, "Fallo al capturar la foto",  Toast.LENGTH_SHORT).show();
		}
	}
	
	public void upload (View view){
		j = new Intent(this, ImagenesActivity.class);
		Conectar conectar = new Conectar(this);
		conectar.execute();
	}
	
public class Conectar extends AsyncTask<Void, Void, Boolean>{
    	
    	final ProgressDialog dialog = new ProgressDialog(CameraActivity.this);
    	Context mcontext;
    	
    	Conectar(Context context){
    		mcontext = context;
    	}
    	
    	
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading...");
		    dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			
			if(networkOperations.doLogin())
				networkOperations.uploadFile(name);
			
			return true;
			
		}
		 
		protected void onPostExecute(Boolean respuesta) {
			 if (respuesta == true)
			 {
				 finish();
				 dialog.dismiss();
				 
				 Toast.makeText(CameraActivity.this, "Imagen subida con éxito",  Toast.LENGTH_SHORT).show();

				 /**We send to the pictures Activity*/
				 finish();
				 mcontext.startActivity(j);
			 }
			 else
			 {
				 Toast.makeText(CameraActivity.this, "Fallo en la subida",  Toast.LENGTH_SHORT).show();
				 dialog.dismiss();
			 }
		 }
	}
}