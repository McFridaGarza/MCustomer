package macguffinco.hellrazorbarber.Services.Users;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import macguffinco.hellrazorbarber.Logic.TormundManager;
import macguffinco.hellrazorbarber.Model.Branches.AddressDC;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Branches.CompanyDC;
import macguffinco.hellrazorbarber.Model.Employees.EmployeeDC;
import macguffinco.hellrazorbarber.Model.Services.ServiceDC;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;

public class EmployeesService {

    public static ArrayList<EmployeeDC> SearchEmployees(EmployeeDC  employeeDC)     {

        if(employeeDC==null) return null;
        ArrayList<EmployeeDC> barbersList=new ArrayList<EmployeeDC>();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", employeeDC.id);
            jsonParam.put("name", employeeDC.name);
            jsonParam.put("event_key","search");


            JSONObject jsonParamCompany = new JSONObject();
            jsonParamCompany.put("id", employeeDC.CompanyDC.Id);
            jsonParam.put("companydc",jsonParamCompany);

            String urlAdress="http://"+ TormundManager.GlobalServer+"/api/employees";
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

                        if(!json.isNull("id")){
                            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                            EmployeeDC barber= new EmployeeDC();
                            barber.id=json.getInt("id");
                            barber.name=json.getString("name");
                            barber.creation_date= TormundManager.JsonStringToDate2(json.getString("creation_date"));



                            if(!json.isNull("companyDC")){
                                JSONObject jsonCompany=json.getJSONObject("companyDC");
                                CompanyDC companyDC=new CompanyDC();
                                companyDC.Id=jsonCompany.getInt("id");
                                companyDC.Name=jsonCompany.getString("Name");
                                barber.CompanyDC=companyDC;
                            }

                            if(!json.isNull("branch")){
                                JSONObject jsonBranch=json.getJSONObject("branch");
                                BranchDC branch=new BranchDC();
                                branch.Id=jsonBranch.getInt("id");
                                barber.branch=branch;
                            }

                            if(!json.isNull("address_id")){
                                JSONObject jsonAddress=json.getJSONObject("address_id");
                                AddressDC addressDC=new AddressDC();
                                addressDC.Street=jsonAddress.getString("street");
                                addressDC.Address_Id=jsonAddress.getInt("address_Id");
                                barber.Address_id=addressDC;
                            }

                            if(!json.isNull("vault_file_id")){
                                JSONObject jsonVault=json.getJSONObject("vault_file_id");
                                VaultFileDC vault=new VaultFileDC();
                                vault.Id=jsonVault.getInt("id");
                                vault.Url=jsonVault.getString("url");
                                barber.vault_file_id=vault;
                            }



                            barbersList.add(barber);

                        }
                    }


                }else {
                    return new ArrayList<EmployeeDC>();
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
            return new ArrayList<EmployeeDC>();
        }


        return  barbersList;

    }

}
