package abstraction.eq2Producteur2;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

/**@author Simon */

public class Producteur2Bourse extends Producteur2Acteur{

	public Producteur2Bourse() {
		super();
	}

	@Override
	public double offre(Feve f, double cours) {
		this.setStockMin(0.1);

		double offre = 0;

		if (this.stockvar.containsKey(f) && this.cout_unit_t.containsKey(f) && this.seuil_stock.containsKey(f)) {
			
			double stockActuel = this.stockvar.get(f).getValeur();
			double quantiteAGarder = this.restantDu(f);
			
			// Calcul du prix minimal voulu : marge de 20% (x1.2) par défaut
			double marge = 1.2;
			
			double prixMinimal = this.cout_unit_t.get(f) * marge;

			this.journalBourse.ajouter("Valeur du cours de la feve " + f + " : " + cours + "\nValeur du prix minimal voulu : " + prixMinimal);

			if ((stockActuel - quantiteAGarder > this.seuil_stock.get(f)) && (prixMinimal < cours)) {
				offre = stockActuel - quantiteAGarder - this.seuil_stock.get(f);
				this.journalBourse.ajouter(Filiere.LA_FILIERE.getEtape() + " Je mets en vente " + offre + " T de " + f);
			}
		}

		return offre;
	}

}