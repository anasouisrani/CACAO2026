package abstraction.eq4Transformateur1;

import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

/**@author Safta Yassine */ 
public class Transformateur1 extends Transformateur1AcheteurAppelDOffre  {
	/** @author Ewan Lefort */
	VariablePrivee totalstocks= new VariablePrivee("EQ4T Total Stocks", "<html>Quantite totale de produits en stock</html>", this,0);
	VariablePrivee stockProntellaM= new VariablePrivee("EQ4T Stock ProntellaM", "<html>Quantite totale de ProntellaM en stock</html>", this, 0);
	VariablePrivee stockFevesM= new VariablePrivee("EQ4T Stock FevesM", "<html>Quantite totale de FevesM en stock</html>", this, 0);
	VariablePrivee stockPrevuProntellaM= new VariablePrivee("EQ4T Stock Prevu ProntellaM", "<html>Quantite Prevue totale de ProntellaM en stock</html>", this, 0);
	VariablePrivee stockProntellaBE= new VariablePrivee("EQ4T Stock ProntellaBE", "<html>Quantite totale de ProntellaBE en stock</html>", this, 0);
	VariablePrivee stockPrevuProntellaBE= new VariablePrivee("EQ4T Stock Prevu ProntellaBE", "<html>Quantite totale de ProntellaBE en stock</html>", this, 0);






	/** @author Safta Yassine */
	public Transformateur1() {
		super();
	}

	/**@author Ewan Lefort */

	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			return this.getStocksProduit(p); // A modifier
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}
	public void next(){
		super.next();
		this.totalstocks.setValeur(this, this.getTotalStocks(), cryptogramme);
		this.stockProntellaM.setValeur(this, this.getStocksProduit(ProntellaM), cryptogramme);
		this.stockPrevuProntellaM.setValeur(this, this.getStocksPrevuProduit(ProntellaM), cryptogramme);
		this.stockFevesM.setValeur(this, this.getStocksProduit(Feve.F_MQ), cryptogramme);
		this.stockProntellaBE.setValeur(this, this.getStocksProduit(ProntellaBE), cryptogramme);
		this.stockPrevuProntellaBE.setValeur(this, this.getStocksPrevuProduit(ProntellaBE), cryptogramme);


		
	}

	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		res.add(totalstocks);
		res.add(stockProntellaM);
		res.add(stockProntellaBE);
		res.add(stockPrevuProntellaM);
		res.add(stockPrevuProntellaBE);
		res.add(stockFevesM);
		return res;
	}
}