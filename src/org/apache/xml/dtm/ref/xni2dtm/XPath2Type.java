/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
package org.apache.xml.dtm.ref.xni2dtm;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.xs.XSTypeDecl;
import org.apache.xerces.impl.xs.psvi.XSSimpleTypeDefinition;
import org.apache.xerces.impl.xs.psvi.XSTypeDefinition;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.psvi.ItemPSVI;
import org.apache.xml.dtm.DTMSequence;

  /** The full XNI ItemPSVI is far too heavy-weight for
   * our needs. But their XSTypeDecl isn't quite heavy enough; it gives
   * us the actual member type, but that may be anonymous... so to get
   * what XPath2 considers the proper typename, we need to examine
   * additional fields as well. This class is an attempt to compromise by
   * resolving the typename and storing that alongside the member type.
   * 
   * A more efficient solution undoubtedly exists. But since XNI's PSVI
   * APIs are still in flux, and since I'm just trying to get an initial
   * demo running, this will suffice for now.
   * %REVIEW% periodically!
   * */
public class XPath2Type
{
	/** Manefest constant: dtm.getTypedValue() wants to be able to return
	 * untyped values. Rather than trying to dig it out of the schema context, 
	 * it's easier to have one ready for use... */
	public static final XPath2Type XSSTRING=
		new XPath2Type(null,"http://www.w3.org/2001/XMLSchema","string");	
	/** Manefest constant: dtm.getTypedValue() wants to be able to return
	 * untyped values as strings, and needs a suitable type object. Rather
	 * than trying to dig it out of the schema context, it's easier to
	 * have one ready for use... */
	public static final XPath2Type XSANYTYPE=
		new XPath2Type(null,"http://www.w3.org/2001/XMLSchema","anyType");	
	/** Manefest constant: dtm.getTypedValue() wants to be able to return
	 * untyped values as strings, and needs a suitable type object. Rather
	 * than trying to dig it out of the schema context, it's easier to
	 * have one ready for use... */
	public static final XPath2Type XSANYSIMPLETYPE=
		new XPath2Type(null,"http://www.w3.org/2001/XMLSchema","anySimpleType");	
	
  	public XSTypeDefinition m_xniType;
  	public String m_namespace;
  	public String m_localName;
  	
  	/** Constructor for our internal type representation
  	 *   We will extract the low-level XSTypeDecl for the Member Type,
  	 *   and determine the proper namespace and localname. Other data
  	 *   can (hopefully) be GC'd after we're done. This is still NOT
  	 *   a lightweight beast.
  	 * 
  	 * @param psvi XNI Post-Schema-Validation Infoset annotation.
  	 * @param isAttr True iff we're defining type for an attribute.
  	 * */
  	public XPath2Type(ItemPSVI psvi, boolean isAttr)
  	{
  		// First get the member type.
  		m_xniType= (psvi==null) ? null : psvi.getTypeDefinition();
  	
  		// Now resolve the typename.
  		// There are some quibbles about algorithm; see comments on
  		// the resolve methods.
  		heavyResolveTypeName(psvi,isAttr);
  	}
  	  	
  	/** Constructor for Xerces workaround
	 * (Xerces isn't reminding us of type at the time it provides
	 * validity, so XNI2DTM is passing that data around via a stack.
	 * This is a kluge and I hope to convince Xerces to fix it.)
  	 * 
  	 * @param psvi XNI Post-Schema-Validation Infoset annotation.
  	 * @param isAttr True iff we're defining type for an attribute.
  	 * */
  	public XPath2Type(ItemPSVI psvi, XSSimpleTypeDefinition member, XSTypeDefinition type,  boolean isAttr)
  	{
  		// First get the member type.
  		m_xniType= (psvi==null) ? null : type;
  	
  		// Now resolve the typename.
  		// There are some quibbles about algorithm; see comments on
  		// the resolve methods.
  		heavyResolveTypeName(psvi,member,type,isAttr);
  	}

