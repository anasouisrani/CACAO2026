package abstraction.eq9Distributeur2;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Paul Juhel
 */

public class Distributeur2AcheteurCC extends Distributeur2AcheteurAO implements IAcheteurContratCadre {

    // Superviseur des contrats cadres
    private SuperviseurVentesContratCadre superviseurCC;

    // Liste des contrats en cours
    protected List<ExemplaireContratCadre> contratsEnCours;

    // Liste des contrats terminés
    protected List<ExemplaireContratCadre> contratsTermines;

    public Distributeur2AcheteurCC() {
        super();
        this.contratsEnCours = new LinkedList<>();
        this.contratsTermines = new LinkedList<>();
    }

    @Override
    public void initialiser() {
        this.superviseurCC = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
        this.journal.ajouter("Initialisation des CC");
    }

    @Override
    public void next() {
        int etape = Filiere.LA_FILIERE.getEtape();
        this.journal.ajouter("=== ETAPE " + etape + " ===");

        List<ChocolatDeMarque> produits = Filiere.LA_FILIERE.getChocolatsProduits();

        if (produits != null && !produits.isEmpty()) {
            // Frais de stockage : 120 €/T par étape (16x le coût producteur de 7.5€/T)
            payerFraisStockage();
            // --- V2 : Ajustement dynamique des prix de vente ---
            ajusterPrixDynamiques();
            fairePropositionCC();
        }

        this.indicateurStockTotal.setValeur(this, getStockTotal());
        journal.ajouter("Stock total : " + (getStockTotal()/1000) + " tonnes");
    }


    /**
     * Paie les frais de stockage pour cette étape
     */
    protected void payerFraisStockage() {
        double stockTotalT = getStockTotal() / 1000.0;
        double fraisStockage = stockTotalT * 120.0; // 120€/t
        if (fraisStockage > 0) {
            Filiere.LA_FILIERE.getBanque().payerCout(this, this.cryptogramme, "Frais de stockage", fraisStockage);
        }
        this.journal.ajouter("Frais de stockage : " + fraisStockage + "€ pour " + stockTotalT + "t");
    }

    /**
     * Ajuste les prix de vente de manière dynamique
     */
    protected void ajusterPrixDynamiques() {
        // Placeholder pour ajustement dynamique des prix
        // À implémenter selon la stratégie de l'équipe
    }

    //         IMPLEMENTATION DE L'INTERFACE IAcheteurContratCadre

    /**
     * Indique si l'acheteur est prêt à faire un contrat cadre pour ce produit
     * @param produit le produit concerné
     * @return true si prêt à négocier, false sinon
     */
    
    @Override
    public double getQuantiteEnStock(IProduit p, int etape) {
        return super.getQuantiteEnStock(p, this.cryptogramme);
    }

    @Override
    public boolean achete(IProduit produit) {
        if (produit instanceof ChocolatDeMarque) {
            ChocolatDeMarque choco = (ChocolatDeMarque) produit;
            if (this.prix.containsKey(choco)) {
                this.journal.ajouter("Prêt à négocier un contrat cadre pour " + choco.getNom());
                return true;
            } else {
                this.journal.ajouter("Refus CC : pas de stratégie de prix pour " + choco.getNom());
                return false;
            }
        }
        return false;
    }

    /**
     * Contre-proposition de l'échéancier
     * @param contrat le contrat en négociation
     * @return l'échéancier proposé, null pour abandonner, ou le même pour accepter
     */
    @Override
    public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
        Echeancier propositionVendeur = contrat.getEcheancier();

        // Analyser l'échéancier proposé
        int stepDebut = propositionVendeur.getStepDebut();
        int nbSteps = propositionVendeur.getNbEcheances();
        double quantiteTotale = propositionVendeur.getQuantiteTotale();

        // Préférences : livraison échelonnée sur plusieurs étapes pour réduire les risques
        int stepCourant = Filiere.LA_FILIERE.getEtape();

