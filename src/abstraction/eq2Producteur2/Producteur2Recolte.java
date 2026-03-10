package abstraction.eq2Producteur2;

/** @author Simon*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;

public class Producteur2Recolte extends Producteur2Acteur {

    protected List<Plantation> plantations;
    protected HashMap<Feve, Double> feve_recolte;
    protected HashMap<Feve, Double> cout_recolte;
    private Journal Journalterrains;
    private Journal JournalRecolte;

    public Producteur2Recolte() {
        super();
        this.plantations = new ArrayList<>();
        this.feve_recolte = new HashMap<Feve, Double>();
        this.cout_recolte = new HashMap<Feve, Double>();
        this.JournalRecolte = new Journal("Journal Recolte Eq2", this);
        this.Journalterrains = new Journal("Journal Terrains Eq2", this);

        for (Feve f : Feve.values()) {
            this.feve_recolte.put(f, 0.0);
            this.cout_recolte.put(f, 0.0);
        }
    }

    public void addPlantation(Plantation p) {
        plantations.add(p);
    }

    @Override
    public void initialiser() {
        super.initialiser();

        int ageDepart = 120; 

        addPlantation(new Plantation(Feve.F_BQ, 300000, ageDepart));

        addPlantation(new Plantation(Feve.F_MQ, 500000, ageDepart));

        addPlantation(new Plantation(Feve.F_HQ_E, 200000, ageDepart));

    }

}