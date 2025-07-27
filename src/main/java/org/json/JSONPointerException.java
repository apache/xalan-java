package org.json;

/*
Public Domain.
*/

/**
 * The JSONPointerException is thrown by {@link JSONPointer} if an error occurs
 * during evaluating a pointer.
 * 
 * @author JSON.org
 * @version 2016-05-13
 */
public class JSONPointerException extends JSONException {
    private static final long serialVersionUID = 8872944667561856751L;

    /**
     * Constructs a new JSONPointerException with the specified error message.
     *
     * @param message The detail message describing the reason for the exception.
     */
    public JSONPointerException(String message) {
        super(message);
    }

    /**
     * Constructs a new JSONPointerException with the specified error message and cause.
     *
     * @param message The detail message describing the reason for the exception.
     * @param cause   The cause of the exception.
     */
    public JSONPointerException(String message, Throwable cause) {
        super(message, cause);
    }

}