  	/** Internal constructor to support getItemType and 
  	 * AbstractSchema.lookUpType
  	 * @param psvi XNI Post-Schema-Validation Infoset annotation.
  	 * @param isAttr True iff we're defining type for an attribute.
  	 * */
  	public XPath2Type(XSTypeDefinition itemType, String namespace, String localName)
  	{
  		m_xniType=itemType;
  		m_namespace=namespace;
  		m_localName=localName;
  	}
  	
  	/** Identity needs to be defined so we can do sparse storage.
  	 * %REVIEW% I'm not sure all three fields need to be checked, but...
  	 * */
  	public boolean equals(XPath2Type other)
  	{
		// Could cache hashCode()'s result and use that to accelerate
		// doesn't-equal testing at the cost of some storage and
		// slowing down does-equal tests. Not convinced it's useful here.
		// %REVIEW%
  		return (m_xniType==other.m_xniType ||
  			m_xniType!=null && m_xniType.equals(other.m_xniType)) &&
  			// These two won't be null
  			m_localName.equals(other.m_localName) && 
  			m_namespace.equals(other.m_namespace);
  	}
  	  	
  	/** Identity needs to be defined so we can do sparse storage.
  	 * %REVIEW% I'm not sure all three fields need to be checked, but...
  	 * */
  	public int hashCode()
  	{
  		// Could cache hashCode(). See discussion in equals().
  		// %REVIEW%
  		return m_namespace.hashCode()+m_localName.hashCode()+
  			(m_xniType==null ? 0 : m_xniType.hashCode());
  	}
  	
  	public String getTargetNamespace() {return m_namespace;}
  	public String getTypeName() {return m_localName;}
  	
  	public boolean derivedFrom(String namespace,String localname)
  	{
  		if(m_xniType instanceof XSTypeDecl)
	  		return ((XSTypeDecl)m_xniType).derivedFrom(namespace,localname);
	  	else
	  	{
	  		// Fallback in case no PSVI info was passed in: exact match
	  		// (it's got to be the correct xsi:any*Type).
		  	return(m_localName.equals(localname) && m_namespace.equals(namespace));
	  	}
  	}
  	
