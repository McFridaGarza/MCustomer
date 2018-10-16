package macguffinco.hellrazorbarber.Services.Dates;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Collector;

import macguffinco.hellrazorbarber.Logic.CropCircleTransformation;
import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.Dates.DateDC;
import macguffinco.hellrazorbarber.Model.Employees.EmployeeDC;
import macguffinco.hellrazorbarber.Model.Services.ServiceDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;
import macguffinco.hellrazorbarber.R;
import macguffinco.hellrazorbarber.Services.Branches.AdapterBranch;

public class AppointmentAdapter extends RecyclerView.Adapter<macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.ViewHolderAppointments> {


    public interface OnItemClickListener {
        void onItemClick(DateDC item);
    }
    private final ArrayList<DateDC> appointmentsList;
    private macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.OnItemClickListener listener;

    public AppointmentAdapter(ArrayList<DateDC> appointments) {
        this.appointmentsList = appointments;
    }


    public AppointmentAdapter(ArrayList<DateDC> appointmentDC, macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.OnItemClickListener listener) {
        this.appointmentsList = appointmentDC;
        this.listener = listener;
    }

    @Override
    public macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.ViewHolderAppointments onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_appointment,parent,false);
        return new macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.ViewHolderAppointments(view);
    }

    @Override
    public void onBindViewHolder(macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.ViewHolderAppointments holder, int position) {
        holder.bind(appointmentsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }

    public static class ViewHolderAppointments extends RecyclerView.ViewHolder {

        TextView BarberName;
        TextView horary;
        ImageView imageBarber;

        public ViewHolderAppointments(View itemView) {
            super(itemView);
            BarberName=(TextView) itemView.findViewById(R.id.BarberName);
            imageBarber= (ImageView) itemView.findViewById(R.id.imageBarber);
            horary=(TextView)itemView.findViewById(R.id.appointmentHorary);
        }

        public void bind(final DateDC appointmentDC, final macguffinco.hellrazorbarber.Services.Dates.AppointmentAdapter.OnItemClickListener listener) {



            if(appointmentDC!=null && appointmentDC.Employee!=null){
                BarberName.setText(appointmentDC.Employee.name);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM");
                String strDate = dateFormat.format(appointmentDC.AppointmentDate);
                DateFormat dateFormat2 = new SimpleDateFormat("hh:mm");
                String strHourInit = dateFormat2.format(appointmentDC.AppointmentDate);
                String strHourFinish = dateFormat2.format(appointmentDC.DueDate);
                String app_horary=strDate+"  "+strHourInit+" a " + strHourFinish;

                horary.setText(app_horary);
                if(appointmentDC.Employee.barber_round_picture_url!=null && !appointmentDC.Employee.barber_round_picture_url.equals("")){

                    Picasso.with(itemView.getContext()).load(appointmentDC.Employee.barber_round_picture_url)
                            .transform(new CropCircleTransformation())
                            .resize(100, 100)
                            .into(imageBarber);
                }
            }



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(appointmentDC   );
                }
            });
        }

    }
}
