package macguffinco.hellrazorbarber.Activities.Dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.UserManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import macguffinco.hellrazorbarber.Activities.Common.BarbersListActivity;
import macguffinco.hellrazorbarber.Activities.Common.BranchesListActivity;
import macguffinco.hellrazorbarber.Activities.Dates.DatesBrunchActivity;
import macguffinco.hellrazorbarber.Activities.Login.LoginActivity;
import macguffinco.hellrazorbarber.Logic.CropCircleTransformation;
import macguffinco.hellrazorbarber.Logic.DatesManager;
import macguffinco.hellrazorbarber.Logic.TormundDate;
import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.MainActivity;
import macguffinco.hellrazorbarber.Model.Comercial.CustomerDC;
import macguffinco.hellrazorbarber.Model.Dates.DateDC;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.R;
import macguffinco.hellrazorbarber.Services.Comercial.Customers.CustomerService;
import macguffinco.hellrazorbarber.Services.Users.UserService;

public class CustomerDashBoard extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private static ImageView profilePic=null;
    private static TextView profileName=null;
    private static TextView profileMail=null;
    private static  NavigationView navigationView=null;
    private static Menu nav_Menu=null;
    private static String facebookToken="";
    ViewPager   viewPager;
    PagerTabStrip pagerTabStrip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);
        //facebookToken=AccessToken.getCurrentAccessToken().getUserId();

     Adapter adapter= new Adapter(getSupportFragmentManager(),this);
     viewPager=(ViewPager)findViewById(R.id.pager);
     viewPager.setAdapter(adapter);
     pagerTabStrip=(PagerTabStrip)findViewById(R.id.titleTab);
     pagerTabStrip.setTabIndicatorColor(Color.parseColor("#D1AC19"));

        if(TormundManager.GlobalCustomer==null){
            TormundManager.UpdateUserData();
        }
        InitializateActivitiesControls();

        //ShowingAppoinmentCardView();
        //ShowingUserProfileCardView();
    }

    private void ShowingAppoinmentCardView(){

        if(TormundManager.CustomerDates==null)return;

        final DateDC date =TormundManager.CustomerDates!=null && TormundManager.CustomerDates.isEmpty()?null:TormundManager.CustomerDates.get(0);
        CardView card= (CardView)findViewById(R.id.cardAppointment);
        TextView cardAppointmentDateAppointment= (TextView)findViewById(R.id.cardAppointmentDateAppointment);
        TextView cardAppointmentServiceAppointment= (TextView)findViewById(R.id.cardAppointmentServiceAppointment);
        TextView cardAppointmentHoursAppointment= (TextView)findViewById(R.id.cardAppointmentHoursAppointment);
        TextView cardAppointmentBarberAppointment= (TextView)findViewById(R.id.cardAppointmentBarberAppointment);

        if(card!=null){
            //card.setVisibility(View.INVISIBLE);



            if(date!=null && date.Status>0 && date.Status!=4){

                if(card!=null){
                    card.setVisibility(View.VISIBLE);


                    cardAppointmentDateAppointment.setText(TormundManager.FormatDate(TormundManager.JsonStringToDate2(date.AppointmentDate.toString())));
                    if(date.Service!=null){
                        cardAppointmentServiceAppointment.setText(date.Service.Name);
                    }

                    cardAppointmentHoursAppointment.setText(TormundManager.FormatHour(TormundManager.JsonStringToDate2(date.AppointmentDate.toString())));

                    if(date.Employee!=null){
                        cardAppointmentBarberAppointment.setText(date.Employee.name);
                    }



                }
            }
        }



    }
    private void ShowingUserProfileCardView(){
        //if(TormundManager.CustomerDates==null)return;

        CardView card= (CardView)findViewById(R.id.cardUserProfile);
        TextView cardUserProfileName= (TextView)findViewById(R.id.cardUserProfileName);
        TextView cardNews= (TextView)findViewById(R.id.cardNews);
        TextView dashboard_date= (TextView) findViewById(R.id.dashboard_date);
        TextView dashboard_day_of_week= (TextView) findViewById(R.id.dashboard_day_of_week);
        TextView dashboard_hora= (TextView) findViewById(R.id.dashboard_hora);
        //TextView cardUserProfilePhone= (TextView)findViewById(R.id.cardUserProfilePhone);
        //TextView cardUserProfileEmail= (TextView)findViewById(R.id.cardUserProfileEmail);

        if(card!=null){
            card.setVisibility(View.INVISIBLE);



            if(TormundManager.GlobalCustomer!=null ){

                if(card!=null){
                    card.setVisibility(View.VISIBLE);

                    cardUserProfileName.setText(TormundManager.name);

                    //cardUserProfileBirthday.setText(TormundManager.birthday);
                    //cardUserProfilePhone.setText(TormundManager.GlobalCustomer.principalPhone==null?"":TormundManager.GlobalCustomer.principalPhone);
                    //cardUserProfileEmail.setText(TormundManager.email);



                       int daysToBirthDay=0;
                        String hourDashboard="";
                        String dateDashboard="";
                        String dayOfWeekDashboard="";
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
                            String[] arrayDate=TormundManager.birthday.split("/");
                            String strBirthDay= arrayDate[0]+"-"+arrayDate[1]+"-"+ TormundDate.Year();
                            Date date = formatter.parse(strBirthDay);
                            Calendar birthDay=Calendar.getInstance();
                            birthDay.setTime(date);

                             daysToBirthDay=(((int)birthDay.getTime().getTime()-((int)TormundDate.Today().getTime()))/86400000)+1;

                            hourDashboard=String.valueOf(TormundDate.hour_12());
                            dayOfWeekDashboard=TormundDate.dayName_of_week();
                            dateDashboard=TormundDate.TodayDDMMMYYY();


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    cardNews.setText("Faltan "+daysToBirthDay+" para tu cumpleaños.");

                    dashboard_hora.setText(hourDashboard);
                    dashboard_day_of_week.setText(dayOfWeekDashboard);
                    dashboard_date.setText(dateDashboard);


                }
            }
        }

       /* Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

               final DateDC date =TormundManager.CustomerDates.isEmpty()?null:TormundManager.CustomerDates.get(0);

                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });



                }catch(Exception e){
                    String hola= e.getMessage();
                }
            }
        });

        thread.start();

*/


    }

    private void InitializateActivitiesControls(){


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    public void onTakePicture(View view)
    {
       Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       startActivityForResult(intent,0);
    }

    public void onCreateDate(View view)
    {
        TormundManager.goDatesScreen(view.getContext());
    }

    public void onOpenGroupChat(View view)
    {
        TormundManager.goGroupChat(view.getContext());
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


    @Override
    public void onClick(View v) {

    }
}
