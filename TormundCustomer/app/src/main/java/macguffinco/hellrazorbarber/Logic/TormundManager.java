package macguffinco.hellrazorbarber.Logic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.UserManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.util.IOUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import macguffinco.hellrazorbarber.Activities.Chat.ChatGroupActivity;
import macguffinco.hellrazorbarber.Activities.Common.BarbersListActivity;
import macguffinco.hellrazorbarber.Activities.Common.BranchesListActivity;
import macguffinco.hellrazorbarber.Activities.Common.ServicesListActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.BillingActivity;
import macguffinco.hellrazorbarber.Activities.Dashboard.CustomerDashBoard;
import macguffinco.hellrazorbarber.Activities.Dashboard.MarketActivity;
import macguffinco.hellrazorbarber.Activities.Dates.AppointmentActivity;
import macguffinco.hellrazorbarber.Activities.Dates.DatesBrunchActivity;
import macguffinco.hellrazorbarber.Activities.Login.LoginActivity;
import macguffinco.hellrazorbarber.MainActivity;
import macguffinco.hellrazorbarber.Market;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Branches.CompanyDC;
import macguffinco.hellrazorbarber.Model.Comercial.CustomerDC;
import macguffinco.hellrazorbarber.Model.Dates.DateDC;
import macguffinco.hellrazorbarber.Model.Employees.EmployeeDC;
import macguffinco.hellrazorbarber.Model.Services.ServiceDC;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;
import macguffinco.hellrazorbarber.R;
import macguffinco.hellrazorbarber.Services.Comercial.Customers.CustomerService;
import macguffinco.hellrazorbarber.Services.Dates.DatesService;
import macguffinco.hellrazorbarber.Services.Users.UserService;

public final class TormundManager {

    private static boolean regreso;
    public static String email = "";
    public static String name ="";
    public static String birthday = "";
    public static String facebookId = "";
    public static CustomerDC GlobalCustomer;
    public static UserDC GlobalUser= new UserDC();
    public static CompanyDC GlobalCompany;
    public static List<DateDC> CustomerDates;
    public static String GlobalServer="dcisneros-001-site2.dtempurl.com";
    public static ArrayList<BranchDC> GlobalBranches;
    public static ArrayList<ServiceDC> GlobalServices;
    public static ArrayList<EmployeeDC> GlobalBarbers;
    public static Calendar FECHA=Calendar.getInstance();
    public static Bitmap GlobalPictureProfile=null;
    public static DateDC GlobalNewAppointment;

    public  static boolean ValidateState(){


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        return isLoggedIn;
    }



    public static HttpURLConnection Connection(JSONObject jsonObject){

        HttpURLConnection conn=null;
        try {
            String urlAdress="http://"+TormundManager.GlobalServer+"/api/branches";
            URL url = new URL(urlAdress);
             conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

          if(jsonObject!=null){
              DataOutputStream os = new DataOutputStream(conn.getOutputStream());
              os.writeBytes(jsonObject.toString());

              os.flush();
              os.close();
          }


        }catch (Exception ex){
            return  null;


        }

        return conn;
    }

    public static UserDC ValidateCreatedUser(UserDC userDC){

        UserDC userFound= UserService.SearchUser(userDC);
        if(userFound!=null){

            return userFound;
        }
        return UserService.NewUser(userDC);
    }

