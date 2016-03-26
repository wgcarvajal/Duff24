package duff24.com.duff24.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import duff24.com.duff24.R;
import duff24.com.duff24.util.FontCache;

/**
 * Created by geovanny on 26/03/16.
 */
public class AdaptadorSpinnerString extends ArrayAdapter
{

    private String font_path="font/A_Simple_Life.ttf";

    private List<String> data;
    private Context context;

    public AdaptadorSpinnerString(Context context, int resource, List objects) {
        super(context, resource, objects);

        this.context=context;
        this.data=objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {


        View v=null;

        if(convertView == null)
        {
            v = View.inflate(context, R.layout.template_spinner_forma_pago ,null);

        }else
        {
            v = convertView;
        }
        TextView item=(TextView)v.findViewById(R.id.txt_item_spinner_forma_pago);
        Typeface TF = FontCache.get(font_path, context);
        item.setTypeface(TF);


        if(context.getResources().getString(R.string.idioma).equals("es"))
        {
            item.setText(data.get(position));
        }
        else
        {
            item.setText(data.get(position));
        }

        return v;
    }

}
