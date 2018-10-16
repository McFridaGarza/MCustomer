package macguffinco.hellrazorbarber;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionActivity extends AppCompatActivity {

    private EditText edititulo;
    private EditText editmensaje;
    private Button btnNotif1;
    private Button btnNotif2;
    private NotificationHelper mNotificationHelper;
    TextView close_detail;
    private TextView mtittle, mdesciption, mprice;
    private ImageView mimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

      edititulo= findViewById(R.id.edititulo);
      editmensaje= findViewById(R.id.editMensaje);
      btnNotif1=findViewById(R.id.btnotificacion1);
      btnNotif2= findViewById(R.id.btnotificacion2);

      mNotificationHelper = new NotificationHelper(this);

      btnNotif1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            sendOnChannel1(edititulo.getText().toString(),editmensaje.getText().toString());
          }

          private void sendOnChannel1(String tittle, String message) {
              NotificationCompat.Builder nb= mNotificationHelper.getChannel1Notification(tittle,message);
              mNotificationHelper.getmManager().notify(1,nb.build());
          }
      });
      btnNotif2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              sendOnChannel2(edititulo.getText().toString(),editmensaje.getText().toString());

          }

          private void sendOnChannel2(String tittle, String message) {
              NotificationCompat.Builder nb= mNotificationHelper.getChanne2lNotification(tittle,message);
              mNotificationHelper.getmManager().notify(2,nb.build());
          }
      });




        try {

            close_detail = findViewById(R.id.close_detail);

            mtittle = (TextView) findViewById(R.id.titulomdescipcion);
            mdesciption = (TextView) findViewById(R.id.descriptionmarket);
            mprice = (TextView) findViewById(R.id.marketprice);
            mimg = (ImageView) findViewById(R.id.imgmarketdescription);


            Intent intent = getIntent();
            String Tittle = intent.getExtras().getString("Titulo");
            String Description = intent.getExtras().getString("Descripcion");
            String Price = intent.getExtras().getString("Precio");
            int image = intent.getExtras().getInt("Imagen");

            mtittle.setText(Tittle);
            mprice.setText(Price);
            mdesciption.setText(Description);
            mimg.setImageResource(image);

            close_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        catch ( Exception ex){
            String Hola="";
            if(Hola.equals(" "))
            {

            }
        }

    }













}


