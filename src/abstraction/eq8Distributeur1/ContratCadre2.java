package abstraction.eq8Distributeur1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

/** @author Ewen Landron */
public class ContratCadre2 extends Approvisionnement2 implements IAcheteurContratCadre {
    
    // Variables de "session" pour le contrat en cours de négociation
    private double besoinCourant;
    private double prixCibleCourant;
    private double prixMaxCourant;
    
    // Listes et Maps persistantes
    protected List<ExemplaireContratCadre> mesContrats;
    private Map<IProduit, Double> quantiteParEtapeVoulue; // Gardé pour l'initiative acheteur
    private Map<IProduit, Double> prixCibleVoulu;         // Gardé pour l'initiative acheteur
    private Map<IProduit, Double> prixMaxVoulu;           // Gardé pour l'initiative acheteur

    public ContratCadre2() {
        super();
        this.mesContrats = new ArrayList<>();
        this.quantiteParEtapeVoulue = new HashMap<>();
        this.prixCibleVoulu = new HashMap<>();
        this.prixMaxVoulu = new HashMap<>();
    }

    /**
     * Initialise les variables de négociation pour le contrat actuel.
     * Cette méthode est appelée au début de chaque contre-proposition.
     */
    private void initialiserParametres(ExemplaireContratCadre contrat) {
        IProduit p = contrat.getProduit();
        
        if (this.quantiteParEtapeVoulue.containsKey(p)) {
            // CAS 1 : Nous sommes à l'initiative (paramètres déjà stockés via methodeIntermediaireAchat)
            this.besoinCourant = this.quantiteParEtapeVoulue.get(p);
            this.prixCibleCourant = this.prixCibleVoulu.get(p);
            this.prixMaxCourant = this.prixMaxVoulu.get(p);
        } else {
            // CAS 2 : Le vendeur est à l'initiative (on doit calculer des valeurs types)
            // On récupère le prix moyen/cible depuis le dictionnaire d'Approvisionnement2 s'il existe
            this.prixCibleCourant = this.prixDAchat.getOrDefault(p, 1000.0); 
            this.prixMaxCourant = this.prixCibleCourant * 1.5; // Marge de 50% par défaut
            
            // Pour le besoin, on peut par exemple viser une quantité standard ou 
            // recalculer un besoin rapide (ici 5% de la capacité totale pour l'exemple)
            this.besoinCourant = 10.0; 
        }
    }

    @Override
    protected double methodeIntermediaireAchat(ChocolatDeMarque cdm, double besoinParEtape, double prixCible, double prixMax) {
        // On stocke pour que initialiserParametres puisse les retrouver
        this.quantiteParEtapeVoulue.put(cdm, besoinParEtape);
        this.prixCibleVoulu.put(cdm, prixCible);
        this.prixMaxVoulu.put(cdm, prixMax);

        SuperviseurVentesContratCadre sup = (SuperviseurVentesContratCadre) (Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
        List<IVendeurContratCadre> vendeurs = sup.getVendeurs(cdm);
        
        if (vendeurs.size() > 0) {
            Echeancier ech = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 12, besoinParEtape);
            ExemplaireContratCadre c = sup.demandeAcheteur(this, vendeurs.get(0), cdm, ech, this.cryptogramme, false);
            
            if (c != null) {
                this.mesContrats.add(c);
                return c.getQuantiteTotale();
            }
        }
        return 0.0;
    }

    public boolean achete(IProduit produit) {
        // On accepte si c'est du chocolat de marque
        return produit instanceof ChocolatDeMarque;
    }

    public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
        // Mise à jour de la "session" de négociation
        this.initialiserParametres(contrat);
        
        if (this.besoinCourant <= 0) return null;

        Echeancier echVendeur = contrat.getEcheancier();
        Echeancier echReponse = new Echeancier(echVendeur.getStepDebut());

        for (int step = echVendeur.getStepDebut(); step <= echVendeur.getStepFin(); step++) {
            double qteVendeur = echVendeur.getQuantite(step);

            if (qteVendeur > this.besoinCourant) {
                echReponse.set(step, this.besoinCourant);
            } else if (Math.abs(qteVendeur - this.besoinCourant) < 0.01) {
                echReponse.set(step, qteVendeur);
            } else {
                double milieu = (qteVendeur + this.besoinCourant) / 2.0;
                echReponse.set(step, milieu);
            }
        }
        return echReponse;
    }

    public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
        // Les paramètres ont été initialisés par contrePropositionDeLAcheteur juste avant
        double pVendeur = contrat.getPrix();

        if (pVendeur <= this.prixCibleCourant * 0.9) {
            return pVendeur;
        }

        double debutNego = this.prixCibleCourant * 0.9;
        double margeTotale = this.prixMaxCourant - debutNego;
        int tourDeNego = contrat.getListePrix().size() / 2;

        double nouvelleOffre = debutNego + (tourDeNego * (margeTotale / 10.0));

        if (tourDeNego >= 10) {
            return (pVendeur <= this.prixMaxCourant) ? pVendeur : -1.0;
        }

        if (nouvelleOffre >= pVendeur) {
            return pVendeur;
        }

        return nouvelleOffre;
    }

    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.journal5.ajouter(Color.GREEN, Color.BLACK, "CC conclu : " + contrat.toString());
        if (!this.mesContrats.contains(contrat)) {
            this.mesContrats.add(contrat);
        }
        // On nettoie les maps d'initiative pour le prochain contrat
        this.quantiteParEtapeVoulue.remove(contrat.getProduit());
    }

    public void receptionner(IProduit p, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
        double stockActuel = this.Stock.getOrDefault(p, 0.0);
        this.Stock.put(p, stockActuel + quantiteEnTonnes);
        this.journal5.ajouter("Réception de " + quantiteEnTonnes + "T de " + p);
    }
}