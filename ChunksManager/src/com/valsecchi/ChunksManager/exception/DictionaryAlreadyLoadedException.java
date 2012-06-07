package com.valsecchi.ChunksManager.exception;

import java.nio.file.Path;

/**
 * La classe rappresenta un'eccezione che viene sollevata quando si tenta di
 * caricare un dizionario nel DictionaryManager quando ne è già presente un
 * altro
 * 
 * @author Davide Valsecchi
 * @version 1.0
 * 
 */
public class DictionaryAlreadyLoadedException extends Exception {

	private Path _current_path;
	private Path _error_path;

	/**
	 * Il costruttore memorizza la path del dizionario che si voleva caricare e
	 * quella del dizionario già in memoria
	 * 
	 * @param current_path
	 *            path del dizionario caricato
	 * @param error_path
	 *            path del dizionario non caricato
	 */
	public DictionaryAlreadyLoadedException(Path current_path, Path error_path) {
		_current_path = current_path;
		_error_path = error_path;
	}

	@Override
	public String toString() {
		return "Si è tentato di caricare il dizionario in "
				+ _error_path.toString() + " ma è già presente il dizionario "
				+ _current_path.toString();
	}

	/** Si restituisce la path del dizionario già caricato */
	public Path getCurrentPath() {
		return _current_path;
	}

	/** Si restituisce la path del dizionario che si voleva caricare */
	public Path getErrorPath() {
		return _error_path;
	}
}
