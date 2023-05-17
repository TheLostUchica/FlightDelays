package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;
import it.polito.tdp.metroparis.model.Fermata;

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
		
		
		
	}
	
	
	public List<Airport> percorso(Airport partenza, Airport arrivo){
		
		/*List<Airport> result = new ArrayList<>();
		
		BreadthFirstIterator<Airport, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);		
		while(visita.hasNext()) {
			Airport f = visita.next();
			result.add(f);
			//mi restituisce per primo il vertice di partenza e poi quelli di livelli superiori
		}*/
		
		BreadthFirstIterator<Airport, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);		
		
		List<Airport> percorso = new ArrayList<>();
		
		Airport corrente = arrivo;
		percorso.add(0, arrivo);
		DefaultEdge e = visita.getSpanningTreeEdge(corrente);
		
		while(e!=null) {
			
			//restituisce il nodo precedente nel percorso tra corrente e il vertice sorgente (in questo caso partenza)
			Airport precedente = Graphs.getOppositeVertex(this.grafo, e, corrente);
			percorso.add(0, precedente);
			corrente = precedente;
			
			e = visita.getSpanningTreeEdge(corrente);		
		}
		
		return percorso;
	}
	}
	
	
	
}



















