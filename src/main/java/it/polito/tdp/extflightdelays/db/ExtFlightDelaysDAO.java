package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Model;
import it.polito.tdp.extflightdelays.model.Rotte;

public class ExtFlightDelaysDAO {
	
	TreeMap<Integer, Airport> aeroporti;

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public TreeMap<Integer, Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		TreeMap<Integer, Airport> result = new TreeMap<Integer, Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.put(airport.getId(), airport);
			}

			conn.close();
			
			aeroporti = new TreeMap<Integer, Airport>(result);
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public boolean setCompagnie() {
		
		String sql = "SELECT r, COUNT(*) AS val "
				+ "FROM ((SELECT ORIGIN_AIRPORT_ID AS r, AIRLINE_ID "
				+ "FROM flights "
				+ "GROUP BY ORIGIN_AIRPORT_ID, AIRLINE_ID) "
				+ "union "
				+ "(SELECT DESTINATION_AIRPORT_ID AS r, AIRLINE_ID "
				+ "FROM flights "
				+ "GROUP BY DESTINATION_AIRPORT_ID, AIRLINE_ID)) AS tabl "
				+ "GROUP BY r "
				+ "ORDER by r";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				int id = rs.getInt("r");
				int value = rs.getInt("val");
				
				if(aeroporti.containsKey(id)) {
					Airport a = aeroporti.get(id);
					a.setCompagnie(value);
				}
			}
			
			return true;
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public List<Rotte> getRotte(){
		
		String sql = "SELECT p,a,COUNT(*) AS val "
				+ "FROM ((SELECT ORIGIN_AIRPORT_ID AS p,DESTINATION_AIRPORT_ID AS a "
				+ "FROM flights) "
				+ "UNION ALL "
				+ "(SELECT DESTINATION_AIRPORT_ID AS p,ORIGIN_AIRPORT_ID AS a "
				+ "FROM flights)) AS tab "
				+ "WHERE p<a "
				+ "GROUP BY p,a ";
		
		List<Rotte> result = new LinkedList<Rotte>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Airport p = this.aeroporti.get(rs.getInt("p"));
				Airport a = this.aeroporti.get(rs.getInt("a"));
				if(p!=null && a!=null) {
					Rotte r = new Rotte(p, a, rs.getInt("val"));
					result.add(r);
				}
			}
			
			return result; 
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}