/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.InstructionConstants;

public interface Constants extends InstructionConstants {

    // Error categories used to report errors to Parser.reportError()

    // Unexpected internal errors, such as null-ptr exceptions, etc.
    // Immediately terminates compilation, no translet produced
    public final int INTERNAL        = 0;
    // XSLT elements that are not implemented and unsupported ext.
    // Immediately terminates compilation, no translet produced
    public final int UNSUPPORTED     = 1;
    // Fatal error in the stylesheet input (parsing or content)
    // Immediately terminates compilation, no translet produced
    public final int FATAL           = 2;
    // Other error in the stylesheet input (parsing or content)
    // Does not terminate compilation, no translet produced
    public final int ERROR           = 3;
    // Other error in the stylesheet input (content errors only)
    // Does not terminate compilation, a translet is produced
    public final int WARNING         = 4;

    public static final String EMPTYSTRING = "";

    public static final String NAMESPACE_FEATURE =
	"http://xml.org/sax/features/namespaces";

    public static final String TRANSLET_INTF
	= "org.apache.xalan.xsltc.Translet";
    public static final String TRANSLET_INTF_SIG        
	= "Lorg/apache/xalan/xsltc/Translet;";
    
    public static final String ATTRIBUTES_SIG 
	= "Lorg/apache/xalan/xsltc/runtime/Attributes;";
    public static final String NODE_ITERATOR_SIG
	= "Lorg/apache/xalan/xsltc/NodeIterator;";
    public static final String DOM_INTF_SIG
	= "Lorg/apache/xalan/xsltc/DOM;";
    public static final String DOM_IMPL_CLASS
	= "org/apache/xalan/xsltc/dom/DOMImpl";
    public static final String DOM_IMPL_SIG
	= "Lorg/apache/xalan/xsltc/dom/DOMImpl;";
    public static final String DOM_ADAPTER_CLASS
	= "org/apache/xalan/xsltc/dom/DOMAdapter";
    public static final String DOM_ADAPTER_SIG
	= "Lorg/apache/xalan/xsltc/dom/DOMAdapter;";
    public static final String MULTI_DOM_CLASS
	= "org.apache.xalan.xsltc.dom.MultiDOM";
    public static final String MULTI_DOM_SIG
	= "Lorg/apache/xalan/xsltc/dom/MultiDOM;";

    public static final String STRING    
	= "java.lang.String";

    public static final int ACC_PUBLIC    
	= org.apache.bcel.Constants.ACC_PUBLIC;
    public static final int ACC_SUPER     
	= org.apache.bcel.Constants.ACC_SUPER;
    public static final int ACC_FINAL     
	= org.apache.bcel.Constants.ACC_FINAL;
    public static final int ACC_PRIVATE   
	= org.apache.bcel.Constants.ACC_PRIVATE;
    public static final int ACC_PROTECTED 
	= org.apache.bcel.Constants.ACC_PROTECTED;
    public static final int ACC_STATIC
	= org.apache.bcel.Constants.ACC_STATIC;

    public static final String STRING_SIG         
	= "Ljava/lang/String;";
    public static final String STRING_BUFFER_SIG  
	= "Ljava/lang/StringBuffer;";
    public static final String OBJECT_SIG         
	= "Ljava/lang/Object;";
    public static final String DOUBLE_SIG         
	= "Ljava/lang/Double;";
    public static final String INTEGER_SIG        
	= "Ljava/lang/Integer;";
    public static final String COLLATOR_CLASS
        = "java/text/Collator";
    public static final String COLLATOR_SIG
        = "Ljava/text/Collator;";

