package abstraction.eq8Distributeur1;

import java.util.List;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

public class Approvisionnement {

    private Distributeur1Acteur acteur;

    public Approvisionnement(Distributeur1Acteur acteur) {
        this.acteur = acteur;
    }

    /**
     * Version 1 :
     * On parcourt tous les chocolats de la filière et on essaie d'en acheter 
     * une quantité fixe pour chaque.
     */
    public void lancerApprovisionnementSimplifie() {
        // 1. Récupérer tous les chocolats existants dans le jeu
        List<ChocolatDeMarque> tousLesChocolats = Filiere.LA_FILIERE.getChocolatsProduits();

        // 2. Quantité arbitraire à acheter pour chaque produit (ex: 10 tonnes)
        double quantiteAchat = 10.0; 

        for (ChocolatDeMarque cdm : tousLesChocolats) {
            // On appelle la méthode de l'acteur qui devra gérer 
            // soit les contrats cadres, soit les enchères.
            this.acheterProduit(cdm, quantiteAchat);
        }
    }

    private void acheterProduit(ChocolatDeMarque cdm, double quantite) {

        /**
         * Ici, on devrait implémenter la logique d'achat :
         */

        System.out.println("Équipe 8 cherche à acheter : " + quantite + " tonnes de " + cdm.getNom());
    }
}
