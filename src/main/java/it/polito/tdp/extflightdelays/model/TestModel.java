package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model();
		
		for (Airport a : model.Compagnie(13)) {
			System.out.println(a.toString());
		}

	}

}