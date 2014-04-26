package edu.uqac.multimedia.uqacmons;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UqacmonAdapter extends BaseAdapter {
	private Activity activity;
    private static LayoutInflater inflater=null;
    
	ArrayList<Boolean> uqacmonIsCaptured;
	ArrayList<Integer> uqacmonImg;
	ArrayList<String> uqacmonName;
	ArrayList<String> uqacmonType;
 
    public UqacmonAdapter(Activity a, ArrayList<String> names,  ArrayList<String> types,  ArrayList<Boolean> captured, ArrayList<Integer> images) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        uqacmonIsCaptured = captured;
        uqacmonName = names;
        uqacmonType = types;
        uqacmonImg = images;
    }
 
    public int getCount() {
        return uqacmonName.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
         vi = inflater.inflate(R.layout.get_uqacmon_ui, null);
 
        TextView name = (TextView)vi.findViewById(R.id.uqacmonUIName); // name
        TextView type = (TextView)vi.findViewById(R.id.uqacmonUIType); // type
        ImageView image=(ImageView)vi.findViewById(R.id.uqacmonUIPicture); // image

        if(uqacmonIsCaptured.get(position)) {
			image.setImageResource(GetUqacmonPicture(uqacmonImg.get(position)));
			name.setText(uqacmonName.get(position));
			type.setText(uqacmonType.get(position));	
		} else {
			image.setImageResource(GetUqacmonPicture(-1));
			name.setText("?????????");
			type.setText("??????");	
		}

        return vi;
    }
    
    public int GetUqacmonPicture(int id) {
		switch (id)
		{    
			case -1:
				return(R.drawable.uqacmon_mistery); //on envoie -1 quand l'uqacmon n'est pas capturé encore
			case 0:
				return(R.drawable.uqacmon_djamal);
			case 1:
				return(R.drawable.uqacmon_verreault);
		    case 2:
		    	return(R.drawable.uqacmon_dallaire);
		    case 3:
		    	return(R.drawable.uqacmon_bob);
		    case 4:
		    	return(R.drawable.uqacmon_pierre);
		    case 5:
		    	return(R.drawable.uqacmon_alexandre);
		    case 6:
		    	return(R.drawable.uqacmon_tarik);
		    case 7:
		    	return(R.drawable.uqacmon_bruno);
		    case 8:
		    	return(R.drawable.uqacmon_jean);
		    default:
		    	return(R.drawable.ic_launcher);
		}
	}
}
