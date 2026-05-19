package abstraction.eq9Distributeur2;

/**
 * @author Paul JUHEL
 * Centralise les constantes
 * Unités : tonnes pour les quantités (suffixe _T), kg si précisé (suffixe _KG), €/T pour les prix.
 */
public class EQ9Config {
    private EQ9Config() {}

    // Stocks et seuils (en tonnes)
    public static final double SEUIL_MIN_T = 10.0;
    public static final double STOCK_CIBLE_T = 50.0;
    public static final double CC_QUANTITE_MIN_T = 100.0;
    public static final double AO_QUANTITE_MIN_T = 1.0; // si AppelDOffre.AO_QUANTITE_MIN est en tonnes

    // Coûts et frais (€/tonne)
    public static final double FRAIS_STOCKAGE_EUR_PAR_T = 120.0;
    public static final double COUT_STOCKAGE_PAR_TONNE = 500.0;
    public static final double COUT_PENURIE_PAR_TONNE = 2000.0;

    // Marges et coefficients financiers
    public static final double COEF_ESTIMATION_ACHAT = 0.75; // utilisé pour estimer coût d'engagement
    public static final double MARGE_SECURITE_SOLDE = 1.2; // multiplicateur pour vérifier fonds

    // Capacités et limites
    public static final double CAPACITE_RAYON_T = 500.0;

    // Paramètres AO/CC
    public static final double MIN_ACHAT_AO_T = 1.0;
}
