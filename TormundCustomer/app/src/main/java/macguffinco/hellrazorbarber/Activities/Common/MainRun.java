package macguffinco.hellrazorbarber.Activities.Common;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.facebook.AccessToken;


import java.net.URI;

import macguffinco.hellrazorbarber.Logic.BranchesManager;
import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.MainActivity;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Branches.CompanyDC;
import macguffinco.hellrazorbarber.Model.Employees.EmployeeDC;
import macguffinco.hellrazorbarber.Model.Services.ServiceDC;
import macguffinco.hellrazorbarber.R;
import macguffinco.hellrazorbarber.Services.Services.ServicesServices;
import macguffinco.hellrazorbarber.Services.Users.EmployeesService;

public class MainRun extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    VideoView videoVG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_run);

        try {
            AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
                @Override
                public void onComplete(AWSStartupResult awsStartupResult) {
                    Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
                }
            }).execute();
        }catch (Exception er){
            String hola= er.getMessage();
            if(hola.isEmpty()){

            }
        }



        new ActivityTask(this).execute("", "", "");

    }
}



 class ActivityTask extends AsyncTask<String, Integer, Long> {

    private Context context;

    public ActivityTask(Context context){
        this.context = context;
    }
    protected Long doInBackground(String... data) {

        CompanyDC company= new CompanyDC();
        company.Id=1;
        company.Name="Hell Razor";
        TormundManager.GlobalCompany=company;

        BranchDC branch=new BranchDC();
        branch.CompanyDC=TormundManager.GlobalCompany;
        TormundManager.GlobalBranches= BranchesManager.SearchBranches(branch);

        ServiceDC service=new ServiceDC();
        service.CompanyDC=TormundManager.GlobalCompany;
        TormundManager.GlobalServices= ServicesServices.SearchServices(service);

        EmployeeDC barber=new EmployeeDC();
        barber.CompanyDC=TormundManager.GlobalCompany;
        TormundManager.GlobalBarbers= EmployeesService.SearchEmployees(barber);

        if(AccessToken.getCurrentAccessToken()!=null && !AccessToken.getCurrentAccessToken().isExpired()){
            TormundManager.UpdateUserData();
        }else{
            try {
                for (int i=0;i<=9000;i++){
                    String hola="";
                    if(hola.isEmpty()){
                        //for (int k=0;k<=900;k++){
                          //  String hola2="";
//                        }
                    }

                }
            }catch (Exception ex){

            }

        }

        return 0L;
    }

    protected void onProgressUpdate(Integer... progress) {
        String hola="";
    }

    protected void onPostExecute(Long result) {

        Intent intent= new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}