package com.valsecchi.ChunksManager;

import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * La classe DictionaryManager fa da ponte tra i dati contenuti in un oggetto
 * DictionaryData, e l'interfaccia grafica.
 * 
 * @author Davide Valsecchi
 * @since v.0.0.0
 * @versione v.0.0.0
 * @see com.valsecchi.ChunksManager.DictionaryData
 * 
 */
public class DictionaryManager {

	private Path path;
	private DictionaryData data;
	/**
	 * buffer mappa che contiene le coppie di word/Chunk per mantenere in
	 * memoria i chunk trovati con la ricerca
	 */
	private Map<String, Chunk> buffer;
	private String dictName;
	private int mode;
	public static final int OFFLINE_MODE = 1;
	public static final int ONLINE_MODE = 2;

	/**
	 * Costruttore dell'oggetto DictionaryManager
	 * 
	 * @param _mode
	 *            parametro che indica la modalità di utilizzo del manager
	 *            {@link #OFFLINE_MODE} o {@link #ONLINE_MODE}
	 * @param name
	 *            nome del dizionario
	 * @param _path
	 *            percorso del dizionario
	 */
	public DictionaryManager(int _mode, String name, Path _path) {
		buffer = new HashMap<>();
		mode = _mode;
		dictName = name;
		path = _path;
		// si crea l'oggetto che gestisce i dati
		data = new DictionaryData(path);
	}

	/**
	 * Metodo che carica il database il memoria nell'oggetto {@link #data}
	 * 
	 * @return True se l'operazione va a buon fine
	 * @throws IOException
	 *             se ci sono problemi nella lettura del file dizionario viene
	 *             lanciata l'eccezione
	 */
	public boolean loadDictionary() throws IOException {
		return this.data.loadData();
	}

	/**
	 * Metodo che aggiunge un Chunk e relative definizioni ai dati dizionario di
	 * {@link #data}. Se il chunk è stato aggiunto si aggiungono le definizioni,
	 * se era già presente si aggiornano. Il metodo non elimina le definizioni
	 * che non sono presenti nel parametro se il chunk esiste già, ma aggiunge
	 * solo quelle non presenti.
	 * 
	 * @param word
	 *            parola del chunk da aggiungere
	 * @param type
	 *            tipo del chunk da aggiungere
	 * @param unit
	 *            unità del chunk da aggiungere
	 * @param definitions
	 *            array si stringhe che rappresenta le definizioni. Deve sempre
	 *            contenere almeno una defizione.
	 * @return ritorna True se il chunk è stato aggiunto, False se il chunk era
	 *         già presente. NB:le definizioni vengono comunque sempre
	 *         aggiornate
	 */
	public boolean addChunk(String word, String type, String unit,
			String[] definitions) {
		// si crea un oggetto chunk da aggiungere
		Chunk newC = new Chunk(word, type, unit);
		// si aggiunge il chunk,
		// isPresent memorizza se il chunk esisteva già in memoria
		boolean isPresent = data.addChunk(newC);
		// allora si aggiornano le definizioni
		List<Definition> defs = new ArrayList<>();
		for (String s : definitions) {
			defs.add(new Definition(newC.getHash(), s));
		}
		// si aggiornano le definizioni
		data.addDefinitions(newC.getHash(), defs);
		// si ritorna isPresent
		return isPresent;
	}

	/**
	 * Il metodo esegue la ricerca dei chunk con
	 * {@link com.valsecchi.ChunksManager.DictionaryData#getChunksWithArguments(String, String, String)}
	 * con i parametri passati come filtro. Il buffer dei chunk temporanei viene
	 * svuotato e vengono inseriti i chunk trovati utilizzando come chiave la
	 * loro word. In questo modo la classe client conoscerà solo le words dei
	 * chunk e tutti gli oggeti chunk saranno contenuti solo in
	 * DictionaryManager e DizionaryData.
	 * 
	 * @param pattern
	 *            parola del cercare nei chunk
	 * @param type
	 *            tipo dei chunk da cercare
	 * @param unit
	 *            unit dei chunk da cercare
	 * @return restituisce una lista di stringhe. I chunks trovati, le altre
	 *         informazioni sono ricavabili con il metodo
	 *         {@link #getChunkAttributes(String)}
	 */
	public List<String> findChunk(String pattern, String type, String unit) {
		// si cerca con data
		List<Chunk> result = data.getChunksWithArguments(pattern, type, unit);
		// si controlla che non sia vuoto
		if (result == null || result.size() == 0) {
			return null;
		}
		// si svuota il buffer
		buffer.clear();
		// si aggiungono al buffer le coppie word/Chunk in modo tale che si
		// possa
		// recuperare velocemente l'hash
		for (Chunk c : result) {
			buffer.put(c.getWord(), c);
		}
		// si restituiscono le parole
		List<String> words = new ArrayList<>();
		for (Chunk k : result) {
			words.add(k.getWord());
		}
		// si restituisce il risultato
		return words;
	}

