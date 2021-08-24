package com.example.sersa_tracking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Menu extends AppCompatActivity {
    String date;
    String s="hello";
    Calendar myCalendar=Calendar.getInstance();
    final int mes = myCalendar.get(Calendar.MONTH);
    final int dia = myCalendar.get(Calendar.DAY_OF_MONTH);
    final int anio = myCalendar.get(Calendar.YEAR);
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    SharedPreferences settings;
    setting setting= new setting();
    OkHttpClient client = new OkHttpClient();
    private TextView mTextView;
    public LinkedHashMap<String, String> Rutas= new LinkedHashMap<String, String>();
    public LinkedHashMap<String, String> Vehiculos= new LinkedHashMap<String, String>();
    private String keyVehiculo,keyRuta,noPlaca;
    EditText FechaActual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        FechaActual  = ((EditText) findViewById(R.id.fechaActual));

        FechaActual.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                obtenerFecha(1);
            }
        });
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
         settings = this.getSharedPreferences("Sersa_Tracking", Context.MODE_PRIVATE);
        if(settings.getString("keyRuta","") !="")
        {
            load();
        }

        String json =bowlingJson("Jesse", "Jake","");
        JSONObject obj;

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//formating according to my need
      date = formatter.format(currentTime);
        
        ArrayList<String> data= new ArrayList<String>();
        ArrayList<String> data2= new ArrayList<String>();
         // Toast.makeText(getApplicationContext(), s.substring(0,s.length()-1), Toast.LENGTH_LONG).show();
        try {

            String response = post("http://"+setting.Link()+"/api/AppMobile/GetAllRoutes", json);
          //  obj = new JSONObject(response);
            JSONArray arr = new JSONArray(response);
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject e = arr.getJSONObject(i);
                data.add(e.getString("descripcionRuta")); //this adds an element to the list.
                Rutas.put(e.getString("descripcionRuta"),e.getString("keyRuta"));

                //  Toast.makeText(getApplicationContext(), LoadSucursal.get(arr.getJSONObject(i).getString("ip")), Toast.LENGTH_LONG).show();
            }

        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }
        mTextView = (TextView) findViewById(R.id.text);


        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_selected, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
                keyRuta=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Enables Always-on

        try {

            String response2 = post("http://"+setting.Link()+"/api/AppMobile/GetAllVehicles", json);
            //  obj = new JSONObject(response);
            JSONArray arr2 = new JSONArray(response2);
            for (int i = 0; i < arr2.length(); i++)
            {
                JSONObject e = arr2.getJSONObject(i);
                data2.add(e.getString("noPlaca")); //this adds an element to the list.
                Vehiculos.put(e.getString("noPlaca"),e.getString("keyVehiculo"));
                //  Toast.makeText(getApplicationContext(), LoadSucursal.get(arr.getJSONObject(i).getString("ip")), Toast.LENGTH_LONG).show();
            }
            // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
        } catch (IOException | JSONException e) {
            Log.d("error", e.getMessage());
        }
        ArrayAdapter adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_selected, data2);
        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner2 = findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
                keyVehiculo=parent.getItemAtPosition(position).toString();
                noPlaca=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void obtenerFecha(final int request){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                if (request==1) {
                    FechaActual.setText(year + "-" + mesFormateado + "-" + diaFormateado);
                }else{
                  // mFechaFinal.setText(year + "-" + mesFormateado + "-" + diaFormateado);
                }


            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        Calendar c = Calendar.getInstance();

        recogerFecha.getDatePicker().setMaxDate(c.getTimeInMillis());
        recogerFecha.show();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void paquete(View view) {
if(isNetworkConnected()==true){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Menu.this);

        // set title
        alertDialogBuilder.setTitle("Aviso!");
        alertDialogBuilder
                // set dialog message

                .setMessage("Esta seguro de aperturar la ruta y vehiculo seleccionado?")
                .setCancelable(false)
                .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        try {
                            keyRuta= Rutas.get(keyRuta);
                            keyVehiculo= Vehiculos.get(keyVehiculo);
                            String json =bowlingJson(keyRuta ,keyVehiculo,date);

                            String response = postwithParameters("http://"+setting.Link()+"/api/AppMobile/insertStatusRoutesDeparture/id", json);
                            //  obj = new JSONObject(response);
                            Intent intent = new Intent(Menu.this, MainActivity.class);
                            settings.edit().putString("keyRuta",keyRuta).apply();
                            settings.edit().putString("keyVehiculo",keyVehiculo).apply();
                            settings.edit().putString("noPlaca",noPlaca).apply();
                            settings.edit().putString("FechaActual",FechaActual.getText().toString()).apply();
                            startActivity(intent);

                            finish();

                        } catch (IOException e) {
                            Log.d("error", e.getMessage());
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
}else{
    Snackbar.make(getWindow().getDecorView().getRootView(), "No hay Conexión a internet!", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
}

    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
               // .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    String postwithParameters(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                 .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


private void load()
{
    Intent intent = new Intent(Menu.this, MainActivity.class);
    startActivity(intent);
    finish();
}
String bowlingJson(String keyRuta, String keyVehiculo,String Fecha) {
      /*  return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";*/

        return "{'idStatusRoutes':'2',"
                + "'keyRuta':'"+keyRuta+"',"
                + "'keyVehiculo':'"+keyVehiculo+"',"
                + "'routeDate':'"+Fecha+"'"
                + "}";
    }


   }