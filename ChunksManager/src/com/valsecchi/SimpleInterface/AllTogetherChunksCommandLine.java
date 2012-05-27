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
import java.io.*;
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
	 * Oggetto DictionaryManager indispensanile per le operazioni sul dizionario
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
	 * Variabile che memorizza il comando find
	 */
	private static final String FIND = "find";
	/**
	 * Variabile che memorizza il comando definition
	 */
	private static final String DEFIN = "definition";
	/**
	 * Variabile che memorizza il comando exit
	 */
	private static final String EXIT = "exit";
	/**
	 * Variabile che indica se un dizionario è stato caricato
	 */
	private static boolean dictLoaded = false;

	/**
	 * Array di stringhe che contiene i vari comandi che saranno poi inseriti in
	 * {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_LIST = { HELP, OPEN_DICTIONARY,
			FIND,DEFIN, EXIT };
	/**
	 * Array di stringe che contiene le istruzioni dei vari comandi che saranno
	 * poi inseriti in {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_INSTR = {
			"help +command: displays instructions for command",
			"open +path:loads the Chunks Dictionary in path",
			"find +word +type +unit: searches a chunk that contains that word, that it's of that type and unit",
			"definition +word: diplays the definitions of the given word",
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

		out.println("Welcome to All Together Chunks!\nEnter a command to start "
				+ "or enter help for a list of all commands available...");

		// viene creato il lettore dell'input della console
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		// comando inserito
		String command = "";
		// inizio loop
		while (canContinue) {
			out.print(">>>");
			command = reader.readLine();
			// si divide il comando nelle sue parti
			StringTokenizer tok = new StringTokenizer(command);
			List<String> cmds = new ArrayList<>();
			while (tok.hasMoreTokens()) {
				cmds.add(tok.nextToken());
			}
			// ora abbiamo i comandi incapsulati
			// si controlla il comando primo comando scelto
			switch (cmds.get(0)) {
			case HELP: {
				// si controlla quale comando segue help
				if (cmds.size() > 1) {
					// si ricava il comando di cui si vuole l'help
					String cmd2 = cmds.get(1);
					if (COMMANDS_MAP.containsKey(cmd2)) {
						out.println("-->  " + cmd2 + " >>> "
								+ COMMANDS_MAP.get(cmd2));
					} else {
						// se non c'è il comando si scrive
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

			}
			case OPEN_DICTIONARY: {
				// si ricava la seconda parte del comando se no si inserisce
				// errore
				if (cmds.size() > 1) {
					// si ricava il comando di cui si vuole l'help
					String cmd2 = cmds.get(1);
					// si ricava la path
					Path file = Paths.get(cmd2);
					// si controlla che esiste
					if (Files.exists(file)) {
						// esiste si carica il dizionario
						// di default è in modalità online
						dictionary = new DictionaryManager(
								DictionaryManager.ONLINE_MODE, file
										.getFileName().toString(), file);
						// ora si carica
						dictionary.loadDictionary();
						out.println("Dictionary Loaded!");
						// si imposta che il dizionario è stato caricato
						dictLoaded = true;
					} else {
						out.println("File not founded!");
					}
				} else {
					out.println("Please enter 'open +path' to load a dictionary...");
				}
				break;
			}
			case FIND: {
				// si contralla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a " +
							"dictionary. Please open a dictionary with 'open +path");
					continue;
				}
				// si richiede la parola
				out.print("--> pattern:  ");
				String pattern = reader.readLine();
				out.print("--> type:  ");
				String type = reader.readLine();
				out.print("--> unit:  ");
				String unit = reader.readLine();
				// si ricavano i chunk trovati
				List<String> results = dictionary.findChunk(pattern, type, unit);
				// si mostrano
				if (results == null || results.size()== 0) {
					out.println("No Chunk Matches...");
					continue;
				}
				for (String r : results) {
					out.println("    -- " + r);
				}
				out.println("N° of Chunks:  " + results.size());
				break;				
			}
			case DEFIN:
			{
				// si contralla che sia caricato un dizionario
				if (dictLoaded == false) {
					out.println("You cannot use this command unless you open a " +
							"dictionary. Please open a dictionary with 'open +path");
					continue;
				}
				//si ricavano le definizioni
//				List<String> defs = dictionary.ge
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