    public static final String NODE               
	= "int";
    public static final String NODE_ITERATOR      
	= "org.apache.xalan.xsltc.NodeIterator";
    public static final String NODE_ITERATOR_BASE
	= "org.apache.xalan.xsltc.dom.NodeIteratorBase";
    public static final String SORT_ITERATOR      
	= "org.apache.xalan.xsltc.dom.SortingIterator";
    public static final String SORT_ITERATOR_SIG     
	= "Lorg.apache.xalan.xsltc.dom.SortingIterator;";
    public static final String REVERSE_ITERATOR      
	= "org.apache.xalan.xsltc.dom.ReverseIterator";
    public static final String NODE_SORT_RECORD 
	= "org.apache.xalan.xsltc.dom.NodeSortRecord";
    public static final String NODE_SORT_FACTORY
	= "org/apache/xalan/xsltc/dom/NodeSortRecordFactory";
    public static final String NODE_SORT_RECORD_SIG 
	= "Lorg/apache/xalan/xsltc/dom/NodeSortRecord;";
    public static final String NODE_SORT_FACTORY_SIG
	= "Lorg/apache/xalan/xsltc/dom/NodeSortRecordFactory;";
    public static final String STRING_VALUE_HANDLER
	= "org.apache.xalan.xsltc.runtime.StringValueHandler";
    public static final String STRING_VALUE_HANDLER_SIG 
	= "Lorg/apache/xalan/xsltc/runtime/StringValueHandler;";
    public static final String OUTPUT_HANDLER
	= "org/apache/xalan/xsltc/TransletOutputHandler";
    public static final String OUTPUT_HANDLER_SIG
	= "Lorg/apache/xalan/xsltc/TransletOutputHandler;";
    public static final String FILTER_INTERFACE   
	= "org.apache.xalan.xsltc.dom.Filter";
    public static final String FILTER_INTERFACE_SIG   
	= "Lorg/apache/xalan/xsltc/dom/Filter;";
    public static final String UNION_ITERATOR_CLASS
	= "org.apache.xalan.xsltc.dom.UnionIterator";
    public static final String STEP_ITERATOR_CLASS
	= "org.apache.xalan.xsltc.dom.StepIterator";
    public static final String NTH_ITERATOR_CLASS
	= "org.apache.xalan.xsltc.dom.NthIterator";
    public static final String ABSOLUTE_ITERATOR
	= "org.apache.xalan.xsltc.dom.AbsoluteIterator";
    public static final String DUP_FILTERED_ITERATOR
	= "org.apache.xalan.xsltc.dom.DupFilterIterator";
    public static final String CURRENT_NODE_LIST_ITERATOR
	= "org.apache.xalan.xsltc.dom.CurrentNodeListIterator";
    public static final String CURRENT_NODE_LIST_FILTER
	= "org.apache.xalan.xsltc.dom.CurrentNodeListFilter";
    public static final String CURRENT_NODE_LIST_ITERATOR_SIG 
	= "Lorg/apache/xalan/xsltc/dom/CurrentNodeListIterator;";
    public static final String CURRENT_NODE_LIST_FILTER_SIG
	= "Lorg/apache/xalan/xsltc/dom/CurrentNodeListFilter;";
    public static final String FILTER_STEP_ITERATOR 
	= "org.apache.xalan.xsltc.dom.FilteredStepIterator";
    public static final String FILTER_ITERATOR 
	= "org.apache.xalan.xsltc.dom.FilterIterator";
    public static final String SINGLETON_ITERATOR 
	= "org.apache.xalan.xsltc.dom.SingletonIterator";
    public static final String MATCHING_ITERATOR 
	= "org.apache.xalan.xsltc.dom.MatchingIterator";
    public static final String NODE_SIG           
	= "I";
    public static final String GET_PARENT         
	= "getParent";
    public static final String GET_PARENT_SIG     
	= "(" + NODE_SIG + ")" + NODE_SIG;
    public static final String NEXT_SIG           
	= "()" + NODE_SIG;
    public static final String NEXT               
	= "next";
    public static final String MAKE_NODE          
	= "makeNode";
    public static final String MAKE_NODE_LIST     
	= "makeNodeList";
    public static final String STRING_TO_REAL     
	= "stringToReal";
    public static final String STRING_TO_REAL_SIG 
	= "(" + STRING_SIG + ")D";
    public static final String STRING_TO_INT     
	= "stringToInt";
    public static final String STRING_TO_INT_SIG 
	= "(" + STRING_SIG + ")I";

