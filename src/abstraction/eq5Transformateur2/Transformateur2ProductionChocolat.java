package abstraction.eq5Transformateur2;

import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.filiere.Banque;
import abstraction.eqXRomu.filiere.Filiere;

/** @author Pierre
 */
public class Transformateur2ProductionChocolat extends Transformateur2VendeurAuxEncheres {
    
    public Transformateur2ProductionChocolat(){
        super();

    }

    public void ProductionChocolat(Feve q, Double p, Double n){
        assert p >= 0.45;
        Double f = p * n;
        if ( f <= this.getStock_feve(q) ){
            Double a = (1 - p) * prix_MP;
            if ( a <= this.getSolde()){
                if (true /* Machine & Employé Dispo */){
                    this.remove_feve(f, q);
                    Filiere.LA_FILIERE.getBanque().payerCout();
                    /* Occupation */
                    // Calcul Quali
                    Double Q = 0.0;
                    if (q == Feve.F_BQ){
                        Q = p + 3 * 0.45;
                    } else if (q == Feve.F_MQ){
                        Q = p + 3 * 0.75;
                    } else if (q == Feve.F_HQ){
                        Q = p + 3;
                    } 
                    if ( Q < 2.58 ){
                        this.add_chocolat(n,Chocolat.C_BQ);
                    } else if ( 2.58 <= Q & Q < 3.575 & p >= 0.60 ){
                        this.add_chocolat(n,Chocolat.C_MQ);
                    } else if ( 3.575 <= Q & p >= 0.80 ){
                        this.add_chocolat(n,Chocolat.C_HQ);
                    }
                }
            }
        }
    }