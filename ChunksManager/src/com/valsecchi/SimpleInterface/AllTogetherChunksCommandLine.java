package com.valsecchi.SimpleInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.valsecchi.ChunksManager.*;

import static java.lang.System.out;

/**
 * Classe che fornisce una semplicissima interfaccia a linea di comando per
 * ChunksManager.
 * 
 * @author Davide Valsecchi
 * @version v.0.0.0
 * @since v.0.0.0
 */
public class AllTogetherChunksCommandLine {

	/**
	 * Oggetto DictionaryManager indispensabile per le operazioni sul dizionario
	 */
	private static DictionaryManager dictionary;
	/**
	 * Variabile che memorizza il comando help
	 */
	private static final String HELP = "help";
	/**
	 * Vabiabile che memorizza il comando open, che serve per caricare il
	 * dizionario
	 */
	private static final String OPEN_DICTIONARY = "open";
	/**
	 * Variabile che memorizza il comando create
	 */
	private static final String CREATE = "create";
	/**
	 * Variabile che memorizza il comando find
	 */
	private static final String FIND = "find";
	/**
	 * Variabile che memotizza il comando addchunk
	 */
	private static final String ADD_CHUNK = "addchunk";
	/**
	 * Varibile shorcut per ADD_CHUNK
	 */
	private static final String ADD = "add";
	/**
	 * Variabile che memorizza il comando deletechunk
	 */
	private static final String DELETE_CHUNK = "deletechunk";
	/**
	 * Variabile shortcut per DELETE_CHUNK
	 */
	private static final String DELETE = "delete";
	/**
	 * Variabile che memorizza il comando modifydefinition
	 */
	private static final String MODIFY_CHUNK = "modifychunk";
	/**
	 * Variabile shortcut per MODIFY_CHUNK
	 */
	private static final String MODIFY = "modify";
	/**
	 * Variabile che memorizza il comando save
	 */
	private static final String SAVE = "save";
	/**
	 * Variabile che memorizza il comando refresh
	 */
	private static final String REFRESH = "refresh";
	/**
	 * Variabile che memorizza il comando mode
	 */
	private static final String SETMODE = "setmode";
	/**
	 * Variabile shortcut per setmode
	 */
	private static final String MODE = "mode";
	/**
	 * Variabile che memorizza il comando definition
	 */
	private static final String DEFIN = "definition";
	/**
	 * Variabile shotcut per DEFIN
	 */
	private static final String DEF = "def";
	/**
	 * Variabile che memorizza il comando undochanges
	 */
	private static final String UNDO_ALL = "undoallchanges";
	/**
	 * Variabile che memorizza il comando exit
	 */
	private static final String EXIT = "exit";
	/**
	 * Variabile che indica se un dizionario � stato caricato
	 */
	private static boolean dictLoaded = false;

	/**
	 * Array di stringhe che contiene i vari comandi che saranno poi inseriti in
	 * {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_LIST = { HELP, OPEN_DICTIONARY,
			SAVE, CREATE, REFRESH, UNDO_ALL, FIND, ADD_CHUNK, ADD,
			MODIFY_CHUNK, MODIFY, DELETE_CHUNK, DELETE, DEFIN, DEF, SETMODE,
			MODE, EXIT };
	/**
	 * Array di stringe che contiene le istruzioni dei vari comandi che saranno
	 * poi inseriti in {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_INSTR = {
			"help +command: it displays instructions for command"
					+ "\n-->  help >>> help: it displays all commands available with instructions",
			"open +path: it loads the Chunks Dictionary in the 'path' you have written",
			"save: it saves and refreshes the current dictionary",
			"create +path: it creates a new dictionary in the given 'path'",
			"refresh: it only refreshes the current dictionary if the mode is ONLINE",
			"undoallchanges: it deletes all the changes you have done since dictionary was loaded",
			"find +chunk: it searches for a chunk that contains the word 'chunk'"
					+ "\n-->  find >>> find: it displays a prompt to search for a chunk with further parameters",
			"addchunk: it displays a prompt to insert datas to add a new chunk;\n              "
					+ "(if the chunk already exists it refresh the definitions)",
			"add: shortcut for command 'addchunk'",
			"modifychunk: it displays a prompt to insert datas to modify an existing chunk",
			"modify: shortcut for command 'modifychunk'",
			"deletechunk +chunk: it deletes the 'chunk' you have written",
			"delete: shortcut for command 'deletechunk",
			"definition +chunk: diplays the definitions of the given chunk",
			"def: shortcut for command 'definition'",
			"setmode +offline/online: it changes the mode of the program, you can insert offline or online. (Default mode is online)"
					+ " to disable or enable the refresh of a shared dictionary"
					+ "\n-->  setmode >>> setmode: without parameters it shows the current mode of the program",
			"mode: shotcut for command 'setmode'",
			"exit: program will terminate" };
	/**
	 * Mappa che incapsula tutti i comandi disponibili con relativa
	 * documentazione.
	 */
	private static Map<String, String> COMMANDS_MAP = new HashMap<String, String>();
	/**
	 * Variabile che ferma il loop del programma
	 */
	private static boolean canContinue = true;

