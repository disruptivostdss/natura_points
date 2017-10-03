package com.fiap.felipevieira.mapsexemplo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;


public class CheckInActivity extends AppCompatActivity {

    private Usuario usuario;
    private ImageView imgCheckin;
    private TextView txtNomeCheckin;
    private Intent ret;
    private Bundle extras;
    private Bitmap bitmap;
    private String uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        imgCheckin = (ImageView) findViewById(R.id.imgCheckin);
        txtNomeCheckin = (TextView) findViewById(R.id.txtNomeCheckin);

        extras = getIntent().getExtras();

        usuario = (Usuario) extras.getSerializable("usuario");

        if(extras.get("uri") != null) {
            uri = extras.get("uri").toString();
            Log.i("NATURA_POINTS", "*CAPTURAR CAMINHO IMAGEM - " + uri);
            usuario.setPontos(usuario.getPontos() + 5);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        exibirFoto();
        super.onPostCreate(savedInstanceState);
        exibirFoto();
    }


    private void exibirFoto() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        bitmap = BitmapFactory.decodeFile(uri, bmOptions);

        imgCheckin.setImageBitmap(bitmap);
    }


    public void confirmCheckin(View v){

        usuario.setPontos(usuario.getPontos() + 5);
        ret = new Intent();
        ret.putExtra("usuario", usuario);
        setResult(RESULT_OK, ret);
        finish();
    }


    @Override
    public void finish() {

        super.finish();
    }

}
