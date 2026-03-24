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
public class ContratCadre extends Approvisionnement2 implements IAcheteurContratCadre {
    
    private Map<IProduit, Double> prixCibleVoulu;
    private Map<IProduit, Double> prixMaxVoulu;
    protected List<ExemplaireContratCadre> mesContrats;

    public ContratCadre() {
        super();
        this.prixCibleVoulu = new HashMap<>();
        this.prixMaxVoulu = new HashMap<>();
        this.mesContrats = new ArrayList<>();
    }

    @Override
    protected double methodeIntermediaireAchat(ChocolatDeMarque cdm, double besoin, double prixCible, double prixMax) {
        this.prixCibleVoulu.put(cdm, prixCible);
        this.prixMaxVoulu.put(cdm, prixMax);

        // Accès au superviseur [cite: 52]
        SuperviseurVentesContratCadre sup = (SuperviseurVentesContratCadre) (Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
        // Récupération des vendeurs possibles [cite: 55]
        List<IVendeurContratCadre> vendeurs = sup.getVendeurs(cdm);
        
        if (vendeurs.size() > 0) {
            // Proposition d'un échéancier sur 12 étapes (environ 6 mois)
            Echeancier ech = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 12, besoin / 12.0);
            
            // On lance la demande [cite: 54, 91]
            ExemplaireContratCadre c = sup.demandeAcheteur(this, vendeurs.get(0), cdm, ech, this.cryptogramme, false);
            
            if (c != null) {
                this.mesContrats.add(c);
                return c.getQuantiteTotale();
            }
        }
        return 0.0;
    }

    public boolean achete(IProduit produit) {
        // On n'accepte la négociation que si nous avons défini un besoin (prixCible présent)
        return produit instanceof ChocolatDeMarque && this.prixCibleVoulu.containsKey(produit);
    }

    public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
        // On récupère le besoin total que nous avions identifié
        double besoinInitial = this.prixCibleVoulu.getOrDefault(contrat.getProduit(), 0.0);
        
        // Si le vendeur propose trop, on ramène à notre besoin [cite: 58, 98]
        if (contrat.getEcheancier().getQuantiteTotale() <= besoinInitial * 1.05) {
            return contrat.getEcheancier();
        } else {
            Echeancier newEch = contrat.getEcheancier();
            // On ajuste chaque étape pour ne pas dépasser notre capacité
            for (int i = newEch.getStepDebut(); i <= newEch.getStepFin(); i++) {
                newEch.set(i, newEch.getQuantite(i) * 0.9); 
            }
            return newEch;
        }
    }

    public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
        double pCible = this.prixCibleVoulu.getOrDefault(contrat.getProduit(), 1000.0);
        double pMax = this.prixMaxVoulu.getOrDefault(contrat.getProduit(), 2000.0);
        double pVendeur = contrat.getPrix();

        // Logique : si < 90% du prix cible, on accepte immédiatement [cite: 64]
        if (pVendeur <= pCible * 0.9) {
            return pVendeur;
        }

        // Sinon, on propose une augmentation progressive (5% de plus que notre dernière offre)
        double derniereOffreAcheteur = contrat.getListePrix().size() < 2 ? pCible : contrat.getListePrix().get(contrat.getListePrix().size()-2);
        double nouvelleOffre = derniereOffreAcheteur * 1.05;

        // Arrêt des négociations si on dépasse le prix max [cite: 65, 114]
        if (nouvelleOffre > pMax) {
            return -1.0; 
        }
        
        return (nouvelleOffre >= pVendeur) ? pVendeur : nouvelleOffre;
    }

    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        // Notification de réussite des négociations [cite: 66, 120]
        this.journal5.ajouter(Color.GREEN, Color.BLACK, "CC conclu : " + contrat.toString());
        if (!this.mesContrats.contains(contrat)) {
            this.mesContrats.add(contrat);
        }
    }

    public void receptionner(IProduit p, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
        // Mise à jour du stock lors de la livraison effective [cite: 67]
        double stockActuel = this.Stock.getOrDefault(p, 0.0);
        this.Stock.put(p, stockActuel + quantiteEnTonnes);
        this.journal5.ajouter("Réception de " + quantiteEnTonnes + "T de " + p);
    }
}