    public static final String XSLT_PACKAGE       
	= "org.apache.xalan.xsltc";
    public static final String COMPILER_PACKAGE   
	= XSLT_PACKAGE + ".compiler";
    public static final String RUNTIME_PACKAGE    
	= XSLT_PACKAGE + ".runtime";
    public static final String TRANSLET_CLASS     
	= RUNTIME_PACKAGE + ".AbstractTranslet";

    public static final String TRANSLET_SIG        
	= "Lorg/apache/xalan/xsltc/runtime/AbstractTranslet;";
    public static final String UNION_ITERATOR_SIG  
	= "Lorg/apache/xalan/xsltc/dom/UnionIterator;";
    public static final String TRANSLET_OUTPUT_BASE_SIG    
	= "Lorg/apache/xalan/xsltc/TransletOutputBase;";
    public static final String TRANSLET_OUTPUT_SIG    
	= "Lorg/apache/xalan/xsltc/TransletOutputHandler;";
    public static final String MAKE_NODE_SIG       
	= "(I)Lorg/w3c/dom/Node;";
    public static final String MAKE_NODE_SIG2      
	= "(" + NODE_ITERATOR_SIG + ")Lorg/w3c/dom/Node;";
    public static final String MAKE_NODE_LIST_SIG  
	= "(I)Lorg/w3c/dom/NodeList;";
    public static final String MAKE_NODE_LIST_SIG2 
	= "(" + NODE_ITERATOR_SIG + ")Lorg/w3c/dom/NodeList;";

    public static final String LOAD_DOCUMENT_CLASS
	= "org.apache.xalan.xsltc.dom.LoadDocument";

    public static final String KEY_INDEX_CLASS
	= "org/apache/xalan/xsltc/dom/KeyIndex";
    public static final String KEY_INDEX_SIG
	= "Lorg/apache/xalan/xsltc/dom/KeyIndex;";

    public static final String DOM_INTF
	= "org.apache.xalan.xsltc.DOM";
    public static final String DOM_IMPL
	= "org.apache.xalan.xsltc.dom.DOMImpl";
    public static final String STRING_CLASS 		
	= "java.lang.String";
    public static final String OBJECT_CLASS 		
	= "java.lang.Object";
    public static final String BOOLEAN_CLASS 		
	= "java.lang.Boolean";
    public static final String STRING_BUFFER_CLASS
	= "java.lang.StringBuffer";

    public static final String TRANSLET_OUTPUT_BASE       
	= "org.apache.xalan.xsltc.TransletOutputBase";
    // output interface
    public static final String TRANSLET_OUTPUT_INTERFACE
	= "org.apache.xalan.xsltc.TransletOutputHandler";
    public static final String BASIS_LIBRARY_CLASS 
	= "org.apache.xalan.xsltc.runtime.BasisLibrary";
    public static final String ATTRIBUTE_LIST_IMPL_CLASS 
	= "org.apache.xalan.xsltc.runtime.AttributeListImpl";
    public static final String DOUBLE_CLASS       
	= "java.lang.Double";
    public static final String INTEGER_CLASS      
	= "java.lang.Integer";
    public static final String RUNTIME_NODE_CLASS 
	= "org.apache.xalan.xsltc.runtime.Node";
    public static final String MATH_CLASS         
	= "java.lang.Math";

    public static final String BOOLEAN_VALUE      
	= "booleanValue";
    public static final String BOOLEAN_VALUE_SIG  
	= "()Z";
    public static final String INT_VALUE          
	= "intValue";
    public static final String INT_VALUE_SIG      
	= "()I";
    public static final String DOUBLE_VALUE       
	= "doubleValue";
    public static final String DOUBLE_VALUE_SIG   
	= "()D";

