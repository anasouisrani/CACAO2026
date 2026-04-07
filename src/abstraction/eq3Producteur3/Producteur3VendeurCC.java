package abstraction.eq3Producteur3;

import java.util.LinkedList;
import java.util.List;
import abstraction.eqXRomu.acteurs.ProducteurXVendeurBourse;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;


/** @author Victor Vannier-Moreau */
public class Producteur3VendeurCC extends Producteur3VendeurBourse implements IVendeurContratCadre {
    
    protected List<ExemplaireContratCadre> contratsEnCours;
    protected Journal journalCC;

    public Producteur3VendeurCC() {
        super();
        this.contratsEnCours = new LinkedList<ExemplaireContratCadre>();
        this.journalCC = new Journal("Journal Ventes CC EQ3", this);
    }

    public List<Journal> getJournaux() {
		List<Journal> jx=super.getJournaux();
		jx.add(journalCC);
		return jx;
	}

    // On ne vend que les gammes MQ et HQ par contrat cadre
    public boolean vend(IProduit produit) {
        if (produit instanceof Feve) {
            Feve f = (Feve) produit;
            if (f.getGamme() == Gamme.MQ) { 
                return true; 
            }
            if (f.getGamme() == Gamme.HQ) { 
                return true; 
            }
        }
        return false;
    }

    /**
     * Négociation de la quantité
     */
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        if (contrat.getProduit() instanceof Feve) {
            Feve f = (Feve) contrat.getProduit();
            double stockTotalReel = this.stock.getStock(f); 

            // On soustrait ce qui est déjà promis aux autres clients
            double totalDejaPromis = 0;
            for (ExemplaireContratCadre c : contratsEnCours) {
                if (c.getProduit().equals(f)) {
                    totalDejaPromis = totalDejaPromis + c.getQuantiteRestantALivrer();
                }
            }

            double disponible = stockTotalReel - totalDejaPromis;
            double demandeAcheteur = contrat.getEcheancier().getQuantiteTotale();

            if (disponible >= demandeAcheteur) {
                return contrat.getEcheancier();
            } else {
                if (disponible > 0) {
                    Echeancier e = contrat.getEcheancier();
                    e.set(e.getStepDebut(), disponible);
                    return e;
                } else {
                    return null;
                }
            }
        }
        return null; 
    }

    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        Feve f = (Feve) produit;
        String nomAcheteur = contrat.getAcheteur().getNom();

        // On vérifie le stock total pour ne pas demander plus que possible
        double stockTotal = this.stock.getStock(f);
        double aLivre = quantite;
        if (stockTotal < quantite) {
            aLivre = stockTotal;
        }

        this.stock.retireStock(f, aLivre);
        this.mettreAJourIndicateurStock();
        this.journalCC.ajouter("période "+Filiere.LA_FILIERE.getEtape()+" : Livraison de " + aLivre + " tonnes de " + f + " à " + nomAcheteur);
        return aLivre;
    }

    public double propositionPrix(ExemplaireContratCadre contrat) { return 2000.0; }
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) { return contrat.getPrix(); }
    
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.journalCC.ajouter("période "+Filiere.LA_FILIERE.getEtape()+ " : Nouveau contrat signé avec " + contrat.getAcheteur().getNom());
        this.contratsEnCours.add(contrat);
    }
}