package com.example.christianfranco.basedatos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.christianfranco.basedatos.ContadordePasos.IntSerBack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Distancia extends AppCompatActivity implements LocationListener {

    Button BtnStart, BtnPausa, BtnStop, BtnRegresar;
    Chronometer simpleChronometer;
    public static boolean yasehizo=false, banderapausa;//para ejecutar formulario
    public static long tiempopausa, tiempofinal;

    //////////////////////////////////////////////ubicacion/////////////////////////////////////////////////////////////////
    public static String tvLongi;
    public static String tvLati;
    Double Lat1,Long1,Lat2,Long2;
    LocationManager locationManager;
    ///////////////////////////////////////////
    ////////////////////////////////////////clima////////////////////////////////////////////////////////////////////////
    final String APP_ID = "b13f14be7c0909550f568e788748a9b8";
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    public String temperatura,ciudad;
//////////////////////////////////////////////////////////////////////////////////////



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distancia);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnPausa = (Button) findViewById(R.id.Pausa);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnRegresar = (Button) findViewById(R.id.btnRegresar);
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer);
        simpleChronometer.setFormat("%s");


        if (yasehizo) {
            BtnStart.setEnabled(false);
            BtnPausa.setEnabled(true);
            BtnStop.setEnabled(true);
            BtnRegresar.setEnabled(false);
            simpleChronometer.start();
        } else {
            detener();//empieza detenido el reloj de pantalla
            //para que solo se pueda presionar empezar
            BtnPausa.setEnabled(false);
            BtnStop.setEnabled(false);
            //preguntas
            guardarpreguntas();
        }

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //cronometro
               start();
                if (yasehizo == false) {
                    Intent preg = new Intent(Distancia.this, Preguntas.class);
                    startActivity(preg);
                    finish();
                } else {
                    BtnPausa.setEnabled(true);
                    BtnStart.setEnabled(false);
                }
            }
        });


        BtnPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              pausa();
                BtnPausa.setEnabled(false);
                BtnStart.setEnabled(true);
                BtnRegresar.setEnabled(false);
                //intent service
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
              detener();
                Intent preg = new Intent(Distancia.this, Preguntas.class);
                startActivity(preg);
                BtnPausa.setEnabled(false);
                BtnStop.setEnabled(false);
                BtnStart.setEnabled(true);
                BtnRegresar.setEnabled(true);
            }
        });

        BtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BtnStart.isEnabled() && !BtnPausa.isEnabled() && !BtnStop.isEnabled()) {
                    Intent menu = new Intent(Distancia.this, Menu.class);
                    startActivity(menu);
                    finish();
                    onPause();//listener del gps
                }
            }
        });

    }


    public static String obtenertiempo(long t) {
        int resmili, resseg, resmin, reshora;
        String mili, seg, min, hora;
        long x;
        String res = "";
        if (t < 3600000) {//minutos, sin horas
            resmili = (int) (t % 1000);
            mili = String.valueOf(resmili);
            x = t / 1000;
            resseg = (int) (x % 100);
            seg = String.valueOf(resseg);
            if (resseg < 10) {
                seg = "0" + String.valueOf(resseg);
            }
            x = x / 100;
            resmin = (int) (x % 100);
            min = String.valueOf(resmin);
            if (resmin < 10) {
                min = "0" + String.valueOf(resmin);
            }
            res = "00:" + min + ":" + seg + ":" + mili;
        } else {//ya hay horas
            resmili = (int) (t % 1000);
            mili = String.valueOf(resmili);
            x = t / 1000;
            resseg = (int) (x % 100);
            seg = String.valueOf(resseg);
            if (resseg < 10) {
                seg = "0" + String.valueOf(resseg);
            }
            x = x / 100;
            resmin = (int) (x % 100);
            min = String.valueOf(resmin);
            if (resmin < 10) {
                min = "0" + String.valueOf(resmin);
            }
            x = x / 100;
            reshora = (int) (x % 100);
            hora = String.valueOf(reshora);
            if (reshora < 10) {
                hora = "0" + String.valueOf(reshora);
            }
            res = hora + ":" + min + ":" + seg + ":" + mili;
        }
        return res;
    }

    /////////////////////////////////metodos reloj///////////////////////////////////////////////////////////////
    public void start() {
        if (!banderapausa) {
            simpleChronometer.setBase(SystemClock.elapsedRealtime() - tiempopausa);
            simpleChronometer.start();
            banderapausa = true;
        }
    }

    public void pausa() {
        if (banderapausa) {
            simpleChronometer.stop();
            banderapausa = false;
            tiempopausa = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        }
    }

    public void detener() {
        simpleChronometer.stop();
        if (!banderapausa) {
            tiempofinal = tiempopausa;
        } else {
            tiempofinal = SystemClock.elapsedRealtime() - simpleChronometer.getBase();//tiempo final
        }
        tiempopausa = 0;
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        banderapausa = false;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //metodo para almacenar datos en memoria del dispositivo
    public void guardatos(String Latitud, String Longitud) {
        SharedPreferences keepdata = getSharedPreferences("Ubicacion", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = keepdata.edit();
        editor.putString("Latitud", Latitud);
        editor.putString("Longitud", Longitud);
        editor.commit();
    }


    ///////////////////////////////////////////////ubicacion///////////////////////////////////////////////
    public void onResume() {
        super.onResume();
        getLocation();
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        ArrayList<String> ubicaciones = new ArrayList<>();
        ubicaciones.add(String.valueOf(location.getLongitude())+","+String.valueOf(location.getLatitude()));
        tvLongi = String.valueOf(location.getLongitude());
        tvLati = String.valueOf(location.getLatitude());
        if(!yasehizo) {//para que solo lo haga al iniciar la pantalla
            if (location.getLongitude() != 0 && location.getLatitude() != 0) {
                guardatos(tvLati, tvLongi);
                //  Toast.makeText(getApplicationContext(), "Se obtuvo posicion " + tvLati + " " + tvLongi, Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation(tvLati, tvLongi);//inicio metodo para obtener clima con la posicion
                onDestroy();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider!" + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }



    public void obtenerdistancia(){

    }


    public double calculodistnacias(String d1,String d2){
        Double r=null;
        String[] parts = d1.split(",");
        Float Lat1=Float.valueOf(parts[0]);
        Float Long1=Float.valueOf(parts[1]);
        String[] parts2=d2.split(",");
        Float Lat2=Float.valueOf(parts2[0]);
        Float Long2=Float.valueOf(parts2[1]);
        Double k=6372.795477598;
        Float dLat=Lat2-Lat1;
        Float dLong=Long2-Long1;
        r=2*k*1/(sin(sqrt((sin(dLat/2)*sin(dLat/2))+(cos(Lat1)*cos(Lat2)*sin(dLong/2)*sin(dLong/2)))));//formula para distancia entre dos coordenadas
        return r;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////CLIMA///////////////////////////////////////////////////////////////////////////////////////////////////////////

    //se utiliza la version gratuita de un api de OPENWEATHERMAP.ORG el cual solo permite 60 consultas por minuto
    //Con onLocationChanged de la posicion, se llama al metodo para que obtenga el clima y la ciudad

    //en este metodo se envia la latitud y longitud, con la key para acceder al OPENWEATHER, a letsDoSomeNetworking
    public void getWeatherForCurrentLocation(String latitud, String longitud) {
        String Longitude = longitud;
        String Latitude = latitud;
        RequestParams params = new RequestParams();
        params.put("lat", Latitude);
        params.put("lon", Longitude);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }

    //se crea instancia AsyncHttpClient el cual envia los datos a la clase WeatherDataModel y a su vez recibe el resultado de  la misma en ObtenerDatos
    public void letsDoSomeNetworking(final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] header, JSONObject response) {
                // Log.d("Clima", "Success" + response.toString());
                WeatherDataModel weatherdata = WeatherDataModel.fromJSON(response);
                ObtenerDatos(weatherdata);
            }

            public void onFailure(int statuscode, Header[] header, Throwable e, JSONObject response) {
                //Log.d("Clima", "Status code" + statuscode);
                //Log.d("Clima", "Fail" + e.toString());
                Toast.makeText(Distancia.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                //  getWeatherForCurrentLocation();
            }

        });
    }

    //los asigna a las variables temperatura y ciudad
    public void ObtenerDatos(WeatherDataModel data){
        temperatura=data.getTemperature();
        ciudad=data.getCity();
        // Toast.makeText(getApplicationContext(),temperatura+" y "+ciudad, Toast.LENGTH_SHORT).show();
        SharedPreferences keepdata = getSharedPreferences("Clima", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = keepdata.edit();
        editor.putString("Temperatura", temperatura);
        editor.putString("Ciudad", ciudad);
        editor.commit();


    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////







    //////////////////////preguntas////////////////////////////////////////////////////////////////////////////////////////
    //guardo las preguntas inicilaes y finales
    Conectar conectar = new Conectar();
    public void guardarpreguntas() {
        List<String> pregi = new ArrayList<>();
        List<String> pregf = new ArrayList<>();
        try {
            Statement pedir = conectar.conectarabase().createStatement();
            String orden = "SELECT Texto FROM Preguntas_db WHERE TIPO='Inicio'";
            ResultSet res = null;
            res = pedir.executeQuery(orden);
            //res.next();
            while (res.next()) {
                pregi.add(res.getString("Texto"));
            }
            res.close();
            //guardo las preguntas las preguntas para mostrar

            SharedPreferences data = getSharedPreferences("Preguntas", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString("i1", pregi.get(0));
            editor.putString("i2", pregi.get(1));
            editor.putString("i3", pregi.get(2));
            editor.putString("i4", pregi.get(3));
            editor.commit();

            res = pedir.executeQuery("SELECT Texto FROM Preguntas_db WHERE TIPO='Fin'");
            while (res.next()) {
                pregf.add(res.getString("Texto"));
            }
            res.close();
            editor.putString("f1", pregf.get(0));
            editor.putString("f2", pregf.get(1));
            editor.putString("f3", pregf.get(2));
            editor.putString("f4", pregf.get(3));
            editor.commit();
        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(),"No se puede obtener preguntas.", Toast.LENGTH_SHORT).show();
        }
    }

}