    public static final String NODE_PNAME         
	= "node";
    public static final String TRANSLET_OUTPUT_PNAME 
	= "handler";
    public static final String ITERATOR_PNAME     
	= "iterator";
    public static final String DOCUMENT_PNAME     
	= "document";
    public static final String TRANSLET_PNAME     
	= "translet";

    public static final String GET_NODE_NAME      
	= "getNodeName";
    public static final String CHARACTERSW        
	= "characters";
    public static final String GET_CHILDREN       
	= "getChildren";
    public static final String GET_TYPED_CHILDREN 
	= "getTypedChildren";
    public static final String CHARACTERS         
	= "characters";
    public static final String APPLY_TEMPLATES    
	= "applyTemplates";
    public static final String GET_NODE_TYPE      
	= "getNodeType";
    public static final String GET_NODE_VALUE     
	= "getNodeValue";
    public static final String GET_ELEMENT_VALUE  
	= "getElementValue";
    public static final String GET_ATTRIBUTE_VALUE  
	= "getAttributeValue";
    public static final String HAS_ATTRIBUTE      
	= "hasAttribute";
    public static final String ADD_ITERATOR       
	= "addIterator";
    public static final String SET_START_NODE     
	= "setStartNode";
    public static final String RESET     	    
	= "reset";

    public static final String ATTR_SET_SIG
	= "(" + TRANSLET_OUTPUT_SIG + NODE_ITERATOR_SIG + ")V";

    public static final String GET_NODE_NAME_SIG   
	= "(" + NODE_SIG + ")" + STRING_SIG;
    public static final String CHARACTERSW_SIG     
	= "("  + STRING_SIG + TRANSLET_OUTPUT_SIG + ")V";
    public static final String CHARACTERS_SIG     
	= "(" + NODE_SIG + TRANSLET_OUTPUT_SIG + ")V";
    public static final String GET_CHILDREN_SIG
	= "(" + NODE_SIG +")" + NODE_ITERATOR_SIG;
    public static final String GET_TYPED_CHILDREN_SIG
	= "(I)" + NODE_ITERATOR_SIG;
    public static final String GET_NODE_TYPE_SIG
	= "()S";
    public static final String GET_NODE_VALUE_SIG
	= "(I)" + STRING_SIG;
    public static final String GET_ELEMENT_VALUE_SIG
	= "(I)" + STRING_SIG;
    public static final String GET_ATTRIBUTE_VALUE_SIG
	= "(II)" + STRING_SIG;
    public static final String HAS_ATTRIBUTE_SIG
	= "(II)Z";
    public static final String GET_ITERATOR_SIG
	= "()" + NODE_ITERATOR_SIG;

    public static final String NAMES_INDEX
	= "namesArray";
    public static final String NAMES_INDEX_SIG
	= "[" + STRING_SIG;
    public static final String NAMESPACE_INDEX
	= "namespaceArray";
    public static final String NAMESPACE_INDEX_SIG
	= "[" + STRING_SIG;

    public static final String DOM_FIELD
	= "_dom";
    public static final String FORMAT_SYMBOLS_FIELD	 
	= "format_symbols";

    public static final String ITERATOR_FIELD_SIG      
	= NODE_ITERATOR_SIG;
    public static final String NODE_FIELD		 
	= "node";
    public static final String NODE_FIELD_SIG		 
	= "I";
	
