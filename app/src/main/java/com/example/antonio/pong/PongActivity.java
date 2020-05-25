package com.example.antonio.pong;

/**
 * Created by Antonio on 20/06/2016.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class PongActivity extends AppCompatActivity implements SensorEventListener {

    private PongView mPongView;
    private PongActivity pongActivity; //La usamos para cortar en caso de elegir Acelerometro
    private SensorManager sensorManager;
    private Sensor sensorGravity;

    public int isGravity;  // 0 touch , 1 acelerometro
    public int is2playerGravity;//Variable para lanzar el aviso de modo de juego

    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong);
        RelativeLayout fondo = (RelativeLayout) findViewById(R.id.RelativeLayoutPong); //activity_pong

        pongActivity=this;
        Intent i = getIntent();
        Bundle b = i.getExtras();

        //Recogemos el valor getExtra y se lo asignamos a isGravity
        int isG=b.getInt(MainActivity.GRAVITY); // 0 touch , 1 acelerometro
        this.isGravity=isG;

        //Recogemos el valor getExtra y se lo asignamos a is2playerGravity
        is2playerGravity=b.getInt(MainActivity.PLAYER);

        mPongView = (PongView) findViewById(R.id.pongView); //activity_pong >>PongView

        //[viene de] MainActivity --> PongActivity --> [va a]PongView
        mPongView.setNumeroJugadores(b.getInt(MainActivity.PLAYER)); //cojo los extras
        mPongView.setDificultad(b.getInt(MainActivity.LEVEL));
        mPongView.setPartidas(b.getInt(MainActivity.PARTIDAS));


       //Compruebo si se ha incluido una foto de fondo, si es así la inserto
        if (i.hasExtra(MainActivity.BITMAP)) {
            bitmap = BitmapFactory.decodeByteArray(
                    i.getByteArrayExtra(MainActivity.BITMAP), 0, i.getByteArrayExtra(MainActivity.BITMAP).length);
            fondo.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }

        //Si hemos seleccionado Gravity
        if(isG==1)
        {
            initializeGravity();
        }

    }//onCreate

    //Inicializamos  Gravity
    public void initializeGravity(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        isSensorAvailaible(sensorGravity);
    }

    //Comprobamos si el sensor está disponible, si no avisamos
    public  boolean isSensorAvailaible(Sensor sensor)
    {
        if (sensor==null)
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.alert_no_gravity))
                    .setTitle(getString(R.string.alert_atencion))
                    .setCancelable(false)
                    .setNeutralButton(getString(R.string.alert_btn_aceptar),
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {

                }
            });

            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
         return false;
        }
        if((sensor!=null) && (is2playerGravity==2))
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.alert_2p_gravity))
                    .setTitle(getString(R.string.alert_atencion))
                    .setCancelable(false)
                    .setNeutralButton(getString(R.string.alert_btn_aceptar),
                            new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            });

            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();
        }
        if((sensor!=null)&&(is2playerGravity==1))
        {
            Toast t=Toast.makeText(this, "GRAVITY por defecto, hasta SALIR", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP,20,20);
            t.show();
        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Envío los datos del sensor
        mPongView.updateFromSensor(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Bloque de Funciones de Ciclo de Vida de la Actividad
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Elimino el listener para el sensor
        if ((isGravity == 1)&&(sensorManager != null))
        {
            sensorManager.unregisterListener(this);
        }
    }

    protected void onStop() {
        super.onStop();
        //Paro el juego
        mPongView.stop();
    }

    protected void onResume() {
        super.onResume();
        mPongView.resume();
        if ((isGravity == 1)&&(sensorManager != null)) {
            sensorManager.registerListener(this, sensorGravity, SensorManager.SENSOR_DELAY_UI);
        }
        if(mPongView!=null)
        {
            mPongView.resume();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        //Libero recursos del juego
        mPongView.release();
    }

}//PongActivity
