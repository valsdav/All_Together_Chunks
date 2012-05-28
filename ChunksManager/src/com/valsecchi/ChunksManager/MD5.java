package com.valsecchi.ChunksManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.*;

public class MD5 {

	public static String GetHash(String stringa) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		if (Charset.isSupported("CP1252"))

			md.update(stringa.getBytes(Charset.forName("CP1252")));
		else
			md.update(stringa.getBytes(Charset.forName("ISO-8859-1")));

		byte[] bytes = md.digest();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)

			str.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100)
					.substring(1, 3));

		return (str.toString().substring(0, 6)
				+ str.toString().substring(str.length() - 7, str.length()-1)).toUpperCase();
	}
}
