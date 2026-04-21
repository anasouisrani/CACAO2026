package abstraction.eq3Producteur3;
import abstraction.eq3Producteur3.Agriculteurs3;
import abstraction.eqXRomu.filiere.Filiere;

/** @author Guillaume Leroy*/
public class GestionCouts3 {

    private double coutHectare;
    private double coutStockageTonne;
    private double coutLabelHappyWorker;

    public GestionCouts3(){
        this.coutHectare= 7.8;
        this.coutLabelHappyWorker=1000;
        this.coutStockageTonne=7.5;
    }

    public void nextCout(Producteur3Acteur acteur) {
        // Coût plantation
        double coutPlantation = acteur.plantationeq3.getNbHectareTotal() * this.coutHectare;
        acteur.journal_cout_periode.ajouter("Période " + Filiere.LA_FILIERE.getEtape() + " : coût plantation = " + coutPlantation);
        Filiere.LA_FILIERE.getBanque().payerCout(acteur, acteur.cryptogramme, "Coût des plantations", coutPlantation);

        // Coût stockage (7.5 par unité)
        double coutStock = acteur.stock.getCoutStockage(this.coutStockageTonne);
        acteur.journal_cout_periode.ajouter("Période " + Filiere.LA_FILIERE.getEtape() + " : coût stockage = " + coutStock);
        Filiere.LA_FILIERE.getBanque().payerCout(acteur, acteur.cryptogramme, "Coût du stockage", coutStock);

        // Coût Main d'oeuvre
        double coutMO = acteur.agriculteurs.getCoutMainOeuvreTotal();
        acteur.journal_cout_periode.ajouter("Période " + Filiere.LA_FILIERE.getEtape() + " : coût main d'oeuvre = " + coutMO);
        Filiere.LA_FILIERE.getBanque().payerCout(acteur, acteur.cryptogramme, "Coût de la main d'oeuvre", coutMO);

        if (acteur.agriculteurs.estEthique()){
            acteur.journal_cout_periode.ajouter("Période " + Filiere.LA_FILIERE.getEtape() + " : label Happyworker = " + this.coutLabelHappyWorker);
            Filiere.LA_FILIERE.getBanque().payerCout(acteur, acteur.cryptogramme, "label Happyworker", this.coutLabelHappyWorker);
        }
    }
    
    public double getCoutTot(Producteur3Acteur acteur){
        return acteur.plantationeq3.getNbHectareTotal() * this.coutHectare + acteur.stock.getCoutStockage(this.coutStockageTonne) + acteur.agriculteurs.getCoutMainOeuvreTotal();
    }
}
