package macguffinco.hellrazorbarber.Activities.Login;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.VideoView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import java.util.Arrays;

import macguffinco.hellrazorbarber.Activities.Common.BarbersListActivity;
import macguffinco.hellrazorbarber.Activities.Common.BranchesListActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.BillingActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.CustomerDashBoard;
import macguffinco.hellrazorbarber.Activities.Dates.DatesBrunchActivity;
import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.MainActivity;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.R;

public class LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private static  NavigationView navigationView=null;
    private static Menu nav_Menu=null;
    RequestQueue requestQueue;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    VideoView videoVG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        InitializateActivitiesControls();

    }


    private void InitializateActivitiesControls(){
        requestQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_login);


        videoVG= (VideoView) findViewById(R.id.videoLogin);
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        nav_Menu= navigationView!=null?navigationView.getMenu():null;
        TormundManager.SyncronizeProfileData(navigationView,getApplicationContext());
        TormundManager.EnabledLogOutButton(nav_Menu);


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            UserDC user= new UserDC();
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                user.TokenFacebook=loginResult.getAccessToken().getUserId();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        user=TormundManager.ValidateCreatedUser(user);
                        if(user!=null){
                            TormundManager.GlobalUser=user;
                        }

                        TormundManager.UpdateUserData();
                        if(user.TokenFacebook!=null && user.TokenFacebook!=""){
                            TormundManager.SaveFacebookProfilePicture(user.TokenFacebook);
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

    }




    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            finish();

    }



    private void goDashboardScreen() {

        Intent intent= new Intent(this,CustomerDashBoard.class);
        getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TormundManager.onNavigationItemSelected(this,item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






}