        // Si le début est trop proche ou trop lointain, proposer un ajustement
        int debutOptimal = stepCourant + 2; // Préférer commencer dans 2 étapes
        if (stepDebut < debutOptimal) {
            int decalage = debutOptimal - stepDebut;
            Echeancier contreProposition = new Echeancier(debutOptimal, nbSteps, quantiteTotale / nbSteps);
            this.journal.ajouter("Contre-proposition échéancier : décalage à l'étape " + debutOptimal
                + " (était " + stepDebut + ")");
            return contreProposition;
        } else if (stepDebut > debutOptimal + 3) {
            int nouveauDebut = Math.max(stepCourant + 1, stepDebut - 2);
            Echeancier contreProposition = new Echeancier(nouveauDebut, nbSteps, quantiteTotale / nbSteps);
            this.journal.ajouter("Contre-proposition échéancier : anticipation à l'étape " + nouveauDebut
                + " (était " + stepDebut + ")");
            return contreProposition;
        }

        // Si la durée est trop courte ou trop longue, ajuster
        if (nbSteps < 3) {
            // Étaler sur plus d'étapes
            int nouvelleDuree = Math.min(6, 24 - stepDebut);
            Echeancier contreProposition = new Echeancier(stepDebut, nouvelleDuree, quantiteTotale / nouvelleDuree);
            this.journal.ajouter("Contre-proposition échéancier : étalement sur " + nouvelleDuree
                + " étapes (était " + nbSteps + ")");
            return contreProposition;
        } else if (nbSteps > 8) {
            // Raccourcir
            int nouvelleDuree = Math.max(3, nbSteps - 2);
            Echeancier contreProposition = new Echeancier(stepDebut, nouvelleDuree, quantiteTotale / nouvelleDuree);
            this.journal.ajouter("Contre-proposition échéancier : réduction à " + nouvelleDuree
                + " étapes (était " + nbSteps + ")");
            return contreProposition;
        }

