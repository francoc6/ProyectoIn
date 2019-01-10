package com.example.christianfranco.basedatos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.christianfranco.basedatos.ContadordePasos.IntSerBack;

public class Menu extends AppCompatActivity {
    ImageButton imaInfo,imaLOut,imaAgregar,imaPasos,imaConsulta,imaLogros,imaStatus;
    SharedPreferences sp;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//para que no bote error

        imaInfo=(ImageButton)findViewById(R.id.imaInfo);
        imaAgregar=(ImageButton)findViewById(R.id.imaAgregar);
        imaConsulta=(ImageButton)findViewById(R.id.imaConsulta);
        imaLOut=(ImageButton)findViewById(R.id.imaLOut);
        imaLogros=(ImageButton)findViewById(R.id.imaLogros);
        imaPasos=(ImageButton)findViewById(R.id.imaPasos);
        imaStatus=(ImageButton)findViewById(R.id.imaStatus);


        //inicio el servicio de background
        Intent intentservice = new Intent(this, IntSerBack.class);//inicio el servicio
        startService(intentservice);

        sp = getSharedPreferences("logged", MODE_PRIVATE);

        imaAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Menu.this,AgregarDato.class);
                startActivity(add);
                finish();
            }
        });

        imaConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ver = new Intent(Menu.this,Consulta.class);
                //Intent ver = new Intent(Menu.this,Menu.class);//prueba borrarrrrrrrrrrrrrr
                startActivity(ver);
                finish();
            }
        });


        imaPasos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent verdatos = new Intent(Menu.this, Actividad.class);
                //startActivity(verdatos);
                //finish();
                comprobar();
            }
        });

        imaLOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  sp.edit().putBoolean("logged", false).apply();
                Intent salir = new Intent(Menu.this, CuestDiab.class);
                startActivity(salir);
                finish();//evito que regresen al presionar el boto del celular
            }
        });

        imaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Menu.this,Informacion.class);
                startActivity(go);
                finish();
            }
        });

        imaStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Menu.this,Status.class);
                startActivity(go);
                finish();
            }
        });
        imaLogros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Menu.this,Logros.class);
                startActivity(go);
                finish();
            }
        });
    }
    //boton fisico
    @Override
    public void onBackPressed() {//al presionarlo regresa al menu principal, solo si no esta contando pasos, obligando que utilicen el btn de  la app regresar
        finish();
    }

    public void comprobar(){
        if(!checkLocation()){
            return;
        }else{
            //Intent op = new Intent(Menu.this,Actividad.class);
            Intent op = new Intent(Menu.this,Distancia.class);
            startActivity(op);
            finish();
        }

    }

    public boolean checkLocation() {
        if (!isLocationEnabled())
            alerta();
        return isLocationEnabled();
    }

    public void alerta() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Configurar Ubicacion")
                .setMessage("Se necesita que Ubicacion este encendido.\nPor favor activelo")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
