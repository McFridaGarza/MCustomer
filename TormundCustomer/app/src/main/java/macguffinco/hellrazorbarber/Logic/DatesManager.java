package macguffinco.hellrazorbarber.Logic;

import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.List;

import macguffinco.hellrazorbarber.Model.Comercial.CustomerDC;
import macguffinco.hellrazorbarber.Model.Dates.DateDC;
import macguffinco.hellrazorbarber.Model.UserDC;
import macguffinco.hellrazorbarber.Services.Dates.DatesService;

public final class DatesManager {

    public  static DateDC GetCustomerPendingDate(CustomerDC customer){

        if(customer==null) return null;
        if (customer.id<=0) return null;

        DateDC date=new DateDC();
        date.Customer_id=customer.id;
        date.Status=4;


        if(customer.id>0){
            List<DateDC> listDates= DatesService.SearchDate(date);

            if(!listDates.isEmpty()){

                return listDates.get(0);
            }else
                return DatesService.NewPendingDate(date);
        }
       else{
            return null;
        }


    }

    public  static DateDC CreateNewDate(DateDC dateDC){

        if(dateDC==null) return null;


        return DatesService.NewPendingDate(dateDC);


    }

    public  static DateDC UpdateDate(DateDC dateDC){

        if(dateDC==null) return null;


        return DatesService.UpdateDate(dateDC);


    }

    public  static ArrayList<DateDC> getEnabledDates(DateDC dateDC){

        if(dateDC==null) return null;


        return DatesService.getEnabledDates(dateDC);


    }

}
