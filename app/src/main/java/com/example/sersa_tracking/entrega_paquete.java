package com.example.sersa_tracking;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
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
import java.util.HashMap;
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
    TextView conteo;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    SharedPreferences settings;
    OkHttpClient client = new OkHttpClient();
    String data []={"Entregado","Rechazado","Cerrado","Direccion no Localizada"};
    String estado,codigo_paquete;
    Gson gson=new Gson();
    Button guardar;
    String paquetes="";
    int insertar=0;
    int control=0;
    public MediaPlayer mp,mp2;
    public SoundPool sp;
    public SoundPool sp2;
    public int flujodemusica=0;
    public int flujodemusica2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_paquete);
        comentarios = (EditText) findViewById(R.id.comentarios);
        identificacion = (EditText) findViewById(R.id.identificacion);
        recibido = (EditText) findViewById(R.id.recibido);
        Cpaquete = (EditText) findViewById(R.id.paquete);
        conteo = (TextView) findViewById(R.id.conteo);
        guardar = (Button) findViewById(R.id.guardar);

        setTitle(getIntent().getStringExtra("codigo_paquete"));
        codigo_paquete=getIntent().getStringExtra("codigo_paquete");
        settings = this.getSharedPreferences("Sersa_Tracking", Context.MODE_PRIVATE);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        guardar.setEnabled(false);
        mp= MediaPlayer.create(this, R.raw.error);
        mp2= MediaPlayer.create(this, R.raw.alerta1);
        sp = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        flujodemusica= sp.load(this,R.raw.error,1);
        flujodemusica2= sp.load(this,R.raw.alerta1,1);

        try {
            JSONObject obj  = new JSONObject(settings.getString("tempSaveCode",""));
            setting.tempSaveCode=setting.JsonToMapString(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    switch(insertar)
    {
        // declaraci??n case
        // los valores deben ser del mismo tipo de la expresi??n
        case 3 :

        case 4 :
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
            break; // break es opcional
        case 8 :
        case 9 :
            if(isNetworkConnected()==true) {
                try {
                    postwithParameters("http://" + setting.Link() + "/api/kontrolaVehicles/getVehiclesCoordinates/id", EntregaPaqueteJson());

                } catch (IOException e) {
                    //Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();
                    guardarPendientes(EntregaPaqueteJson());
                 //   guardar.setEnabled(true);
                }
            }else{
                guardarPendientes(EntregaPaqueteJson());
            }
            break;

        default :
            // Declaraciones
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

    String EntregaPaqueteJson() {

        return "{'vehicleCoordinatesPackages':" +
                "[" +
                paquetes
                + "]," +
                "'firma':''," +
                "'comentarios':'"+comentarios.getText().toString()+"'," +
                "'recibidoPor':''," +
                "'identificacion':''" +
                "}";
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
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

                                guardarPendientes(json);

                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult(1);
                                finish();


                            }
                        });
                    }
                });
    }
                    private void guardarPendientes(String json){
                        if( settings.getString("paquete_pendientes","")!=""){

                            try {
                                JSONObject obj = new JSONObject(settings.getString("paquete_pendientes",""));
                                setting.tempSavePackage=setting.JsonToMapString(obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String key = String.valueOf(setting.tempSavePackage.size()+1);
                        setting.tempSavePackage.put(key,json);
                        settings.edit().putString("paquete_pendientes",gson.toJson(setting.tempSavePackage)).apply();
                      //  findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        setResult(1);
                        finish();
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

    public void play_sp1() {
// TODO Auto-generated method stub
        sp.play(flujodemusica, 1, 1, 0, 0, 1);
    }

    public void play_sp2() {
// TODO Auto-generated method stub
        sp.play(flujodemusica2, 1, 1, 0, 0, 1);
    }
    public void siguiente(View view) {
        if (Cpaquete.getText().toString().trim().length()>=8) {
        if (!Cpaquete.getText().toString().trim().isEmpty()) {
            if (estado == "Rechazado") {
                insertar = 4;
            } else if (estado == "Entregado") {
                insertar = 3;
            } else if (estado == "Cerrado") {
                insertar = 8;
            } else if (estado == "Direccion no Localizada") {
                insertar = 9;
            }

  if(setting.tempSaveCode.get(Cpaquete.getText().toString().trim())==null && setting.tempSaveCode.size()>0)
  {
      play_sp2();
      Toast.makeText(getApplicationContext(),"Paquete no valido!", Toast.LENGTH_LONG).show();
      return;
  }
  if(setting.tempSaveScannedCode.get(Cpaquete.getText().toString().trim())!=null)
            {
                play_sp2();
                Toast.makeText(getApplicationContext(),"Este paquete ya fue escaneado", Toast.LENGTH_LONG).show();
                return;
            }
            //Toast.makeText(getApplicationContext(),setting.tempSaveScannedCode.get(Cpaquete.getText().toString().trim()), Toast.LENGTH_LONG).show();
            paquetes += " {\n" +
                    "            \"noPlaca\": \"" + settings.getString("noPlaca", "") + "\",\n" +
                    "            \"imei\": \"868718052609110\",\n" +
                    "            \"keyRuta\": " + settings.getString("keyRuta", "") + ",          \n" +
                    "            \"keyEstadoPaquete\": " + insertar + ",\n" +
                    "            \"codigoPaquete\": \"" + Cpaquete.getText().toString().trim() + "\"            \n" +
                    "        },";
            setting.tempSaveScannedCode.put(Cpaquete.getText().toString().trim(),Cpaquete.getText().toString().trim());
            identificacion.setEnabled(false);
            recibido.setEnabled(false);
            Cpaquete.setText("");
            Cpaquete.requestFocus();
            guardar.setEnabled(true);
            control++;


            conteo.setText(""+control);

        } else {
            Toast.makeText(getApplicationContext(), "El c??digo del paquete no puede estar vac??o!", Toast.LENGTH_LONG).show();
            Cpaquete.requestFocus();
        }
    }
        else {
            Toast.makeText(getApplicationContext(), "El c??digo del paquete no puede estar vac??o!", Toast.LENGTH_SHORT).show();
            Cpaquete.requestFocus();
            play_sp1();
        }
     //   Toast.makeText(getApplicationContext(),paquetes, Toast.LENGTH_LONG).show();
    }
}