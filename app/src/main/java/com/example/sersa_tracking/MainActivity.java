package com.example.sersa_tracking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.mikepenz.aboutlibraries.LibsBuilder;
public class MainActivity extends AppCompatActivity {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    ListView simpleList;
    setting setting= new setting();
    CustomAdapter customAdapter;
    String date;
    String countryList[] = {"India", "China", "australia", "Portugle", "America", "NewZealand"};
    RequestBody body;
    Gson gson = new Gson();
    ArrayList<String> KeyOrden= new ArrayList<String>();
    ArrayList<String> Cliente= new ArrayList<String>();
    ArrayList<String> Destinatario= new ArrayList<String>();
    ArrayList<String> Origen= new ArrayList<String>();
    ArrayList<String> Destino= new ArrayList<String>();
    ArrayList<String> Cantidad= new ArrayList<String>();
    OkHttpClient client = new OkHttpClient();
    String response_2="";
   // int flags[] = {R.drawable.india, R.drawable.china, R.drawable.australia, R.drawable.portugle, R.drawable.america, R.drawable.new_zealand};
   SharedPreferences settings;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public final int CUSTOMIZED_REQUEST_CODE = 0x0000ffff;


    @Override
    protected void onCreate( Bundle savedInstanceState) {

      setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        simpleList = (ListView) findViewById(R.id.simpleListView);
        settings = this.getSharedPreferences("Sersa_Tracking", Context.MODE_PRIVATE);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//formating according to my need
        date = formatter.format(currentTime);

        try {
            Paquetes_Pendientes();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        // Toast.makeText(this, settings.getString("paquete_pendientes",""), Toast.LENGTH_LONG).show();
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        String json =bowlingJson(settings.getString("keyRuta",""), settings.getString("keyVehiculo",""),settings.getString("FechaActual",""),1);
       body = RequestBody.create(json, JSON);
        try {

             postwithParameters("http://"+setting.Link()+"/api/AppMobile/GetAllRoutesPackages/id", json);
            //  obj = new JSONObject(response);


        } catch (IOException e ) {
            Log.d("error", e.getMessage());
        }
       /* Snackbar.make(getWindow().getDecorView().getRootView(), settings.getString("keyVehiculo","0"), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/
     //   Toolbar toolbar = findViewById(R.id.toolbar);
   //     setSupportActionBar(toolbar);

       // FloatingActionButton fab = findViewById(R.id.fab);
    /*    fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/



    }
private void Paquetes_Pendientes() throws JSONException, IOException {
 //   Toast.makeText(getApplicationContext(), settings.getString("paquete_pendientes",""), Toast.LENGTH_LONG).show();
    if(settings.getString("paquete_pendientes","") !="")
    {
        if(isNetworkConnected()==true)
        {
            JSONObject obj = new JSONObject(settings.getString("paquete_pendientes",""));
           setting.tempSavePackage=setting.JsonToMapString(obj);
            Iterator it2 =setting.tempSavePackage.keySet().iterator();

            while (it2.hasNext()) {
                String key = it2.next().toString();
                String json=setting.tempSavePackage.get(key);
                post("http://"+setting.Link()+"/api/kontrolaVehicles/getVehiclesCoordinates/id", json,key);
            }


        }
    }else{
        Toast.makeText(getApplicationContext(), "No hay paquetes pendientes " , Toast.LENGTH_LONG).show();
    }
}
    void post(String url, String json,String key) throws IOException{

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
                               // String key = String.valueOf(setting.tempSavePackage.size()+1);
                              //  setting.tempSavePackage.remove(key);
                               //setting.tempSavePackage.put(key,json);

                                Toast.makeText(getApplicationContext(), "Vuelva a intentarlo, " + e.getMessage(), Toast.LENGTH_LONG).show();
                             //   guardar.setEnabled(true);


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
                                setting.tempSavePackage.remove(key);
                                if(setting.tempSavePackage.size()==0){
                                    settings.edit().putString("paquete_pendientes","").apply();
                                }


                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            //    setResult(1);
                              //  finish();


                            }
                        });
                    }
                });
    }

    void postwithParameters(String url, String json) throws IOException{

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120,TimeUnit.SECONDS)

                .build();
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
                                Log.d("OnFailure",e.getMessage());


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

                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                try {
                                    JSONArray  arr = new JSONArray(res);
                                    for (int i = 0; i < arr.length(); i++)
                                    {
                                        JSONObject e = arr.getJSONObject(i);
                                        KeyOrden.add(e.getString("keyOT"));
                                        Cliente.add(e.getString("cliente"));
                                        Destinatario.add(e.getString("destinatario"));
                                        Origen.add(e.getString("origen"));
                                        Destino.add(e.getString("destino"));
                                        Cantidad.add(e.getString("cantidad"));


                                    }
                                    customAdapter = new CustomAdapter(getApplicationContext(), KeyOrden,Cliente,
                                            Destinatario,Origen,Destino,Cantidad);
                                    simpleList.setAdapter(customAdapter);
                                    //   response();
                                    // Log.d("JASON",res);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });




                        // Do something with the response
                    }
                });
    }

    String bowlingJson(String keyRuta, String keyVehiculo,String Fecha,int caso) {
        String res="";
      /*  return "{'winCondition':'HIGH_SCORE',"
                + "'name':'Bowling',"
                + "'round':4,"
                + "'lastSaved':1367702411696,"
                + "'dateStarted':1367702378785,"
                + "'players':["
                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                + "]}";*/
if(caso==1)
{
    res= "{"
            + "'keyRuta':"+keyRuta+","
            + "'keyVehiculo':"+keyVehiculo+","
            + "'date':'"+Fecha+"'"
            + "}";
}else
    {
        res= "{'idStatusRoutes':'4',"
                + "'keyRuta':'"+keyRuta+"',"
                + "'keyVehiculo':'"+keyVehiculo+"',"
                + "'routeDate':'"+Fecha+"'"
                + "}";
    }
return res;
    }
    private String CurrentTime()
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//formating according to my need
        date = formatter.format(currentTime);
        return date;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.actualizar).setVisible(true);
        return true;
    }
    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    public void scanBarcodeInverted(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.INVERTED_SCAN);
        integrator.initiateScan();
    }
    public void scanMixedBarcodes(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        integrator.initiateScan();
    }

    public void scanBarcodeCustomLayout(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(scan.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan something");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actualizar) {
            String json =bowlingJson(settings.getString("keyRuta",""), settings.getString("keyVehiculo",""),settings.getString("FechaActual",""),1);
            body = RequestBody.create(json, JSON);
            try {
                Paquetes_Pendientes();
                postwithParameters("http://"+setting.Link()+"/api/AppMobile/GetAllRoutesPackages/id", json);
                //  obj = new JSONObject(response);


            } catch (IOException | JSONException e ) {
                Log.d("error", e.getMessage());
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void scanContinuous(View view) {
        Intent intent = new Intent(this, scan.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOMIZED_REQUEST_CODE && requestCode != IntentIntegrator.REQUEST_CODE) {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case CUSTOMIZED_REQUEST_CODE: {
                Toast.makeText(this, "REQUEST_CODE = " + requestCode, Toast.LENGTH_LONG).show();
                break;
            }
            case 1:
                Toast.makeText(getApplicationContext(), "Entrega Actualizada!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);

        if(result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                Toast.makeText(this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("MainActivity", "Scanned");
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void cerrarRuta(View view) {


        if(isNetworkConnected()==true){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    com.example.sersa_tracking.MainActivity.this);

            // set title
            alertDialogBuilder.setTitle("Aviso!");
            alertDialogBuilder
                    // set dialog message

                    .setMessage("Esta seguro de Cerrar la ruta?")
                    .setCancelable(false)
                    .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            try {

                                String json =bowlingJson(settings.getString("keyRuta",""),settings.getString("keyVehiculo",""),CurrentTime(),2);

                                String response = postwith("http://"+setting.Link()+"/api/AppMobile/insertStatusRoutesDeparture/id", json);
                                //  obj = new JSONObject(response);
                                Intent intent = new Intent(MainActivity.this, com.example.sersa_tracking.Menu.class);
                                settings.edit().clear().apply();
                                startActivity(intent);
                                // Log.d("JaSON", json);
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

    String postwith(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public void Scan(View view) {
        Intent intent = new Intent(this, entrega_paquete.class);
        startActivityForResult(intent,1);
    }
}