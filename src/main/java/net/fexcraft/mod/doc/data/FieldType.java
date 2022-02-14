package net.fexcraft.mod.doc.data;

public enum FieldType {
	
	TEXT		(true),
	INFO_TEXT	(false),
	UUID		(true),
	PLAYER_NAME	(false),
	NUMBER		(true),
	DATE		(true),
	JOIN_DATE	(false),
	IMG			(true),
	INFO_IMG	(false),
	PLAYER_IMG	(false),
	ENUM		(true),
	ISSUER		(false),
	;
	
	public final boolean editable;
	
	FieldType(boolean editable){
		this.editable = editable;
	}
	
	public boolean number(){
		return this == NUMBER;
	}

}
