/*
 * $Id$
 * 
 * Copyright (c) 1998-1999 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package javax.xml.transform;

/**
 * Indicates a serious configuration error.
 *
 * @since JAXP 1.0
 * @version 1.0
 */

public class TransformerConfigurationException extends Exception {
  
    private Exception exception;

    /**
     * Create a new <code>TransformerConfigurationException</code> with no
     * detail mesage.
     */

    public TransformerConfigurationException() {
        super();
    }

    /**
     * Create a new <code>TransformerConfigurationException</code> with
     * the <code>String </code> specified as an error message.
     *
     * @param msg The error message for the exception.
     */
    
    public TransformerConfigurationException(String msg) {
        super(msg);
    }
    
    /**
     * Create a new <code>TransformerConfigurationException</code> with a
     * given <code>Exception</code> base cause of the error.
     *
     * @param e The exception to be encapsulated in a
     * TransformerConfigurationException.
     */
    
    public TransformerConfigurationException(Exception e) {
        super();
        this.exception = e;
    }

    /**
     * Create a new <code>TransformerConfigurationException</code> with the
     * given <code>Exception</code> base cause and detail message.
     *
     * @param e The exception to be encapsulated in a
     * TransformerConfigurationException
     * @param msg The detail message.
     * @param e The exception to be wrapped in a TransformerConfigurationException
     */
    
    public TransformerConfigurationException(String msg, Exception e) {
        super(msg);
        this.exception = e;
    }


    /**
     * Return the message (if any) for this error . If there is no
     * message for the exception and there is an encapsulated
     * exception then the message of that exception will be returned.
     *
     * @return The error message.
     */
    
    public String getMessage () {
        String message = super.getMessage ();
  
        if (message == null && exception != null) {
            return exception.getMessage();
        }

        return message;
    }
  
    /**
     * Return the actual exception (if any) that caused this exception to
     * be raised.
     *
     * @return The encapsulated exception, or null if there is none.
     */
    
    public Exception getException () {
        return exception;
    }


}

