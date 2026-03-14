package com.cinerolodex.manager;

import com.cinerolodex.contract.IPersistence;
import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Film;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.StatoVisione;
import com.cinerolodex.contract.IFilm;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * DatabaseManager è responsabile della gestione della persistenza dei dati dei film utilizzando un database SQLite.
 * Implementa l'interfaccia IPersistence per garantire un contratto chiaro per le operazioni di salvataggio, aggiornamento, cancellazione e caricamento dei film.
 * Utilizza il pattern Singleton per assicurare che ci sia una sola istanza di DatabaseManager durante l'esecuzione dell'applicazione, evitando così problemi di connessione concorrente al database.
 * Tutte le transazioni sul database sono gestite tramite PreparedStatement per prevenire SQL injection, migliorare le performance e gestire la conversione tra i tipi Java e quelli SQL.
*/

public class DatabaseManager implements IPersistence {
    private static DatabaseManager instance = null;

	/*
	 * Connessione al database come attributo della classe per evitare continue riaperture e chiusure
	 * Tutti i metodi di salvataggio, aggiornamento, cancellazione e caricamento dei film possono utilizzare questa connessione per eseguire le operazioni sul database.
	 */
	private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:cinerolodex.db";


	
    private DatabaseManager() { //costruttore privato per garantire il Singleton
		/*
		 * Separazione della logica di conessione al DB e di inizializzazione del DB.
		 * nota: Il metodo initDatabase() ha inoltre bisogno di un oggetto connection valito per poter essere eseguito.
		 */
		try {
			connection = DriverManager.getConnection(DB_URL);
			initDatabase(); // Inizializzazione del database e creazione delle tabelle se non esistono
			System.out.println("Connessione al database stabilita con successo.");
		} catch (SQLException e) {
			System.err.println("Errore durante la connessione al DB: " + e.getMessage());
		}
	}



    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }



	@Override
	public boolean save(IFilm f) {
		String sql = "INSERT INTO Film(titolo, path, rating, stato_visione, regista_id, genere_id, anno_id) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?)";

		try {
			// Recupero degli ID delle tabelle satellite (Regista, Genere, Anno) o inserimento se non esistono già
			int idRegista = getOrInsertMetadata("Regista", "nome", f.getRegista().getNome());
			int idGenere = getOrInsertMetadata("Genere", "nome", f.getGenere().getNome());
			int idAnno = getOrInsertMetadata("Anno", "valore", f.getAnno().getValore());

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, f.getTitolo());
				pstmt.setString(2, f.getPath().toString());
				// Conversione Enum -> String
				pstmt.setString(3, f.getRating().name());
				pstmt.setString(4, f.getStato().name());
				
				pstmt.setInt(5, idRegista);
				pstmt.setInt(6, idGenere);
				pstmt.setInt(7, idAnno);

				pstmt.executeUpdate();
				System.out.println("Film salvato con successo: " + f.getTitolo());
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Errore nel salvataggio del film: " + e.getMessage());
			return false;
		}
	}
	


	@Override
	public boolean update(IFilm f) {
		// Query SQL per aggiornare tutti i campi del film identificato dal suo ID
		String sql = "UPDATE Film SET titolo = ?, path = ?, rating = ?, stato_visione = ?, " +
					"regista_id = ?, genere_id = ?, anno_id = ? WHERE id = ?";

		try {
			// 1. Verifichiamo se i (potenziali) nuovi metadati esistono e otteniamo i loro ID
			int idRegista = getOrInsertMetadata("Regista", "nome", f.getRegista().getNome());
			int idGenere = getOrInsertMetadata("Genere", "nome", f.getGenere().getNome());
			int idAnno = getOrInsertMetadata("Anno", "valore", f.getAnno().getValore());

			// 2. Eseguiamo l'aggiornamento della tabella principale
			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, f.getTitolo());
				pstmt.setString(2, f.getPath().toString());
				pstmt.setString(3, f.getRating().name()); // Mapping Enum -> String
				pstmt.setString(4, f.getStato().name());
				pstmt.setInt(5, idRegista);
				pstmt.setInt(6, idGenere);
				pstmt.setInt(7, idAnno);
				pstmt.setInt(8, f.getId()); // Fondamentale per la clausola WHERE

				int affectedRows = pstmt.executeUpdate();
				
				if (affectedRows > 0) {
					System.out.println("Film aggiornato correttamente nel DB: " + f.getTitolo());
					return true;
				}
			}
		} catch (SQLException e) {
			System.err.println("Errore durante l'aggiornamento del film: " + e.getMessage());
			return false;
		}
		return false;
	}
	


	@Override
	public boolean delete(IFilm f) {
		// La query agisce solo sulla tabella Film identificando il record tramite l'ID
		String sql = "DELETE FROM Film WHERE id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			//si imposta l'id del film da rimuovere come parametro della query
			pstmt.setInt(1, f.getId());

			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows > 0) {
				System.out.println("Film rimosso correttamente dal database: " + f.getTitolo());
				return true;
			} else {
				System.out.println("Nessun film trovato con ID: " + f.getId());
			}
		} catch (SQLException e) {
			System.err.println("Errore durante la rimozione del film: " + e.getMessage());
		}
		return false;
	}
	
	
	@Override
	public List<IFilm> loadAll() {
		List<IFilm> movies = new ArrayList<>();
		
		// Cache locali per il pattern Flyweight
		Map<String, Regista> registiCache = new HashMap<>();
		Map<String, Genere> generiCache = new HashMap<>();
		Map<Integer, Anno> anniCache = new HashMap<>();

		String sql = "SELECT f.*, r.nome AS regista_nome, g.nome AS genere_nome, a.valore AS anno_valore " +
				"FROM Film f " +
				"JOIN Regista r ON f.regista_id = r.id " +
				"JOIN Genere g ON f.genere_id = g.id " +
				"JOIN Anno a ON f.anno_id = a.id";

		try (Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				// APPLICAZIONE FLYWEIGHT: Se il regista esiste già in cache, usa quello
				String nomeRegista = rs.getString("regista_nome");
				Regista regista = registiCache.computeIfAbsent(nomeRegista, Regista::new);

				String nomeGenere = rs.getString("genere_nome");
				Genere genere = generiCache.computeIfAbsent(nomeGenere, Genere::new);

				int valoreAnno = rs.getInt("anno_valore");
				Anno anno = anniCache.computeIfAbsent(valoreAnno, Anno::new);

				// Ora tutti i film dello stesso regista punteranno allo STESSO oggetto in memoria
				IFilm film = new Film(
					rs.getInt("id"),
					rs.getString("titolo"),
					Paths.get(rs.getString("path")),
					Rating.valueOf(rs.getString("rating")),
					StatoVisione.valueOf(rs.getString("stato_visione")),
					regista, genere, anno
				);

				movies.add(film);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return movies;
	}


	/*
	 * Metodo per inizializzare il database.
	 */
	private void initDatabase() {
        // Creazione prima delle tabelle indipendenti per rispettare i vincoli di integrità
        String sqlRegista = "CREATE TABLE IF NOT EXISTS Regista (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "nome TEXT UNIQUE NOT NULL);";

        String sqlGenere = "CREATE TABLE IF NOT EXISTS Genere (" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"nome TEXT UNIQUE NOT NULL);";

        String sqlAnno = "CREATE TABLE IF NOT EXISTS Anno (" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"valore INTEGER UNIQUE NOT NULL);";

        // Tabella Film con chiavi esterne verso le precedenti
        String sqlFilm = "CREATE TABLE IF NOT EXISTS Film (" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"titolo TEXT NOT NULL, " +
						"path TEXT UNIQUE NOT NULL, " +
                         "rating TEXT, " +			// Mappatura stringa per Enum
						"stato_visione TEXT, " +	// Mappatura stringa per Enum
						"regista_id INTEGER, " +
						"genere_id INTEGER, " +
						"anno_id INTEGER, " +
						"FOREIGN KEY (regista_id) REFERENCES Regista(id), " +
						"FOREIGN KEY (genere_id) REFERENCES Genere(id), " +
						"FOREIGN KEY (anno_id) REFERENCES Anno(id));";

        try (Statement stmt = connection.createStatement()) {
            // Esecuzione dei comandi SQL
            stmt.execute(sqlRegista);
            stmt.execute(sqlGenere);
            stmt.execute(sqlAnno);
            stmt.execute(sqlFilm);
        } catch (SQLException e) {
            System.err.println("Errore creazione tabelle: " + e.getMessage());
        }
    }

	/*
	 * Metodo helper per ottenere l'ID di un metadato (Regista, Genere, Anno) o inserirlo se non esiste già.
	 * Questo metodo viene utilizzato durante l'inserimento o l'aggiornamento di un film per garantire che i metadati dipendenti da entità
	 * satellite siano sempre coerenti e non duplicati nel database.
	*/
	private int getOrInsertMetadata(String table, String column, Object value) throws SQLException {
		// Ricerca se il valore esiste già nella tabella
		String selectSql = "SELECT id FROM " + table + " WHERE " + column + " = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
			pstmt.setObject(1, value);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("id");
			}
		}

		// Se il valore non esiste viene inserito e restituito il nuovo ID generato
		String insertSql = "INSERT INTO " + table + "(" + column + ") VALUES(?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setObject(1, value);
			pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getInt(1);
			}
		}
		throw new SQLException("Errore durante l'inserimento dei metadati in " + table);
	}
}
