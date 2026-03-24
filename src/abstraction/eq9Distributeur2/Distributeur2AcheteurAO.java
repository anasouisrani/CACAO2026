package abstraction.eq9Distributeur2;

import abstraction.eqXRomu.appelDOffre.AppelDOffre;
import abstraction.eqXRomu.appelDOffre.IAcheteurAO;
import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.appelDOffre.SuperviseurVentesAO;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import java.util.List;

    /**  
	 * @author Anass Ouisrani
     */
public class Distributeur2AcheteurAO extends Distributeur2Acteur implements IAcheteurAO {

    //  recherche 
    public void faireUnAppelDOffre() {
        // On récupère le superviseur des appels d'offres
        SuperviseurVentesAO superviseurAO = (SuperviseurVentesAO) Filiere.LA_FILIERE.getActeur("Sup.AO");
        
        // On récupère la liste de tous les chocolats du marché
        List<ChocolatDeMarque> produits = Filiere.LA_FILIERE.getChocolatsProduits();

        // on vérifie notre stock pour CHAQUE chocolat
        for (ChocolatDeMarque choco : produits) {
            
            //stock actuel
            double stockActuel = this.stock.getOrDefault(choco, 0.0);
            
            // Seuil de sécurité réaliste : 100 tonnes minimum en stock
            double seuilDeSecurite = 100000.0; // 100 tonnes = 100 000 kg
            
            if (stockActuel < seuilDeSecurite) {
                
                // Quantité à acheter : viser 200 tonnes total en stock
                double quantiteCible = 200000.0; // 200 tonnes
                double quantiteAcheter = quantiteCible - stockActuel;
                
                // Respecter la quantité minimum pour les appels d'offres
                if (quantiteAcheter < AppelDOffre.AO_QUANTITE_MIN) {
                    quantiteAcheter = AppelDOffre.AO_QUANTITE_MIN;
                }
                
                this.journal.ajouter("Alerte stock bas pour " + choco.getNom() + 
                                   " (" + (stockActuel/1000) + "t). Lancement d'AO pour " + 
                                   (quantiteAcheter/1000) + "t");
                
                OffreVente offreRetenue = superviseurAO.acheterParAO(this, this.cryptogramme, choco, quantiteAcheter);
                
                if (offreRetenue != null) {
                    this.journal.ajouter("✅ Achat réussi : " + (quantiteAcheter/1000) + "t de " + 
                                       choco.getNom() + " à " + offreRetenue.getPrixT() + "€/T chez " + 
                                       offreRetenue.getVendeur().getNom());
                    
                    this.stock.put(choco, stockActuel + quantiteAcheter);
                    this.indicateurStockTotal.setValeur(this, getStockTotal());
                } else {
                    this.journal.ajouter("❌ Échec : Aucune offre acceptable pour " + choco.getNom());
                }
            }
        }
    }

    //  Le superviseur donne la liste de toutes les propositions de vente
    public OffreVente choisirOV(List<OffreVente> propositions) {
        OffreVente meilleureOffre = null;
        double meilleurPrix = Double.MAX_VALUE;
        
        // Prix maximum acceptable selon la qualité du chocolat demandé
        double prixMaxAcceptable = 30000.0; // 30 000 €/T maximum
        
        // On compare les offres concurrentes pour prendre la moins chère 
        for (OffreVente offre : propositions) {
            double prixPropose = offre.getPrixT();
            
            if (prixPropose < meilleurPrix && prixPropose <= prixMaxAcceptable) {
                meilleurPrix = prixPropose;
                meilleureOffre = offre;
            }
        }
        
        if (meilleureOffre != null) {
            this.journal.ajouter("Offre sélectionnée : " + meilleureOffre.getPrixT() + "€/T pour " + 
                               (meilleureOffre.getQuantiteT()) + "t de " + 
                               ((ChocolatDeMarque)meilleureOffre.getProduit()).getNom());
        } else {
            this.journal.ajouter("Aucune offre acceptable (prix > " + prixMaxAcceptable + "€/T)");
        }
        
        return meilleureOffre;
    }
}