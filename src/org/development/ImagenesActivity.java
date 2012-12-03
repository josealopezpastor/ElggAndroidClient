package org.development;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class ImagenesActivity extends Activity {
	
	ListView list;
	Intent intent;
	TextView toma_foto;
	Button button_foto;
	ImageAdapter adapter;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagenesactivity);
                
        toma_foto = (TextView) findViewById(R.id.toma_foto);
        button_foto = (Button) findViewById(R.id.button_foto);
        list=(ListView)findViewById(R.id.list);
        
        File file = new File(Environment.getExternalStorageDirectory() + "/genx/");
        String[] filas = file.list();
               
        if(filas==null)
        	filas = new String[0];
        
        adapter=new ImageAdapter(this, filas);
        list.setAdapter(adapter);     
    }
	
	
	public void foto (View view){
		/**We send to the camera Activity*/
		Intent j = new Intent(this, CameraActivity.class);
		startActivity(j);
		finish();		
	}

	
	
	
	
}
