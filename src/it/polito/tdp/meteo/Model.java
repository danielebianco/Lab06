package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private MeteoDAO mdao;
	private List<Citta> citta;
	private double punteggioMigliore;
	private List<SimpleCity> soluzioneMigliore;

	public Model() {
		this.mdao = new MeteoDAO();
		this.citta = new ArrayList<Citta>();
		this.soluzioneMigliore = null;
		
		
		for(String nc : mdao.getCitta()) {
			citta.add(new Citta(nc));
		}
	}

	public String getUmiditaMedia(int mese) {
		
		Map<String,Double> mappa = mdao.getAvgRilevamentiLocalitaMese(mese);
		
		StringBuilder sb = new StringBuilder();
		
		for(String s : mappa.keySet()) {
			sb.append(String.format("%s: %.3f \n", s, mappa.get(s)));
		}
			
		return sb.toString();
	}
	
	public String trovaSequenza(int mese) {
		
		punteggioMigliore = Double.MAX_VALUE;
		soluzioneMigliore = null;
		
		resetta(mese);
		
		recursive(new ArrayList<SimpleCity>(),0);
		
		if(soluzioneMigliore!=null) {
			return soluzioneMigliore.toString();
		}
		
		return "Soluzione non trovata.";
	}

	private void resetta(int mese) {
		for(Citta c : citta) {
			c.setCounter(0);
			c.setRilevamenti(mdao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		
		if(soluzioneCandidata==null || soluzioneCandidata.size()==0) {
			return Double.MAX_VALUE;
		}
		
		for(Citta c : citta) {
			if(!soluzioneCandidata.contains(new SimpleCity(c.getNome()))) {
				return Double.MAX_VALUE;
			}
		}
		
		double score = 0.0;
		SimpleCity precedente = soluzioneCandidata.get(0);		
		
		for(SimpleCity sc : soluzioneCandidata) {
			if(!precedente.equals(sc)) {
				score += COST;
			}
			score += sc.getCosto(); // umidita: costo giornaliero variabile
			precedente = sc;
		}
				
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		if(parziale==null)
			return false;
		if(parziale.size()==0)
			return true;
		
		for(Citta c : citta) {
			if(c.getCounter()>NUMERO_GIORNI_CITTA_MAX)
				return false;
		}
		
		SimpleCity precedente = parziale.get(0);
		int counter = 0;
		
		for(SimpleCity sc : parziale) {
			if(!precedente.equals(sc)) {
				if(counter < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					return false;
				}
				counter = 1;
				precedente = sc;
			}
			else
				counter++;
		}
		
		return true;
	}
	
	private void recursive(List<SimpleCity> parziale, int livello) {
		if(livello >= NUMERO_GIORNI_TOTALI) {
			
			double score = this.punteggioSoluzione(parziale);
			
			if(score < punteggioMigliore) {
				punteggioMigliore = score;
				soluzioneMigliore = new ArrayList<SimpleCity>(parziale);
			}
			return;
		}
		
		for(Citta c : citta) {
			SimpleCity sc = new SimpleCity(c.getNome(), c.getRilevamenti().get(livello).getUmidita());
		
			parziale.add(sc);
			c.increaseCounter();
			
			if(controllaParziale(parziale))
				recursive(parziale, livello+1);
			
			parziale.remove(livello);
			c.decreaseCounter();
		
		}
	}

}