  /** Broken out into a subroutine so I can use it for debugging purposes.
   * This logic is adapted from the Xerces SimpleTypeUsage.validateString() example.
   * 
   * %REVIEW% May be more efficient to fold it back in.
   * 
   * NOTE: Typed value depends on context information -- for example, a
   * QName must be interpreted w/r/t a specific Namespace context. It's
   * the caller's responisibility to pass down that information, since only
   * the caller knows which context this operation is being performed in.
   * 
   * %REVIEW% Currently NS context comes down as a SAX accessor. Should we be
   * able to take a DTM node directly as context? 
   * 
   * @param textValue Text content to be interpreted
   * @param nsctxt SAX namespace context, which will be proxied as XNI.
   * @return DTM_XSequence containing one or more Java values, as appropriate
   *   to the Built-In Type we have inherited from -- or null if no such 
   *   mapping exists (eg, if actualType was complex)
   * */         
  public DTMSequence typedValue(String textvalue, org.xml.sax.helpers.NamespaceSupport nsctxt)
  {
    Object value;
    DTM_XSequence seq=null;

    if(m_xniType==null)
    {
      seq=new DTM_XSequence(textvalue,this);
    }

    else if(m_xniType instanceof XSSimpleTypeDefinition)
    {           
      //create an instance of 'ValidatedInfo' to get back information 
      //(like actual value, normalizedValue etc..)after content is validated.
      ValidatedInfo validatedInfo = new ValidatedInfo(); // %REVIEW% Can we reuse???

      //get proper validation context. This is very important, since
      // it's where we get data for resolving prefixes (in QNames),
      // known IDs (for IDREFs and ID conflicts), entities, etc.
      //Validation context passed is generally different while 
      // validating content and creating simple type (applyFacets)
      ValidationState validationState = new ValidationState();
      if(nsctxt!=null)
	      validationState.setNamespaceSupport(new XPath2NamespaceSupport(nsctxt));

      // This may need to be bound to additional data using:
      //validationState.setSymbolTable(....);
      //validationState.setFacetChecking(...);
      //validationState.setExtraChecking(...);        

      // Validate and parse the string
      try{
        ((XSSimpleTypeDecl)m_xniType).validate(textvalue, validationState, validatedInfo);
      } catch(InvalidDatatypeValueException ex){
		// %REVIEW% Won't happen in prototype, where I'm only revalidating
		// existing data. _WILL_ happen in production, when we start generating
		// new typed data. Needs to be handled better at that time...
        System.err.println(ex.getMessage());
        ex.printStackTrace(System.err);
      }

      //now 'validatedInfo' object contains information

      // for number types (decimal, double, float, and types derived from them),
      // Object return is BigDecimal, Double, Float respectively.
      // Boolean is handled similarly.
      // Some types (string and derived) just return the string itself.
      value = validatedInfo.actualValue;

      //The normalized value of a string type
      // (Should we check for stings and return this instead?)
      //String normalizedValue = validatedInfo.normalizedValue ;
      
      // If the type is a union type, then the member type which
      // actually validated the string value will be:
      // XSSimpleType memberType = validatedInfo.memberType ;

      // %REVIEW% I presume this handles lists by returning arrays...?
      
      // Some types may want to be converted from XNI-native to Xalan-native
      if(value instanceof org.apache.xerces.xni.QName)
      {
      	org.apache.xerces.xni.QName xniq=(org.apache.xerces.xni.QName)value;
      	value=new org.apache.xml.utils.QName(xniq.uri,xniq.localpart);
      }
                
      seq=new DTM_XSequence(value,this);
    }
    
    // Sloppy recognition of anyType -- but it appears to be correct
	// (if not valid/recognizable, the typed value appears to be
	// the string value).
    else if("anyType".equals(m_xniType.getName())
    		&& "http://www.w3.org/2001/XMLSchema".equals(m_xniType.getNamespace())
	    )
    {
      seq=new DTM_XSequence(textvalue,this);
    }
    
    // Should the failure be empty, or error?
    return seq==null ? DTMSequence.EMPTY : seq;
  }
  
  /** @return individual element type, if this is a list-of; else null.
   * %REVIEW% -- else self?
   * */
  public XPath2Type getItemType() 
  {
  	if(m_xniType instanceof XSSimpleTypeDefinition)
  	{
  		XSTypeDefinition itemType=((XSSimpleTypeDefinition)m_xniType).getItemType();
  		if(itemType!=null)
	 	 	return new XPath2Type(
 		 		itemType,
 		 		m_xniType.getNamespace(),
 	 			m_xniType.getName());
  	}
 	return null;
  }

  /** Implementation of the XPath2 type-name resolution algorithm
   * (data model 3.5). 
   * 
   * Code donated by Sandy Gao, using the new
   * Heavy-Weight PSVI interfaces.
   * Fractionated to deal with validity and type info not being
   * simultaneously available in current Xerces.
   * 
   * @param psvi  the psvi information for the current node
   * @param attr false for element (fallback is xs:anyType)
   *              true for attribute (fallback is xs:anySimpleType)
   * */  
  protected void heavyResolveTypeName(ItemPSVI psvi, XSSimpleTypeDefinition member, XSTypeDefinition type, boolean attr)
  {
  	/* Not compatable with old light-weight schema APIs
  	*/
        // check whether the node is valid
        if (psvi == null ||
            psvi.getValidity() != ItemPSVI.VALIDITY_VALID) {
            // if the node is not valid, then return xs:anyType
            m_namespace = "http://www.w3.org/2001/XMLSchema";
            m_localName = attr ? "anySimpleType" : "anyType";
            return;
        }
        
        // try provided member type definition, and return its name
        if (member != null) {
            m_namespace = member.getNamespace();
            m_localName = member.getName();
            return;
        }
        
        // try provided type definition, and return its name
        if (type != null) {
            m_namespace = type.getNamespace();
            m_localName = type.getName();
            return;
        }
        
        
  	// Member type definitions promised to be available;
  	// can't proceed to check names independently

        // all failed, return xs:anyType
        m_namespace = "http://www.w3.org/2001/XMLSchema";
        m_localName = attr ? "anySimpleType" : "anyType";
        return;
  // throw new java.lang.UnsupportedOperationException("Xerces Heavyweight PSVI not yet avaialble"); /**/       
  }

