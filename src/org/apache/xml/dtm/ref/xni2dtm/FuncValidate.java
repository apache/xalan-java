/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.xni2dtm.XNI2DTM;
import org.apache.xml.dtm.ref.xni2dtm.DTM2XNI;
import org.apache.xpath.XPathContext;

/** Temporary extension function, prototyping proposed XPath2 FuncValidate.
 * 
 * This may be moved back to XSLT/XQuery, or may be folded all the way down
 * as an operation implied by low-level expression syntax, depending on what
 * happens in the Working Group.
 * 
 * %REVIEW% Current code validates only a single root element. If we want more,
 * we can do it but it'll take a bit more coding.
 * 
 * %BUG% Need to change this to write into the RTFDTM -- which should
 * be an instance of XNI2DTM for this purpose -- rather than always
 * instantiating a new DTM.
 * */
public class FuncValidate {
	private static final boolean JJK_USE_RTFDTM=true; // validate into shared DTM
	private static final boolean JJK_DUMMY_CODE=true; // debugging hook
	
	
	public static Node eval(org.apache.xalan.extensions.ExpressionContext context,
		Node root) 
	{
		return eval(context,root,null);
	}
	public static Node eval(org.apache.xalan.extensions.ExpressionContext expressionContext,
		Node root, String contextPath) 
	{
		// This happens to work in current code. It isn't really
		// documented. Future versions expect to expose it more elegantly,
		// according to Don Leslie. But since this extension is just
		// temporary, let's use the cheat... We know it's going to be a
		// particular inner class, which has an accessor to retrieve its
		// associated XPathContext, so we reach in and ask it to reach back.
		XPathContext xctxt = ((XPathContext.XPathExpressionContext)expressionContext).getXPathContext();
		
    	int sourceHandle=xctxt.getDTMHandleFromNode(root);
    	DTM sourceDTM=xctxt.getDTM(sourceHandle);

		DTM2XNI d2x=new DTM2XNI(sourceDTM,sourceHandle);

		// %REVIEW% Still need to do something with contextPath.
		// d2x is set up to synthesize a wrapper and then discard it again --
		// BUT this fails when one of the synthesized elements has attr requirements,
		// as Xerces apparently optimizes by not validating kids if the start-tag
		// isn't valid.
		// 
		// A better answer would be to set the validator's context;
		// Xerces agrees that this is a desirable feature but may not have
		// implemented it yet.
		if(contextPath!=null)
		{
			java.util.Vector d2xContext=new java.util.Vector();
			// xctxt not available to extensions?
			org.apache.xml.utils.PrefixResolver pfxresolver=xctxt.getNamespaceContext();
			
			java.util.StringTokenizer e=new java.util.StringTokenizer(contextPath,"/");
			while(e.hasMoreElements())
			{
				String name=(String)e.nextElement();
				org.apache.xml.utils.QName qn=new org.apache.xml.utils.QName(name,pfxresolver);
				d2xContext.addElement(qn);
			}
			d2x.setContext(d2xContext);
		}		

		// VALIDATION GOES HERE!
		// Sandy Gao recommends "create a customized parser configuration,
		// which contains an XNI event source, a validator, and (optionally)
		// a document handler." See "pipelines" in the XNI docs.
		// I don't really grok that yet, so this is a SWAG based on what
		// Elena Litani did for the DOM revalidator.
		//
		// If we need to suppress explicit xsi:*schemaLocation directives, recommended 
		// kluge is to install a entity-resolver into the pipe which delivered
		// a schema with appropriate targetNamespae and no details (or just empty???)
		//
		// If we need to suppress explicit xsi:type directives -- Xerces developers
		// agree with me that this is Inherently Stupid. Solution would be to pass
		// those through a secondary channel, eg by turning them into custom
		// annotations until they get back to XNI.
		XNISource xsrc;
		{
			// ISSUE: Do we need to explicitly normalize namespaces?
			
			/* Adapted from Xerces DOMRevalidator.
				Probably shouldn't be using this class, but
				I haven't yet gotten the Xerces team to give me
				a real stand-alone example I could model this on.
				
				I'm also REALLY not convinced this is a typical XNI setup;
				I'm surprised I have to muck directly with the validator
				rather than setting up a configuration that encapsulates
				the dataflow more completely. (On the other hand, I'm also
				surpised that Configuration doesn't seem to implement
				DocumentSource.
				*/
			DOMValidationConfigurationSwipedFromXerces xnipipe
				=new DOMValidationConfigurationSwipedFromXerces(null);
			xnipipe.setFeature(DOMValidationConfigurationSwipedFromXerces.VALIDATION,true);
			xnipipe.setFeature(DOMValidationConfigurationSwipedFromXerces.SCHEMA,true);
			org.apache.xerces.impl.xs.XMLSchemaValidator validator
				=new org.apache.xerces.impl.xs.XMLSchemaValidator();
			xnipipe.addComponent(validator); // so config's reset() hits it
			
			if(JJK_DUMMY_CODE)
			{
				/* %REVIEW% Code in this section is preliminary/bogus,
				   for debugging only */
				
				/* %ISSUE% 1) Is there such a thing as a base URI for this fragment?
					%BUG% It is very unclear that this fragment has a real base URI.
					Also, note that this call fails for RTFDTMs.
					
					(We may in fact need to set this otherwise to prevent
					xsi:schemaLocation from succeeding, unless we can tell
					Xerces not to honor that directive.. or filter it out
					in DTM2XNI, ugh. For now leave this unset to expose
					failure to reference the explicitly loaded GrammarPool.)
					*/
				//validator.setBaseURI(sourceDTM.getDocumentBaseURI());				
				
				/* %ISSUE% 2) Do we need to track mid-stream changes by issuing
					entity/locator information into the stream?
					%BUG% It is very unclear that this fragment has a real base URI.
					Also, we don't actually record that data node-by-node unless
					tooling support is turned on... and even then I'm not sure
					this is retained after a layer or two of RTFs.
				*/
				// No.
				
				/* %ISSUE% 3) XPath2 says schema location comes from the XPath context,
					*not* from the document. Need to assert this via the
					schemaLocation property, or preparse into a Grammar Pool
					and use that. (I expect the latter since other parts of
					XPath2/XQuery/XSLT2 also want to muck with schemas.)
					
					%TODO%: Need to add that to the XPathContext and
					Xalan startup?
				*/
				// Experimental, force it in manually just to see what happens
				xnipipe.setProperty(xnipipe.GRAMMAR_POOL,
					xctxt.getInScopeSchemaDefinitions().getGrammarPool());
							
				/* %ISSUE% 5) NOTE that we're making no effort to retain existing
					type info (per latest XPath data I've seen). If we were,
					we get into ugly issues w/r/t having to retain type on
					Literal Result Elements in the stylesheet, plus questions
					about what (if anything) happens when an LRE has
					an xsi:type. The XQUERY team is still flailing on this topic.
				*/
			}
			
			
			d2x.setDocumentHandler(validator);
			
			// Wrap up the pipeline for our use.
			// There ought to be a way to avoid passing all these parts...
			xsrc=new XNISource(d2x,xnipipe,validator,null);
		}
		
		// %REVIEW% %BUG%
		// I don't think we can make this incremental, attractive
		// as that thought might be -- the source DTM might be an RTF and
		// might Go Away. We probably _should_ make it an RTF DTM, but those
		// currently aren't supported for XNI.
		if(!JJK_USE_RTFDTM)
		{
			DTM newDTM=xctxt.getDTM(xsrc,
				true, // unique
				null, // whitespace filter
				false, // incremental -- not supported at this writing
				false // doIndexing -- open to debate
				);
			return newDTM.getNode(newDTM.getDocument());
		}
		else
		{
			XNI2DTM dtm=(XNI2DTM)xctxt.getRTFDTM();
    	    xsrc.setDocumentHandler(dtm);
        	xsrc.setErrorHandler(dtm);
            // XNI's document scanner does support incremental.
	        // Would require yet another flavor of incremental-source to
    	    // glue it to our APIs. For now, just run it to completion.
        	// MOVE THIS DOWN !!!!!
	        // %REVIEW%
	        
	        try
    	    {
	    	    xsrc.reset();
	        	xsrc.scanDocument(true);	        	
	        }
	        catch (RuntimeException re)
    	    {
        	  throw re;
	        }
    	    catch (Exception e)
        	{
	          throw new org.apache.xml.utils.WrappedRuntimeException(e);
    	    }

			// We need to retrieve this. Requires either removing our
			// block-getDocument-in-shared-DTMs hook (giving up a safety net)
			// or creating a separate retrieve-last-document-built call.
			// See SAX2DTM.getDocument for discussion.          
			return dtm.getNode(dtm.getDocument());
		}		
	}
}

