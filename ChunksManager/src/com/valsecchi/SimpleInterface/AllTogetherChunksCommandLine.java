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
	 * Variabile che memorizza il comando help
	 */
	private static final String HELP = "help";
	/**
	 * Variabile che memorizza il comando find
	 */
	private static final String FIND = "find";
	/**
	 * Variabile che memorizza il comando exit
	 */
	private static final String EXIT = "exit";
	
	/**
	 * Array di stringhe che contiene i vari comandi che saranno poi inseriti in
	 * {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_LIST = { HELP, FIND,EXIT };
	/**
	 * Array di stringe che contiene le istruzioni dei vari comandi che saranno
	 * poi inseriti in {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_INSTR = {
			"help + command: display instructions for command",
			"find +word +type +unit: search a chunk that contains that word, that it's of that type and unit",
			"exit: program will terminate"};
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

		out.println("Benvenuti nel programma All Together Chunks!\nDigitare "
				+ "un comando e help per una lista dei comandi disponibili...");

		// viene creato il lettore dell'input della console
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		// comando inserito
		String command = "";
		// inizio loop
		while (canContinue) {
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
					out.println("-->  " + cmd2 + " >>> "
							+ COMMANDS_MAP.get(cmd2));

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
