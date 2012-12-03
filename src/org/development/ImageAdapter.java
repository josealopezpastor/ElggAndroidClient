package org.development;



import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageAdapter extends BaseAdapter {
	
	private static Bitmap miImagen;
	private Activity activity;
	private String[] data;
	private static LayoutInflater inflater=null;
	
	public ImageAdapter(Activity a, String[] d) {
        this.activity = a;
        data=d;
        
    }
	
	
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
        	inflater = activity.getLayoutInflater();
            vi = inflater.inflate(R.layout.item, null);

        TextView text=(TextView)vi.findViewById(R.id.text);
        ImageView image=(ImageView)vi.findViewById(R.id.image);
        
        text.setText("Picture "+ position);
        
        miImagen = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/genx/" + data[position]);
        miImagen = Bitmap.createScaledBitmap(miImagen, 360, 360, true);
        
        image.setImageBitmap(miImagen);
        
        return vi;
	}

	
}