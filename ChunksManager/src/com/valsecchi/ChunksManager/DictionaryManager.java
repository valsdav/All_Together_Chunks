package com.valsecchi.ChunksManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
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
	private Map<String, String> buffer;
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
	 * Metodo che aggiunge un Chunk alla banca dati del dizionario.
	 * @param word parola del chunk da aggiungere
	 * @param type tipo del chunk da aggiungere
	 * @param unit unità del chunk da aggiungere
	 * @return ritorna True se il chunk è stato aggiunto correttamente
	 */
	public boolean addChunk(String word, String type, String unit) {
		// si crea un oggetto chunk da aggiungere
		Chunk newC = new Chunk(word, type, unit);
		// si aggiunge
		return data.addChunk(newC);
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
