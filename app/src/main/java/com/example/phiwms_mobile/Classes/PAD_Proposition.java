package com.example.phiwms_mobile.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessica on 03/04/2018.
 */

public class PAD_Proposition  implements Serializable {

    public int depotID;
    public String patientIPP;
    public List<PAD_Proposition_Ligne> padPropositionLigneList;

    public PAD_Proposition(int depotID, String patientIPP){
        this.depotID = depotID;
        this.patientIPP = patientIPP;
        this.padPropositionLigneList = new ArrayList<>();
    }

    public void setPadPropositionLigneList(PAD_Proposition_Ligne padPropositionLigne){
        padPropositionLigneList.add(padPropositionLigne);
    }
}
