package abstraction.eq7Transformateur4;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

//Auteur -> Aymeric
public class Transformateur4Vente extends Transformateur4Production implements IVendeurContratCadre {
    
    @Override
    public boolean vend(IProduit produit) {
        return produit instanceof Chocolat; // On ne vend que du chocolat de base, pas de marques
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        // Accepter l'échéancier proposé par l'acheteur
        return contrat.getEcheancier();
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        // Proposer un prix fixe, par exemple 5000 euros par tonne
        return 5000.0;
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        // Accepter le prix proposé par l'acheteur
        return contrat.getPrix();
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        // Ajouter au journal
        this.journal.ajouter("Nouveau contrat cadre signé : " + contrat.toString());
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        // Livrer depuis le stock
        double stockDisponible = this.getQuantiteEnStock(produit, this.cryptogramme);
        double quantiteLivree = Math.min(quantite, stockDisponible);
        if (quantiteLivree > 0) {
            // Retirer du stock
            if (produit == Chocolat.C_BQ) {
                this.StockChoco_BQ.retirer(this, quantiteLivree);
            } else if (produit == Chocolat.C_MQ) {
                this.StockChoco_MQ.retirer(this, quantiteLivree);
            } else if (produit == Chocolat.C_HQ) {
                this.StockChoco_HQ.retirer(this, quantiteLivree);
            }
            // Pour ChocolatDeMarque, il faudrait gérer séparément, mais pour l'instant on ignore
            this.journal.ajouter("Livraison de " + quantiteLivree + " tonnes de " + produit.toString());
        }
        return quantiteLivree;
    }
}
