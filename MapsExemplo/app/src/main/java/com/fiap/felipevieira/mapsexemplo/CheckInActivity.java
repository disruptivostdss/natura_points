package com.fiap.felipevieira.mapsexemplo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class CheckInActivity extends AppCompatActivity {

    private Usuario usuario;
    private ImageView imgCheckin;
    private Intent ret;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        imgCheckin = (ImageView) findViewById(R.id.imgCheckin);
        Bundle extras = getIntent().getExtras();

         usuario = (Usuario) extras.getSerializable("usuario");

        if(extras.get("uri") != null){
            String uri = extras.get("uri").toString();
            Log.i("NATURA_POINTS", "*CAPTURAR CAMINHO IMAGEM - " + uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(uri, bmOptions);

            imgCheckin.setImageBitmap(bitmap);
            usuario.setPontos(usuario.getPontos() + 5);
        }

    }


    public void confirmCheckin(View v){

        usuario.setPontos(usuario.getPontos() + 5);
        ret = new Intent();
        ret.putExtra("usuario", usuario);
        setResult(RESULT_OK, ret);
        finish();
    }

    public void share(View view) {

        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");

        i.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap));
        try {
            startActivity(Intent.createChooser(i, "My Profile ..."));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void finish() {

        super.finish();
    }
}