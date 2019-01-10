package com.example.christianfranco.basedatos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class Preguntas extends AppCompatActivity {

    //widgets
    private RadioButton p11, p12, p13, p14, p15;
    private RadioButton p21, p22, p23, p24, p25;
    private RadioButton p31, p32, p33, p34, p35;
    private RadioButton p41, p42, p43, p44, p45;
    private Button Aceptar;
    public static TextView Preg1, Preg2, Preg3, Preg4;
    SharedPreferences usuariognr, posicion, temp;//lo uso para obtener el usuario almacenado
    public String PF;
    boolean confirmar = false;

    ///////////////////////Obtengo la fecha y la hora////////////////////////////////////////////////
    java.util.Calendar calendarNow = new GregorianCalendar(TimeZone.getTimeZone("America/Guayaquil"));
    String time = calendarNow.getTime().toString();
    String[] parts = time.split(" ");//Thu Dec 06 21:06:21 GMT-05:00 2018
    String fecha = parts[2] + "/" + parts[1] + "/" + parts[5];
    String hora = parts[3];
//////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        p11 = (RadioButton) findViewById(R.id.i11);
        p12 = (RadioButton) findViewById(R.id.i12);
        p13 = (RadioButton) findViewById(R.id.i13);
        p14 = (RadioButton) findViewById(R.id.i14);
        p15 = (RadioButton) findViewById(R.id.i15);
        p21 = (RadioButton) findViewById(R.id.i21);
        p22 = (RadioButton) findViewById(R.id.i22);
        p23 = (RadioButton) findViewById(R.id.i23);
        p24 = (RadioButton) findViewById(R.id.i24);
        p25 = (RadioButton) findViewById(R.id.i25);
        p31 = (RadioButton) findViewById(R.id.i31);
        p32 = (RadioButton) findViewById(R.id.i32);
        p33 = (RadioButton) findViewById(R.id.i33);
        p34 = (RadioButton) findViewById(R.id.i34);
        p35 = (RadioButton) findViewById(R.id.i35);
        p41 = (RadioButton) findViewById(R.id.i41);
        p42 = (RadioButton) findViewById(R.id.i42);
        p43 = (RadioButton) findViewById(R.id.i43);
        p44 = (RadioButton) findViewById(R.id.i44);
        p45 = (RadioButton) findViewById(R.id.i45);
        Preg1 = (TextView) findViewById(R.id.Pregunta1);
        Preg2 = (TextView) findViewById(R.id.Pregunta2);
        Preg3 = (TextView) findViewById(R.id.Pregunta3);
        Preg4 = (TextView) findViewById(R.id.Pregunta4);
        Aceptar = findViewById(R.id.iniAceptar);

///////////////////////////////////////////////////////DATOS GUARDADOS EN EL DISPOSITIVO//////////////////////////////////////////////////////////////////////////////////
        usuariognr = getSharedPreferences("Guardarusuario", MODE_PRIVATE);//instancio el objeto para obtener usuario
        final String usuario = usuariognr.getString("usuario", "vacio");
        posicion = getSharedPreferences("Ubicacion", MODE_PRIVATE);//instancio para obtener latitud y longitud
        final String Lati = posicion.getString("Latitud", "vacio");
        final String Longi = posicion.getString("Longitud", "vacio");
        temp = getSharedPreferences("Clima", MODE_PRIVATE);//instancio el objeto para obtener las vaiables el clima
        final String Temperatura = temp.getString("Temperatura", "vacio");
        final String Ciudad = temp.getString("Ciudad", "vacio");
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //valido si es el inicio o final para mostrar las preguntas correctas
        if (!Conectar.banderaformulario) {
            //Actividad.yasehizo = true;//cuando se lo hace por primera vez, ya no presenta el formulario interfiere cuando se presiona el boton para pausar cronometro
            Distancia.yasehizo = true;//cuando se lo hace por primera vez, ya no presenta el formulario interfiere cuando se presiona el boton para pausar cronometro
            Conectar.banderaformulario = true;//para ver si se presentan las preguntas iniciales o finales
        } else {
           // Actividad.yasehizo = false;
             Distancia.yasehizo = false;
            Conectar.banderaformulario = false;
        }

        //cargo las preguntas correspondientes a presentar
        //obtenerPreguntas();
        cargarpreguntas();

        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultados();
                if (confirmar == true) {
                    if (!Conectar.banderaformulario) {
                        // posicion(usuario, String.valueOf(Actividad.pasos));//actualizo la tabla de posicion para logros
                        agregaractividad(usuario, fecha, hora, String.valueOf(Actividad.obtenertiempo(Actividad.tiempofinal)), String.valueOf(Actividad.pasos), Actividad.Preguntas_I, PF, Lati, Longi, Temperatura, Ciudad);
                        Intent salir = new Intent(Preguntas.this, Menu.class);
                        startActivity(salir);
                        finish();
                    } else {
//                        Intent salir = new Intent(Preguntas.this, Actividad.class);
                        Intent salir = new Intent(Preguntas.this, Distancia.class);

                        startActivity(salir);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor responda el formulario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //obtengo las respuestas de las preguntas en un string
    public void resultados() {//se lo podria hacer con un case, pero seria casi lo mismo obteniendo los id
        String res = "";//creo un string que se concatenara con las respuestas
        //Pregunta 1
        if (p11.isChecked()) {
            res += "1 ";
        } else if (p12.isChecked()) {
            res += "2 ";
        } else if (p13.isChecked()) {
            res += "3 ";
        } else if (p14.isChecked()) {
            res += "4 ";
        } else if (p15.isChecked()) {
            res += "5 ";
        }
        //Pregunta 2
        if (p21.isChecked()) {
            res += "1 ";
        } else if (p22.isChecked()) {
            res += "2 ";
        } else if (p23.isChecked()) {
            res += "3 ";
        } else if (p24.isChecked()) {
            res += "4 ";
        } else if (p25.isChecked()) {
            res += "5 ";
        }
        //Pregunta 3
        if (p31.isChecked()) {
            res += "1 ";
        } else if (p32.isChecked()) {
            res += "2 ";
        } else if (p33.isChecked()) {
            res += "3 ";
        } else if (p34.isChecked()) {
            res += "4 ";
        } else if (p35.isChecked()) {
            res += "5 ";
        }
        //Pregunta 4
        if (p41.isChecked()) {
            res += "1 ";
        } else if (p42.isChecked()) {
            res += "2 ";
        } else if (p43.isChecked()) {
            res += "3 ";
        } else if (p44.isChecked()) {
            res += "4 ";
        } else if (p45.isChecked()) {
            res += "5 ";
        }

        if (res.length() == 8) {
            confirmar = true;
            if (Conectar.banderaformulario) {
                Actividad.Preguntas_I = res;//agrego a la variable correspondiente
            } else {
                PF = res;
            }
        }
    }

    public void cargarpreguntas() {
        String tipo;
        if (Conectar.banderaformulario) {
            tipo = "Inicio";
        } else {
            tipo = "Fin";
        }
        if (tipo.equals("Inicio")) {
            SharedPreferences data = getSharedPreferences("Preguntas", getApplicationContext().MODE_PRIVATE);//instancio el objeto para obtener usuario
            Preg1.setText(data.getString("i1", "vacio"));
            Preg2.setText(data.getString("i2", "vacio"));
            Preg3.setText(data.getString("i3", "vacio"));
            Preg4.setText(data.getString("i4", "vacio"));
        } else {
            SharedPreferences data = getSharedPreferences("Preguntas", getApplicationContext().MODE_PRIVATE);//instancio el objeto para obtener usuario
            Preg1.setText(data.getString("f1", "vacio"));
            Preg2.setText(data.getString("f2", "vacio"));
            Preg3.setText(data.getString("f3", "vacio"));
            Preg4.setText(data.getString("f4", "vacio"));
        }

    }

    //metodo para agregar datos a la base
    Conectar conectar = new Conectar();
    public void agregaractividad(String u, String f, String h, String t, String p, String PI, String PF, String Lat, String Long, String temp, String Ciudad) {
        String orden = "insert into ACTIVIDAD values(?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pedir = conectar.conectarabase().prepareStatement(orden);
            pedir.setString(1, u);
            pedir.setString(2, f);
            pedir.setString(3, h);
            pedir.setString(4, t);
            pedir.setString(5, p);
            pedir.setString(6, PI);
            pedir.setString(7, PF);
            pedir.setString(8, Lat + "," + Long);
            pedir.setString(9, temp);
            pedir.setString(10, Ciudad);
            pedir.executeUpdate();
            Toast.makeText(getApplicationContext(), "Se agrego a la base", Toast.LENGTH_SHORT).show();
            pedir.close();//cierro la conexion
            posicion(u, p);//actualizo la tabla de posicion para logros
        } catch (SQLException e) {
            guardarluego(u, f, h, t, p, PI, PF, Lat, Long, temp, Ciudad);//los almaceno para luego subirlos a la base
            Toast.makeText(getApplicationContext(), "Hubo un problemqa de red, se guardaran los  datos luego", Toast.LENGTH_SHORT).show();
        }
    }

    //boton fisico
    @Override
    public void onBackPressed() {//al presionarlo regresa al menu principal, solo si no esta contando pasos, obligando que utilicen el btn de  la app regresar
    }

    //metodo para almacenar datos en memoria del dispositivo, si no hay conexion a la base
    public void guardarluego(String u, String f, String h, String t, String p, String PI, String PF, String Lat, String Long, String temp, String Ciudad) {
        SharedPreferences keepdata = getSharedPreferences("Backup", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = keepdata.edit();
        editor.putString("Usuario", u);
        editor.putString("Tiempo", t);
        editor.putString("Fecha", f);
        editor.putString("Hora", h);
        editor.putString("Pasos", p);
        editor.putString("PregIni", PI);
        editor.putString("PregFin", PF);
        editor.putString("Lat", Lat);
        editor.putString("Long", Long);
        editor.putString("temp", temp);
        editor.putString("Ciudad", Ciudad);
        editor.commit();
    }

    //actualiza el registro de pasos totales
    public void posicion(String u, String p) {
        String orden = "select Pasos from TotalPasos_db WHERE Usuario='" + u + "'";
        try {
            Statement pedir = conectar.conectarabase().createStatement();
            ResultSet res = null;
            res = pedir.executeQuery(orden);
            res.next();
            int r1, r2, r3;
            r1 = Integer.valueOf(p);
            r2 = Integer.valueOf(res.getInt("Pasos"));//paso el string a int para poder sumarlo
            r3 = r1 + r2;
            //utilizo executeUodate ya que no necesito respuesta
            pedir.executeUpdate("UPDATE TotalPasos_db SET Pasos =" + r3 + " WHERE Usuario ='" + u + "'");//actualizo el valor de la tabl
            Toast.makeText(getApplicationContext(), "Se modifico registro", Toast.LENGTH_SHORT).show();
            pedir.close();//cierro la conexion
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
