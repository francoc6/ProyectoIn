package com.example.christianfranco.basedatos;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conectar extends AppCompatActivity {
    public static boolean banderaformulario=false;//bandera externa para formualario nada relacionado con conexion

    public Conectar() {
    }

    public Connection conectarabase(){
        Connection conexion=null;//objeto tipo conection para acceder
        try{
            //registramos el permiso de acceso para la aplicacion
            StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
           conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://172.19.69.52;port=1433;databaseName=Proyecto_db;user=sa;password=admin;");

            //conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.104;port=1433;databaseName=Proyecto_db;user=sa;password=admin;");
        }catch(Exception d){
            Toast.makeText(getApplicationContext(),d.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }
}
