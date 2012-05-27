package com.valsecchi.ChunksManager;

/**
 * Classe che rappresenta un chunk, senza definizioni
 * 
 * @author Davide
 * 
 */
public class Chunk {

	private String Hash;
	private String Word;
	private String Type;
	private String Unit;

	/**
	 * Costruttore di un oggetto Chunk che rappresetna una parola con tutti i
	 * suoi attributi
	 * 
	 * @param _hash
	 * @param _type
	 * @param _unit
	 * @param _word
	 */
	public Chunk(String _word, String _hash, String _type, String _unit) {
		Hash = _hash;
		Word = _word;
		Type = _type;
		Unit = _unit;
	}

	/**
	 * 
	 * Costruttore di un oggetto Chunk che rappresetna una parola con tutti i
	 * suoi attributi Questo costruttore non necessita dell'hash perchè a ricava
	 * lui stesso.
	 * 
	 * @param _type
	 * @param _unit
	 * @param _word
	 */
	public Chunk(String _word, String _type, String _unit) {
		Word = _word;
		Type = _type;
		Unit = _unit;
		Hash = MD5.GetHash(_word);
	}

	public String getHash() {
		return Hash;
	}

	public String getWord() {
		return Word;
	}

	public String getType() {
		return Type;
	}

	public String getUnit() {
		return Unit;
	}

}
