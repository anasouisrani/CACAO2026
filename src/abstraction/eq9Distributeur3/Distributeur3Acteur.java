package abstraction.eq9Distributeur3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.acteurs.Romu;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;
import java.util.HashMap;
import java.util.LinkedList;

import abstraction.eqXRomu.general.VariablePrivee;

public class Distributeur3Acteur implements IActeur {
	
	protected int cryptogramme;

	protected Journal journal;

	private List<ChocolatDeMarque>chocosProduits;
	protected HashMap<ChocolatDeMarque, Double> stockChocoMarque;
	protected List<ChocolatDeMarque> chocolatsVillors;
	protected Variable totalStocksChocoMarque;

	public Distributeur3Acteur() {
    this.journal = new Journal("Journal EQ9", this);
	this.chocosProduits = new LinkedList<ChocolatDeMarque>();
	this.totalStocksChocoMarque = new VariablePrivee("Eq9StockChocoMarque", "<html>Quantite totale de chocolat de marque en stock</html>",this, 0.0, 1000000.0, 0.0);
	}
	
	public void initialiser() {
		this.stockChocoMarque=new HashMap<ChocolatDeMarque,Double>();
		chocosProduits= Filiere.LA_FILIERE.getChocolatsProduits();
		for (ChocolatDeMarque cm : chocosProduits) {
			this.stockChocoMarque.put(cm, 40000.0);
			this.journal.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN," stock("+cm+")->"+this.stockChocoMarque.get(cm));
			this.totalStocksChocoMarque.ajouter(this,  40000, cryptogramme);
		}
	}

	public String getNom() {// NE PAS MODIFIER
		return "EQ9";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
   	 	int etape = Filiere.LA_FILIERE.getEtape();
    	this.journal.ajouter("ETAPE" + etape);

		this.journal.ajouter("=== STOCKS === ");
		
		// Remettre 100 tonnes d'un produit en rayon à chaque étape
		// Les unités sont des kg
		double TONNE = 1000.0;
		double replenish = 100.0 * TONNE;
		if (this.chocosProduits == null || this.chocosProduits.size() == 0) {
			this.chocosProduits = Filiere.LA_FILIERE.getChocolatsProduits();
		}
		if (this.chocosProduits != null && this.chocosProduits.size() > 0) {
			ChocolatDeMarque toAdd = this.chocosProduits.get(0); // choix arbitraire on prend le premier produit qui vient
			double oldQ = this.stockChocoMarque.getOrDefault(toAdd, 0.0);
			double newQ = oldQ + replenish;
			this.stockChocoMarque.put(toAdd, newQ);
			this.journal.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Remise en rayon : +100t de "+toAdd+" ("+oldQ+" -> "+newQ+")");
		}
		
		double total = 0.0;
		if (this.stockChocoMarque.keySet().size()>0) {
			for (ChocolatDeMarque cm : this.stockChocoMarque.keySet()) {
				double q = this.stockChocoMarque.get(cm);
				this.journal.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN,"Stock de "+Journal.texteSurUneLargeurDe(cm+"", 15)+" = "+q);
				total += q;
			}
		}
		// Mettre à jour l'indicateur (historique) du volume total de stock
		this.totalStocksChocoMarque.setValeur(this, total, this.cryptogramme);
		this.journal.ajouter(Romu.COLOR_LLGRAY, Romu.COLOR_BROWN, "Total stock = " + total);
	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(245, 155, 185); 
	}

	public String getDescription() {
		return "Distributeur 3";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		res.add(totalStocksChocoMarque);
		return res;
	}

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res=new ArrayList<Variable>();
		return res;
	}

	// Renvoie les journaux
	public List<Journal> getJournaux() {
		List<Journal> res=new ArrayList<Journal>();
		res.add(journal);
		return res;
	}

	////////////////////////////////////////////////////////
	//               En lien avec la Banque               //
	////////////////////////////////////////////////////////

	// Appelee en debut de simulation pour vous communiquer 
	// votre cryptogramme personnel, indispensable pour les
	// transactions.
	public void setCryptogramme(Integer crypto) {
		this.cryptogramme = crypto;
	}

	// Appelee lorsqu'un acteur fait faillite (potentiellement vous)
	// afin de vous en informer.
	public void notificationFaillite(IActeur acteur) {
	}

	// Apres chaque operation sur votre compte bancaire, cette
	// operation est appelee pour vous en informer
	public void notificationOperationBancaire(double montant) {
	}
	
	// Renvoie le solde actuel de l'acteur
	protected double getSolde() {
		return Filiere.LA_FILIERE.getBanque().getSolde(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme);
	}

	////////////////////////////////////////////////////////
	//        Pour la creation de filieres de test        //
	////////////////////////////////////////////////////////

	// Renvoie la liste des filieres proposees par l'acteur
	public List<String> getNomsFilieresProposees() {
		ArrayList<String> filieres = new ArrayList<String>();
		return(filieres);
	}

	// Renvoie une instance d'une filiere d'apres son nom
	public Filiere getFiliere(String nom) {
		return Filiere.LA_FILIERE;
	}

	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			if (p instanceof ChocolatDeMarque) {
				if (this.stockChocoMarque.keySet().contains(p)) {
					return this.stockChocoMarque.get(p);
				} else {
					return 0.0;
				}
			} else {
				return 0.0;
			}
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}
}
