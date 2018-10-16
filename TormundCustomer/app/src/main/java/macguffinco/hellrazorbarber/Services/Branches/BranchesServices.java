package macguffinco.hellrazorbarber.Services.Branches;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.Model.Branches.AddressDC;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Dates.DateDC;
import macguffinco.hellrazorbarber.Model.Employees.EmployeeDC;
import macguffinco.hellrazorbarber.Model.Services.ServiceDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;

public final class BranchesServices {

    public static ArrayList<BranchDC> SearchBranches(BranchDC branchDC)     {

        if(branchDC==null) return null;
        ArrayList<BranchDC> branchesList=new ArrayList<BranchDC>();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", branchDC.Id);
            jsonParam.put("branchname", branchDC.BranchName);
            jsonParam.put("event_key","search");


            JSONObject jsonParamCompany = new JSONObject();
            jsonParamCompany.put("id", branchDC.CompanyDC.Id);
            jsonParam.put("companydc",jsonParamCompany);

            String urlAdress="http://"+TormundManager.GlobalServer+"/api/branches";
            URL url = new URL(urlAdress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);


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
                    for (int i=0 ;i<array.length();i++) {
                        JSONObject json =array.getJSONObject(i);

                        if(json.getString("id")!="0"){
                            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                            BranchDC branch= new BranchDC();
                            branch.Id=json.getInt("id");
                            branch.Email=json.getString("email");
                            branch.BranchName= json.getString("branchName");
                            branch.foto1=2131165326;
                            branch.Phone= json.getString("principalPhone");
                            branch.Horary=json.getString("horary");

                            branch.CreationDate= TormundManager.JsonStringToDate2(json.getString("creationDate"));

                            if(json.getJSONObject("address_Id")!=null){
                                JSONObject jsonService=json.getJSONObject("address_Id");
                                AddressDC address=new  AddressDC(jsonService.getInt("address_Id"),jsonService.getString("street"),jsonService.getString("number"),jsonService.getInt("addressType"),jsonService.getBoolean("isEnabled"),jsonService.getString("googleLocation"));

                                branch.Address_Id=address;

                            }






                            branchesList.add(branch);

                        }
                    }


                }else {
                    return new ArrayList<BranchDC>();
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
            return new ArrayList<BranchDC>();
        }


        return  branchesList;

    }

}
