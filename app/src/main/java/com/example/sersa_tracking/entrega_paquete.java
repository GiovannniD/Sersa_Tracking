package com.example.sersa_tracking;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class entrega_paquete extends AppCompatActivity {

    setting setting= new setting();
    EditText comentarios,identificacion,recibido,Cpaquete;
    String date;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    SharedPreferences settings;
    OkHttpClient client = new OkHttpClient();
    String data []={"Entregado","Rechazado"};
    String estado,codigo_paquete;
    Gson gson=new Gson();
    Button guardar;
    String paquetes="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_paquete);
        comentarios = (EditText) findViewById(R.id.comentarios);
        identificacion = (EditText) findViewById(R.id.identificacion);
        recibido = (EditText) findViewById(R.id.recibido);
        Cpaquete = (EditText) findViewById(R.id.paquete);
        guardar = (Button) findViewById(R.id.guardar);

        setTitle(getIntent().getStringExtra("codigo_paquete"));
        codigo_paquete=getIntent().getStringExtra("codigo_paquete");
        settings = this.getSharedPreferences("Sersa_Tracking", Context.MODE_PRIVATE);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        guardar.setEnabled(false);


        identificacion.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 14)
                    recibido.requestFocus();
            }
        });

        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_selected, data);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 // Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
               // keyRuta=parent.getItemAtPosition(position).toString();
                estado=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

public void firma(View view) {

if(identificacion.getText().toString()!="" || recibido.getText().toString()!="")
{

    Intent intent = new Intent(entrega_paquete.this, Sign.class);
    intent.putExtra("paquetes",paquetes);
    intent.putExtra("comentarios",comentarios.getText().toString());
    intent.putExtra("identificacion",identificacion.getText().toString());
    intent.putExtra("recibido",recibido.getText().toString());
    startActivityForResult(intent,1);
}else{
    Toast.makeText(getApplicationContext(), "debe rellenar los campos identificacion o la persona que recibe el paquete!" , Toast.LENGTH_LONG).show();
}

 /*if(estado=="Rechazado") {
     guardar.setEnabled(false);
     findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
     String json = bowlingJson(settings.getString("keyRuta", ""), settings.getString("noPlaca", ""), "4", codigo_paquete,
             comentarios.getText().toString(),recibido.getText().toString(),identificacion.getText().toString());
     try {
        // Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
          postwithParameters("http://"+setting.Link()+"/api/kontrolaVehicles/getVehiclesCoordinates/id", json);

     } catch (IOException e) {
         Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();

         guardar.setEnabled(true);
     }
 }else
     {
         Intent intent = new Intent(entrega_paquete.this, Sign.class);
         intent.putExtra("estado","3");
         intent.putExtra("comentarios",comentarios.getText().toString());
         intent.putExtra("identificacion",identificacion.getText().toString());
         intent.putExtra("recibido",recibido.getText().toString());
         intent.putExtra("codigo_paquete",codigo_paquete);
         startActivityForResult(intent,1);
     }*/

    //Toast.makeText(getApplicationContext(),paquetes, Toast.LENGTH_LONG).show();
    }

    /*String postwith(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }*/

    String bowlingJson(String keyRuta,String noPlaca, String estado,String codigo_paquete,String comentarios,
                       String recibido,
                       String identificacion) {

        return "{'vehicleCoordinatesPackages':" +
                "[{" +
                "'noPlaca':'"+noPlaca+"',"
                + "'imei':'868718052609110',"
                + "'keyRuta':'"+keyRuta+"',"
                + "'keyEstadoPaquete':'"+estado+"',"
                + "'codigoPaquete':'10309182',"
                + "}]," +
                "'firma':''," +
                "'comentarios':'"+comentarios+"'," +
                "'recibidoPor':'"+recibido+"'," +
                "'identificacion':'"+identificacion+"'" +
                "}";
    }

    private String CurrentTime()
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//formating according to my need
        date = formatter.format(currentTime);
        return date;
    }

    void postwithParameters(String url, String json) throws IOException{

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120,TimeUnit.SECONDS)

                .build();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();
                                guardar.setEnabled(true);

                                setting.tempSavePackage.put(codigo_paquete,json);
                                settings.edit().putString("paquete_pendientes",gson.toJson(setting.tempSavePackage)).apply();
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                setResult(1);
                                finish();


                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();
                        //   findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                setResult(1);
                                finish();


                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1: {
                setResult(1);
                finish();
                break;
            }
            default:
                break;
        }


    }

    public void siguiente(View view) {
        int insertar=0;
        if(Cpaquete.getText().toString()!=""){
        if(estado=="Rechazado") {
            insertar=4;
        }else  {
            insertar=3;
        }
    /*  paquetes+="'{noPlaca':'"+settings.getString("noPlaca", "")+"',"
                + "'imei':'868718052609110',"
                + "'keyRuta':'"+settings.getString("keyRuta", "")+"',"
                + "'keyEstadoPaquete':'"+ insertar+"',"
                + "'codigoPaquete':'"+Cpaquete.getText().toString()+"'"
                + "},";*/
            paquetes+=" {\n" +
                    "            \"noPlaca\": \""+settings.getString("noPlaca", "")+"\",\n" +
                    "            \"imei\": \"868718052609110\",\n" +
                    "            \"keyRuta\": "+settings.getString("keyRuta", "")+",          \n" +
                    "            \"keyEstadoPaquete\": "+insertar+",\n" +
                    "            \"codigoPaquete\": \""+Cpaquete.getText().toString()+"\"            \n" +
                    "        },";
        identificacion.setEnabled(false);
        recibido.setEnabled(false);
            Cpaquete.setText("");
        Cpaquete.requestFocus();
        guardar.setEnabled(true);
        }else{
            Toast.makeText(getApplicationContext(),"El código del paquete no puede estar vacío!", Toast.LENGTH_LONG).show();
            Cpaquete.requestFocus();
        }
     //   Toast.makeText(getApplicationContext(),paquetes, Toast.LENGTH_LONG).show();
    }
}