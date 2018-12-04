package com.example.christianfranco.basedatos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editUSR, editPSW;
    TextView addusr;
    Button btnIN;

    SharedPreferences sp,keepusr;//mantener logeado, y guardar usuario
    Context context =this;
    String usr="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUSR = (EditText) findViewById(R.id.edtUSR);
        editPSW = (EditText) findViewById(R.id.editPSW);
        btnIN = (Button) findViewById(R.id.btnIN);
        addusr = (TextView) findViewById(R.id.registarse);
        sp = getSharedPreferences("logged",MODE_PRIVATE);//varia para mantenerse logeado

        if (sp.getBoolean("logged", false)) {//este metodo revisa si ya esta logeado
            iraprincipal();
            finish();
        }

        btnIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ingreso(editUSR.getText().toString(), editPSW.getText().toString())) {
                        usr=editUSR.getText().toString();
                        sp.edit().putBoolean("logged", true).apply();//cambia el valor a que ya esta logueado
                        iraprincipal();
                        guardarusr();//solo se guarda cundo se inicia sesion
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "CREDENCIALES INCORRECTAS", Toast.LENGTH_SHORT).show();
                        editUSR.setText("");
                        editPSW.setText("");
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "REVISA TU CONEXION", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addusr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newusr = new Intent(MainActivity.this, Registro.class);
                startActivity(newusr);
                finish();//evito que regresen al presionar el boton del celular
            }
        });
    }

    //ir a menu principal
    public void iraprincipal() {

       // Intent menuprin = new Intent(MainActivity.this, MenuuPrincipal.class); //menu solo con botones
        Intent menuprin = new Intent(MainActivity.this, Menu.class);//nuevo menu con imagenes
        startActivity(menuprin);

    }

    Conectar contacto = new Conectar();
    //descargar lista para comprobar usuarios y contraseña
    public boolean ingreso(String u, String p) {
        List<Usuario> usuarios = new ArrayList<>();
        Usuario test = new Usuario();
        try {
            Statement pedir = contacto.conectarabase().createStatement();
            ResultSet res = pedir.executeQuery("select * from RegistroUsuarios_db");
            while (res.next()) {
                usuarios.add(new Usuario(res.getString("Usuario"), res.getString("Pass")));
            }
            res.close();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return test.comprobar(u, p, usuarios);
    }

    //metodo para guardar usuario
    public void guardarusr(){
        keepusr = getSharedPreferences("Guardarusuario",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = keepusr.edit();
        editor.putString("usuario",usr);//guardo el usuario con el que estoy trabajando
        editor.commit();
    }
}