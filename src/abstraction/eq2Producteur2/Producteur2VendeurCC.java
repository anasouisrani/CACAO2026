package abstraction.eq2Producteur2;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.general.Journal;

import java.util.LinkedList;
import java.util.List;

public class Producteur2VendeurCC extends Producteur2Acteur implements IVendeurContratCadre {

    private SuperviseurVentesContratCadre supCC;
    private List<ExemplaireContratCadre> contratsEnCours;
    private Journal journal;
    private int PRIX_DEFAUT = 5000;

    public Producteur2VendeurCC() {
        super();
        this.contratsEnCours = new LinkedList<>();
        this.journal = new Journal("Journal CC Eq2", this);
    }

    @Override
    public void initialiser() {
        super.initialiser();
        this.supCC = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
        this.journal.ajouter("Producteur2VendeurCC initialisé");
    }

    @Override
    public void next() {
        super.next();
        this.journal.ajouter("Step " + Filiere.LA_FILIERE.getEtape() + " : vérification CC");
        for (Feve f : stocks.keySet()) {
            double disponible = stocks.get(f).getValeur(this.cryptogramme) - restantDu(f);
            this.journal.ajouter("Stock disponible " + f + " = " + disponible);
            if (disponible > 1200) {
                double parStep = Math.max(100.0, disponible / 24.0);
                Echeancier e = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 12, parStep);
                List<IAcheteurContratCadre> acheteurs = supCC.getAcheteurs(f);
                if (!acheteurs.isEmpty()) {
                    IAcheteurContratCadre acheteur = acheteurs.get(Filiere.random.nextInt(acheteurs.size()));
                    ExemplaireContratCadre contrat = supCC.demandeVendeur(acheteur, this, f, e, cryptogramme, false);
                    if (contrat != null) {
                        this.contratsEnCours.add(contrat);
                        this.journal.ajouter("Contrat signé avec " + acheteur.getNom() + " pour " + f + " = " + contrat.getQuantiteTotale());
                    } else {
                        this.journal.ajouter("Négociation échouée pour " + f);
                    }
                } else {
                    this.journal.ajouter("Pas d'acheteur pour " + f);
                }
            }
        }
    }

    public double restantDu(Feve f) {
        double res = 0;
        for (ExemplaireContratCadre c : this.contratsEnCours) {
            if (c.getProduit().equals(f)) {
                res += c.getQuantiteRestantALivrer();
            }
        }
        return res;
    }

    @Override
    public boolean vend(IProduit produit) {
        if (!(produit instanceof Feve)) {
            return false;
        }
        Feve f = (Feve) produit;
        boolean peutVendre = stocks.get(f).getValeur(this.cryptogramme) - restantDu(f) > 1200;
        this.journal.ajouter("vend? " + f + " -> " + peutVendre);
        return peutVendre;
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        if (!(contrat.getProduit() instanceof Feve)) {
            return null;
        }
        Feve f = (Feve) contrat.getProduit();
        double disponible = stocks.get(f).getValeur(this.cryptogramme) - restantDu(f);
        if (disponible < 1200) {
            return null;
        }
        if (contrat.getEcheancier().getQuantiteTotale() <= disponible) {
            return contrat.getEcheancier();
        }
        return new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 12, disponible / 12.0);
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        if (!(contrat.getProduit() instanceof Feve)) {
            return 0;
        }
        double cours = ((Feve) contrat.getProduit()).isEquitable() ? 0.0
                : ((abstraction.eqXRomu.bourseCacao.BourseCacao) Filiere.LA_FILIERE.getActeur("BourseCacao"))
                        .getCours((Feve) contrat.getProduit()).getValeur();
        double prix = Math.max(PRIX_DEFAUT, cours * 1.2);
        this.journal.ajouter("Proposition prix " + prix + " pour " + contrat.getProduit());
        return prix;
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        if (!(contrat.getProduit() instanceof Feve)) {
            return 0;
        }
        double prixActuel = contrat.getPrix();
        return prixActuel <= PRIX_DEFAUT ? prixActuel : prixActuel * 1.05;
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.contratsEnCours.add(contrat);
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        if (!(produit instanceof Feve)) {
            return 0.0;
        }
        Feve f = (Feve) produit;
        Variable stockFeve = stocks.get(f);
        double stockActuel = stockFeve != null ? stockFeve.getValeur(this.cryptogramme) : 0.0;
        double livre = Math.min(quantite, stockActuel);
        if (stockFeve != null) {
            stockFeve.retirer(this, livre, this.cryptogramme);
        }
        this.journal.ajouter("Livraison de " + livre + " t de " + f + " pour contrat " + contrat.getNumero());
        return livre;
    }

    @Override
    public List<Journal> getJournaux() {
        List<Journal> res = super.getJournaux();
        res.add(this.journal);
        return res;
    }
}