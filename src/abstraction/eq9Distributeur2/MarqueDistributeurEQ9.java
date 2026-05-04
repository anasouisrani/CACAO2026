package abstraction.eq9Distributeur2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;

/**
 * @author Paul ROSSIGNOL
 */
public class MarqueDistributeurEQ9 {
    private static final String MARQUE_EQ9 = "EQ9";
    private static final double PRIX_MAX_TRANSFORMATEUR_PAR_TONNE = 30000.0;

    public List<String> getMarques() {
        return Arrays.asList(MARQUE_EQ9);
    }

    public List<ChocolatDeMarque> creerProduitsMarque() {
        List<ChocolatDeMarque> produits = new ArrayList<>();

        int cacaoMQ = (int) Filiere.LA_FILIERE.getParametre("pourcentage min cacao MQ").getValeur();
        int cacaoBQ = (int) Filiere.LA_FILIERE.getParametre("pourcentage min cacao BQ").getValeur();

        produits.add(new ChocolatDeMarque(Chocolat.C_MQ, MARQUE_EQ9, cacaoMQ));
        produits.add(new ChocolatDeMarque(Chocolat.C_BQ, MARQUE_EQ9, cacaoBQ));

        return produits;
    }

    /**
     * Choisit l'offre transformateur la plus interessante.
     * Critere simple : prix/tonne minimal, puis quantite la plus elevee.
     */
    public OffreVente choisirOffreTransformateur(List<OffreVente> propositions) {
        if (propositions == null || propositions.isEmpty()) {
            return null;
        }

        OffreVente meilleure = null;
        double meilleurPrix = Double.MAX_VALUE;
        double meilleureQuantite = -1.0;

        for (OffreVente offre : propositions) {
            if (offre == null || offre.getOffre() == null) {
                continue;
            }

            double prix = offre.getPrixT();
            double quantite = offre.getOffre().getQuantiteT();
            if (prix <= 0.0 || quantite <= 0.0 || prix > PRIX_MAX_TRANSFORMATEUR_PAR_TONNE) {
                continue;
            }

            if (prix < meilleurPrix || (prix == meilleurPrix && quantite > meilleureQuantite)) {
                meilleurPrix = prix;
                meilleureQuantite = quantite;
                meilleure = offre;
            }
        }
        return meilleure;
    }
}
