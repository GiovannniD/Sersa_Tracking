package com.example.sersa_tracking;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String countryList[];
    int flags[];
    LayoutInflater inflter;
    ArrayList<String> KeyOrden= new ArrayList<String>();
    ArrayList<String> Cliente= new ArrayList<String>();
    ArrayList<String> Destinatario= new ArrayList<String>();
    ArrayList<String> Origen= new ArrayList<String>();
    ArrayList<String> Destino= new ArrayList<String>();
    ArrayList<String> Cantidad= new ArrayList<String>();

    public CustomAdapter(Context applicationContext, ArrayList<String> Keyorden,ArrayList<String> Cliente,ArrayList<String> Destinatario,ArrayList<String> Origen
            ,ArrayList<String> Destino,ArrayList<String> Cantidad) {
        this.context = context;
        this.KeyOrden = Keyorden;
        this.Cliente=Cliente;
        this.Destinatario=Destinatario;
        this.Origen=Origen;
        this.Destino=Destino;
        this.Cantidad=Cantidad;
      //  this.flags = flags;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return KeyOrden.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.activity_listview, null);
        TextView country = (TextView) view.findViewById(R.id.textView);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        String txt="";
        txt=KeyOrden.get(i)+"\n"+
                "Nombre del cliente: "+Cliente.get(i)+"\n"+
                "Destinatario: "+Destinatario.get(i)+"\n"+
                "Origen: "+Origen.get(i)+"\n"+
                "Destino: "+Destino.get(i)+"\n"+
                "Cantidad: "+Cantidad.get(i)+"\n";
        country.setText(txt);
        //  icon.setImageResource(flags[i]);
        return view;
    }
}