  /** Implementation of the XPath2 type-name resolution algorithm
   * (data model 3.5). 
   * 
   * Code donated by Sandy Gao, using the new
   * Heavy-Weight PSVI interfaces
   * This one works *ONLY* if type and validity are all known in the same
   * PSVI, which is not the case in current Xerces.
   * 
   * @param psvi  the psvi information for the current node
   * @param attr false for element (fallback is xs:anyType)
   *              true for attribute (fallback is xs:anySimpleType)
   * */  
  protected void heavyResolveTypeName(ItemPSVI psvi, boolean attr)
  {
  	heavyResolveTypeName(psvi,psvi.getMemberTypeDefinition(),
		psvi.getTypeDefinition(), attr);
  }
  
  /** Modification of the XPath2 type-name resolution algorithm
   * to reflect Sandy Gao's concerns about the official version
   * not tolerating processors that implement the "lightweight" version
   * of PSVI.
   * 
   * Code donated by Sandy Gao, using the new
   * Heavy-Weight PSVI interfaces
   * 
   * @param psvi  the psvi information for the current node
   * @param attr false for element (fallback is xs:anyType)
   *              true for attribute (fallback is xs:anySimpleType)
   * */  
  protected void proposedHeavyResolveTypeName(ItemPSVI psvi, boolean attr)
  {
  	/* Not compatable with old light-weight schema APIs
  	*/

  	// NAME OF THIS CONSTANT IS IN FLUX
  	int VALID=ItemPSVI.VALIDITY_VALID;
  	//int VALID=ItemPSVI.VALID_VALIDITY;
  	
        // check whether the node is valid
        if (psvi == null ||
            psvi.getValidity() != VALID) {
            // if the node is not valid, then return xs:anyType
            m_namespace = "http://www.w3.org/2001/XMLSchema";
	        m_localName = attr ? "anySimpleType" : "anyType";
            return;
        }
        
        // try to get the member type definition, and return its name
        XSSimpleTypeDefinition member = psvi.getMemberTypeDefinition();
        if (member != null && member.getName() != null) {
            m_namespace = member.getNamespace();
            m_localName = member.getName();
            return;
        }
        
        // try to get the type definition, and return its name
        XSTypeDefinition type = psvi.getTypeDefinition();
        if (type != null && type.getName() != null) {
            m_namespace = type.getNamespace();
            m_localName = type.getName();
            return;
        }
        
  	// Member type definitions promised to be available;
  	// can't proceed to check names independently

        // all failed, return xs:anyType
        m_namespace = "http://www.w3.org/2001/XMLSchema";
        m_localName = attr ? "anySimpleType" : "anyType";
        return;
  // throw new java.lang.UnsupportedOperationException("Xerces Heavyweight PSVI not yet avaialble"); /**/       
  }
  
