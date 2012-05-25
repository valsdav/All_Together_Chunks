package com.valsecchi.ChunksManager;


public class Definition {

	private String Hash;
	private String Definition;
	
	public Definition (String hash,String definition){
		Hash = hash;
		Definition= definition;
	}

	public String getHash() {
		return Hash;
	}

	public String getText() {
		return Definition;
	}
	
	public boolean equals(Definition def){
		//confronta due oggetti
		if(def.getHash().equals(this.getHash()) && def.getText().equals(this.getText())){
			return true;
		}
		else{
			return false;
		}
	}
}
