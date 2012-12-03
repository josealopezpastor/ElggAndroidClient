package org.development;

import org.development.network.NetworkOperations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class GenxActivity extends Activity {
	
	
	private NetworkOperations netOp = NetworkOperations.getNetworkOperations(this);
	public static Intent j;
	
	/**The component will be used */
	EditText edit_nombre, edit_password;
	Button button_acceder;
	CheckBox check_recordar;
	
	/** Variables will be used	 */ 
	String username, password;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /** Link with the activity components */
        edit_nombre = (EditText) findViewById(R.id.edit_nombre);
        edit_password = (EditText) findViewById(R.id.edit_password);
        button_acceder = (Button) findViewById(R.id.button_acceder);
        check_recordar = (CheckBox) findViewById(R.id.check_recordar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("Genx",MODE_PRIVATE);
        edit_nombre.setText(sharedPreferences.getString("username", ""));

        if(sharedPreferences.getBoolean("remember", false))
            edit_password.setText(sharedPreferences.getString("password", ""));
        else
            edit_password.setText("");

        check_recordar.setChecked(sharedPreferences.getBoolean("remember", false));

        
    }
    
    public void acceder (View view) {
    	
    	username = edit_nombre.getText().toString();
        password = edit_password.getText().toString();
        

    	Editor editor = (Editor) this.getSharedPreferences("Genx",Context.MODE_PRIVATE).edit();
    	editor.putString("username", username);
       
        if(check_recordar.isChecked()){
        	editor.putString("password",password);
        }
        else
        	editor.putString("password","");
        
        editor.putBoolean("remember", check_recordar.isChecked());
    	editor.commit();
        
    	Conectar loggin = new Conectar(this);
    	j = new Intent(this, ImagenesActivity.class);
    	loggin.execute(username);
        
    }
    
    public class Conectar extends AsyncTask<String, Void, Boolean>{
    	
    	final ProgressDialog dialog = new ProgressDialog(GenxActivity.this);
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
		protected Boolean doInBackground(String... params) {
			
			if(netOp.doLogin(username, password)){
	        	return true;
	        }
	        else{      	
	        	
	        	return false;
	        }
			
		}
		
		 protected void onPostExecute(Boolean respuesta) {
			 if (respuesta == true)
			 {
				 finish();
				 dialog.dismiss();
				 
				 Toast.makeText(GenxActivity.this, "User successfully registered",  Toast.LENGTH_SHORT).show();
		        	//netOp.uploadFile("/mnt/sdcard/Pictures/DemoPicture.jpg");
				 
				 mcontext.startActivity(j);
				 
			 }
			 else{
				 Toast.makeText(GenxActivity.this, "Registration failure",  Toast.LENGTH_SHORT).show();
				 dialog.dismiss();
			 }
	
			 
		}
    
    }
    
   
}