        // Accepter l'échéancier
        this.journal.ajouter("Acceptation de l'échéancier pour " + ((ChocolatDeMarque)contrat.getProduit()).getNom());
        return propositionVendeur;
    }

    /**
     * Contre-proposition sur le prix
     * @param contrat le contrat en négociation
     * @return le prix proposé, négatif pour abandonner, ou le même pour accepter
     */
    /**
     * @author Anass Ouisrani
     */
    @Override
    public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
        double prixPropose = contrat.getPrix();

        ChocolatDeMarque choco = (ChocolatDeMarque) contrat.getProduit();

        int etape = Filiere.LA_FILIERE.getEtape();

        EQ9_StrategieFixationPrix strat = new EQ9_StrategieFixationPrix(this.journal);

        // Simulation d’un prix de vente réaliste
        double prixVenteEstime = strat.calculerPrixVente(
            prixPropose, // on suppose que c’est le coût d’achat
            choco.getNom(),
            this.stock.getOrDefault(choco, 0.0),
            20000.0, // demande estimée simple
            prixPropose 
        );

        // marge minimale que tu veux garder
        double margeMin = 1.2; // 20%

        double prixMax = prixVenteEstime / margeMin;

        // Si le vendeur demande plus que notre maximum → on abandonne la négociation
        // -1.0 = stop dans le protocole
        if (prixPropose > prixMax) {
            this.journal.ajouter("Abandon négociation CC : prix " + prixPropose
                + "€/T trop élevé (max=" + prixMax + "€/T)");
            return -1.0;
        }

        // Évaluer notre situation financière
        double solde = getSolde();
        double quantiteTotale = contrat.getQuantiteTotale() / 1000.0; // en tonnes
        double coutTotalEstime = quantiteTotale * prixPropose;

        // Si on n'a pas les fonds, être plus ferme dans la négociation
        double margeSecurite = (solde < coutTotalEstime * 2) ? 1.1 : 1.05;

        // On consulte le prix moyen du marché à l'étape précédente
        // Si etape=0 on n'a pas de référence donc on met NaN (Not a Number = pas de valeur)
        double prixMoyen = (etape >= 1) ? Filiere.LA_FILIERE.prixMoyen(choco, etape - 1) : Double.NaN;

        // Si on a une référence marché valide
        if (!Double.isNaN(prixMoyen) && prixMoyen > 0) {

            // Ajuster notre contre-proposition selon la situation
            double contreProposition = prixPropose * 0.95; // Base : 5% de réduction

            // Si le prix proposé est déjà attractif par rapport au marché, accepter
            if (prixPropose <= prixMoyen * 1.05) {
                this.journal.ajouter("Acceptation CC : prix attractif " + prixPropose + "€/T (marché=" + prixMoyen + ")");
                return prixPropose;
            }

            // Réduire davantage si nécessaire
            if (prixPropose > prixMoyen * 1.15) {
                contreProposition = Math.max(contreProposition * 0.95, prixMoyen * 1.08);
            }

            // Mais jamais en dessous de 95% du prix moyen marché
            contreProposition = Math.max(contreProposition, prixMoyen * 0.95);

            // Si notre contre-proposition est proche du prix demandé, accepter directement
            if (contreProposition >= prixPropose * 0.98) {
                this.journal.ajouter("Acceptation CC : " + prixPropose + "€/T pour " + choco.getNom());
                return prixPropose;
            }

            // Vérifier qu'on peut payer
            double coutContreProposition = quantiteTotale * contreProposition;
            if (solde < coutContreProposition * margeSecurite) {
                // Fonds insuffisants, abandonner
                this.journal.ajouter("Abandon CC : fonds insuffisants pour " + coutContreProposition
                    + "€ (solde=" + solde + "€)");
                return -1.0;
            }

            // On envoie notre contre-proposition
            this.journal.ajouter("Contre-proposition CC : " + contreProposition
                + "€/T (proposé=" + prixPropose + ", marché=" + prixMoyen + ")");

            return contreProposition;
        }

        // Pas de référence marché disponible
        // Accepter si le prix est raisonnable par rapport à notre maximum
        if (prixPropose <= prixMax * 0.9) {
            this.journal.ajouter("Acceptation CC (pas de ref marché) : " + prixPropose + "€/T");
            return prixPropose;
        }

        // Faire une contre-proposition modérée
        double contreProposition = Math.max(prixPropose * 0.97, prixMax * 0.85);
        double coutContreProposition = quantiteTotale * contreProposition;

        if (solde >= coutContreProposition * margeSecurite) {
            this.journal.ajouter("Contre-proposition CC (pas de ref) : " + contreProposition + "€/T");
            return contreProposition;
        } else {
            this.journal.ajouter("Abandon CC : fonds insuffisants (pas de ref marché)");
            return -1.0;
        }
    }

    /**
     * Notification de la réussite des négociations
     * @param contrat le contrat signé
     */
    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.contratsEnCours.add(contrat);

        this.journal.ajouter("Nouveau contrat cadre signé pour " + ((ChocolatDeMarque)contrat.getProduit()).getNom() +
                           " : " + contrat.getQuantiteTotale() + "t à " + contrat.getPrix() + "€/t");
    }

    /**
     * Réception d'une livraison dans le cadre d'un contrat
     * @param produit le produit livré
     * @param quantiteEnTonnes la quantité livrée (en tonnes)
     * @param contrat le contrat concerné
     */

    @Override
    public void receptionner(IProduit produit, double quantiteEnTonnes, ExemplaireContratCadre contrat) {

        double quantiteEnKg = quantiteEnTonnes * 1000.0;

        // Ajouter au stock
        double stockActuel = this.stock.getOrDefault(produit, 0.0);
        this.stock.put(produit, stockActuel + quantiteEnKg);

        // Mettre à jour l'indicateur de stock total
        this.indicateurStockTotal.setValeur(this, getStockTotal());

        this.journal.ajouter("Livraison reçue : " + (quantiteEnKg/1000) + "t de " +
                           produit + " (contrat cadre)");
    }


    //         MÉTHODES UTILITAIRES

    
    /**
     * Calcule la quantité restante à livrer pour un produit donné
     * @param produit le produit
     * @return la quantité totale restant à livrer (en kg)
     */
    protected double restantDu(IProduit produit) {
        double res = 0.0;
        for (ExemplaireContratCadre contrat : this.contratsEnCours) {
            if (contrat.getProduit().equals(produit)) {
                res += contrat.getQuantiteRestantALivrer() * 1000.0;
            }
        }
        return res;
    }

    /**
     * Renvoie la liste des contrats en cours
     * @return liste des contrats actifs
     */
    public List<ExemplaireContratCadre> getContratsEnCours() {
        return new LinkedList<>(this.contratsEnCours);
    }

    /**
     * Renvoie la liste des contrats terminés
     * @return liste des contrats terminés
     */
    public List<ExemplaireContratCadre> getContratsTermines() {
        return new LinkedList<>(this.contratsTermines);
    }

    /**
     * Méthode pour initier des propositions de contrats cadres
     * @author Paul JUHEL
     */
    public void fairePropositionCC() {
        List<ChocolatDeMarque> produits = Filiere.LA_FILIERE.getChocolatsProduits();
        for (ChocolatDeMarque choco : produits) {
            double stockActuel = this.stock.getOrDefault(choco, 0.0);
            double enCours = restantDu(choco);
            double seuilDeSecurite = 10000 ;
            double stockProjete = stockActuel + enCours;

            if (stockProjete < seuilDeSecurite) {
                double quantiteCible = 50000.0; // 50 tonnes
                double quantiteAcheter = quantiteCible - stockActuel;
                if (quantiteAcheter < 1000.0) { // Minimum 1 tonne
                    continue;
                }

                // Vérifier les fonds disponibles (estimation prudente)
                double prixEstime = getPrixMaxAcceptable(choco);
                double coutEstime = (quantiteAcheter / 1000.0) * prixEstime;
                if (getSolde() < coutEstime * 1.2) { // Marge de sécurité
                    this.journal.ajouter("Fonds insuffisants pour CC " + choco.getNom()
                        + " : besoin " + coutEstime + "€, solde " + getSolde() + "€");
                    continue;
                }

                List<IVendeurContratCadre> vendeurs = this.superviseurCC.getVendeurs(choco);
                if (vendeurs.isEmpty()) {
                    this.journal.ajouter("Aucun vendeur disponible pour " + choco.getNom());
                    continue;
                }

                // Essayer de négocier avec les vendeurs
                boolean propositionReussie = false;
                for (IVendeurContratCadre vendeur : vendeurs) {
                    int stepDebut = Filiere.LA_FILIERE.getEtape() + 1;
                    int nbSteps = Math.min(6, 24 - stepDebut);
                    if (nbSteps <= 0) continue;

                    double quantiteParStep = quantiteAcheter / nbSteps;
                    Echeancier echeancierPropose = new Echeancier(stepDebut, nbSteps, quantiteParStep);

                    // Initier la négociation (le résultat sera géré par les méthodes de contre-proposition)
                    ExemplaireContratCadre contrat = this.superviseurCC.demandeAcheteur(
                        this, vendeur, choco, echeancierPropose, this.cryptogramme, false);

                    if (contrat != null) {
                        this.journal.ajouter("Proposition CC initiée pour " + (quantiteAcheter/1000)
                            + "t de " + choco.getNom() + " chez " + vendeur.getNom());
                        propositionReussie = true;
                        break; // On s'arrête au premier vendeur qui accepte de négocier
                    } else {
                        this.journal.ajouter("Proposition CC rejetée par " + vendeur.getNom()
                            + " pour " + choco.getNom());
                    }
                }

                if (!propositionReussie) {
                    this.journal.ajouter("Échec de toutes les propositions CC pour " + choco.getNom());
                }
            }
        }
    }

    /**
     * Méthode utilitaire qui définit le prix maximum qu'on accepte
     * selon la qualité du chocolat (en €/T)
     * Ces valeurs sont nos prix d'achat maximum pour garder une marge rentable
     */
    private double getPrixMaxAcceptable(ChocolatDeMarque choco) {
        return this.prix.getOrDefault(choco, 100.0) * 0.75; // 25% de marge systématique
    }

    @Override
    public Filiere getFiliere(String nom) {
        return Filiere.LA_FILIERE;
    }

    @Override
    public java.util.List<String> getNomsFilieresProposees() {
        return new java.util.ArrayList<>();
    }
}