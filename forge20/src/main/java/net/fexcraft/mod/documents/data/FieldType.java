package net.fexcraft.mod.documents.data;

public enum FieldType {
	
	TEXT		(true),
	INFO_TEXT	(false),
	UUID		(true),
	PLAYER_NAME	(false),
	INTEGER		(true),
	FLOAT		(true),
	DATE		(true),
	JOIN_DATE	(false),
	IMG			(true),
	INFO_IMG	(false),
	PLAYER_IMG	(false),
	ENUM		(true),
	ISSUER		(false),
	ISSUED		(false),
	ISSUER_NAME	(false),
	;
	
	public final boolean editable;
	
	FieldType(boolean editable){
		this.editable = editable;
	}
	
	public boolean number(){
		return this == INTEGER || this == FLOAT;
	}

	public boolean image(){
		return this == IMG || this == INFO_IMG || this == PLAYER_IMG;
	}
	
	public boolean date(){
		return this == DATE || this == JOIN_DATE;
	}

}
