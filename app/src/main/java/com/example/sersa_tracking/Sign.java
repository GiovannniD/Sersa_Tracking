package com.example.sersa_tracking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Sign extends AppCompatActivity {
    SignaturePad signaturePad;
    Button saveButton, clearButton,guardar;
    setting setting= new setting();
    SharedPreferences settings;
    private TextView mTextView;
    String paquetes="";
    Gson gson=new Gson();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        signaturePad = (SignaturePad)findViewById(R.id.signaturePad);
     //   saveButton = (Button)findViewById(R.id.saveButton);
        clearButton = (Button)findViewById(R.id.clearButton);
        guardar = (Button) findViewById(R.id.guardar);

        //disable both buttons at start
        guardar.setEnabled(false);
        clearButton.setEnabled(false);

        //change screen orientation to landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        settings = this.getSharedPreferences("Sersa_Tracking", Context.MODE_PRIVATE);
         paquetes=getIntent().getStringExtra("paquetes");
      //  Toast.makeText(getApplicationContext(), paquetes.substring(0,paquetes.length()-1), Toast.LENGTH_LONG).show();

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                guardar.setEnabled(true);
                clearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                guardar.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Toast.makeText(getApplicationContext(),  bowlingJson(settings.getString("keyRuta", ""), settings.getString("noPlaca", ""), paquetes.substring(0,paquetes.length()-1),
                        intent.getExtra("comentarios",""),

                        "",
                        getIntent().getStringExtra("recibido"),
                        getIntent().getStringExtra("identificacion")), Toast.LENGTH_LONG).show();*/
                guardar.setEnabled(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                //write code for saving the signature here
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap=signaturePad.getSignatureBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                String json = bowlingJson(settings.getString("keyRuta", ""), settings.getString("noPlaca", ""), paquetes.substring(0,paquetes.length()-1),
                        getIntent().getStringExtra("comentarios"),

                        encoded,
                        getIntent().getStringExtra("recibido"),
                        getIntent().getStringExtra("identificacion"));
                if(isNetworkConnected()==true) {
                    try {
                        postwithParameters("http://" + setting.Link() + "/api/kontrolaVehicles/getVehiclesCoordinates/id", json);

                    } catch (IOException e) {
                        //Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();
                        guardarPendientes(json);
                        guardar.setEnabled(true);
                    }
                }else{
                    guardarPendientes(json);
                }
              //  Toast.makeText(getApplicationContext(), paquetes.substring(0,paquetes.length()-1), Toast.LENGTH_SHORT).show();
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
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
    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    setResult(1);
    finish();
}
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    String bowlingJson(String keyRuta,String noPlaca, String paquete,String comentarios,String firma,
                       String recibido,
                       String identificacion) {

        return "{'vehicleCoordinatesPackages':" +
                "[" +
                paquete
                + "]," +
                "'firma':'"+firma+"'," +
                "'comentarios':'"+comentarios+"'," +
                "'recibidoPor':'"+recibido+"'," +
                "'identificacion':'"+identificacion+"'" +
                "}";
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
                               // Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();
                                guardar.setEnabled(true);
                                guardarPendientes(json);

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
                              //  Toast.makeText(getApplicationContext(),paquetes.substring(0,paquetes.length()-1), Toast.LENGTH_LONG).show();
                                setResult(1);
                                finish();


                            }
                        });
                    }
                });
    }



    }
