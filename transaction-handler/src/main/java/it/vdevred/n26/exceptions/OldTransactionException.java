package it.vdevred.n26.exceptions;

public class OldTransactionException extends IllegalArgumentException  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2378794493780557051L;

	public OldTransactionException(String message) {
		super(message);
	}

	public static void validate(boolean condition, String message) {
		if (!condition) {
			throw new OldTransactionException(String.format(message));
		}
	}

}
