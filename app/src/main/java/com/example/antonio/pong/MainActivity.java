package com.example.antonio.pong;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;

import android.app.Dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity { //En vez de Activity ;)
    //Definimos los botones
    Button player1;
    Button player2;
    Button opciones;
    Button fondoFoto;
    Button exit;

    //Variables para pasarlas como parámetros extra en el intent.
    public static final String PLAYER ="player";
    public static final String PARTIDAS = "partidas"; //(Static final sino caca al pasar a PongActivity)
    public static final String LEVEL = "level";
    public static final String GRAVITY ="gravity";
    public static final String BITMAP = "bitmap";

    private MediaPlayer sonidoIntro; //Gestor de sonido para la intro

    //Variables auxiliares para intent
    int partidas; //numero de partidas
    int level;    //nivel de juego
    int gravity=0; //Gravity, inicializamos a 0= touch

    private Bitmap mBitmap; //bitmap para tomar imagen


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSettings();
        setInterfaz();
      }

    //Construimos la Interfaz del menú principal
    public void setInterfaz(){

        //Asignamos las referencias a los botones
        player1 = (Button) findViewById(R.id.oneplayer);
        player2 = (Button) findViewById(R.id.twoplayer);
        opciones = (Button) findViewById(R.id.opciones);
        fondoFoto = (Button) findViewById(R.id.button);
        exit = (Button)findViewById(R.id.salir);

        //Asignamos los botones a sus respectivos Listener
        player1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClick1player();
            }
        });
        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick2player();
            }
        });
        fondoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickTakePhoto();
            }
        });
        opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOptions();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickExit();
            }
        });


        //Animacion cambiando el color de fondo
        ObjectAnimator backgroundColorAnimator =
        ObjectAnimator.ofObject(findViewById(R.id.ll_container), "backgroundColor", new ArgbEvaluator(), 0xff003399,
        0xff0000ff, 0xff00ffff, 0xffffffff, 0xffff0000, 0xffffff00, 0xff00ff00);
        backgroundColorAnimator.setDuration(3000); //Tiempo de duración color
        backgroundColorAnimator.setRepeatCount(2); //Numero de repeticiones
        backgroundColorAnimator.start();

        //Lanzamos el sonido de la intro
        sonidoIntro = MediaPlayer.create(this, R.raw.getlucky);
        sonidoIntro.start();
    }

    //Valores por defecto
    void setSettings() {
        level = 0;
        partidas=2; //Inicializamos el número de partidas
    }

    /**
     * Bloque de métodos onCLICK
     */

    //Un jugador - Comprobamos sensor por medio del intent que pasamos a PongActivity
    public void onClick1player() {
        sonidoIntro.stop(); //Paramos sonido de intro

        //LLamamos a PongActivity y le pasamos las opciones de juego
        Intent i = new Intent(this, PongActivity.class);
        i.putExtra(PLAYER, 1);
        i.putExtra(LEVEL, level);
        i.putExtra(PARTIDAS, partidas); //MainActivity->PongView
        i.putExtra(GRAVITY,gravity);


        if (mBitmap != null) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            i.putExtra(BITMAP, bs.toByteArray());
        }
        startActivity(i);
    }

    //Dos jugadores - Comprobamos sensor por medio del intent que pasamos a PongActivity
    public void onClick2player() {
             //Paramos sonido de intro
             sonidoIntro.stop();

            //Creamos un intent para incluir parámetros en PongActivity
            Intent i = new Intent(this, PongActivity.class);
            i.putExtra(PLAYER, 2);
            i.putExtra(LEVEL, level);
            i.putExtra(PARTIDAS, partidas);
            i.putExtra(GRAVITY,gravity);


            if (mBitmap != null) {
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                i.putExtra(BITMAP, bs.toByteArray());
            }
            startActivity(i);
    }

    //Submenú de opciones de juego
    public void onClickOptions() {
        setSettings();

        //Creamos un nuevo cuadro de dialogo
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_submenu);

        //creamos el grupoSensor
        RadioGroup grupoSensor = (RadioGroup) dialog.findViewById(R.id.sensores);
        grupoSensor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                gravity = radioGroup.indexOfChild(radioButton);
            }
        });


        //Creamos el grupoDificultad
        RadioGroup grupoDificultad = (RadioGroup) dialog.findViewById(R.id.niveldificultad);
        grupoDificultad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                level = radioGroup.indexOfChild(radioButton);
            }
        });

       //Creamos el grupoPartidas
        RadioGroup grupoPartidas = (RadioGroup) dialog.findViewById(R.id.numPartidas);
        grupoPartidas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int aux;
                aux=radioGroup.indexOfChild(radioButton);

                //Asignamos el número de partidas
                //aux=0 por defecto a 2;
                if(aux==0)
                {
                    partidas=2;
                }
                if (aux==1)
                {
                    partidas=3; //radioButton Tres
                }
                if(aux==2)
                {
                    partidas=5; //radioButton Cinco
                }
                if(aux==3)
                {
                    partidas=7; //radioButton Siete
                }
            }
        });
        //Creamos el botón OK
        Button btn_OK=(Button)dialog.findViewById(R.id.btnOk);
        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //Botón Cambiar Fondo-Foto:  Abre CamaraBox
    public void onClickTakePhoto() {
        CamaraBox.setFoto(this, this);
    }

    //Recibimos el resultado de realizar la foto con la cámara
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CamaraBox.REQUEST_CAMERA) {
                mBitmap = (Bitmap) data.getExtras().get("data");
            }
        }
    }
    //Botón Salir: Matamos el proceso
    public void onClickExit(){
       android.os.Process.killProcess(android.os.Process.myPid());
    }

}//MainActivity
