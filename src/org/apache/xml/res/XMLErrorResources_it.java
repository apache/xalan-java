/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.res;


/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And you need
 * to enter key, value pair as part of the contents
 * array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */
public class XMLErrorResources_it extends XMLErrorResources
{

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 61;

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 0;

  /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 4;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


  // Error messages...

  /** The lookup table for error messages.   */
  public static final Object[][] contents = {

  /** Error message ID that has a null message, but takes in a single object.    */
    {"ER0000" , "{0}" },
 

 /** ER_FUNCTION_NOT_SUPPORTED          */
 //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


 {
   ER_FUNCTION_NOT_SUPPORTED, "Funzione non supportata"},
 
 /** Can't overwrite cause         */
 //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


 {
   ER_CANNOT_OVERWRITE_CAUSE,
			"Impossibile sovrascrivere la causa."},
 
  /**  No default implementation found */
 //public static final int ER_NO_DEFAULT_IMPL = 156;


 {
   ER_NO_DEFAULT_IMPL,
        "Non \u00e8 stata trovata alcuna implementazione predefinita "},

 
  /**  ChunkedIntArray({0}) not currently supported */
 //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


 {
   ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
      "ChunkedIntArray({0}) non \u00e8 correntemente supportato."},

 
  /**  Offset bigger than slot */
 //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


 {
   ER_OFFSET_BIGGER_THAN_SLOT,
      "L'offset \u00e8 maggiore dello slot."},

 
  /**  Coroutine not available, id= */
 //public static final int ER_COROUTINE_NOT_AVAIL = 159;


 {
   ER_COROUTINE_NOT_AVAIL,
      "Coroutine non disponibile, id={0}"},

 
  /**  CoroutineManager recieved co_exit() request */
 //public static final int ER_COROUTINE_CO_EXIT = 160;


 {
   ER_COROUTINE_CO_EXIT,
      "CoroutineManager ha ricevuto una richiesta co_exit()."},

 
  /**  co_joinCoroutineSet() failed */
 //public static final int ER_COJOINROUTINESET_FAILED = 161;


 {
   ER_COJOINROUTINESET_FAILED,
      "co_joinCoroutineSet() non riuscito."},

 
  /**  Coroutine parameter error () */
 //public static final int ER_COROUTINE_PARAM = 162;


 {
   ER_COROUTINE_PARAM,
      "Errore del parametro di coroutine ({0})."},

 
  /**  UNEXPECTED: Parser doTerminate answers  */
 //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


 {
   ER_PARSER_DOTERMINATE_ANSWERS,
      "\nUNEXPECTED: Risposte doTerminate del parser {0}"},

 
  /**  parse may not be called while parsing */
 //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


 {
   ER_NO_PARSE_CALL_WHILE_PARSING,
      "Impossibile chiamare l'analisi mentre \u00e8 in esecuzione."},

 
  /**  Error: typed iterator for axis  {0} not implemented  */
 //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


 {
   ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Errore: tipo di iteratore per l'asse {0} non implementato."},

 
  /**  Error: iterator for axis {0} not implemented  */
 //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


 {
   ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Errore: l'iteratore per l'asse {0} non \u00e8 implementato. "},

 
  /**  Iterator clone not supported  */
 //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


 {
   ER_ITERATOR_CLONE_NOT_SUPPORTED,
      "Il duplicato dell'iteratore non \u00e8 supportato."},

 
  /**  Unknown axis traversal type  */
 //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


 {
   ER_UNKNOWN_AXIS_TYPE,
      "Tipo di asse trasversale sconosciuto : {0}."},

 
  /**  Axis traverser not supported  */
 //public static final int ER_AXIS_NOT_SUPPORTED = 169;


 {
   ER_AXIS_NOT_SUPPORTED,
      "Attraversatore dell'asse non supportato: {0}"},

 
  /**  No more DTM IDs are available  */
 //public static final int ER_NO_DTMIDS_AVAIL = 170;


 {
   ER_NO_DTMIDS_AVAIL,
      "Non sono pi\u00f9 disponibili ID DTM."},

 
  /**  Not supported  */
 //public static final int ER_NOT_SUPPORTED = 171;


 {
   ER_NOT_SUPPORTED,
      "Non supportato: {0}"},

 
  /**  node must be non-null for getDTMHandleFromNode  */
 //public static final int ER_NODE_NON_NULL = 172;


 {
   ER_NODE_NON_NULL,
      "Il nodo deve essere non nullo per getDTMHandleFromNode."},

 
  /**  Could not resolve the node to a handle  */
 //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


 {
   ER_COULD_NOT_RESOLVE_NODE,
      "Impossibile risolvere il nodo a un handle"},

 
  /**  startParse may not be called while parsing */
 //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


 {
   ER_STARTPARSE_WHILE_PARSING,
      "Impossibile chiamare startParse durante l'analisi."},

 
  /**  startParse needs a non-null SAXParser  */
 //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


 {
   ER_STARTPARSE_NEEDS_SAXPARSER,
      "startParse richiede un SAXParser non nullo."},

 
  /**  could not initialize parser with */
 //public static final int ER_COULD_NOT_INIT_PARSER = 176;


 {
   ER_COULD_NOT_INIT_PARSER,
      "Impossibile inizializzare il parser con"},
 
  /**  exception creating new instance for pool  */
 //public static final int ER_EXCEPTION_CREATING_POOL = 178;


 {
   ER_EXCEPTION_CREATING_POOL,
      "l'eccezione crea una nuova istanza del pool"},

 
  /**  Path contains invalid escape sequence  */
 //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


 {
   ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
      "Il percorso contiene una sequenza di escape non valida."},

 
  /**  Scheme is required!  */
 //public static final int ER_SCHEME_REQUIRED = 180;


 {
   ER_SCHEME_REQUIRED,
      "Lo schema \u00e8 necessario."},

 
  /**  No scheme found in URI  */
 //public static final int ER_NO_SCHEME_IN_URI = 181;


 {
   ER_NO_SCHEME_IN_URI,
      "Nessuno schema trovato nell'URI: {0}"},

 
  /**  No scheme found in URI  */
 //public static final int ER_NO_SCHEME_INURI = 182;


 {
   ER_NO_SCHEME_INURI,
      "Nessuno schema trovato nell'URI"},

 
  /**  Path contains invalid character:   */
 //public static final int ER_PATH_INVALID_CHAR = 183;


 {
   ER_PATH_INVALID_CHAR,
      "Il percorso contiene un carattere non valido: {0}"},

 
  /**  Cannot set scheme from null string  */
 //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


 {
   ER_SCHEME_FROM_NULL_STRING,
      "Impossibile impostare lo schema da una stringa nulla."},

 
  /**  The scheme is not conformant. */
 //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


 {
   ER_SCHEME_NOT_CONFORMANT,
      "Lo schema non \u00e8 conforme."},

 
  /**  Host is not a well formed address  */
 //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


 {
   ER_HOST_ADDRESS_NOT_WELLFORMED,
      "L'host non \u00e8 un indirizzo corretto."},

 
  /**  Port cannot be set when host is null  */
 //public static final int ER_PORT_WHEN_HOST_NULL = 187;


 {
   ER_PORT_WHEN_HOST_NULL,
      "Impossibile impostare la porta quando l'host \u00e8 nullo."},

 
  /**  Invalid port number  */
 //public static final int ER_INVALID_PORT = 188;


 {
   ER_INVALID_PORT,
      "Numero di porta non valido"},

 
  /**  Fragment can only be set for a generic URI  */
 //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


 {
   ER_FRAG_FOR_GENERIC_URI,
      "\u00c8 possibile impostare il frammento solo per un URI generico."},

 
  /**  Fragment cannot be set when path is null  */
 //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


 {
   ER_FRAG_WHEN_PATH_NULL,
      "Impossibile impostare il frammento quando il percorso \u00e8 nullo."},

 
  /**  Fragment contains invalid character  */
 //public static final int ER_FRAG_INVALID_CHAR = 191;


 {
   ER_FRAG_INVALID_CHAR,
      "Il frammento contiene un carattere non valido."},

 

 
  /** Parser is already in use  */
 //public static final int ER_PARSER_IN_USE = 192;


 {
   ER_PARSER_IN_USE,
       "Il parser \u00e8 gi\u00e0 in uso."},

 
  /** Parser is already in use  */
 //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


 {
   ER_CANNOT_CHANGE_WHILE_PARSING,
       "Impossibile cambiare {0} {1} durante l'analisi."},

 
  /** Self-causation not permitted  */
 //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


 {
   ER_SELF_CAUSATION_NOT_PERMITTED,
       "Non \u00e8 consentito essere causa ed effetto contemporaneamente."},

 
  /** Userinfo may not be specified if host is not specified   */
 //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


 {
   ER_NO_USERINFO_IF_NO_HOST,
       "Impossibile specificare Userinfo se non \u00e8 specificato l'host."},

 
  /** Port may not be specified if host is not specified   */
 //public static final int ER_NO_PORT_IF_NO_HOST = 199;


 {
   ER_NO_PORT_IF_NO_HOST,
       "Impossibile specificare la porta se non \u00e8 specificato l'host."},

 
  /** Query string cannot be specified in path and query string   */
 //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


 {
   ER_NO_QUERY_STRING_IN_PATH,
       "La stringa di query non pu\u00f2 essere specificata nella stringa di percorso e di query."},

 
  /** Fragment cannot be specified in both the path and fragment   */
 //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


 {
   ER_NO_FRAGMENT_STRING_IN_PATH,
       "Il frammento non pu\u00f2 essere specificato sia nel percorso sia nel frammento."},

 
  /** Cannot initialize URI with empty parameters   */
 //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


 {
   ER_CANNOT_INIT_URI_EMPTY_PARMS,
       "Impossibile inizializzare l'URI con parametri vuoti."},

 
 /**  Method not yet supported    */
 //public static final int ER_METHOD_NOT_SUPPORTED = 210;


 {
   ER_METHOD_NOT_SUPPORTED,
       "Metodo non ancora supportato. "},


 /** IncrementalSAXSource_Filter not currently restartable   */
 //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


 {
   ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
    "IncrementalSAXSource_Filter non correntemente riavviabile."},

 
 /** IncrementalSAXSource_Filter not currently restartable   */
 //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


 {
   ER_XMLRDR_NOT_BEFORE_STARTPARSE,
    "XMLReader non prima della richiesta startParse."},
  
 
 // Axis traverser not supported: {0}
   //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
 
   {
     ER_AXIS_TRAVERSER_NOT_SUPPORTED,
      "Secante asse non supportata: {0}"},
  
 
 // ListingErrorHandler created with null PrintWriter!
   //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
 
   {
     ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
      "ListingErrorHandler creato con PrintWriter nullo."},
  
 
   //public static final int ER_SYSTEMID_UNKNOWN = 240;
 
   {
     ER_SYSTEMID_UNKNOWN,
      "ID sistema sconosciuto"},
  
 
   // Location of error unknown
   //public static final int ER_LOCATION_UNKNOWN = 241;
 
   {
     ER_LOCATION_UNKNOWN,
      "Ubicazione errore sconosciuta"},


 /** Field ER_PREFIX_MUST_RESOLVE          */
 //public static final int ER_PREFIX_MUST_RESOLVE = 52;


 {
   ER_PREFIX_MUST_RESOLVE,
     "Il prefisso deve risolvere in namespace: {0}"},


 /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
 //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


 {
   ER_CREATEDOCUMENT_NOT_SUPPORTED,
     "createDocument() non supportato in XPathContext."},


 /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
 //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


 {
   ER_CHILD_HAS_NO_OWNER_DOCUMENT,
     "L'elemento secondario dell'attributo non ha un documento di propriet\u00e0."},


 /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
 //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


 {
   ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
     "L'elemento secondario dell'attributo non ha un elemento del documento di propriet\u00e0."},


 /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
 //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


 {
   ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
     "Avvertenza: impossibile generare un output di testo prima dell'elemento documento. Richiesta ignorata..."},


 /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
 //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


 {
   ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
     "Impossibile avere pi\u00f9 di una root su un DOM."},

 
  /**  Argument 'localName' is null  */
 //public static final int ER_ARG_LOCALNAME_NULL = 70;


 {
   ER_ARG_LOCALNAME_NULL,
      "L'argomento 'localName' \u00e8 nullo"},
  

   // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
   // The localname is the portion after the optional colon; the message indicates
   // that there is a problem with that part of the QNAME.
 
   /** localname in QNAME should be a valid NCName */
   //public static final int ER_ARG_LOCALNAME_INVALID = 101;
 
 
   {
     ER_ARG_LOCALNAME_INVALID,
        "Localname in QNAME deve essere un NCName valido"},
  
   
   // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
   // The prefix is the portion before the optional colon; the message indicates
   // that there is a problem with that part of the QNAME.
 
   /** prefix in QNAME should be a valid NCName */
   //public static final int ER_ARG_PREFIX_INVALID = 102;
 
 
   {
     ER_ARG_PREFIX_INVALID,
        "Prefisso in QNAME deve essere un NCName valido"},

   { "BAD_CODE",
     "I parametri di createMessage sono esterni ai limiti"},
   { "FORMAT_FAILED",
     "Eccezione generata durante la chiamata di messageFormat"},
   { "line", "Linea #"},
   { "column", "Colonna #"}
   
   };

 /**
  * Get the association list.
  *
  * @return The association list.
  */
 public Object[][] getContents()
 {
   return contents;
 }
}