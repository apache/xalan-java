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
package org.apache.xalan.processor;

import java.net.URL;
import java.util.Stack;
import java.util.Vector;
import java.io.File;
import java.util.StringTokenizer;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemAttributeSet;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.XMLNSDecl;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathFactory;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.utils.PrefixResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.InputSource;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

// Java Compiler support.
import org.apache.xml.utils.synthetic.JavaUtils;

/**
 * <meta name="usage" content="advanced"/>
 * Initializes and processes a stylesheet via SAX events.
 * This differs from StylesheetHandler in adding a post-
 * processing stage which attempts to replace part or all
 * of the recursively-interpreted Templates collection with
 * Java code, which is generated, compiled, and patched
 * back into the tempate trees.
 * @see StylesheetHandler
 */
public class CompilingStylesheetHandler
  extends StylesheetHandler
{
	/** Constants for the "maythrow" field (gates whether catch
	 * statements are generated):
	 */
	final int MAY_THROW_SAX_EXCEPTION=0x01;
	
	
  /**
   * Create a StylesheetHandler object, creating a root stylesheet 
   * as the target.
   * @throws May throw TransformerConfigurationException if a StylesheetRoot 
   * can not be constructed for some reason.
   */
  public CompilingStylesheetHandler(TransformerFactoryImpl processor)
    throws TransformerConfigurationException
  {
    super(processor);
  }
  
  /**
   * Receive notification of the end of the document.
   * Run standard cleanup of the internal representation,
   * then start trying to replace that rep with custom code.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   */
  public void endDocument ()
    throws org.xml.sax.SAXException
  {
	  
//    try
//    {
	  
	  // Perform normal end-of-parse operations -- in particular, 
	  // the "composition" process which performs data gathering
	  // and optimization of the interpretable tree.
      super.endDocument();
      
      if(isStylesheetParsingComplete())
      {    
        Stylesheet current=getStylesheet();

		// Record the Templates as we compile them; this list gets
		// passed into the "bundling" code to copy their excutable code
		// into the .xsb file.
		// TODO: Can we just use the StylesheetRoot's list?
        Vector compiledTemplates=new Vector();

		// cache the typecast
        StylesheetRoot root=(StylesheetRoot)getStylesheetRoot();
        
		// For all templates used in this stylesheet (including
		// those from imported and included stylesheets), 
		// compile to Java. (Takes advantage of the fact that
		// ComposedStylesheet processing has already gathered
		// these and reconciled conflicts.)
		
		// New TemplateList being generated
		org.apache.xalan.templates.TemplateList newTl 
			= new org.apache.xalan.templates.TemplateList();
		// Iterate over contents of old TemplateList
		org.apache.xalan.templates.TemplateList.TemplateWalker tw
			=root.getTemplateListComposed().getWalker();
		
		// Scan all templates in old list, compile, insert into new
		ElemTemplate et;
		while ( null != (et = tw.next()) )
		{
			ElemTemplate ct = compileTemplate(et);
			
			// If compilation succeeds, use it; else fall back on interp
			newTl.setTemplate( (ct!=null) ? ct : et);
		}
		// Postprocess/reconcile list
		newTl.compose();
		// And make it active
		root.setTemplateListComposed(newTl);

		// TODO: Theoretically, we can now discard imports/includes
		// -- they're no longer pointing at the right ElemTemplates
		// anyway. There's discussion of doing so in any case when
		// they aren't needed for tooling support. Always do it here,
		// leave it to be handled there, or other?

/****** OLD APPROACH
		int nImports = root.getGlobalImportCount();
        for(int imp = 0; imp < nImports; imp++)
        {
          org.apache.xalan.templates.StylesheetComposed
            sheet = root.getGlobalImport(imp);

          // loop from sheet.recomposeTemplates
          // Scan both main and included stylesheets
          int nIncludes = sheet.getIncludeCountComposed();
          for(int k = nIncludes-1; k >= -1; k--)
          {
            Stylesheet included = sheet.getIncludeComposed(k);
            int n = included.getTemplateCount();
            for(int i = 0; i < n; i++)
            {
              ElemTemplate t=included.getTemplate(i);
              
              if(!(t instanceof CompiledTemplate))
              {
                ElemTemplate newT=compileTemplate(t);
                if(newT!=t)
                {
                  included.replaceTemplate(newT,i);
                  compiledTemplates.addElement(newT);
                }
              }
            }
          }
          // Need to rebuild each sheet's cache.
          sheet.recomposeTemplates(true); 
        }
        
        // After compiling I think we have to reconstruct the cached
        // "composed templates" set. 
        // NOTE: RECOMPOSE WAS A ONE-TIME OP
        // I've set up an alternate entry that allows me to flush
        // before composing. That could take over from the standard
        // entry point... Or flush might be made the new default
        // behavior; I don't know whether that would be appropriate.
        root.recomposeTemplates(true); 
 ****** OLD APPROACH */

		// TODO: Theoretically, we no longer need the Imported trees.
		// Eliminating them would save some bytes in memory and in the
		// .xsb bundle file. (They should be needed only for tooling 
		// support, which doesn't seem to apply to compiled operation.)
		// Try that _AFTER_ the compile loop has been revived!
		
        // TODO: Should bundling occur elsewhere or be optional?
        CompiledStylesheetBundle.createBundle(root,compiledTemplates);
      }
	  
//    }
//    catch(TransformerException te)
//    {
//      throw new org.xml.sax.SAXException(te);
//    }
  }
  
  
  // Minor optimization: Only need to look these up once
  private static final org.apache.xml.utils.synthetic.Class sClassCompiledTemplate=org.apache.xml.utils.synthetic.Class.forClass(org.apache.xalan.processor.CompiledTemplate.class);
  private static final org.apache.xml.utils.synthetic.Class sClassObjectArray=org.apache.xml.utils.synthetic.Class.forClass(Object[].class);

  
  /**
    Analyse an <xsl:template> tree, converting some (most?)
    of its recursive evaluate() behavior into straight-line
    Java code. That code is then compiled and instantiated
    to produce a new "equivalent" object, which can be used
    to replace the original Template.
    <p>
    Note that the compiled Template may have to reference
    children that we don't yet know how to compile. This
    is done by copying references to those kids into a vector,
    and having the generated code invoke them via offsets
    in that vector.
    <p>
    At this time, the compiler's rather simpleminded. No
    common subexpression extraction is performed, and no
    attempt is made to optimize across stylesheets. We're
    just flattening some of the code and reorganizing it
    into direct SAX invocations.
    <p>
    Literal result elements become SAX begin/endElement
    Context-insensitive attributes become literal assignment
        to the attribute trees.
    xsl:choose and xsl:for-each may be flattened
    Namespace declarations become SAX prefix operations.
    <p>
    Other nodes simply have their .evaluate() invoked
    TODO: Their children really should be walked for further compilation opportunities.
    */
  ElemTemplate compileTemplate(ElemTemplate source)
  {
    ElemTemplate instance=source;

    String className=generateUniqueClassName("org.apache.xalan.processor.ACompiledTemplate");
    
    try
    {
        // public class ACompiledTemplate000... 
                // extends CompiledTemplate (partly abstract superclass)
        org.apache.xml.utils.synthetic.Class tClass=
            org.apache.xml.utils.synthetic.Class.declareClass(className);
        tClass.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        tClass.setSuperClass(sClassCompiledTemplate);

        // public constructor: Copy values from original
        // template object, pick up "uncompiled children"
        // array from compilation/instantiation process.
        org.apache.xml.utils.synthetic.reflection.Constructor ctor=
            tClass.declareConstructor();
        ctor.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        ctor.addParameter(tClass.forClass(ElemTemplate.class),"original");
        ctor.addParameter(sClassObjectArray,"interpretArray");
        ctor.getBody().append(
                        "super(original,\n"
                        +'\t'+source.getLineNumber()+','+source.getColumnNumber()+",\n"
                        +'\t'+makeQuotedString(source.getPublicId())+",\n"
                        +'\t'+makeQuotedString(source.getSystemId())+",\n"
                        +"\tinterpretArray);\n"
                  );

        // vector built during compilation, winds up in m_interpretArray
        Vector interpretVector=new Vector();

        // Now for the big guns: the execute() method is where all the
        // actual work of the template is performed, and is what we've
        // really set out to compile.
        
        //   public void execute(TransformerImpl transformer, 
        //      Node sourceNode, QName mode)
        org.apache.xml.utils.synthetic.reflection.Method exec=
            tClass.declareMethod("execute");
        exec.setModifiers(java.lang.reflect.Modifier.PUBLIC);
        exec.addParameter(
            tClass.forClass(org.apache.xalan.transformer.TransformerImpl.class),
            "transformer");
        exec.addParameter(
            tClass.forClass(org.w3c.dom.Node.class),"sourceNode");
        exec.addParameter(
            tClass.forClass(org.apache.xml.utils.QName.class),"mode");
        exec.addExceptionType(
            tClass.forClass(javax.xml.transform.TransformerException.class));

        // If there are no kids, the body is a no-op.
        ElemTemplateElement firstChild = source.getFirstChildElem();
        if(null == firstChild)
          {
            exec.getBody().append("//empty template");
          }
        else
        {
          // Body startup
          // **** FIRST DRAFT, I'm continuing to use ResultTreeHandler
          // In future we might want to move toward direct SAX generation,
          // (though that requires reordering data into normal SAX
          // event order, generating trace events, and figuring
          // out how to cooperate w/ template fragments not yet
          // switched over to compiled mode)... or to raw text output,
          // though I doubt that's significantly faster than SAX and it's
          // definitely less convenient if further processing is desired.
          StringBuffer body=exec.getBody().append(
              "if(transformer.S_DEBUG)\n"
              +"  transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);\n"
              +"org.apache.xalan.transformer.ResultTreeHandler rhandler = transformer.getResultTreeHandler();\n"
              +"org.xml.sax.ContentHandler saxChandler = rhandler.getContentHandler();\n"
              +"if(null == sourceNode) {\n"
              // throws(javax.xml.transform.TransformerException
              +"  transformer.getMsgMgr().error(this, sourceNode,\n" 
              +"    org.apache.xalan.res.XSLTErrorResources.ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES);\n"
              //sourceNode is null in handleApplyTemplatesInstruction!
              +"  return; }\n"
              +"org.apache.xpath.XPathContext xctxt = transformer.getXPathContext();\n"
              +"// Check for infinite loops if requested\n"
              +"boolean check = (transformer.getRecursionLimit() > -1);\n"
              +"if (check)\n"
              +"  transformer.getStackGuard().push(this, sourceNode);\n"
              +"String avtStringedValue; // ***** Optimize away?\n\n"
                  // Establish dynamic namespace context for this invocation
                          +"org.xml.sax.helpers.NamespaceSupport nsSupport=new org.xml.sax.helpers.NamespaceSupport();\n"
                          +"org.xml.sax.helpers.NamespaceSupport savedNsSupport=(org.xml.sax.helpers.NamespaceSupport)m_nsThreadContexts.get(Thread.currentThread());\n"
                          +"m_nsThreadContexts.put(Thread.currentThread(),nsSupport);\n"
                          );
          
          compileChildTemplates(source,body,interpretVector);
          
                  // Body Cleanup
          body.append(
                  // Restore dynamic namespace context for this invocation
                          "if(null!=savedNsSupport) m_nsThreadContexts.put(Thread.currentThread(),savedNsSupport);\n"
                          +"else m_nsThreadContexts.remove(Thread.currentThread());\n\n"
              +"// Decrement infinite-loop check\n"
              +"if (check)\n"
              +"  transformer.getStackGuard().pop();\n"
              );

        }
        
        // Compile the new java class. Note that any existing class
		// by the same name will be walked upon... which is why I'm
		// going to the trouble of generating unique classnames.
        // TODO: ***** ISSUE: Where write out the class? Parameterize.
        // Needs to be somewhere on the classpath, or we need a classloader.
        Class realclass=compileSyntheticClass(tClass,".");
        // Prepare the array of execute()ables
        Object[] eteParms=new Object[interpretVector.size()];
        interpretVector.copyInto(eteParms);
        // Instantiate -- note that this will be a singleton,
        // as each template is probably unique
        org.apache.xml.utils.synthetic.reflection.Constructor c=
            tClass.getConstructor(ctor.getParameterTypes());    
        Object[] parms={source,eteParms};
        instance=(ElemTemplate)c.newInstance(parms);
    }
    catch(org.apache.xml.utils.synthetic.SynthesisException e)
    {
        System.out.println("CompilingStylesheetHandler class synthesis error");
        e.printStackTrace();
    }
    catch(java.lang.IllegalAccessException e)
    {
        System.out.println("CompilingStylesheetHandler class comilation error");
        e.printStackTrace();
    }
    catch(java.lang.NoSuchMethodException e)
    {
        System.out.println("CompilingStylesheetHandler constructor resolution error");
        e.printStackTrace();
    }
    catch(java.lang.InstantiationException e)
    {
        System.out.println("CompilingStylesheetHandler constructor invocation error");
        e.printStackTrace();
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
        System.out.println("CompilingStylesheetHandler constructor invocation error");
        e.printStackTrace();
    }

    return instance;
  }
  
  int compileElemTemplateElement(ElemTemplateElement kid,StringBuffer body,Vector interpretVector)
  {
	int maythrow=0;  
    ++uniqueVarSuffix; // Maintain unique variable naming
      
        switch(kid.getXSLToken())
        {
        case Constants.ELEMNAME_LITERALRESULT:
			maythrow=compileElemLiteralResult((ElemLiteralResult)kid,body,interpretVector);
            break;

	// TODO: ***** Redirection of attr value not working yet.
	// TODO: ***** Attrs should be preprocessed (SAX-ordered)
	//case Constants.ELEMNAME_ATTRIBUTE:
        //    compileElemAttribute((ElemAttribute)kid,body,interpretVector);
        //    break;
                
        default:
        // Safety net: We don't yet know how to compile this
        // type of node, so instead we'll pass it into the
        // compiled instance and invoke it interpretively.
        int offset=interpretVector.size();
        interpretVector.addElement(kid);
        body.append(
            "((org.apache.xalan.templates.ElemTemplateElement)m_interpretArray["+offset+"]).execute(transformer,sourceNode,mode);\n"
            );
                break;
        }
		
		return maythrow;
  }  
  
  int compileElemLiteralResult(ElemLiteralResult ele,StringBuffer body,Vector interpretVector)
  {
	int maythrow=0;
	
    ++uniqueVarSuffix; // Maintain unique variable naming
      
    body.append("rhandler.startElement(\""
                +ele.getNamespace()+"\",\""
                +ele.getLocalName()+"\",\""
                +ele.getRawName()+"\");\n");
    
    // Handle xsl:use-attribute-sets
    // expand ElemUse.execute(transformer, sourceNode, mode);
    compileUseAttrSet(ele,body,interpretVector);
    
    // Add stylesheet namespace declarations -- unrolling of
    // ElemTemplateElement.executeNSDecls, plus additional logic to
    // track that information within the generated code. 
    // (I really wish I could avoid the latter replication of data. 
    // Unfortunately the possibility of <xsl:attribute name="prefix:foo"/> 
    // with no explicit namespace  requires that we be able to resolve a 
    // prefix in terms of the stylesheet, _not_ in terms of the output 
    // document... which means we need to track the stylesheet's NS context.)
    Vector prefixTable=ele.getPrefixes();
    int n = prefixTable.size();
    boolean newNSlevel=(n>0);
    if(newNSlevel)
        body.append("nsSupport.pushContext();\n");
    for(int i = 0; i < n; i++)
    {
      XMLNSDecl decl = (XMLNSDecl)prefixTable.elementAt(i);
      // Output document
      if(!decl.getIsExcluded())
          body.append(
            "rhandler.startPrefixMapping(\""
            +decl.getPrefix()+"\",\""
            +decl.getURI()+"\");\n"
            );
      // CompiledTemplate state
        body.append(
            "nsSupport.declarePrefix(\""
            +decl.getPrefix()+"\",\""
            +decl.getURI()+"\");\n"
            );
    }    
        
    // Process AVTs.
    // TODO: Should we be checking for excluded namespace prefixes?
    // ***** That wasn't done in non-compiled version, but was an open issue.
    java.util.Enumeration avts=ele.enumerateLiteralResultAttributes();
    if (avts!=null) 
    {
        while(avts.hasMoreElements())
        {
            org.apache.xalan.templates.AVT avt = (org.apache.xalan.templates.AVT)avts.nextElement();
            String avtValueExpression=null;
            boolean literal=avt.isContextInsensitive();
            
            if(literal)
            {
                // Literal value, can fully resolve at compile time.
                // Exception won't be thrown, but we've gotta catch
                try{
                    avtValueExpression=makeQuotedString(
                        avt.evaluate(null,null,null)
                        );
                }catch(TransformerException e)
                {
                }
            }
            else
            {
                // Expression. Must resolve at runtime.
                // TODO: ***** It might be possible to unwind this too.
                int offset=interpretVector.size();
                interpretVector.addElement(avt);
                body.append(
                    "avtStringedValue=((org.apache.xalan.templates.AVT)"
                    +"(m_interpretArray["+offset+"])"
                    +").evaluate(xctxt,sourceNode,this,new StringBuffer());\n"
                    +"if(null!=avtStringedValue)\n{\n"
                    );
                avtValueExpression="avtStringedValue";
            }
            
            body.append(
                "rhandler.addAttribute(\""
                +avt.getURI()+"\",\""
                +avt.getName()+"\",\""
                +avt.getRawName()
                +"\",\"CDATA\","
                +avtValueExpression
                +");\n");

            // Match the open brace, if one was issued
            if(!literal)
                body.append("} // endif\n");
            } // end while more AVTs
       } // end process AVTs

    // Process children
    // TODO:***** "Process m_extensionElementPrefixes && m_attributeSetsNames"

	// TODO: Should maythrow be passed outward, unwinding try/catch?
	// maythrow !=
    compileChildTemplates(ele,body,interpretVector);

    // Close the patient
    body.append("rhandler.endElement(\""
                +ele.getNamespace()+"\",\""
                +ele.getLocalName()+"\",\""
                +ele.getRawName()+"\");\n");
    if(newNSlevel)
        body.append("nsSupport.popContext();\n");
	
	return maythrow | MAY_THROW_SAX_EXCEPTION;
  }

  // Detect and report AttributeSet loops.
  Stack attrSetStack=new Stack();
    
  void compileUseAttrSet(ElemTemplateElement ete,StringBuffer body,Vector interpretVector)
  {
    ++uniqueVarSuffix; // Maintain unique variable naming
    
    body.append(
        "if(transformer.S_DEBUG)\n"
        +"  transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);\n"
        );
    // expand ElemUse.applyAttrSets(transformer, getStylesheetComposed(),
    //               ele.getUseAttributeSets(), sourceNode, mode);
    // ***** DOES THIS CAST NEED TO BE CHECKED?
    org.apache.xml.utils.QName[] attributeSetsNames=((org.apache.xalan.templates.ElemUse)ete).getUseAttributeSets();
    if(null != attributeSetsNames)
    {
        org.apache.xalan.templates.StylesheetRoot stylesheet=ete.getStylesheetRoot();
        int nNames = attributeSetsNames.length;
        for(int i = 0; i < nNames; i++)
        {
            org.apache.xml.utils.QName qname = attributeSetsNames[i];
            Vector attrSets = stylesheet.getAttributeSetComposed(qname);
            int nSets = attrSets.size();
            for(int k = 0; k < nSets; k++)
            {
                ElemAttributeSet attrSet = 
                    (ElemAttributeSet)attrSets.elementAt(k);
                // expand ElemAttributeSet.execute(transformer, sourceNode, mode);
                if(attrSetStack.contains(attrSet))
                {
                    // TODO: ***** WHAT'S THE RIGHT WAY TO REPORT THIS ERROR?
                    String errmsg="TEMPLATE COMPILATION ERROR: ATTRIBUTE SET RECURSION SUPPRESSED in "+attrSet.getName().getLocalPart();
                    /**/System.err.println(errmsg);
                    /**/body.append("// ***** "+errmsg+" *****/\n");
                    /**/return;
                    //throw new TransformerException(XSLMessages.createMessage(XSLTErrorResources.ER_XSLATTRSET_USED_ITSELF, new Object[]{attrSet.getName().getLocalPart()})); //"xsl:attribute-set '"+m_qname.m_localpart+
                }
                attrSetStack.push(attrSet);

                // Recurse, since attrsets can reference attrsets
                compileUseAttrSet(attrSet,body,interpretVector);
                        
                ElemAttribute attr = (ElemAttribute)attrSet.getFirstChild();
                while(null != attr)
                {
                    compileElemTemplateElement(attr,body,interpretVector);
                    attr = (ElemAttribute)attr.getNextSibling();
                }
                    
                attrSetStack.pop();
            }
        }
    }
  }
  
  String compileAVTvalue(org.apache.xalan.templates.AVT avt,StringBuffer body,Vector interpretVector)
  {
      // Literal string is easy -- except for potential of " within "".
      if(avt.isContextInsensitive())
          try
          {
            return makeQuotedString(avt.evaluate(null,null,null));
          } catch(TransformerException e)
          {
              // Should never arise
              String s=">UNEXPECTED ERROR evaluating context-insensitive AVT<";
              System.err.println(s);
              e.printStackTrace();
              return "\""+s+'"';
          }
      
      // Otherwise no compilation yet, just reference and return expression.
      // Note that we do _not_ code-gen directly into the body, due to
      // some concerns about where this might be used. 
      // YES, this is inconsistant. 
      int offset=interpretVector.size();
      interpretVector.addElement(avt);
      return 
          "( ((org.apache.xalan.templates.AVT)m_interpretArray["
		  +offset
		  +"]).evaluate(transformer.getXPathContext(),sourceNode,this,new StringBuffer()) )"
          ;
  }
  
  // Wrap quotes around a text string. 
  // Escapes any contained quotes, converts null to the string "null". 
  // Used to prepare literal string arguments.
  String makeQuotedString(String in)
  {
        if(in==null)
                return "null";
        
    StringBuffer out=new StringBuffer("\""); // don't use '"', it's taken as int
      
    int startpos=0,quotepos;
    for(quotepos=in.indexOf('"',startpos);
        quotepos!=-1;
        startpos=quotepos+1,quotepos=in.indexOf('"',startpos))
    {    
        out.append(in.substring(startpos,quotepos)).append('\\').append('\"');
    }
    out.append(in.substring(startpos)).append('"');
    return out.toString();
  }

  // Get the children of the xsl:attribute element as the string value.
  // String val = transformer.transformToString(this, sourceNode, mode);
  // Returns string econtaiing result expression
  String compileTransformToString(ElemTemplateElement ea,StringBuffer body,Vector interpretVector)
  {
    ++uniqueVarSuffix; // Maintain unique variable naming
    String savedResultTreeHandler="savedResultTreeHandler"+uniqueVarSuffix;
    String shandler="shandler"+uniqueVarSuffix;
    String sw="sw"+uniqueVarSuffix;
    String sfactory="sfactory"+uniqueVarSuffix;
    String format="format"+uniqueVarSuffix;
    String serializer="serializer"+uniqueVarSuffix;
    String ioe="ioe"+uniqueVarSuffix;
    
    body.append("\n// Begin transformToString (probably of attribute contents)\n"
                +"// by redirecting output into a StringWriter.\n"
                +"org.apache.xalan.transformer.ResultTreeHandler "+savedResultTreeHandler+"=rhandler;\n"
                +"org.xml.sax.ContentHandler "+shandler+";\n"
                +"java.io.StringWriter "+sw+";\n"
                
                +"try\n{\n"
                +"org.apache.xml.org.apache.xalan.serialize.SerializerFactory "+sfactory+"=org.apache.xml.org.apache.xalan.serialize.SerializerFactory.getSerializerFactory(\"text\");\n"
                +sw+"=new java.io.StringWriter();\n"
                +"org.apache.xalan.templates.OutputProperties "+format+"=new org.apache.xalan.templates.OutputProperties();\n"
                +format+".setPreserveSpace(true);\n"
                +"org.apache.xml.org.apache.xalan.serialize.Serializer "+serializer+"="+sfactory+".makeSerializer("+sw+","+format+");\n"
                +shandler+"="+serializer+".asContentHandler();\n"
                +"}\ncatch (java.io.IOException "+ioe+")\n{\n"
                +"throw new javax.xml.transform.TransformerException("+ioe+");\n}\n"
                // TODO: ***** DO WE NEED transformer.setResultTreeHandler()?
                +"rhandler=new org.apache.xalan.transformer.ResultTreeHandler(transformer,"+shandler+");\n\n"
                +"rhandler.startDocument();\n"
                +"\n//unwind executeChildTemplates\n"
                );
    compileChildTemplates(ea,body,interpretVector);
    body.append(
                "\nrhandler.flushPending();\n"
                +"rhandler.endDocument();\n"
                +"rhandler="+savedResultTreeHandler+";\n"
                +"//End transformToString unwind; result in "+sw+"\n\n"
                );
    return "("+sw+".toString())";
  }
  
  
  void compileElemAttribute(ElemAttribute ea,StringBuffer body,Vector interpretVector)
  {
    ++uniqueVarSuffix; // Maintain unique variable naming
    String attrName="attrName"+uniqueVarSuffix;
    String val="val"+uniqueVarSuffix;
    String attrNameSpace="attrNameSpace"+uniqueVarSuffix;
    String prefix="prefix"+uniqueVarSuffix;
    String ns="ns"+uniqueVarSuffix;
    String attributeHandled="attributeHandled"+uniqueVarSuffix;
    String nsprefix="nsprefix"+uniqueVarSuffix;
    String ex="ex"+uniqueVarSuffix;
    String localName="localName"+uniqueVarSuffix;
    
    // The attribute name has to be evaluated as an AVT.
    // We may add/alter the prefix later.
    //String origAttrName="origAttrName"+uniqueVarSuffix;
    String origAttrName=compileAVTvalue(ea.getName(),body,interpretVector);

    // If they are trying to add an attribute when there isn't an 
    // element pending, it is an error.
    // TODO: ****** Could these tests occur _before_ we transform? Factor?
    body.append(
        "if(null == rhandler.getPendingElementName())\n"
        +"{\n"
        +"transformer.getMsgMgr().warn(org.apache.xalan.res.XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME, new Object[]{"+origAttrName+"}); \n"
        +"// warn(templateChild, sourceNode, \"Trying to add attribute after element child has been added, ignoring...\");\n"
        +"}\n"
        );
        
        // This check was done in the interpretive code... is it Really Needed?
        if(null==origAttrName)
                return;

    body.append("boolean "+attributeHandled+"=false;\n");

    // The attribute name has to be evaluated as an AVT,
    // and gets stashed because we may add a prefix later
    body.append("String "+attrName+"="+origAttrName+";\n");
    
    // Get the children of the xsl:attribute element as the string value.
    // String val = transformer.transformToString(this, sourceNode, mode);
    String strval=compileTransformToString(ea,body,interpretVector);
    body.append("String "+val+"="+strval+";\n");
    
    // If they are trying to add an attribute when there isn't an 
    // element pending, it is an error.
    // TODO: ****** Could these tests occur _before_ we transform?
    body.append(
        "if(null == rhandler.getPendingElementName())\n"
        +"{\n"
        +"transformer.getMsgMgr().warn(org.apache.xalan.res.XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME, new Object[]{"+origAttrName+"}); \n"
        +"// warn(templateChild, sourceNode, \"Trying to add attribute after element child has been added, ignoring...\");\n"
        +"}\n"
        +"if(null=="+attrName+")\n return;\n\n"
        );
    
    // Namespace is also an AVT, which means we can't count on it having been
    // fully evaluated at stylesheet load time.
    body.append(
        "String "+attrNameSpace+"=null; // by default\n"
        );
    if(null!=ea.getNamespace()) // Can/must decide at compile time!
    {
        String avtValueExpression=compileAVTvalue(ea.getNamespace(),body,interpretVector);
        body.append(
            attrNameSpace+"="+avtValueExpression+";\n"
            +"if(null!="+attrNameSpace+" && "+attrNameSpace+".length()>0)\n"
            +"{\n"
            +"  String "+prefix+"=rhandler.getPrefix("+attrNameSpace+");\n"
            +"  if(null=="+prefix+")\n"
            +"  {\n"
            +"    "+prefix+"=rhandler.getNewUniqueNSPrefix();\n"
            +"    rhandler.startPrefixMapping("+prefix+","+attrNameSpace+");\n"                    
            +"  }\n"
            +"  "+attrName+"=("+prefix+"+\':'+org.apache.xml.utils.QName.getLocalPart("+attrName+"));\n"
            +"}\n"
            );
    }
    // Else: Is attribute xmlns type?
    // TODO: ***** Must this retest for non-null namespace, due to
    // compile-time decision above? Shouldn't really need to since the
    // qname test ought to cover it, but....
    body.append(
        "if(org.apache.xml.utils.QName.isXMLNSDecl("+origAttrName+"))\n"
        +"{ // Just declare namespace prefix \n"
        +"  String "+prefix+"=org.apache.xml.utils.QName.getPrefixFromXMLNSDecl("+origAttrName+");\n"
        +"  String "+ns+"=rhandler.getURI("+prefix+");\n"
        +"  if(null=="+ns+")\n"
        +"    rhandler.startPrefixMapping("+prefix+","+val+");\n"
        );
    // Here the original code returned from ea.execute. Since
    // we're expanding inline, we can't return, so instead we gate
    // the later code via a boolean flag. (Could generate a jump to a
    // label instead, but I think this is clearer.)
    body.append("  "+attributeHandled+"=true;\n");
    body.append("}\n"
                +"else\n{\n"
                +"  String "+nsprefix+"=org.apache.xml.utils.QName.getPrefixPart("+origAttrName+");\n"
                +"  if(null=="+nsprefix+") "+nsprefix+"=\"\";\n"
                
                //  attrNameSpace = getNamespaceForPrefix(nsprefix);
                // Handles the case where name was specified, namespace wasn't
                // Resolves prefix in terms of STYLESHEET context, not input
                // or output doc context... which requires that we track
                // that context in our compiled code. Grrr.
                +attrNameSpace+"=nsSupport.getURI("+nsprefix+");\n"
                
                // The if here substitutes for early returns in original code
                +"if(!"+attributeHandled+")\n{\n"
                +"String "+localName+"=org.apache.xml.utils.QName.getLocalPart("+attrName+");\n"
                +"rhandler.addAttribute("+attrNameSpace+","+localName+","+attrName+",\"CDATA\","+val+");\n"
                +"} //end attributeHandled\n"
                +"} //end else\n"
                );
  }

  
  // Issue: When unrolling code, variable name resuse becomes an issue.
  // Java doesn't permit local name scoping such as
  //    int i; { int i; }
  // so we have to generate unique names. We don't really need a stack,
  // since we don't care whether the suffix matches level of unrolling.
  int uniqueVarSuffix=0;
  
  void compileChildTemplates(ElemTemplateElement source,StringBuffer body,Vector interpretVector)
  {      
    int maythrow=0;
	
    ++uniqueVarSuffix; // Maintain unique variable naming
      
    // If no kids, no code gen.
    if(source.getFirstChildElem()!=null)
    {
      // ***** TransformerImpl.executeChildTemplates does a
      // bunch of additional setup/shutdown work. Since I
      // don't know otherwise, I'm assuming I have to
      // emulate that work as part of unwinding this.
      //
      // Set up the TransformerImpl context:
      String savedLocatorName="savedLocator"+uniqueVarSuffix;
      String varstackName="varstack"+uniqueVarSuffix;
      body.append(
        "\n// Unwound Transformer.executeChildTemplates: //\n\n"
        +"// We need to push an element frame in the variables stack,\n" 
        +"// so all the variables can be popped at once when we're done.\n"
        +"org.apache.xpath.VariableStack "+varstackName+" = transformer.getXPathContext().getVarStack();\n"
        +varstackName+".pushElemFrame();\n"
        +"javax.xml.transform.SourceLocator "+savedLocatorName+" = xctxt.getSAXLocator();\n"
        );

      body.append("try {\n\n");

      // Process the kids
      for(ElemTemplateElement kid=source.getFirstChildElem();
          kid!=null;
          kid=kid.getNextSiblingElem())
         {
			//TODO: NEED EQUIVALENT? This Node is Going Away...
			// body.append("transformer.pushElemTemplateElement(kid);\n");
		  
			maythrow|=compileElemTemplateElement(kid,body,interpretVector);

			//TODO: NEED EQUIVALENT? This Node is Going Away...
			// body.append("transformer.popElemTemplateElement(kid);\n");
         }

	  // End the class wrapper. 
	  // TODO: Should "maythrow" be returned and processed @ outermost compile?
	  body.append("\n\n}\n");
	  if(0!=(maythrow & MAY_THROW_SAX_EXCEPTION))
		body.append("catch(org.xml.sax.SAXException se) {\n"
					+"  throw new javax.xml.transform.TransformerException(se);\n"
					+"}\n"
					);
      body.append(
		"finally {\n"
        +"  xctxt.setSAXLocator("+savedLocatorName+");\n"
        +"  // Pop all the variables in this element frame.\n"
        +"  "+varstackName+".popElemFrame();\n"
        +"}\n"
        );
    }

  }
 
  
  /** Run this class description through the Java compiler,
   * and patch the result back into the Synthetic system.
   * Note that classLocation is treated as a directory iff
   * it ends in FileLocator; if not, it's treated as a file
   * name and output is written to the directory that file
   * would be found in (possibly relative). However, "."
   * is treated as being found in itself rather than in "..".
   * TODO: ***** Move polished version into org.apache.xml.utils.synthetic.Class
   * TODO: Should we use a classloader rather than std. classpath?
   */
  Class compileSyntheticClass(org.apache.xml.utils.synthetic.Class tClass, String classLocation)
  {
    Class resolved=null;
    // Write class relative to specified starting location
    // (which should be on the classpath, so we can load
    // the resulting class!).

    String filename=classLocation;
    
    int fnstart=filename.lastIndexOf(File.separator);
    StringBuffer subdir=new StringBuffer(
        (fnstart>=0)
        ? filename.substring(0,fnstart)
        : ".");
    StringTokenizer parts=
        new StringTokenizer(tClass.getPackageName(),".");
    while(parts.hasMoreTokens())
    {
        subdir.append(File.separator).append(parts.nextToken());
    }
    if(fnstart<0)
      subdir.append(File.separator);

    File jfile=new File(subdir.toString());
    jfile.mkdirs();
    
    subdir.append(tClass.getShortName()).append(".java");
    filename=subdir.toString();
    jfile=new File(filename);

    // Write Java source
    try {
        java.io.PrintWriter out=
            new java.io.PrintWriter(new java.io.FileWriter(filename));
        tClass.toSource(out,0);
        out.close();
    }
    catch(java.io.IOException e)
    {
        System.err.println("ERR: File open failed for "+
            filename);
        e.printStackTrace();
        return null;
    }

    // Try to pick up the same classpath we're executing under. That
    // ought to include everything in Xalan and the standard libraries.
    String classpath=System.getProperty ("java.class.path");
	
    // If compiling with the -g switch (Java debugging), we should retain 
    // the Java source code to support debugging into the synthesized class.
    // Some additional diagnostics are also turned on as a side effect.
    // TODO: Find a better place to put the debugging control. Property? Parm?
    String javac_options=
	System.getProperty("org.apache.xalan.processor.CompilingStylesheetHandler.options","");
    boolean debug=(javac_options.indexOf("-g")>=0);

    // Run the compilation. Encapsulates the fallbacks and
    // workarounds needed to achieve this in various environments.
    JavaUtils.setDebug(debug);
    boolean compileOK=
                JavaUtils.JDKcompile(filename,classpath);
        
    if (compileOK)
    {
        if(debug)
            System.err.println("\tCompilation successful. Debug specified, .java file retained.");
        else if(jfile.exists())
            jfile.delete();
           
        // Now try to load it!
        try {
            resolved=Class.forName(tClass.getName());
            tClass.setRealClass(resolved);
        }
        catch(ClassNotFoundException e)
        {
            System.err.println("ERR: synthesized Template class load failed for "+
                tClass.getName());
            e.printStackTrace();
        }
        catch(org.apache.xml.utils.synthetic.SynthesisException e)
        {
            System.err.println("ERR: synthesized Template class realization failed for "+
                tClass.getName());
            e.printStackTrace();
        }
    }
    else
    {
        if(debug)
            System.err.println("\tTemplate compilation failed; retaining .java file");
        // This should probably be an exception instead
        System.err.println("ERR: Java compilation failed for "+
                filename);
                
        System.exit(1);
    }
    
    return resolved;
  }

  /** Generate a mostly-unique class name.
   * The longterm answer here is File.createTempFile... but that
   * requires JDK 1.2 or above.
   */
  static int templateCounter=0;
  String generateUniqueClassName(String basename)
  {
    //TODO: ***** ISSUE: CLASS NAMING. This is kluged
    //as a temporary measure; we need to think about a
    //more formal solution. Each compilation of each
    //stylesheet winds up with its own set of classes, 
    //which need to be made globally unique if they aren't
    //to collide with other compilations loaded into the same
    //JVM. We really need classnames based on something like
    //UUID or GID, combining the source stylesheet's URI and locator, 
    //where it was processed, exactly when it was processed
    //... and then going the extra step to guard against
    //multitasking causing two stylesheets to start within
    //the same clock tick.
    
    // TODO: ***** Subissue: Package name components will correspond
    // to directories when we compile. We shouldn't spawn more
    // directories than we must. That may mean we'd rather
    // flatten the source address.

    long intAddr=0;
    try
    {
        byte[] ipAddr=java.net.InetAddress.getLocalHost().getAddress();
        for(int i=0;i<ipAddr.length;++i)
            intAddr=(intAddr<<8)+ipAddr[i];
    }
    catch(java.net.UnknownHostException e)
    {
        // Should never occur
        e.printStackTrace();
    }

    long templateNumber;
    synchronized(this)
    {   // Atomic get-next-count -- last-ditch protection against
		// the risk of multiple threads trying to process the same
		// stylesheet at the same instant on the same machine.
        templateNumber = ++templateCounter;
    }
    
    // Generate a mostly-unique name.
    // TODO: ***** NOT GUARANTEED TO BE COMPLETELY UNIQUE, since
    // there could, theoretically, be two seperate PROCESSES
    // which are running this code simultaneously. The officious
    // answer would be to tap into a system-level unique ID service,
    // like UUID or GID.
    // ***** Unclear these names will be supportable on all
    // systems. Some platforms may have limits on filename length.
    // Could move more of the fields into directory names if
    // that helps.
    String className=
        basename
                +"On"
        +intAddr
        +"at"
        +new java.util.Date().getTime() // msec since 1970 epoch
        +'n'
        +templateNumber // minor multithread protection
        ; 
        
    return className;
  }

}
