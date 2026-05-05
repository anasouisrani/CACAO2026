/**@author Yassine Safta */

package abstraction.eq4Transformateur1;

import abstraction.eqXRomu.filiere.Filiere;

public class Transformateur1Couts extends Transformateur1Stock {
    
    public void next() {
		super.next();
        int now = Filiere.LA_FILIERE.getEtape();
        Filiere.LA_FILIERE.getBanque().payerCout(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme,"salaires",11250000 );
        Filiere.LA_FILIERE.getBanque().payerCout(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme,"energie",  this.ChocoProduit*90 );
        Filiere.LA_FILIERE.getBanque().payerCout(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme,"couts fixes",  2000000);
}}