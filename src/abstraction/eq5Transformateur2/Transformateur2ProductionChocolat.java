package abstraction.eq5Transformateur2;

import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.filiere.Filiere;

/** @author Pierre
 */
public class Transformateur2ProductionChocolat extends Transformateur2FabriquantChocolatDeMarque {
    
    public Transformateur2ProductionChocolat(){
        super();

    }

    @Override
    public void next() {
        super.next();

        // 1. PAIEMENT DES EMPLOYÉS (Coût fixe) 
        double coutSalaires = 9000 * 625.0;
        
        // On demande à la banque de payer nos employés
        Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Salaires des employés", coutSalaires);

        // 2. OPTIMISATION DE LA PRODUCTION (Flux tendu)
        double stockCibleHQ = 2500.0;
        double stockCibleMQ = 4000.0;
        double stockCibleBQ = 3000.0;

        // On recrée nos références exactes pour lire les stocks
        ChocolatDeMarque chocoHQ = new ChocolatDeMarque(Chocolat.C_HQ, "Ferrara Rocher", 100);
        ChocolatDeMarque chocoMQ = new ChocolatDeMarque(Chocolat.C_MQ, "Ferrara Rocher", 100);
        ChocolatDeMarque chocoBQ = new ChocolatDeMarque(Chocolat.C_BQ, "Ferrara Rocher", 45); 

        // On calcule ce qu'il nous MANQUE pour atteindre l'objectif
        double aProduireHQ = stockCibleHQ - this.getStock_chocolatDeMarque(chocoHQ);
        double aProduireMQ = stockCibleMQ - this.getStock_chocolatDeMarque(chocoMQ);
        double aProduireBQ = stockCibleBQ - this.getStock_chocolatDeMarque(chocoBQ);

        // Capacité de production de notre usine 
        double capaciteRestante = 9000 * 8.4; 

        // 3. On lance la production par ordre de priorité (le HQ rapporte le plus !)        
        if (aProduireHQ > 0 && capaciteRestante > 0) {
            double prodHQ = Math.min(aProduireHQ, capaciteRestante);
            this.ProductionFerraraHQ(prodHQ);
            capaciteRestante -= prodHQ; // On met à jour la capacité restante
        }
        
        if (aProduireMQ > 0 && capaciteRestante > 0) {
            double prodMQ = Math.min(aProduireMQ, capaciteRestante);
            this.ProductionFerraraMQ(prodMQ);
            capaciteRestante -= prodMQ;
        }

        if (aProduireBQ > 0 && capaciteRestante > 0) {
            double prodBQ = Math.min(aProduireBQ, capaciteRestante);
            this.ProductionFerraraBQ(prodBQ);
            capaciteRestante -= prodBQ;
        }
    }

    public void ProductionChocolat(Chocolat c,Double n){
        if(c==Chocolat.C_HQ){
            ProductionFerraraHQ(n);
        }
        else if(c==Chocolat.C_MQ){
            ProductionFerraraMQ(n);
        }
        else if(c==Chocolat.C_BQ){
            ProductionFerraraBQ(n);
        }
    }


    /**@author Maxence 
     * Notre Chocolat HQ a 100% de cacao, dont 49% de fèves HQ et 51% de fèves MQ
    */
    public void ProductionFerraraHQ(Double quantite){
        Double quantiteFeveHQ=quantite*0.49;
        Double quantiteFeveMQ=quantite*0.51;
        if((quantiteFeveHQ<=this.getStock_feve(Feve.F_HQ)) && (quantiteFeveMQ<=this.getStock_feve(Feve.F_MQ))){
            this.remove_feve(quantiteFeveHQ,Feve.F_HQ);
            this.remove_feve(quantiteFeveMQ,Feve.F_MQ);
            ChocolatDeMarque chocoHQ = new ChocolatDeMarque(Chocolat.C_HQ, "Ferrara Rocher", 100);
            this.add_chocolatDeMarque(chocoHQ, quantite);
        }
    }
/** @author Maxence
* notre chocolat MQ a 100% de cacao, dont 26% de fèves MQ et 74% de fèves BQ
 */
    public void ProductionFerraraMQ(Double quantite){
        Double quantiteFeveMQ=quantite*0.26;
        Double quantiteFeveBQ=quantite*0.74;
        if((quantiteFeveMQ<=this.getStock_feve(Feve.F_MQ)) && (quantiteFeveBQ<=this.getStock_feve(Feve.F_BQ))){
            this.remove_feve(quantiteFeveMQ,Feve.F_MQ);
            this.remove_feve(quantiteFeveBQ,Feve.F_BQ);
            ChocolatDeMarque chocoMQ = new ChocolatDeMarque(Chocolat.C_MQ, "Ferrara Rocher", 100);
            this.add_chocolatDeMarque(chocoMQ, quantite);       
        }
    }
/** @author Maxence
* notre chocolat BQ a 45% de cacao
 */
    public void ProductionFerraraBQ(Double quantite){
        Double quantiteFeveBQ=quantite*0.45;
        Double quantiteMP=quantite*0.65;
        if(quantiteFeveBQ<=this.getStock_feve(Feve.F_BQ)){
            ChocolatDeMarque chocoBQ = new ChocolatDeMarque(Chocolat.C_BQ, "Ferrara Rocher", 100);
            this.add_chocolatDeMarque(chocoBQ, quantite);
        }
        // J'ai ajouté une petite sécurité ici pour éviter un crash si prix_MP n'est pas initialisé
        if (prix_MP != null) {
            Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Achat de MP pour production de chocolat FerraraBQ", quantiteMP*prix_MP);
        }
    }
}