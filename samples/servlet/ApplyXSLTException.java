/*****************************************************************************************************
 * $Id$
 * 
 * Copyright (c) 1998-1999 Lotus Corporation, Inc. All Rights Reserved.
 *				This software is provided without a warranty of any kind.
 * 
 * $State$
 *****************************************************************************************************/
 
/*****************************************************************************************************
 *
 * Wrapper for exceptions occurring during apply XSL processing.  
 * Allows for exceptions to be returned with an associated HTTP Status Code.
 *
 * @author Spencer Shepard (sshepard@us.ibm.com)
 * @author R. Adam King (rak@us.ibm.com)
 * @author Tom Rowe (trowe@us.ibm.com)
 *
 *****************************************************************************************************/
package servlet;

public class ApplyXSLTException extends Exception {

    /**
      * Exception Message.
      * @serial
      */ 
    private String myMessage = "";

    /**
      * HTTP Status Code. Default= internal server error.
      * @serial
      */
    private int  myHttpStatusCode = javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR; 

    /**
      * Wrapped exception
      * @serial
      */
    private Exception myException = null;

    /**
      * Constructor for exception with no additional detail.
      */
    public ApplyXSLTException() 
    { 
        super(); 
    }

    /**
      * Constructor for exception with message.
      * @param s Exception message
      */
    public ApplyXSLTException(String s) 
    { 
        super(); 
	myMessage = s;
    }

    /**
      * Constructor for exception with HTTP status code.
      * @param hsc Valid status code from javax.servlet.http.HttpServletResponse
      */
    public ApplyXSLTException(int hsc) 
    {
	super();
	myHttpStatusCode = hsc;
    }

    /**
      * Constructor for exception with message and HTTP status code.
      * @param s Exception message
      * @param hsc Valid status code from javax.servlet.http.HttpServletResponse
      */
    public ApplyXSLTException(String s, int hsc)
    {
	super();
	myHttpStatusCode = hsc;
    }

    /**
      * Constructor for exception.
      * @param e Exception to be wrapped.
      */
    public ApplyXSLTException(Exception e)
    {
	super();
	myMessage = e.getMessage();
	myException = e;
    }

    /**
      * Constructor for passed exception with message.
      * @param s Exception message
      * @param e Exception to be wrapped.
      */
    public ApplyXSLTException (String s, Exception e)
    {
	super();
	myMessage = s;
	myException = e;
    }

    /**
      * Constructor for passed exception with HTTP status code.
      * @param e Exception to be wrapped.
      * @param hsc Valid status code from javax.servlet.http.HttpServletResponse
      */
    public ApplyXSLTException(Exception e, int hsc)
    {
	super();
	myMessage = e.getMessage();
	myException = e;
	myHttpStatusCode = hsc;
    }

    /**
      * Constructor for passed exception with HTTP status code and message.
      * @param s Exception message
      * @param e Exception to be wrapped.
      * @param hsc Valid status code from javax.servlet.http.HttpServletResponse
      */
    public ApplyXSLTException(String s, Exception e, int hsc)
    {
	super();
	myMessage = s;
	myException = e;
	myHttpStatusCode = hsc;
    }

    /**
      * Returns exception message.
      * @return exception message
      */
    public String getMessage()
    {
	return myMessage;
    }

    /**
      * Appends string to exception message.
      * @param s String to be added to message
      */
    public void appendMessage(String s)
    {
	myMessage += s;
    }

    /**
      * Returns the wrapped exception.
      * @return Wrapped exception
      */
    public Exception getException()
    {
	return myException;
    }

    /**
      * Returns the HTTP status code associated with the exception.
      * @return Valid status code from javax.servlet.http.HttpServletResponse
      */
    public int getStatusCode()
    {
	return myHttpStatusCode;
    }
}

