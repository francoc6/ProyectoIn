package com.example.christianfranco.basedatos.DialogPre;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christianfranco.basedatos.Actividad;
import com.example.christianfranco.basedatos.Conectar;
import com.example.christianfranco.basedatos.R;
import com.example.christianfranco.basedatos.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class DialogIni extends DialogFragment {
    //widgets
    private RadioButton p11, p12, p13, p14, p15;
    private RadioButton p21, p22, p23, p24, p25;
    private RadioButton p31, p32, p33, p34, p35;
    private RadioButton p41, p42, p43, p44, p45;
    private Button Aceptar;
    public static TextView Preg1,Preg2,Preg3,Preg4;

    public static String tabla="",toast="";
    SharedPreferences usuariognr;//lo uso para obtener el usuario almacenado

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_formulario_ini, container, false);
        p11 = (RadioButton) view.findViewById(R.id.i11);
        p12 = (RadioButton) view.findViewById(R.id.i12);
        p13 = (RadioButton) view.findViewById(R.id.i13);
        p14 = (RadioButton) view.findViewById(R.id.i14);
        p15 = (RadioButton) view.findViewById(R.id.i15);

        p21 = (RadioButton) view.findViewById(R.id.i21);
        p22 = (RadioButton) view.findViewById(R.id.i22);
        p23 = (RadioButton) view.findViewById(R.id.i23);
        p24 = (RadioButton) view.findViewById(R.id.i24);
        p25 = (RadioButton) view.findViewById(R.id.i25);

        p31 = (RadioButton) view.findViewById(R.id.i31);
        p32 = (RadioButton) view.findViewById(R.id.i32);
        p33 = (RadioButton) view.findViewById(R.id.i33);
        p34 = (RadioButton) view.findViewById(R.id.i34);
        p35 = (RadioButton) view.findViewById(R.id.i35);

        p41 = (RadioButton) view.findViewById(R.id.i41);
        p42 = (RadioButton) view.findViewById(R.id.i42);
        p43 = (RadioButton) view.findViewById(R.id.i43);
        p44 = (RadioButton) view.findViewById(R.id.i44);
        p45 = (RadioButton) view.findViewById(R.id.i45);

        Preg1=(TextView)view.findViewById(R.id.Pregunta1);
        Preg2=(TextView)view.findViewById(R.id.Pregunta2);
        Preg3=(TextView)view.findViewById(R.id.Pregunta3);
        Preg4=(TextView)view.findViewById(R.id.Pregunta4);

        Aceptar = view.findViewById(R.id.iniAceptar);

        usuariognr = getActivity().getSharedPreferences("Guardarusuario",MODE_PRIVATE);//instancio el objeto para obtener usuario

        //valido si es el inicio o final para mostrar las preguntas correctas
        if(!Usuario.banderaformulario){
            Actividad.yasehizo=true;//cuando se lo hace por primera vez, ya no presenta el formulario interfiere cuando se presiona el boton para pausar cronometro
            Usuario.banderaformulario=true;//para ver si se presentan las preguntas iniciales o finales
        }else{
            Actividad.yasehizo=false;
            Usuario.banderaformulario=false;
        }

        obtenerPreguntas();

        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String res =usuariognr.getString("usuario","  ");
                getDialog().dismiss();//salir de la pantalla

            }
        });

        return view;
    }

    public void resultados() {//se lo podria hacer con un case, pero seria casi lo mismo obteniendo los id
        String res="";//creo un string que se concatenara con las respuestas
        //Pregunta 1
        if (p11.isChecked()) {
            res+="1 ";
        } else if (p12.isChecked()) {
            res+="2 ";
        } else if (p13.isChecked()) {
            res+="3 ";
        } else if (p14.isChecked()) {
            res+="4 ";
        } else if (p15.isChecked()) {
            res+="5 ";
        }
        //Pregunta 2
        if (p21.isChecked()) {
            res+="1 ";
        } else if (p22.isChecked()) {
            res+="2 ";
        } else if (p23.isChecked()) {
            res+="3 ";
        } else if (p24.isChecked()) {
            res+="4 ";
        } else if (p25.isChecked()) {
            res+="5 ";
        }
        //Pregunta 3
        if (p31.isChecked()) {
            res+="1 ";
        } else if (p32.isChecked()) {
            res+="2 ";
        } else if (p33.isChecked()) {
            res+="3 ";
        } else if (p34.isChecked()) {
            res+="4 ";
        } else if (p35.isChecked()) {
            res+="5 ";
        }
        //Pregunta 4
        if (p41.isChecked()) {
            res+="1 ";
        } else if (p42.isChecked()) {
            res+="2 ";
        } else if (p43.isChecked()) {
            res+="3 ";
        } else if (p44.isChecked()) {
            res+="4 ";
        } else if (p45.isChecked()) {
            res+="5 ";
        }
        if(Usuario.banderaformulario)
        Actividad.Preguntas_I=res;//agrego a la variable correspondiente
        else {
            Actividad.Preguntas_F=res;
        }
    }

//obtengo las preguntas a presentar desde la base de datos
    Conectar conectar = new Conectar();
    public void obtenerPreguntas() {
        List<String> preg = new ArrayList<>();
        try {
            String tipo;
            if (Usuario.banderaformulario){ tipo="Inicio"; }else{ tipo="Fin";}
            Statement pedir = conectar.conectarabase().createStatement();
            String orden ="SELECT Texto FROM Preguntas_db WHERE TIPO='"+tipo+"'";
            ResultSet res=null;
            res = pedir.executeQuery(orden);
           // res.next();
            while (res.next()) {
                preg.add(res.getString("Texto"));
            }
            res.close();
        } catch (SQLException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Preg1.setText(preg.get(0));
        Preg2.setText(preg.get(1));
        Preg3.setText(preg.get(2));
        Preg4.setText(preg.get(3));
    }

}