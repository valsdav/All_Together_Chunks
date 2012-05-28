package com.valsecchi.ChunksManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.valsecchi.ChunksManager.Chunk;
import com.valsecchi.ChunksManager.Definition;

/**
 * La classe DictionaryData è il cuore dei dati dell'applicazione. Essa
 * gestisce, a basso livello, gli spostamenti di Chunks e definizioni, agendo
 * direttamente sulle lista dei Chunk e Definition. La classe fornisce i metodi
 * base per aggiungere, eliminare, modificare i dati. Inoltre la classe fornisce
 * il metodo {@link #refreshData()} che sincronizza e aggiorna i dati con il
 * file dizionario. Questa classe fornisce i metodi base di gestione del
 * dizionario alla classe DictionaryManager, che si presenta a un più alto
 * livello di astrazione e interagisce direttamente con il livello client del
 * dizionario.
 * 
 * @author Davide Valsecchi
 * @version v.0.0.0
 * @since v.0.0.0
 * @see com.valsecchi.ChunksManager.DictionaryManager
 * 
 */
public class DictionaryData {

	/**
	 * Mappa che contiene tutti gli oggetti Chunk indicizzati con la loro hash
	 */
	private Map<String, Chunk> chunksMap;
	/**
	 * Lista che contiene tutte le definizioni del dizionario
	 */
	private List<Definition> defsList;
	/**
	 * Mappa che contiene la lista di definizioni da eliminare per ogni chunk
	 * rappresentato dal codice hash. Le definizioni vengono raggruppate a
	 * seconda dell'hash
	 */
	private Map<String, List<Definition>> defsToDelete;
	private List<String> chunksToDelete;
	private boolean dictionaryLoaded = false;
	private Path dictPath;

	/**
	 * Costruttore che richiede la path del dizionario.
	 * 
	 */
	public DictionaryData(Path _path) {
		chunksMap = new HashMap<>();
		defsList = new ArrayList<>();
		defsToDelete = new HashMap<>();
		chunksToDelete = new ArrayList<>();
		dictPath = _path;
	}

