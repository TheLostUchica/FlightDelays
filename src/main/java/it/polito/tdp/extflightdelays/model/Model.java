package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	ExtFlightDelaysDAO dao;
	TreeMap<Integer, Airport> aeroporti;
	Graph<Airport, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.aeroporti = dao.loadAllAirports();
	}

	public TreeMap<Integer, Airport> getAeroporti() {
		return aeroporti;
	}
	
	public List<Airport> Compagnie(int x){
		List<Airport> lista = new ArrayList<>();
		if(dao.setCompagnie()) {
			for (Airport a : aeroporti.values()) {
				if(a.getCompagnie()>=x) {
					lista.add(a);
				}
			}
			return lista;
		}else {
			return null;
		}
	}
	
	public void creaGrafo(int x) {
		
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.grafo, this.Compagnie(x));
		
		for (Rotte r : dao.getRotte()) {
			if(grafo.containsVertex(r.getA()) && grafo.containsVertex(r.getP())) {
				Graphs.addEdgeWithVertices(this.grafo, r.getP(), r.getA(), r.getCount());
			}
		}
	}
	
	public Graph<Airport, DefaultWeightedEdge> getGrafo(){
		return this.grafo;
	}
	
	public boolean isGraphloaded() {
		return this.grafo.vertexSet().size()>0;
	}
	
	/**
	 * Metodo per verificare se due aeroporti sono connessi nel grafo, e quindi se esiste un percorso tra i due
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean esistePercorso(Airport origin, Airport destination) {
		ConnectivityInspector<Airport, DefaultWeightedEdge> inspect = new ConnectivityInspector<Airport, DefaultWeightedEdge>(this.grafo);
		if(this.grafo.containsVertex(origin)) {
			Set<Airport> componenteConnessaOrigine = inspect.connectedSetOf(origin);
			return componenteConnessaOrigine.contains(destination);
		}else {
			return false;
		}
	}
	
	
	
	/**
	 * Metodo che calcola il percorso tra due aeroporti. Se il percorso non viene trovato, 
	 * il metodo restituisce null.
	 * @param origin
	 * @param destination
	 * @return
	 */
	public List<Airport> trovaPercorso(Airport origin, Airport destination){
		List<Airport> percorso = new ArrayList<>();
	 	BreadthFirstIterator<Airport,DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, origin);
	 	Boolean trovato = false;
	 	
	 	//visito il grafo fino alla fine o fino a che non trovo la destinazione
	 	while(it.hasNext() & !trovato) {
	 		Airport visitato = it.next();
	 		if(visitato.equals(destination))
	 			trovato = true;
	 	}
	 
	 
	 	/* se ho trovato la destinazione, costruisco il percorso risalendo l'albero di visita in senso
	 	 * opposto, ovvero partendo dalla destinazione fino ad arrivare all'origine, ed aggiiungo gli aeroporti
	 	 * ad ogni step IN TESTA alla lista
	 	 * se non ho trovato la destinazione, restituisco null.
	 	 */
	 	if(trovato) {
	 		percorso.add(destination);
	 		Airport step = it.getParent(destination);
	 		while (!step.equals(origin)) {
	 			percorso.add(0,step);
	 			step = it.getParent(step);
	 		}
		 
		 percorso.add(0,origin);
		 return percorso;
	 	} else {
	 		return null;
	 	}
		
	}
	
	
}



















