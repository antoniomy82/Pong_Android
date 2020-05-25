package com.example.antonio.pong;

/**
 * Created by antonio on 27/06/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

public class CamaraBox {
    public static final int REQUEST_CAMERA = 100;

    public static void setFoto(Activity activity, final Context mContext) {
        final int REQUEST_CAMERA = 100;
        final Dialog dialog = new Dialog(mContext);

        //Llamamos a activity_camera como cuadro de dialogo
        dialog.setContentView(R.layout.activity_camara);
        dialog.setCancelable(true);
        LinearLayout camara = (LinearLayout)dialog.findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            //Capturamos la imagen de la cámara al hacer click
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CAMERA);
                dialog.dismiss();

            }
        });

        //Botón cancelar con su acción onClick
        LinearLayout cancelar = (LinearLayout)dialog.findViewById(R.id.cancelarcamera);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
