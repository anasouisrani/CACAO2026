package abstraction.eq5Transformateur2;

import java.util.List;

import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.ChocolatDeMarque;


/**
 * @author Pierre GUTTIEREZ
 */
 
public class Transformateur2VendeurCC extends Transformateur2AchatCC implements IVendeurContratCadre{

    public Transformateur2VendeurCC() {
        super();
    }

	public boolean vend(IProduit produit){
		if (produit instanceof ChocolatDeMarque){ 
			ChocolatDeMarque cdm = (ChocolatDeMarque) produit;
			if (!cdm.getMarque().equals("Ferrara Rocher")) {
        	return false; // On ne vend pas les marques des concurrents !
			}
			if (cdm.getChocolat().isEquitable()){ // On extrait le chocolat générique pour tester
				return false;
			} else {
			    return true;
			}
		} else {
			return false;
		}
	}

	public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat){
		return contrat.getEcheancier();
	}
	
	public double propositionPrix(ExemplaireContratCadre contrat){
    // 1. On vérifie le produit (Sécurité)
    if (!(contrat.getProduit() instanceof ChocolatDeMarque)) {
        return 0.0;
    }
    
    ChocolatDeMarque cdm = (ChocolatDeMarque) contrat.getProduit();
    
    // 2. On définit un prix plancher (minimum absolu) à la tonne selon la gamme
    // N'hésitez pas à ajuster ces valeurs selon vos stratégies !
    double prixPlancherTonne;
    switch (cdm.getChocolat()) {
        case C_HQ: 
            prixPlancherTonne = 4500.0; // Le HQ est très cher
            break;
        case C_MQ: 
            prixPlancherTonne = 3000.0; // Le MQ est standard
            break;
        case C_BQ: 
            prixPlancherTonne = 2000.0; // Le BQ est abordable
            break;
        default:   
            prixPlancherTonne = 2500.0;
            break;
    }

    // 3. On calcule le prix souhaité basé sur nos coûts (prix_MP) + une marge de 35%
    // Sécurité : si prix_MP vaut 0 au début du jeu, on simule un coût de base de 1500€
    double coutDeRevient = (prix_MP > 0) ? prix_MP : 1500.0; 
    double prixCalculeTonne = coutDeRevient * 1.35;

    // 4. Stratégie : On prend le plus haut entre notre prix calculé et notre prix plancher absolu
    double prixFinalTonne = Math.max(prixCalculeTonne, prixPlancherTonne);

    // 5. On retourne le prix TOTAL demandé par le contrat
    return prixFinalTonne * contrat.getQuantiteTotale();
	}

	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat){
    // 1. On récupère notre prix plancher selon la gamme demandée
    double prixPlancherTonne;
    if (contrat.getProduit() instanceof ChocolatDeMarque) {
        switch (((ChocolatDeMarque) contrat.getProduit()).getChocolat()) {
            case C_HQ: prixPlancherTonne = 4500.0; break;
            case C_MQ: prixPlancherTonne = 3000.0; break;
            case C_BQ: prixPlancherTonne = 2000.0; break;
            default:   prixPlancherTonne = 2500.0; break;
        }
    } else {
        return 0.0; 
    }

    double prixPlancherTotal = prixPlancherTonne * contrat.getQuantiteTotale();
    
    // 2. Si le prix proposé par l'acheteur est déjà supérieur à notre minimum, 
    // c'est parfait, on accepte tout de suite !
    if (contrat.getPrix() >= prixPlancherTotal) {
        return contrat.getPrix();
    }

    // 3. Sinon, on négocie
    List<Double> listePrix = contrat.getListePrix();
    
    if (listePrix.size() < 2) {
        // Premier tour de négo : On demande notre prix plancher + 20% pour avoir de la marge
        return prixPlancherTotal * 1.20; 
    }
    
    // Tours suivants : On fait un pas vers l'acheteur (on coupe la poire en deux)
    double notreDerniereOffre = listePrix.get(listePrix.size() - 2);
    double nouvelleOffre = (contrat.getPrix() + notreDerniereOffre) / 2;
    
    // SÉCURITÉ : On s'assure que notre nouvelle offre ne descendra JAMAIS sous le prix plancher
    return Math.max(nouvelleOffre, prixPlancherTotal);
    }

	public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat){
		if (produit instanceof ChocolatDeMarque){ 
        ChocolatDeMarque cdm = (ChocolatDeMarque) produit;
        double stockDispo = this.getStock_chocolatDeMarque(cdm);

        if (stockDispo >= quantite){
            this.remove_chocolatDeMarque(cdm, quantite); // <--- LIGNE CRITIQUE
            return quantite;
        } else {
            // On livre ce qu'il nous reste
            this.remove_chocolatDeMarque(cdm, stockDispo); // <--- LIGNE CRITIQUE
            return stockDispo; 
        }
    }
    return 0.0;
	}
}