  /** Attempt to write a simplified version of the type resolution
   * algorithm (data model 3.5) using only the Light-Weight PSVI
   * interfaces previously supported in Xerces (which I believe will be
   * phased out when heavyweight come into play).
   * 
   * Based on code donated by Sandy Gao.
   * 
   * @param psvi  the psvi information for the current node
   * @param ret   a String array with size 2
   *              index 0 is used to return the namespace name;
   *              index 1 is used to return the local name.
   * @param attr false for element (fallback is xs:anyType)
   *              true for attribute (fallback is xs:anySimpleType)
   * */  
  protected void lightResolveTypeName(ItemPSVI psvi, boolean attr)
  {
  	/* Not compatable with new heavy-weight schema APIs
  	//
  	
  	int VALID=ItemPSVI.VALID_VALIDITY;
  	
        // check whether the node is valid
        if (psvi == null ||
            psvi.getValidity() != VALID) {
            // if the node is not valid, then return xs:anyType
            m_namespace = "http://www.w3.org/2001/XMLSchema";
            m_localName = attr ? "anySimpleType" : "anyType";
            return;
        }
        
	  	// Member type definitions promised NOT to be available;
 	 	// proceed to check names independently

		// Need the second test, apparently
        if (!psvi.isMemberTypeAnonymous() && null!=psvi.getMemberTypeName() ) {
            m_namespace = psvi.getMemberTypeNamespace();
            m_localName = psvi.getMemberTypeName();
            return;
        }
        
		// Need the second test, apparently
        if (!psvi.isTypeAnonymous() && null!=psvi.getTypeName()) {
            m_namespace = psvi.getTypeNamespace();
            m_localName = psvi.getTypeName();
            return;
        }
        
        // all failed, return xs:anyType
        m_namespace = "http://www.w3.org/2001/XMLSchema";
        m_localName = attr ? "anySimpleType" : "anyType";
        return;
  */ throw new java.lang.UnsupportedOperationException("Xerces Lightweight PSVI phased out"); /**/
  }
  
  
  //---------------------------------------------------------------------
  /** Bridge from Xalan/SAX namespace context to XNI namespace context
   */
  class XPath2NamespaceSupport 
    extends org.apache.xerces.util.NamespaceSupport
  {
	org.xml.sax.helpers.NamespaceSupport saxNamespaceSupport;

    /** Default constructor, not useful in this wrapper. */
    public XPath2NamespaceSupport() {
		throw new UnsupportedOperationException("Wrong c'tor call");    	
    } // <init>()

    /** 
     * Constructs a namespace context object and initializes it with
     * the prefixes declared in the specified context. Not useful in this
     * wrapper.
     */
    public XPath2NamespaceSupport(org.apache.xerces.xni.NamespaceContext context) {
		throw new UnsupportedOperationException("Wrong c'tor call");    	
    } // <init>(xni.NamespaceContext)
    
    /** 
     * Constructs a namespace context object and initializes it with
     * a SAX NamespaceSupport object. Use this c'tor.
     * */
    public XPath2NamespaceSupport(org.xml.sax.helpers.NamespaceSupport saxNS) {
		saxNamespaceSupport=saxNS;
    } // <init>(sax.helpers.NamespaceSupport)
    
    //
    // Public methods
    //

    // context management
    
    /**
     * Reset this Namespace support object for reuse.
     *
     * <p>It is necessary to invoke this method before reusing the
     * Namespace support object for a new session.</p>
     */
    public void reset(org.apache.xerces.util.SymbolTable symbolTable) {
    	// no-op
    } // reset(SymbolTable)

    /**
     * Start a new Namespace context.
     * <p>
     * Normally, you should push a new context at the beginning
     * of each XML element: the new context will automatically inherit
     * the declarations of its parent context, but it will also keep
     * track of which declarations were made within this context.
     * <p>
     * The Namespace support object always starts with a base context
     * already in force: in this context, only the "xml" prefix is
     * declared.
     *
     * @see #popContext
     */
    public void pushContext() {
    	// I don't know if this will ever be used during validation...
    	saxNamespaceSupport.pushContext();
    } // pushContext()


    /**
     * Revert to the previous Namespace context.
     * <p>
     * Normally, you should pop the context at the end of each
     * XML element.  After popping the context, all Namespace prefix
     * mappings that were previously in force are restored.
     * <p>
     * You must not attempt to declare additional Namespace
     * prefixes after popping a context, unless you push another
     * context first.
     *
     * @see #pushContext
     */
    public void popContext() {
    	// I don't know if this will ever be used during validation...
    	saxNamespaceSupport.popContext();
    } // popContext()

    // operations within a context.

    /**
     * Declare a Namespace prefix.
     * <p>
     * This method declares a prefix in the current Namespace
     * context; the prefix will remain in force until this context
     * is popped, unless it is shadowed in a descendant context.
     * <p>
     * To declare a default Namespace, use the empty string.  The
     * prefix must not be "xml" or "xmlns".
     * <p>
     * Note that you must <em>not</em> declare a prefix after
     * you've pushed and popped another Namespace.
     *
     * @param prefix The prefix to declare, or null for the empty
     *        string.
     * @param uri The Namespace URI to associate with the prefix.
     *
     * @return true if the prefix was legal, false otherwise
     *
     * @see #getURI
     * @see #getDeclaredPrefixAt
     */
    public boolean declarePrefix(String prefix, String uri) {
    	// I don't know if this will ever be used during validation...
    	return saxNamespaceSupport.declarePrefix(prefix,uri);
    } // declarePrefix(String,String):boolean

    /**
     * Look up a prefix and get the currently-mapped Namespace URI.
     * <p>
     * This method looks up the prefix in the current context.
     * Use the empty string ("") for the default Namespace.
     *
     * @param prefix The prefix to look up.
     *
     * @return The associated Namespace URI, or null if the prefix
     *         is undeclared in this context.
     *
     * @see #getDeclaredPrefixAt
     */
    public String getURI(String prefix) {
    	return saxNamespaceSupport.getURI(prefix);
    } // getURI(String):String



    /**
     * Look up a namespace URI and get one of the mapped prefix.
     * <p>
     * This method looks up the namespace URI in the current context.
     *
     * @param uri The namespace URI to look up.
     *
     * @return one of the associated prefixes, or null if the uri
     *         does not map to any prefix.
     *
     * @see #getPrefix
     */
    public String getPrefix(String uri) {
    	return saxNamespaceSupport.getPrefix(uri);
    } // getURI(String):String


    /**
     * Return a count of all prefixes currently declared, including
     * the default prefix if bound.
     */
    public int getDeclaredPrefixCount() {
    	java.util.Enumeration e=saxNamespaceSupport.getDeclaredPrefixes();
		int count=0;
		while(e.hasMoreElements())
		{
			++count;
			e.nextElement();
		}
		return count;
    } // getDeclaredPrefixCount():int

    /** 
     * Returns the prefix at the specified index in the current context.
     * %REVIEW% Massively inefficient in this implementation.
     */
    public String getDeclaredPrefixAt(int index) {
    	java.util.Enumeration e=saxNamespaceSupport.getDeclaredPrefixes();
    	String result=null;
		for(; index>=0;--index)
		{
			if(e.hasMoreElements())
				result=(String)e.nextElement();
			else
				return null;
		}
		return result;
    } // getDeclaredPrefixAt(int):String

    /**
     * Returns the parent namespace context or null if there is no
     * parent context. The total depth of the namespace contexts 
     * matches the element depth in the document.
     * <p>
     * <strong>Note:</strong> This method <em>may</em> return the same 
     * NamespaceContext object reference. The caller is responsible for
     * saving the declared prefix mappings before calling this method.
     */
    public NamespaceContext getParentContext() {
    	// I suppose I could construct a new XPath2NamespaceSupport
    	// wrapped around the SAX parent context. I'd rather not
    	// unless I must.
		throw new UnsupportedOperationException("Not supported; involves further crossing XNI/SAX boundaries");    	
    } // getParentContext():NamespaceContext

  } // class XPath2NamespaceSupport
  
} // XPath2Type

