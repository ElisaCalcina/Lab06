package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private List<Citta> leCitta;
	private List<Citta> best;
	
	public List<Citta> getLeCitta(){
		return leCitta;
	}
	

	public Model() {
		MeteoDAO meteoDAO= new MeteoDAO();
		this.leCitta=meteoDAO.getAllCitta();
		}
	

	// of course you can change the String output with what you think works best
	//se non delegassi a meteoDAO il calcolo dell'umidità media
/*	public String getUmiditaMedia(int mese) {
		String result="";
		int media1=0, media2=0, media3=0;
		int somma1=0, somma2=0, somma3=0;
		
		List<Rilevamento> rilevamenti1 = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Torino");
		List<Rilevamento> rilevamenti2 = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Milano");
		List<Rilevamento> rilevamenti3 = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Genova");
		
		for(Rilevamento r: rilevamenti1) {
			somma1+=r.getUmidita();
			media1=somma1/rilevamenti1.size();
		}
		
		for(Rilevamento r: rilevamenti2) {
			somma2+=r.getUmidita();
			media2=somma2/rilevamenti2.size();
		}
		
		for(Rilevamento r: rilevamenti3) {
			somma3+=r.getUmidita();
			media3=somma3/rilevamenti3.size();
		}
		
		result="Torino"+ " "+ media1 + "\n"+ "Milano"+ " "+ media2 + "\n"+ "Genova"+ " "+ media3;
		
		return result;
	}*/
	
	//delego a meteoDAO il calcolo dell'umidità media
	public Double getUmiditaMedia(int mese, Citta citta) {
		MeteoDAO dao= new MeteoDAO();
		return dao.getUmiditaMedia(mese, citta);
	}
	
	// of course you can change the String output with what you think works best
	
	//calcola la sequenza ottimale di visita delle città nel mese specificato
	public List<Citta> trovaSequenza(int mese) {
		List <Citta> parziale = new ArrayList<>();
		this.best = null;  //soluzione migliore, ora non ne abbiamo quindi la inizializzo a null
		
		MeteoDAO dao = new MeteoDAO();
		
		for (Citta c: leCitta) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c)); 
		}

		cerca(parziale,0);
		return best;
	}
	
	//procedura ricorsiva per il calcolo delle città ottimali
	private void cerca(List<Citta> parziale, int livello) {
		//caso terminale --> soluzione lunga 15 giorni (15 elementi)
		if (livello== NUMERO_GIORNI_TOTALI) {
			Double costo = calcolaCosto(parziale); //costo di una soluzione
			if (best ==null || costo < calcolaCosto(best)) {
				best = new ArrayList<>(parziale);
			}
		}else {
		
		//costruisco la soluzione se non sono nel caso terminale --> metodo ricorsivo
			for (Citta prova: leCitta) {
				if (aggiuntaValida(prova,parziale)) { //per valutare se l'aggiunta da fare ha senso o no --> faccio questo controllo per evitare di costruire soluzioni inutili
					parziale.add(prova);
					cerca(parziale, livello+1);
					parziale.remove(parziale.size()-1);
				}
		}
	}
		
}



	//calcola il costo di una determinata soluzione (totale)
	//assumiamo che tutti i dati/giorni del mese sono presenti nel database, ma nel nostro caso ciò non accade (in alcuni giorni il dato è mancante)
	private Double calcolaCosto(List<Citta> parziale) {
		double costo=0.0;
		
		//calcoliamo il costo seguendo le regole del testo
		
		for (int giorno=1; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			Citta c = parziale.get(giorno-1); //perchè la prima città si troverà in parziale =0
			double umid = c.getRilevamenti().get(giorno-1).getUmidita();
			costo+=umid;
		}
		
		//altra parte di costo: ogni volta che cambio città aggiungo un costo fisso 

		for (int giorno=2; giorno<=NUMERO_GIORNI_TOTALI; giorno++) {
			//se la città visitata in giorno -1 non è uguale alla città visitata nel giorno -2 allora ho cambiato città--> sommo al costo una componente fissa
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				costo +=COST;
			}
		}
		return costo;
	}

	
	private boolean aggiuntaValida(Citta prova, List<Citta> parziale) {
		//1. controllo il numero di occorrenze di prova in lista parziale --> nel nostro caso massimo 6
		//se size=0 posso inserire sempre; se poi =1 o =2: se prova corrisponde all'ultimo elemento della lista è vero, altrimenti torna falso
		//2. ora size parziale >=3 --> mantengo città o cambio
		//se mantengo la stessa città è true (controllo ultimo elemento della lista, se è uguale a quello che voglio inserire ok)
		//se cambio città devo capire se posso farlo cioè se gli ultimi 3 elementi della lista sono uguali allora posso farlo, altrimenti no
		
		int conta=0;
		//controllo di tipo zero
		for (Citta precedente:parziale) {
			if (precedente.equals(prova))
				conta++; 
		}
		
		if(conta >= NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		
		//controllo di tipo uno
		if(parziale.size()==0) {
			return true;
		}
		
		//if(parziale.size() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
		if (parziale.size()==1 || parziale.size()==2) {
			return parziale.get(parziale.size()-1).equals(prova); //ultimo elemento della lista
		}
		
		//se non voglio cambiare città
		if(parziale.get(parziale.size()-1).equals(prova)) {
			return true;
		}
		//se voglio cambiare città devo verificare che negli ultimi 3 giorni sono rimasta in quella città
		/*for(int i=0; i<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN -1 ; i++) {
			if(!parziale.get(parziale.size() - (i+1)).equals(parziale.get(parziale.size() - (i+2)))){
				return false;
			}
		}*/
		
		if (parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
				&& parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3))) {
			return true;
		}
		return false;
	}


	/*public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita){
		MeteoDAO meteoDAO= new MeteoDAO();
		return meteoDAO.getAllRilevamentiLocalitaMese(mese, localita);
	}*/

}
