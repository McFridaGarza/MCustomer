package macguffinco.hellrazorbarber.Services.Comercial.Customers;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Comercial.CustomerDC;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;
import macguffinco.hellrazorbarber.Services.Users.UserService;

public class CustomerService {

public static CustomerDC SearchCustomerByFaceId(String face_user_id) {


    if (face_user_id == null || face_user_id == "") return null;
    UserDC user=new UserDC();
    user.TokenFacebook=face_user_id;
        user= UserService.SearchUser(user);
        if(user==null){
            UserDC user2=new UserDC();
            user2.TokenFacebook=face_user_id;
            user=TormundManager.ValidateCreatedUser(user2);

        }
    if(user!=null && user.Id>0){
        CustomerDC customer= new CustomerDC();
        customer.user_id=user.Id;
        CustomerDC cstm= CustomerService.SearchCustomer(customer);
return cstm;
    }
return null;
}

    public static CustomerDC SearchCustomer(CustomerDC customer) {



        final UserDC userOut=new UserDC();


      /* Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
*/
        try {
            String urlAdress="http://"+TormundManager.GlobalServer+"/api/customers";
            URL url = new URL(urlAdress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("user_id", customer.user_id);

            jsonParam.put("event_key","search");



            Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            String responseMessage = conn.getResponseMessage();

            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = TormundManager.readStream(conn.getInputStream());

                JSONArray array= new JSONArray(responseString);
                if(array.length()>0){
                    JSONObject json =array.getJSONObject(0);

                    if(!json.isNull("id")){
                        CustomerDC customerDC= new CustomerDC();
                        customerDC.id=json.getInt("id");
                        customerDC.user_id=json.getInt("user_id");
                        customerDC.customerName=json.getString("name");
                        customerDC.principalPhone=json.getString("principalphone").replace("null","");
                        customerDC.birthDate= TormundManager.JsonStringToDate2(json.getString("birthDate"));
                        customerDC.creationDate= TormundManager.JsonStringToDate2(json.getString("creationDate"));
                        if(!json.isNull("branch")){
                            JSONObject jsonBranch=json.getJSONObject("branch");
                            BranchDC branch=new BranchDC();
                            branch.Id=jsonBranch.getInt("id");
                            branch.BranchName=jsonBranch.getString("branchName");
                            customerDC.branch=branch;
                        }
                        if(!json.isNull("ultimaCita")){
                            customerDC.ultimacita=json.getString("ultimaCita");
                        }
                        if(!json.isNull("cantidadCitas")){
                            customerDC.cantidadCitas=json.getInt("cantidadCitas");
                        }
                        if(!json.isNull("repo_files")){
                            customerDC.repo_files=new ArrayList<>();
                            JSONArray files=json.getJSONArray("repo_files");
                            for(int k=0;k<files.length() ;k++){
                                JSONObject jsonObjPicture=files.getJSONObject(k);
                                VaultFileDC vault= new VaultFileDC();
                                vault.Id=jsonObjPicture.getInt("id");
                                //vault.Description=jsonObjPicture.getString("description");
                                if(!jsonObjPicture.isNull("url")){
                                    vault.Url=jsonObjPicture.getString("url");
                                }

                                customerDC.repo_files.add(vault);
                            }
                        }

                        return  customerDC;

                    }

                }else {
                    return null;
                }



            }else{
                Log.v("CatalogClient", "Response code:"+ responseCode);
                Log.v("CatalogClient", "Response message:"+ responseMessage);
            }
            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
/*
        }});

        thread.start();*/

        return  null;

    }
}
