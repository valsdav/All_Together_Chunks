package com.valsecchi.SimpleInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

	// vcostanti per il controllo dei comandi inseriti dall'utente
	private static final String HELP = "help";
	private static final String FIND = "find";

	private static final String[] COMMANDS = { "help", "find" };
	/**
	 * Variabile che ferma il loop del programma
	 */
	private static boolean canContinue = true;

	/**
	 * Viene eseguito un loop continuo per gestire i comandi
	 */
	public static void main(String[] args) throws IOException {
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
			// si controlla il comando scelto
			switch (command) {
			case HELP: {
				// si scrivono i comandi disponibili
				for (String c : COMMANDS) {
					out.println(c);
				}
				break;
			}

			}
		}
	}
}