	/**
	 * Loop continuo del programma che interpreta ed esegue i comandi
	 */
	public static void main(String[] args) throws IOException {
		// si caricano i comandi
		setupInstructions();
		// viene creato il lettore dell'input della console
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		// messaggio di benvenuto
		out.println("Welcome to All Together Chunks!\nEnter a command to start "
				+ "or enter help for a list of all commands available...");
		// lettura argomnti da riga di comando
		if (args != null && args.length != 0 && !args[0].equals("")) {
			// se c'� una path valida si apre il dizionario
			Path file = Paths.get(args[0]);
			if (Files.exists(file)) {
				// prima si fa il refresh
				if (dictionary != null && dictionary.isLoaded()) {
					out.println("Dictionary saving and refreshing in progress...");
					dictionary.saveDictionary();
					out.println("Dictionary  successfully saved and refreshed!");
				}
				// allora si apre il dizionario
				dictionary = new DictionaryManager(
						DictionaryManager.ONLINE_MODE, Paths.get(args[0])
								.getFileName().toString(), file);
				// ora si carica
				out.println("Dictionary loading in progress..");
				dictionary.loadDictionary();
				out.println("Dictionary loaded successfully!");
				// si imposta che il dizionario � stato caricato
				dictLoaded = true;
			}
		}
		// comando inserito
		String command = "";
		// inizio loop
		while (canContinue) {
			out.println();
			out.print(">>>");
			command = reader.readLine();
			// se non sono stati inseriti comandi
			if (command.equals("")) {
				out.println("Please write a command or help to start...");
				continue;
			}
			// si divide il comando nelle sue parti
			StringTokenizer tok = new StringTokenizer(command);
			// si ricava il primo comando
			String mainCommand = tok.nextToken();
			// si ricava il resto del comando
			StringBuilder build = new StringBuilder();
			int k = tok.countTokens();
			while (tok.hasMoreTokens()) {
				if (k > 1) {
					build.append(tok.nextToken() + " ");
				} else {
					build.append(tok.nextToken());
				}
				k--;
			}
			// argomenti del comando
			String arg = build.toString().toLowerCase();
			// ora abbiamo i comandi incapsulati
			// si controlla il comando primo comando scelto
			switch (mainCommand) {
			case HELP: {
				// si controlla quale comando segue help
				if (!arg.equals("")) {
					// si ricava il comando di cui si vuole l'help
					if (COMMANDS_MAP.containsKey(arg)) {
						out.println("-->  " + arg + " >>> "
								+ COMMANDS_MAP.get(arg));
					} else {
						// se non c'� il comando si scrive
						out.println("Command not founded!");
					}

				} else {
					// allora bisogna scrivere tutti i comandi a schermo con
					// relative istruzioni
					for (int i = 0; i < COMMANDS_MAP.size(); i++) {
						out.println("-->  "
								+ COMMANDS_MAP.keySet().toArray()[i] + " >>> "
								+ COMMANDS_MAP.values().toArray()[i]);
					}
				}
				break;
			}
			case OPEN_DICTIONARY: {
				// si ricava la seconda parte del comando se no si inserisce
				// errore
				if (!arg.equals("")) {
					// si ricava la path
					Path file;
					try {
						file = Paths.get(arg);
					} catch (Exception e) {
						out.println("Invalid path!");
						continue;
					}
					// si controlla che esiste
					if (Files.exists(file)) {
						// prima si deve salvare
						// si fa il refresh
						if (dictionary != null && dictionary.isLoaded()) {
							out.println("Dictionary saving and refreshing in progress...");
							dictionary.saveDictionary();
							out.println("Dictionary  successfully saved and refreshed!");
						}
						// esiste si carica il dizionario
						// di default � in modalit� online
						dictionary = new DictionaryManager(
								DictionaryManager.ONLINE_MODE, file
										.getFileName().toString(), file);
						// ora si carica
						out.println("Dictionary loading in progress..");
						dictionary.loadDictionary();
						out.println("Dictionary loaded successfully!");
						// si imposta che il dizionario � stato caricato
						dictLoaded = true;
					} else {
						out.println("File not founded!");
					}
				} else {
					out.println("Please enter 'open +path' to load a dictionary...");
				}
				break;
			}
			case CREATE: {
				if (arg.equals("")) {
					out.println("Please try again and insert a valid path...");
					continue;
				}
				if (DictionaryManager.CreateDictionary(arg)) {
					out.println("New dictionary successfully created in: "
							+ arg);
				} else {
					out.println("Error! Try again");
				}
				// si crea un nuovo dizionario
				break;
			}
			case FIND: {
				// si contralla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary. Please open a dictionary with 'open +path'...");
					continue;
				}
				// si controlla se ci sono argomenti.
				// se c'� argomenti � il pattern
				String pattern, type, unit;
				if (arg.equals("")) {
					// si richiede la parola
					out.print("--> pattern:  ");
					pattern = reader.readLine();
					out.print("--> type:  ");
					type = reader.readLine();
					out.print("--> unit:  ");
					unit = reader.readLine();
				} else {
					pattern = arg;
					type = "";
					unit = "";
				}
				// si ricavano i chunk trovati
				List<String> results = dictionary
						.findChunk(pattern, type, unit);
				// si mostrano
				if (results == null || results.size() == 0) {
					out.println("No Chunk Matches...");
					continue;
				}
				for (String r : results) {
					// si ricavano gli attributi del chunk
					String[] attr = dictionary.getChunkAttributes(r);
					out.println("    -- " + r + "    --type: " + attr[1]
							+ "    --unit: " + attr[2]);
				}
				out.println("N� of Chunks:  " + results.size());
				break;
			}
			case ADD_CHUNK:
			case ADD: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// si scrive il prompt
				String chunkWord;
				if (!arg.equals("")) {
					out.println("--> chunk:  " + arg);
					chunkWord = arg;
				} else {
					out.print("--> chunk:  ");
					chunkWord = reader.readLine().toLowerCase();
				}
				if (chunkWord.equals("")) {
					out.println("Please try again and insert a chunk...");
					continue;
				}
				out.print("--> type:  ");
				String type = reader.readLine().trim().toLowerCase();
				out.print("--> unit:  ");
				String unit = reader.readLine().trim().toLowerCase();
				out.print("--> please write at least one definition (write separated by ;):  ");
				String definitions = reader.readLine();
				if (definitions.equals("")) {
					out.println("Please try again and insert at least one definition...");
					continue;
				}
				List<String> defs = new ArrayList<>();
				StringTokenizer tok2 = new StringTokenizer(definitions, ";");
				while (tok2.hasMoreTokens()) {
					defs.add(tok2.nextToken().trim().toLowerCase());
				}
				// ora si crea il nuovo chunk
				if (dictionary.addChunk(chunkWord, type, unit, defs)) {
					// si dice che � stato aggiunto
					out.println("Chunk: " + chunkWord + " successfully added!");
				} else {
					out.println("Chunk: " + chunkWord
							+ " already in dictionary, definitions refreshed!");
				}
				break;
			}
			case MODIFY_CHUNK:
			case MODIFY: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// si scrive il prompt
				String chunkWord;
				if (!arg.equals("")) {
					out.println("--> chunk to change:  " + arg);
					chunkWord = arg;
				} else {
					out.print("--> chunk to change:  ");
					chunkWord = reader.readLine().toLowerCase();
				}
				if (chunkWord.equals("")) {
					out.println("Please try again and insert a chunk...");
					continue;
				}
				out.print("--> new chunk (leave it blanck if you want to change only definitions):  ");
				String newChunk = reader.readLine().toLowerCase();

				out.print("--> please write at least one definition (write separated by ;):");
				String definitions = reader.readLine();
				if (definitions.equals("")) {
					out.println("Please try again and insert at least one definition...");
					continue;
				}
				List<String> defs = new ArrayList<>();
				StringTokenizer tok2 = new StringTokenizer(definitions, ";");
				while (tok2.hasMoreTokens()) {
					defs.add(tok2.nextToken().trim().toLowerCase());
				}
				// ora si chima il dizionario
				if (dictionary.modifyChunk(chunkWord, newChunk, defs)) {
					out.println("Chunk: " + chunkWord
							+ " modified successfully to: " + newChunk + "!");
					continue;
				} else {
					// se � stato ritornato false significa che il chunk non
					// esiste
					// si chiede se aggiungerlo
					out.println("Chunk: " + chunkWord + " not exists!");
					out.print("Do you want to add it? (y/n):  ");
					String answer = reader.readLine();
					if (answer.equals("") || answer.equals("n")) {
						out.println("Chunk: " + chunkWord + " not added!");
					} else if (answer.equals("y")) {
						out.print("--> type:  ");
						String type = reader.readLine().trim().toLowerCase();
						out.print("--> unit:  ");
						String unit = reader.readLine().trim().toLowerCase();
						// si aggiunge
						dictionary.addChunk(chunkWord, type, unit, defs);
						out.println("Chunk: " + chunkWord
								+ " added successfully!");
					}
				}
				break;
			}
			case DELETE_CHUNK:
			case DELETE: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				if (arg.equals("")) {
					out.println("Please insert a valid chunk to delete...");
					continue;
				}
				if (dictionary.removeChunk(arg)) {
					out.println("Chunk: " + arg + " deleted successfully!");
				} else {
					out.println("Chunk: " + arg
							+ " doesn't exist! Please try again...");
				}
				break;
			}
			case DEFIN:
			case DEF: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				String wordToSearch = "";
				if (!arg.equals("")) {
					// si ricava la parola di cui si vuole la definizione
					wordToSearch = arg;
				} else {
					out.println("Missing word! Write 'definition +chunk'");
					continue;
				}
				// si ricavano le definizioni
				List<String> defs = dictionary.getDefinitions(wordToSearch);
				if (defs != null) {
					// prima si scrivono le caratteristiche del chunk
					String[] attr = dictionary.getChunkAttributes(wordToSearch);
					out.println("   >Chunk:   " +wordToSearch +"    --type: " + attr[1]
							+ "    --unit: " + attr[2]);
					out.println("   >Definitions:");
					for (String s : defs) {
						out.println("    -- " + s);
					}
				} else {
					out.println("Chunk: " + arg + " not exists!");
				}
				break;
			}
			case SAVE: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// si fa il refresh
				if (dictionary != null && dictionary.isLoaded()) {
					out.println("Dictionary saving and refreshing in progress...");
					dictionary.saveDictionary();
					out.println("Dictionary  successfully saved and refreshed!");
				}
				break;
			}
			case REFRESH: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// controllo modalit�
				if (dictionary.getMode() == DictionaryManager.OFFLINE_MODE) {
					// allora si esce
					out.println("You cannot use this command! Dictionary is now set to OFFLINE mode.\n"
							+ " To use command 'refresh' please set first the mode ONLINE with the command 'setmode'");
					continue;
				}
				// si fa il refresh
				if (dictionary != null && dictionary.isLoaded()) {
					out.println("Dictionary refreshing in progress...");
					dictionary.refreshDictionary();
					out.println("Dictionary successfully refreshed !");
				}
				break;
			}
			case UNDO_ALL: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// si chiede prima conferma
				out.print("Are you sure to delete all the changes you have made during this session? (y/n)");
				String ans = reader.readLine();
				if (ans.equals("y")) {
					// allora si annullano le modifiche
					if (dictionary.undoChanges()) {
						out.println("Changes successfully deleted!");
					} else {
						out.println("Error! Please try again...");
					}
				} else if (ans.equals("n")) {
					out.println("Operation cancelled!");
				} else {
					out.println("Please try again and insert a valid answer...");
				}
				break;
			}
			case SETMODE:
			case MODE: {
				// si controlla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a "
							+ "dictionary.\nPlease open a dictionary with 'open +path'...");
					continue;
				}
				// ora si controlla il valore passato
				switch (arg) {
				case "offline":
					if (dictionary.changeMode(DictionaryManager.OFFLINE_MODE)) {
						out.println("Mode successfully changed to OFFLINE!");
					} else {
						out.println("Mode already OFFLINE!");
					}
					break;
				case "online":
					if (dictionary.changeMode(DictionaryManager.ONLINE_MODE)) {
						out.println("Mode successfully changed to ONLINE!");
					} else {
						out.println("Mode already ONLINE!");
					}
					break;
				case "":
					int m = dictionary.getMode();
					if (m == DictionaryManager.OFFLINE_MODE) {
						out.println("Current mode: OFFLINE");
					} else {
						out.println("Current mode: ONLINE");
					}
					break;
				default:
					out.println("Please insert offline/online. Leave 'setmode' blank "
							+ "to show the current mode...");
					break;
				}
				break;
			}
			case EXIT: {
				// prima di uscire si salva il dizionario
				if (dictionary != null && dictionary.isLoaded()) {
					out.println("Dictionary saving in progress...");
					dictionary.saveDictionary();
					out.println("Dictionary saved successfully!");
				}
				out.println("Goodbye");
				// si esce
				System.exit(0);
				break;
			}
			default:
				out.println("Command not founded!");
				break;
			}
		}
	}

	/**
	 * Il metodo inserisce in {@link #COMMANDS_MAP} tutti i comandi con relative
	 * istruzioni
	 */
	private static void setupInstructions() {
		for (int i = 0; i < COMMANDS_LIST.length; i++) {
			COMMANDS_MAP.put(COMMANDS_LIST[i], COMMANDS_INSTR[i]);
		}
	}
}