	/**
	 * Metodo che carica in memoria il dizionario memorizzaro in
	 * {@link #dictPath}. E' importante che il client di questo metodo controlli
	 * precedentemente che il file dizionario esiste altrimenti il metodo
	 * rilancerà l'eccezione IOException.
	 * 
	 * @return ritorna True se il dizionario è stato caricato correttamente
	 * @throws IOException
	 *             l'eccezione viene lanciata se si hanno problemi nella lettura
	 *             del file dizionario.
	 */
	public boolean loadData() throws IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(dictPath.toFile());
		} catch (JDOMException e) {
			return false;
		}
		Element root = doc.getRootElement();
		// si ricava la lista di chunks
		List<Element> _chunks = root.getChildren("Chunks");
		for (Element chu : _chunks) {
			String hash, type, unit, word;
			hash = chu.getChildText("hash");
			word = chu.getChildText("chunk");
			type = chu.getChildText("type");
			unit = chu.getChildText("unit");

			Chunk newChunk = new Chunk(word,hash, type, unit);
			// si aggiunge alla mappa
			this.chunksMap.put(hash, newChunk);
		}
		// ora si ricavano le definizioni
		List<Element> _defs = root.getChildren("Definitions");
		for (Element def : _defs) {
			String _hash, definition;
			_hash = def.getChildText("hash");
			definition = def.getChildText("definition");
			Definition newDef = new Definition(_hash, definition);
			// si aggiunge alla lista
			this.defsList.add(newDef);
		}
		dictionaryLoaded = true;
		// il processo è completato
		return true;
	}

	/**
	 * Il metodo refreshData() permette la <i> sincronizzazione </i> del
	 * DictionaryData corrente con un altro DictionaryData. Questo metodo viene
	 * utilizzato solitamente per aggiornare i dati in un contesto in cui il
	 * file dizionario è condiviso tra diversi utenti: si ha così la necessità
	 * di scrivere i dati aggiunti, ma anche di aggiornare quelli già presenti.
	 * Inoltre viene eseguito un controllo su ogni chunk del dictionario passato
	 * come parametro per il refresh di quello corrente, in modo tale da
	 * verificare se esso deve essere eliminato definitivamente; lo stesso
	 * controllo viene applicato per le definizioni. N.B.: i chunk e le
	 * definizioni marcate come da eliminare non vengono eliminate dal
	 * DictionaryData di confronto, ma semplicemente non vengono presi in
	 * considerazione per il refresh; questo permette al DictionaryData corrente
	 * di mantenere il suo stato, perchè solo i dati aggiornati vengono
	 * aggiunti, e non quelli già eliminati in precedenza.
	 * 
	 * 
	 * @param data
	 *            viene richiesto come argomento un oggetto DictionaryData da
	 *            confrontare con quello corrente
	 */
	public boolean refreshData(DictionaryData data) {
		// si recupera una lista di tutti i chunk del dictionary esterno
		List<Chunk> externalChunks = data.getAllChunks();
		// array di boolean che memorizza i chunk trovati;
		boolean[] chunksFounded = new boolean[externalChunks.size()];
		// ora si inizia il confronto
		int index = 0;
		for (Chunk extC : externalChunks) {
			// variabile che indica se è stato trovato
			boolean founded = false;
			for (Chunk intC : this.getAllChunks()) {
				if (extC.getHash().equals(intC.getHash())) {
					founded = true;
					// si esce dal ciclo
					break;
				}
			}
			// si memorizza se è stato trovato
			chunksFounded[index] = founded;
			index += 1;
		}
		// ora si scorre l'array di boolean che indica quali chunks sono già
		// presenti
		for (int k = 0; k < chunksFounded.length; k++) {
			if (chunksFounded[k] == false) {
				// se il chunk non è stato trovato si controlla che non era da
				// eliminare
				if (this.getChunkMustBeRemoved(externalChunks.get(k)) == true) {
					// se era da eliminare allora non si considera e si continua
					// il ciclo
					continue;
				}
				// se non è da eliminare si aggiorna
				this.addChunk(externalChunks.get(k));
				// ora si inseriscono anche le definizioni
				this.addDefinitions(externalChunks.get(k).getHash(),
						data.getDefinitions(externalChunks.get(k)));
				// il controllo se le definizioni esistono già è compreso nel
				// metodo addDefinitions();
			} else {
				// se il chunk è già presente allora si controllano le sue
				// definizioni: se alcune sono da eliminare o no
				List<Definition> extDef = data.getDefinitions(externalChunks
						.get(k));
				// ora si controlla quali sono da eliminare
				boolean[] to_delete = this.getDefinitionsMustBeRemoved(
						externalChunks.get(k).getHash(), extDef);
				// quelle da eliminare in pratica non vengono eliminate da data,
				// ma solo dalla lista di quelle da aggiungere.
				for (int z = 0; z < to_delete.length; z++) {
					if (to_delete[z] == true) {
						// viene rimossa dalla lista la defiizione
						extDef.remove(z);
					}
				}
				// ora si aggiungono le definizioni. Il controllo se le
				// definizioni esistono già è compreso nel
				// metodo addDefinitions();
				this.addDefinitions(externalChunks.get(k).getHash(), extDef);
			}
		}
		// completata la sincronizzazione
		return true;
	}

	/**
	 * Metodo che riscrive il file dizionario partendo dai dati in memoria. Esso
	 * utilizza i comandi base della libreria jdom2 ricorstruendo un documento e
	 * salvandolo nella path passata dall'utente.
	 * 
	 * @param path
	 *            percorso in cui salvare il dizionario
	 * @return ritorna True se il dizionario è stato scritto correttamente
	 * @throws IOException
	 *             viene lanciata l'eccezione se ci sono problemi con la
	 *             scrittura del file
	 */
	public boolean writeData(Path path) throws IOException {
		// nuovo document
		Element root = new Element("ChunksDictionary");
		Document newD = new Document(root);
		// si aggiungono tutti gli elementi
		// nuovo document
		// ciclo per i chunk
		for (Chunk c : this.chunksMap.values()) {
			// nuovo chunk radice
			Element chunk = new Element("Chunks");
			// aggiunta degli elementi
			Element h = new Element("hash");
			h.setText(c.getHash());
			chunk.addContent(h);
			Element k = new Element("chunk");
			k.setText(c.getWord());
			chunk.addContent(k);
			Element t = new Element("type");
			t.setText(c.getType());
			chunk.addContent(t);
			Element u = new Element("unit");
			u.setText(c.getUnit());
			chunk.addContent(u);
			// si aggiunte a root
			root.addContent(chunk);
		}
		// ciclo per le definizioni
		for (Definition d : this.defsList) {
			// nuovo definition radice
			Element def = new Element("Definitions");
			// aggiunta elementi
			Element h = new Element("hash");
			h.setText(d.getHash());
			def.addContent(h);
			Element definit = new Element("definition");
			definit.setText(d.getText());
			def.addContent(definit);
			// si aggiunge a root
			root.addContent(def);
		}
		// ora si scrive il document
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		// scrittura
		try {
			outputter.output(newD, new FileOutputStream(path.toString()));
		} catch (IOException e) {
			// si rilancia l'eccezione
			throw e;
		}
		// se tutto è andato bene si ritorna true
		return true;
	}

	/**
	 * Metodo che aggiunge un Chunk ai dati, controllando che non sia già
	 * presente
	 * 
	 * @param chunk_to_add
	 *            chunk da aggiungere
	 * @return ritorna True se il chunk è stato aggiunto correttamente
	 */
	public boolean addChunk(Chunk chunk_to_add) {
		// si controlla prima che non esista già
		if (chunkExist(chunk_to_add) == false) {
			chunksMap.put(chunk_to_add.getHash(), chunk_to_add);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Metodo che rimuove un chunk dalla lista. N.B.: questo metodo provoca la
	 * rimozione anche di tutte le definizioni associate. Inoltre l'hash del
	 * chunk viene memorizzato, in modo tale che al refresh del dictionary si
	 * verificherà la cancellazione del chunk se ancora presente e verranno
	 * anche cancellate tutte le relative definizioni.
	 * 
	 * @param hash
	 *            hash del chunk da rimuovere
	 * @return ritorna True se le operazioni vanno a buon fine
	 */
	public boolean removeChunk(String hash) {
		// l'esistenza del chunk è controllata in getChunk()
		Chunk current = this.getChunk(hash);
		if (current != null) {
			this.chunksMap.remove(current);
			// si aggiunge l'hash alla lista dei chunk eliminati
			this.chunksToDelete.add(current.getHash());
			// ora si rimuovono le definizioni
			this.removeAllDefinitions(hash);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo che rimuove un chunk dalla lista. N.B.: questo metodo provoca la
	 * rimozione anche di tutte le definizioni associate. Inoltre l'hash del
	 * chunk viene memorizzato, in modo tale che al refresh del dictionary si
	 * verificherà la cancellazione del chunk se ancora presente e verranno
	 * anche cancellate tutte le relative definizioni.
	 * 
	 * @param chunk chunk da rimuovere
	 * @return ritorna True se le operazioni vanno a buon fine
	 */
	public boolean removeChunk(Chunk chunk){
		//si controlla se esiste
		if(this.chunkExist(chunk)){
			this.chunksMap.remove(chunk);
			// si aggiunge l'hash alla lista dei chunk eliminati
			this.chunksToDelete.add(chunk.getHash());
			// ora si rimuovono le definizioni
			this.removeAllDefinitions(chunk.getHash());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Metodo che aggiunge le difinitioni di un chunk identificato dal suo
	 * codice hash.
	 * 
	 * @param hash
	 *            codice hash delle definizioni
	 * @param defs
	 *            lista di definizioni da aggiungere.
	 * @return ritorna un array di boolean che indica quali definizioni sono
	 *         state aggiunte. Se è False, vuol dire che la definizione non
	 *         esisteva ed è stata aggiunta, se True significa che la
	 *         definizione esisteva già;
	 * 
	 */
	public boolean[] addDefinitions(String hash, List<Definition> defs) {
		// prima si ricava un array di boolean per controllare se esistono o no
		boolean[] exists = this.definitionsExist(hash, defs);
		for (int i = 0; i < defs.size(); i++) {
			if (exists[i] == false) {
				// si aggiunge la definizione
				this.defsList.add(defs.get(i));
			}
		}
		return exists;
	}

	/**
	 * Metodo che rimuove tutte le definizioni legate a un certo codice hash
	 * 
	 * @param hash
	 *            codice hash che identifica le definizioni da rimuovere
	 */
	public void removeAllDefinitions(String hash) {
		List<Definition> list = this.getDefinitions(hash);
		// ora si rimuovono
		for (Definition d : list) {
			this.defsList.remove(d);
		}
	}

	/**
	 * Metood che rimuove le definizioni del paramentro defs, se presenti,
	 * aventi hashcode uguale a quello passato come parametro. Questo metodo
	 * rimuove solo quindi le definizioni solo le legate allo stesso chunk.
	 * Inoltre le definizioni eliminate verranno aggiunte alla mappa
	 * {@link #defsToDelete}, in modo tale che al refresh del dictionary, le
	 * definizioni verranno oppurtunamente cancellate e aggiornate.
	 * 
	 * @param hash
	 *            codice hash delle definizioni
	 * @param defs_to_delete
	 *            lista di definizioni da eliminare
	 * @return Viene restituito un array di boolean che rappresenta quali
	 *         definizioni sono state eliminate e quali no
	 */
	public boolean[] removeDefinitions(String hash, List<Definition> defs_to_delete) {
		// si ricava se le definizioni esistono
		boolean[] exists = this.definitionsExist(hash, defs_to_delete);
		// lista per inserire le definizioni eliminare
		List<Definition> toDelete = new ArrayList<>();
		for (int i = 0; i < defs_to_delete.size(); i++) {
			if (exists[i] == true) {
				// allora si elimina dalla lista
				this.defsList.remove(defs_to_delete.get(i));
				// si aggiunge alla lista da eliminare
				toDelete.add(defs_to_delete.get(i));
			}
		}
		// ora si controlla se c'è già un elemento con la stessa hash in
		// this.defsToDelete
		if (this.defsToDelete.containsKey(hash)) {
			// se è già contenuto si aggiungono
			this.defsToDelete.get(hash).addAll(toDelete);
		} else {
			// se non c'è si crea
			this.defsToDelete.put(hash, toDelete);
		}
		return exists;
	}

	/**
	 * Metodo che restituisce un chunk dato un certo hash. Se il chunk non
	 * esiste si restituisce null.
	 * 
	 * @param hash
	 * @return
	 */
	public Chunk getChunk(String hash) {
		// si controlla se il chunk esiste
		if (chunkExist(hash) == true) {
			return chunksMap.get(hash);
		} else {
			return null;
		}
	}

	/**
	 * Il metodo effettua una ricerca fra i chunks come {@link #getChunksByWord(String)}, ma restituisce solo il chunk 
	 * la cui parola è esattamente uguale al parametro.
	 * @param word parola del chunk da cercare
	 * @return ritorna il chunk se trovato, se no null
	 */
	public Chunk getChunkBySpecificWord(String word){
		Chunk result = null;
		for (Chunk ck : this.chunksMap.values()) {
			//da notare l'uso di equals e non contains
			if (ck.getWord().equals(word)) {
				result = ck;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Metodo public che effettua una ricerca tra i vari chunk e restituisce
	 * quelli che contengono il pattern specificato. La ricerca viene effettuata
	 * solo sulla parola che caratterizza il chunk. Il metodo viene lasciato
	 * public perchè è di comodo utilizzo e accessibile dall'esterno senza
	 * riferimenti ai dati interni.
	 * La ricerca non controlla l'identità dell'eguaglianza ma solo se pattern è contenuto
	 * 
	 * @param pattern
	 *            filtro da confrontare con i chunks per la ricerca. Il filtro
	 *            può contenere anche espressioni regolari
	 * @return ritorna una lista di chunks che corrispondono ai criteri di
	 *         ricerca.
	 */
	public List<Chunk> getChunksByWord(String pattern) {
		// lista in cui inserire i risultati
		List<Chunk> results = new ArrayList<>();
		for (Chunk ck : this.chunksMap.values()) {
			if (ck.getWord().contains(pattern)) {
				results.add(ck);
			}
		}
		return results;
	}

	/**
	 * Metodo private che effettua una ricerca tra i chunk passati come
	 * parametro e restituisce quelli che sono del tipo specificato.
	 * 
	 * @param listChunk
	 *            lista di chunk tra cui cercare
	 * @param type
	 *            il tipo di chunk da cercare
	 * @return ritorna una lista di chunk che corrispondono ai criteri di
	 *         ricerca.
	 */
	private List<Chunk> getChunksByType(Collection<Chunk> listChunk, String type) {
		// si ricercano i chunk per tipo tra quelli nella lista in argomento
		List<Chunk> results = new ArrayList<>();
		for (Chunk c : listChunk) {
			// il tipo non deve corrispondere perfettamente, cosi si può
			// scrivere più di un tipo
			if (c.getType().matches(type)) {
				results.add(c);
			}
		}
		return results;
	}

	/**
	 * Metodo private che effettua una ricerca tra i chunk passati come
	 * parametro e restituisce quelli che sono del specificato.
	 * 
	 * @param listChunk
	 *            lista di chunk tra cui cercare
	 * @param unit
	 *            l'unità da cercare
	 * @return ritorna una lista di chunk che corrispondono ai criteri di
	 *         ricerca.
	 */
	private List<Chunk> getChunksByUnit(Collection<Chunk> listChunk, String unit) {
		// si ricercano i chunks per unita tra quelli in lista
		List<Chunk> results = new ArrayList<>();
		for (Chunk ck : listChunk) {
			// l'unita deve corrispondere perfettamente
			if (ck.getType().equals(unit)) {
				results.add(ck);
			}
		}
		return results;
	}

	/**
	 * Metodo pubblico che ricerca tra i chunks specificando tre
	 * parametri:pattern,type e unit. Questo metodo apponggiandosi a
	 * {@link #getChunksByWord(String)},
	 * {@link #getChunksByType(Collection, String)},
	 * {@link #getChunksByUnit(Collection, String)}, ricerca i chunk
	 * corrispondenti. *
	 * 
	 * @param pattern
	 *            si riferisce alla parola del chunk da cercare
	 * @param type
	 *            tipo del chunk da cercare
	 * @param unit
	 *            unità del chunk da cercare
	 * @return ritorna una lista di chunk che corrispondono ai criteri di
	 *         ricerca.
	 */
	public List<Chunk> getChunksWithArguments(String pattern, String type,
			String unit) {
		// prima si controlla se il pattern è nullo
		if (pattern.equals("")) {
			// allora si controlla il type
			if (type.equals("")) {
				// ora si controlla l'unità
				if (unit.equals("")) {
					// allora si restituiscono tutti i chunk
					return this.getAllChunks();
				} else {
					// allora si ricerca solo per unità passando tutti i chunks
					return this.getChunksByUnit(this.chunksMap.values(), unit);
				}
			} else {
				// se il tipo non nullo
				// si ricerca per tipo
				List<Chunk> temp1 = this.getChunksByType(
						this.chunksMap.values(), type);
				// si controlla l'unit
				if (unit.equals("")) {
					// si esce
					return temp1;
				} else {
					// si ricerca anche per unità
					return this.getChunksByUnit(temp1, unit);
				}
			}
		} else {
			// se il pattern non è nullo si ricavano prima tutti i chunks con un
			// nome
			List<Chunk> temp = this.getChunksByWord(pattern);
			// poi si controlla come sopra
			if (type .equals("")) {
				// ora si controlla l'unità
				if (unit .equals("")) {
					// allora si restituiscono i chunks in temp
					return temp;
				} else {
					// allora si ricerca solo per unità passando temp
					return this.getChunksByUnit(temp, unit);
				}
			} else {
				// se il tipo non nullo
				// si ricerca per tipo
				List<Chunk> temp2 = this.getChunksByType(temp, type);
				// si controlla l'unit
				if (unit .equals("")) {
					// si ritorna temp2
					return temp2;
				} else {
					// si ricerca anche per unità
					return this.getChunksByUnit(temp2, unit);
				}
			}
		}
	}

	/**
	 * Metodo che restituisce tutti i chunk della lista.
	 * 
	 * @return Restituisce tutti i chunks della lista
	 */
	public List<Chunk> getAllChunks() {
		// si restituisce tutta la lista di chunk
		List<Chunk> results = new ArrayList<>();
		for(Chunk c :this.chunksMap.values()){
			results.add(c);
		}
		return results;
	}

	/**
	 * Metodo che controlla l'esistenza di un chunk
	 * 
	 * @param chunk
	 *            chunk di cui controllare l'esistenza
	 * @return
	 */
	public boolean chunkExist(Chunk chunk) {
		return chunkExist(chunk.getHash());
	}

	/**
	 * Metodo che controlla l'esistenza di un chunk
	 * 
	 * @param hash
	 *            codice hash per il confronto
	 * @return restituisce True se il chunk è in lista
	 */
	public boolean chunkExist(String hash) {
		return chunksMap.containsKey(hash);
	}

	/**
	 * Metodo che restituisce un array di Definition che hanno un certo codice
	 * hash. Differente da {@link #getDefinition(String)} poichè questo metodo
	 * accetta un argomento di tipo Chunk. Almeno una definizione è sempre presente.
	 * 
	 * @param chunk
	 *            Chunk per la ricerca.
	 * @return
	 */
	public List<Definition> getDefinitions(Chunk chunk) {
		return getDefinitions(chunk.getHash());
	}

	/**
	 * Metodo che restituisce un array di Definition che hanno un certo codice
	 * hash. Almeno una definizione è sempre presente.
	 * 
	 * @param hash
	 *            codice hash per il confronto
	 * @return
	 */
	public List<Definition> getDefinitions(String hash) {
		// si ricercano le definizioni
		List<Definition> defs = new ArrayList<>();
		for (Definition d : this.defsList) {
			if (d.getHash().equals(hash)) {
				defs.add(d);
			}
		}
		return defs;
	}

	/**
	 * Metodo che controlla l'esistenza delle definizioni di un chunk. E'
	 * fondamentale che le definizioni appertengano tutte a un solo chunk,
	 * quello con l'hash passata come argomento
	 * 
	 * @param defs
	 *            lista di definizioni da controllare
	 * @param hash
	 *            hash delle definizioni da cercare
	 * @return restituisce un array di boolean che indica o meno l'esistenza di
	 *         ognuna delle definizioni nella lista.
	 */
	public boolean[] definitionsExist(String hash, List<Definition> defs) {
		// si controlla se la lista di definizioni esiste
		// devono essere tutte dello stesso chunk
		List<Definition> founded = this.getDefinitions(hash);
		boolean[] listB = new boolean[defs.size()];
		int index = 0;
		for (Definition d1 : defs) {
			boolean is_present = false;
			for (Definition f1 : founded) {
				if (d1.getText().equals(f1.getText())) {
					is_present = true;
				}
			}
			listB[index] = is_present;
			index += 1;
		}
		return listB;
	}

	/**
	 * Metodo che controlla se il chunk rappresentato dall'hash passato come
	 * parametro deve essere eliminato al refresh o no.
	 * 
	 * @param hash
	 *            codice hash che identifica il chunk
	 * @return restituisce True se il chunk deve essere eliminato
	 */
	public boolean getChunkMustBeRemoved(String hash) {
		return this.chunksToDelete.contains(hash);
	}

	/**
	 * Metodo che controlla se il chunk passato come parametro deve essere
	 * eliminato al refresh o no.
	 * 
	 * @param chunk
	 *            chunk da controllare
	 * @return restituisce True se il chunk deve essere eliminato
	 */
	public boolean getChunkMustBeRemoved(Chunk chunk) {
		return this.getChunkMustBeRemoved(chunk.getHash());
	}

	/**
	 * Metodo che controlla se le definizioni passate come parametro e facenti
	 * riferimento a un certo hash, siano da eliminare o no. Restituisce True se
	 * la definione è da eliminare.
	 * 
	 * @param hash
	 *            codice hash che identifica le definizioni
	 * @param defs
	 *            array di definitioni da controllare
	 * @return restituisce un array di boolean che indica quali definizioni
	 *         siano da eliminare. Restituisce True se la definione è da
	 *         eliminare.
	 */
	public boolean[] getDefinitionsMustBeRemoved(String hash, Definition[] defs) {
		// si ricavano le definizioni da eliminare memorizzate per lo specifico
		// hash.
		List<Definition> toDelete = this.defsToDelete.get(hash);
		boolean[] to_del = new boolean[toDelete.size()];
		int index = 0;
		for (Definition d1 : toDelete) {
			boolean founded = false;
			for (Definition d2 : defs) {
				if (d2.getText().equals(d1.getText())) {
					founded = true;
				}
			}
			to_del[index] = founded;
			index += 1;
		}
		// si restituisce il risultato
		return to_del;
	}

	/**
	 * Overloading del metodo
	 * {@link #getDefinitionsMustBeRemoved(String, Definition[])}
	 */
	public boolean[] getDefinitionsMustBeRemoved(String hash,
			List<Definition> defs) {
		return this.getDefinitionsMustBeRemoved(hash,
				(Definition[]) defs.toArray());
	}

	/**
	 * Proprietà che indica se il dizionario è stato caricato
	 * 
	 * @return True se il dizionario è stato caricato correttamente in memoria.
	 */
	public boolean isDictionaryLoaded() {
		return this.dictionaryLoaded;
	}

	/**
	 * Metodo che svuota la memoria e cancella tutti i chunk e le definizioni
	 * memorizzate
	 */
	public void clear() {
		chunksMap.clear();
		defsList.clear();
		defsToDelete.clear();
		chunksToDelete.clear();
	}

}