	/**
	 * Metodo che ricerca nei dati con {@link #data} le definizioni di una certa
	 * parola. Il metodo prima controlla se la parola è memorizzata già in
	 * buffer (questa è la situazione più favorevole perchè il chunk è già
	 * disponibile; se non è in buffer, il metodo ricava il chunk da
	 * {@link #data} con in metodo
	 * {@link com.valsecchi.ChunksManager.DictionaryData#getChunksByWord(String)}
	 * .
	 * 
	 * @param word
	 *            parola di cui cercare le definizioni
	 * @return ritorna la lista di definizioni, null se il chunk non esiste
	 */
	public List<String> getDefinitions(String word) {
		// lista di definizioni
		List<Definition> def = new ArrayList<>();
		// si ricava il chunk
		Chunk c = this.getChunk(word);
		// si controlla che esista
		if (c == null) {
			return null;
		} else {
			// si aggiunge al buffer
			this.buffer.put(c.getWord(), c);
			// si cercano le definizioni
			def = data.getDefinitions(c);
		}

		// si rielaborano le definizioni in stringhe
		List<String> defs = new ArrayList<>();
		for (Definition d : def) {
			defs.add(d.getText());
		}
		return defs;
	}

	/**
	 * Il metodo restituisce al client gli attributi di un chunk, accettando
	 * come parametro la word del chunk. Infatti grazie al buffer, viene
	 * ricavato il chunk giusto e costruito l'array risultato così:
	 * |hash|type|unit|.
	 * 
	 * @param word
	 *            la parola che rappresenta il chunk
	 * @return ritorna un array di stringhe che contengono gli attributi
	 */
	public String[] getChunkAttributes(String word) {
		// si ricava il chunk
		Chunk current = buffer.get(word);
		String[] result = new String[3];
		result[0] = current.getHash();
		result[1] = current.getType();
		result[2] = current.getUnit();
		// si restituisce il risultato
		return result;
	}

	/**
	 * Metodo che dato un chunk permette di modificarne: -la parola: in questo
	 * caso il vecchio chunk viene cancellato e ne viene creato uno nuovo,
	 * meccanismo invisibile al codice client. -le definizioni: in questo caso
	 * viene eseguito un confronto tra le vecchie definizioni e quelle nuove e
	 * vengono effettuate le operazioni di aggiornamento necessarie.
	 * 
	 * Se la nuovo parola e quella originale sono uguali allora solo le
	 * definizioni vengono modificate.
	 * 
	 * @param word_original
	 *            parola del chunk originale
	 * @param word_new
	 *            nuova parola del chunk
	 * @param newDefinition
	 *            array di stringhe che contengono le definizioni da impostare
	 */
	public void modifyChunk(String word_original, String word_new,
			String[] newDefinition) {
		// si ricava il chunk
	}

	/**
	 * Metodo privato che ricava un chunk dalla corrispettiva parola, prima
	 * cercandolo nel buffer e in caso non sia presente, caricandolo da
	 * {@link #data}.
	 * 
	 * @param word
	 *            parola del chunk da restituire
	 * @return ritorna null se il chunk non è presente
	 */
	private Chunk getChunk(String word) {
		if (this.buffer.containsKey(word)) {
			return this.buffer.get(word);
		} else {
			return data.getChunkBySpecificWord(word);
		}
	}

	/**
	 * Metodo che crea un nuovo dizionario.
	 * 
	 * @param _path
	 *            percorso in cui verrà salvato il nuovo dizionario.
	 * @return ritorna true se le operazioni sono andate a buon fine, false in
	 *         caso di errore.
	 */
	public static boolean CreateDictionary(String _path) {
		// nuovo document
		Element root = new Element("ChunksDictionary");
		Document newD = new Document(root);
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		try {
			outputter.output(newD, new FileOutputStream(_path));
		} catch (IOException e) {
			// si ritorna false
			return false;
		}
		return true;
	}

}
