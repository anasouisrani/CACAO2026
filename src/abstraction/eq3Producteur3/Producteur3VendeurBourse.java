package abstraction.eq3Producteur3;

import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;


/** @author Victor Vannier-Moreau */
public class Producteur3VendeurBourse extends Producteur3Acteur implements IVendeurBourse {


    public Producteur3VendeurBourse() {
		super();
	}

	public double offre(Feve f, double cours) {
		if (f.getGamme()==Gamme.MQ) {
			return 120;
			} 
		else {
		if (f.getGamme()==Gamme.BQ){
				return this.stock.getStock(Feve.F_BQ);
			}
		else{
				return 0.0;
			}
			
		}
	}

	public double notificationVente(Feve f, double quantiteEnT, double coursEnEuroParT) {
		double retire = Math.min(this.stock.getStock(f), quantiteEnT);
		this.stock.retireStock(f, retire);
		this.mettreAJourIndicateurStock();
		this.journal_vente_bouse.ajouter("période "+Filiere.LA_FILIERE.getEtape()+" : vente de "+ retire + " tonnes de fève de  "+ f.toString() );
		return retire;
	}

	public void notificationBlackList(int dureeEnStep) {

	}

}

    

