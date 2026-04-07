package abstraction.eq2Producteur2;

import java.awt.Color;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;

/**@author Simon */

public class Producteur2Bourse extends Producteur2Acteur{

	private Journal journalBourse;
	
	public Producteur2Bourse() {

		super();
		this.journalBourse = new Journal(" journal Bourse Eq2", this);
		

	}

	public double offre(Feve f, double cours) {
		this.stockManager.setStockMin(0.1);

		double offre = 0;

		if (this.stockvar.containsKey(f) && this.stockManager.cout_unit_t.containsKey(f) && this.stockManager.seuil_stock.containsKey(f)) {
			
			double stockActuel = this.stockvar.get(f).getValeur();
			double quantiteAGarder = this.restantDu(f);
			
			// Calcul du prix minimal voulu : marge de 20% (x1.2) par défaut
			double marge = 1.2;
			
			double prixMinimal = this.stockManager.cout_unit_t.get(f) * marge;

			this.journalBourse.ajouter("Valeur du cours de la feve " + f + " : " + cours + "\nValeur du prix minimal voulu : " + prixMinimal);

			if ((stockActuel - quantiteAGarder > this.stockManager.seuil_stock.get(f)) && (prixMinimal < cours)) {
				offre = stockActuel - quantiteAGarder - this.stockManager.seuil_stock.get(f);
				this.journalBourse.ajouter(Filiere.LA_FILIERE.getEtape() + " Je mets en vente " + offre + " T de " + f);
			}
		}

		return offre;
	}

	public List<Journal> getJournaux() {
		List<Journal> res = super.getJournaux();
		res.add(journalBourse);
		return res;
	}

}