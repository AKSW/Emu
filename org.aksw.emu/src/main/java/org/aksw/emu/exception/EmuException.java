package org.aksw.emu.exception;

public class EmuException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmuException(String e){
		super(e);
	}
	
	public EmuException(Exception e){
		super(e);
	}
}
