package com.valsecchi.SimpleInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

	// singoli comandi
	private static final String HELP = "help";
	private static final String FIND = "find";
	/**
	 * Array di stringhe che contiene i vari comandi che saranno poi inseriti in
	 * {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_LIST = { HELP, FIND };
	/**
	 * Array di stringe che contiene le istruzioni dei vari comandi che saranno
	 * poi inseriti in {@link #COMMANDS_MAP}
	 */
	private static final String[] COMMANDS_INSTR = { "richiede aiuto",
			"aggiunge un chunk" };
	/**
	 * Mappa che incapsula tutti i comandi disponibili con relativa
	 * documentazione.
	 */
	private static Map<String, String> COMMANDS_MAP;
	/**
	 * Variabile che ferma il loop del programma
	 */
	private static boolean canContinue = true;

	/**
	 * Loop continuo del programma che interpreta ed esegue i comandi
	 */
	public static void main(String[] args) throws IOException {
		//si caricano i comandi
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

				} else {
					// allora bisogna scrivere tutti i comandi a schermo con
					// relative istruzioni
					for(int i = 0; i<= COMMANDS_MAP.size();i++){
//						out.println(COMMANDS_MAP.keySet().,COMMANDS_MAP.values()[i]);
					}
				}

			}
			}
		}
	}
	
	/**
	 * Il metodo inserisce in {@link #COMMANDS_MAP} tutti i comandi con relative istruzioni
	 */
	private static void setupInstructions(){
		for(int i= 0; i<= COMMANDS_LIST.length;i++){
			COMMANDS_MAP.put(COMMANDS_LIST[i], COMMANDS_INSTR[i]);
		}
	}
}