    public static void SaveFacebookProfilePicture(String userID){

        final String user_id=userID;
        Thread tt= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageURL = new URL("https://graph.facebook.com/" + user_id + "/picture?type=large");
                    byte[] bytes = IOUtils.toByteArray(imageURL.openConnection().getInputStream());
                    if(bytes.length>0){
                        GlobalUser.facebook_profile_picture=bytes;
                        GlobalUser.event_key="update";
                    }
                    GlobalUser=UserService.SearchUser(GlobalUser);
                    GlobalPictureProfile = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                }catch (Exception e){
                    String hola=e.getMessage();
                    if(hola.equals("")){


                    }


                }
            }
        });

       tt.start();


    }




    public static void SyncronizeProfileData(NavigationView navigationView ,Context context){
        if (navigationView==null) return;
        //Header NavView
        View header = navigationView.getHeaderView(0);
        if(header!=null){

            ImageView profilePic = (ImageView) header.findViewById(R.id.ProfileViewFace);
            TextView profileName = (TextView) header.findViewById(R.id.ProfileName);

            profileName.setText(TormundManager.name);
                 Picasso.with(context)
                    .load("https://graph.facebook.com/v2.2/" + TormundManager.facebookId + "/picture?height=250&type=normal") //extract as User instance method
                    .transform(new CropCircleTransformation())
                    .resize(250, 250)
                    .into(profilePic);
        }
    }

    public static void EnabledLogOutButton(Menu nav_Menu){

        if (nav_Menu==null) return;

        if(TormundManager.ValidateState()){
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_logIn).setVisible(false);
        }else{
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_logIn).setVisible(true);
        }

    }

    public static void onNavigationItemSelected(Context context,int idItemSelected) {

        switch (idItemSelected) {

            case R.id.nav_dates: {
                goDatesScreen(context);
                break;
            }
            case R.id.nav_logIn: {
                goLoginScreen(context);
                break;
            }
            case R.id.nav_barbers: {
                goBarbersScreen(context);
                break;
            }
            case R.id.nav_branches: {
                goBranchesScreen(context);
                break;
            }
            case R.id.nav_services: {
                goServicesScreen(context);
                break;
            }
            case R.id.nav_creditCard: {
                goBillingScreen(context);
                break;
            }
            case R.id.nav_logout: {
                LoginManager.getInstance().logOut();
                goMainScreen(context);
                break;
            }
            default:{goMainScreen(context);break;}
        }
    }

    public static void goLoginScreen(Context context){
        Intent intent= new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goDatesScreen(Context context){
        Intent intent= new Intent(context, AppointmentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goBarbersScreen(Context context){
        Intent intent= new Intent(context, BarbersListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goBranchesScreen(Context context){
        Intent intent= new Intent(context, BranchesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goBillingScreen(Context context){
        Intent intent= new Intent(context,MarketActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
    public static void goGroupChat(Context context){
        Intent intent= new Intent(context, ChatGroupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goDashboardScreen(Context context){
        Intent intent= new Intent(context, CustomerDashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void goMainScreen(Context context){
        Intent intent= new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void goServicesScreen(Context context){
        Intent intent= new Intent(context, ServicesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }






    public static Date JsonStringToDate(String strDate){
        java.util.Date result;
        try{
            String target = strDate;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            result =  df.parse(target);
            return result;

        }catch (Exception e){

        }

        return new Date();
    }

    public static Date JsonStringToDate2(String strDate){
        java.util.Date result;
        try{
            String target = strDate;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result =  df.parse(target);
            return result;

        }catch (Exception e){

        }

        return new Date();
    }

    public static String JsonDateToHours(Date date){
        String result;
        try{
            DateFormat format = new SimpleDateFormat( "HH:mm");
            result = format.format(date);

            return result;

        }catch (Exception e){

        }

        return "";
    }


    public static String FormatDate(Date date){
       String result="";
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             result =  df.format(date);
            return result;

        }catch (Exception e){

        }

        return result;
    }

    public static String FormatDateTime(Date date){
        String result="";
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            result =  df.format(date);
            return result;

        }catch (Exception e){

        }

        return result;
    }

    public static String FormatHour(Date date){
        String result="";
        try{
            DateFormat df = new SimpleDateFormat("hh:mm a");
            result =  df.format(date);
            return result;

        }catch (Exception e){

        }

        return result;
    }


    private static void LogginCleaner(){

        email="";
        name="";
        birthday="";
    }

    public static void UpdateUserData(){

        try {
            AccessToken accessToken= AccessToken.getCurrentAccessToken();
            String id= accessToken.getUserId();
            if(!id.isEmpty()){
                facebookId=id;
            }
            MenuProfileActions();
            if(!facebookId.isEmpty()){
                GlobalCustomer =  CustomerService.SearchCustomerByFaceId(facebookId);
            }
            if(GlobalCustomer!=null){
                if(CustomerDates==null  || CustomerDates.isEmpty()){

                    DateDC date=new DateDC();
                    date.Customer_id=GlobalCustomer.id;
                    date.status_list="1,5,6,7,8";
                    CustomerDates=new ArrayList<DateDC>();
                    CustomerDates.addAll(DatesService.SearchDate(date));
                }else if(!CustomerDates.isEmpty()){
                    CustomerDates.clear();
                    DateDC date=new DateDC();
                    date.Customer_id=GlobalCustomer.id;

                    CustomerDates.addAll(DatesService.SearchDate(date));
                }
            }
        }catch (Exception e){
            LogginCleaner();
        }


    }
    public static boolean MenuProfileActions( ){

        try {

               if(!facebookId.isEmpty()) {


                    GraphRequest mGraphRequest = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject me, GraphResponse response) {
                                    if (response.getError() != null) {
                                        // handle error
                                    } else {
                                        email = me.optString("email");
                                        name = me.optString("name");
                                        birthday = me.optString("birthday");
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    mGraphRequest.setParameters(parameters);
                    mGraphRequest.executeAsync().get();

                }
            } catch (Exception ex){
                    LogginCleaner();
                     return false;

        }
        return  true;
    }



    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }


}
