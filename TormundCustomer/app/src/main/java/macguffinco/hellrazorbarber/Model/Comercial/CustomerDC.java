package macguffinco.hellrazorbarber.Model.Comercial;

import java.util.ArrayList;
import java.util.Date;

import macguffinco.hellrazorbarber.Model.Branches.BranchDC;
import macguffinco.hellrazorbarber.Model.VaultFiles.VaultFileDC;

public class CustomerDC {

    public int id;
    public String customerName;
    public BranchDC branch;
    public String branchName;
    public String event_key;
    public String principalPhone;
    public int user_id;
    public Date birthDate;
    public Date creationDate;
    public String ultimacita;
    public int cantidadCitas;
    public ArrayList<VaultFileDC> repo_files;

}