    public static final String EMPTYATTR_FIELD	     
	= "EmptyAttributes";
    public static final String ATTRIBUTE_LIST_FIELD    
	= "attributeList";
    public static final String CLEAR_ATTRIBUTES        
	= "clear";
    public static final String ADD_ATTRIBUTE           
	= "addAttribute";
    public static final String ATTRIBUTE_LIST_IMPL_SIG 
	= "Lorg/apache/xalan/xsltc/runtime/AttributeListImpl;";
    public static final String CLEAR_ATTRIBUTES_SIG    
	= "()" + ATTRIBUTE_LIST_IMPL_SIG;
    public static final String ADD_ATTRIBUTE_SIG   
	= "(" + STRING_SIG + STRING_SIG + ")" + ATTRIBUTE_LIST_IMPL_SIG;
	
    public static final String ADD_ITERATOR_SIG   
	= "(" + NODE_ITERATOR_SIG +")" + UNION_ITERATOR_SIG;

    public static final String ORDER_ITERATOR
	= "orderNodes";
    public static final String ORDER_ITERATOR_SIG
	= "("+NODE_ITERATOR_SIG+"I)"+NODE_ITERATOR_SIG;
	
    public static final String SET_START_NODE_SIG   
	= "(" + NODE_SIG + ")" + NODE_ITERATOR_SIG;

    public static final String NODE_COUNTER 
	= "org.apache.xalan.xsltc.dom.NodeCounter";
    public static final String NODE_COUNTER_SIG 
	= "Lorg/apache/xalan/xsltc/dom/NodeCounter;";
    public static final String DEFAULT_NODE_COUNTER 
	= "org.apache.xalan.xsltc.dom.DefaultNodeCounter";
    public static final String DEFAULT_NODE_COUNTER_SIG 
	= "Lorg/apache/xalan/xsltc/dom/DefaultNodeCounter;";
    public static final String TRANSLET_FIELD 
	= "translet";
    public static final String TRANSLET_FIELD_SIG 
	= TRANSLET_SIG;

    public static final String RESET_SIG   	       
	= "()" + NODE_ITERATOR_SIG;
    public static final String GET_PARAMETER      
	= "getParameter";
    public static final String ADD_PARAMETER         
	= "addParameter";
    public static final String PUSH_PARAM_FRAME
	= "pushParamFrame";
    public static final String PUSH_PARAM_FRAME_SIG  
	= "()V";
    public static final String POP_PARAM_FRAME       
	= "popParamFrame";
    public static final String POP_PARAM_FRAME_SIG   
	= "()V";
    public static final String GET_PARAMETER_SIG     
	= "(" + STRING_SIG + ")" + OBJECT_SIG;
    public static final String ADD_PARAMETER_SIG
	= "(" + STRING_SIG + OBJECT_SIG + "Z)" + OBJECT_SIG;

    public static final String STRIP_SPACE
	= "stripSpace";
    public static final String STRIP_SPACE_INTF
	= "org/apache/xalan/xsltc/StripFilter";
    public static final String STRIP_SPACE_SIG
	= "Lorg/apache/xalan/xsltc/StripFilter;";
    public static final String STRIP_SPACE_PARAMS
	= "(Lorg/apache/xalan/xsltc/DOM;II)Z";

    public static final String GET_NODE_VALUE_ITERATOR
	= "getNodeValueIterator";
    public static final String GET_NODE_VALUE_ITERATOR_SIG
	= "("+NODE_ITERATOR_SIG+"I"+STRING_SIG+"Z)"+NODE_ITERATOR_SIG;

    public static final int POSITION_INDEX = 2;
    public static final int LAST_INDEX     = 3;

    public static final String XMLNS_PREFIX = "xmlns";
    public static final String XMLNS_STRING = "xmlns:";
    public static final String XMLNS_URI 
	= "http://www.w3.org/2000/xmlns/";
    public static final String XSLT_URI
	= "http://www.w3.org/1999/XSL/Transform";
    public static final String XHTML_URI
	= "http://www.w3.org/1999/xhtml";
    public static final String TRANSLET_URI
	= "http://xml.apache.org/xalan/xsltc";
    public static final String FALLBACK_CLASS
	= "org.apache.xalan.xsltc.compiler.Fallback";
}
