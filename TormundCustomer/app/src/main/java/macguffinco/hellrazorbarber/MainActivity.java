package macguffinco.hellrazorbarber;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import macguffinco.hellrazorbarber.Activities.Common.BarbersListActivity;
import macguffinco.hellrazorbarber.Activities.Common.BranchesListActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.BillingActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.CustomerDashBoard;
import macguffinco.hellrazorbarber.Activities.Dates.DatesBrunchActivity;
import macguffinco.hellrazorbarber.Activities.Login.LoginActivity;
import macguffinco.hellrazorbarber.Logic.CropCircleTransformation;
import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.Model.Branches.CompanyDC;
import macguffinco.hellrazorbarber.Model.UserDC;

import static macguffinco.hellrazorbarber.Logic.TormundManager.GlobalCompany;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    VideoView videoVG;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        videoVG= (VideoView) findViewById(R.id.video);
        Uri uri=Uri.parse("android.resource://"
                +getPackageName()
                + "/"
                + R.raw.facebook);
        videoVG.setVideoURI(uri);
        videoVG.start();

        videoVG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer=mp;
                mMediaPlayer.setLooping(true);
                if(mCurrentVideoPosition!=0){
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                TormundManager.GlobalUser.TokenFacebook=loginResult.getAccessToken().getUserId();
                goDashboardScreen();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        TormundManager.GlobalUser=TormundManager.ValidateCreatedUser(TormundManager.GlobalUser);


                        TormundManager.UpdateUserData();
                        if(TormundManager.GlobalUser.TokenFacebook!=null && TormundManager.GlobalUser.TokenFacebook!=""){
                            TormundManager.SaveFacebookProfilePicture(TormundManager.GlobalUser.TokenFacebook);
                        }
                        goDashboardScreen();

                    }
                });
                thread.start();

            }

            @Override
            public void onCancel() {
                // App code

                String hola= "";
                if(hola.equals("")){
                    String candcion="";


                }
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                String error="";
                error=exception.toString();
            }


        });

            new MainActivityTask(this,this).execute("", "", "");



    }

    public void onClickFakeButton(View view){
            loginButton.performClick();

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void goLoginScreen(){
        Intent intent= new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goDatesScreen(){
        Intent intent= new Intent(this, DatesBrunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    /* private void goDatesScreen(){
        Intent intent= new Intent(this, DatesBrunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }*/

    private void goBarbersScreen(){
        Intent intent= new Intent(this, BarbersListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goBranchesScreen(){
        Intent intent= new Intent(this, BranchesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goBillingScreen(){
        Intent intent= new Intent(this, BillingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void goGroupChat(){
        Intent intent= new Intent(this, BillingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goDashboardScreen(){
        Intent intent= new Intent(this, CustomerDashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goMainScreen(){
        Intent intent= new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        return;
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_logIn){
          loginButton.performClick();
        }else{
            TormundManager.onNavigationItemSelected(this,item.getItemId());
        }





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {


    }

    public void onClickBtn(View view)
    {
        int id = view.getId();


       /* if (id == R.id.buttonLogIn) {
            goLoginScreen();

        }else{
            goDatesScreen();
        }*/
    }

    @Override
    public void onRestart(){
        super.onRestart();
       goMainScreen();

    }
}


