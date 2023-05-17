package it.polito.tdp.extflightdelays.model;

public class Rotte {
	
	Airport p;
	Airport a;
	int count;
	
	public Rotte(Airport p, Airport a, int count) {
		super();
		this.p = p;
		this.a = a;
		this.count = count;
	}
	public Airport getP() {
		return p;
	}
	public Airport getA() {
		return a;
	}
	public int getCount() {
		return count;
	}
	@Override
	public String toString() {
		return "Rotte [" + p + " - " + a + "]";
	}
	
	
	
	

}
