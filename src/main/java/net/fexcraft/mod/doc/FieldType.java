package net.fexcraft.mod.doc;

public enum FieldType {
	
	TEXT, INFO_TEXT, NUMBER, DATE, JOIN_DATE, IMG, INFO_IMG, PLAYER_IMG, ENUM;
	
	public boolean text(){
		return this == TEXT || this == IMG || this == DATE;
	}
	
	public boolean number(){
		return this == NUMBER;
	}
	
	public boolean autogen(){
		return this == JOIN_DATE || this == PLAYER_IMG || this == INFO_TEXT || this == INFO_IMG;
	}

}
