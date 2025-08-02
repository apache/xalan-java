/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.processor;

import java.util.HashMap;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemAnalyzeString;
import org.apache.xalan.templates.ElemApplyImport;
import org.apache.xalan.templates.ElemApplyTemplates;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemCatch;
import org.apache.xalan.templates.ElemCharacterMap;
import org.apache.xalan.templates.ElemChoose;
import org.apache.xalan.templates.ElemComment;
import org.apache.xalan.templates.ElemContextItem;
import org.apache.xalan.templates.ElemCopy;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemDocument;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemEvaluate;
import org.apache.xalan.templates.ElemExsltFuncResult;
import org.apache.xalan.templates.ElemExsltFunction;
import org.apache.xalan.templates.ElemExtensionDecl;
import org.apache.xalan.templates.ElemExtensionScript;
import org.apache.xalan.templates.ElemFallback;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.ElemForEachGroup;
import org.apache.xalan.templates.ElemFork;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemIf;
import org.apache.xalan.templates.ElemImportSchema;
import org.apache.xalan.templates.ElemIterate;
import org.apache.xalan.templates.ElemIterateBreak;
import org.apache.xalan.templates.ElemIterateNextIteration;
import org.apache.xalan.templates.ElemIterateOnCompletion;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemMatchingSubstring;
import org.apache.xalan.templates.ElemMerge;
import org.apache.xalan.templates.ElemMergeAction;
import org.apache.xalan.templates.ElemMergeKey;
import org.apache.xalan.templates.ElemMergeSource;
import org.apache.xalan.templates.ElemMessage;
import org.apache.xalan.templates.ElemNonMatchingSubstring;
import org.apache.xalan.templates.ElemNumber;
import org.apache.xalan.templates.ElemOtherwise;
import org.apache.xalan.templates.ElemOutputCharacter;
import org.apache.xalan.templates.ElemPI;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemResultDocument;
import org.apache.xalan.templates.ElemSequence;
import org.apache.xalan.templates.ElemSort;
import org.apache.xalan.templates.ElemSourceDocument;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemTry;
import org.apache.xalan.templates.ElemUnknown;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemWhen;
import org.apache.xalan.templates.ElemWithParam;
import org.apache.xml.utils.QName;

/**
 * This class defines the allowed structure for an XSLT 3.0 stylesheet, and the
 * mapping between Xalan classes and the markup elements in the stylesheet.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#xsd11-schema-for-xslt">XSD 1.1 Schema for XSLT Stylesheets</a>
 */
public class XSLTSchema extends XSLTElementDef
{

  /**
   * Construct an XSLTSchema object which represents the XSLT "schema".
   */
  XSLTSchema()
  {
    build();
  }

  /**
   * This method builds an XSLT "schema" according to https://www.w3.org/TR/xslt-30/#xsd11-schema-for-xslt.
   * This schema provides instructions for building the Xalan Stylesheet (Templates) structure.
   */
  void build()
  {
	// xsl:import, xsl:include
    XSLTAttributeDef hrefAttr = new XSLTAttributeDef(null, "href",
                                  XSLTAttributeDef.T_URL, true, false, XSLTAttributeDef.ERROR);
    
    // Required.
    // Support AVT
    // xsl:result-document, xsl:source-document                  
    XSLTAttributeDef hrefAVTRequired = new XSLTAttributeDef(null, "href",
                                         XSLTAttributeDef.T_AVT, true, true, XSLTAttributeDef.WARNING);
    
    // xsl:import-schema
    XSLTAttributeDef namespaceAttr = new XSLTAttributeDef(null, "namespace",
                                  XSLTAttributeDef.T_URL, false, false, XSLTAttributeDef.ERROR);
    
    // xsl:import-schema
    XSLTAttributeDef schemaLocationAttr = new XSLTAttributeDef(null, "schema-location",
                                  XSLTAttributeDef.T_URL, false, false, XSLTAttributeDef.ERROR);
                                  
	// xsl:preserve-space, xsl:strip-space
    XSLTAttributeDef elementsAttr = new XSLTAttributeDef(null, "elements",
                                      XSLTAttributeDef.T_SIMPLEPATTERNLIST,
                                      true, false, XSLTAttributeDef.ERROR);
    
    XSLTAttributeDef xpathDefaultNamespaceAttrOpt = new XSLTAttributeDef(null, "xpath-default-namespace",
                                           XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.WARNING);
    
    XSLTAttributeDef xslXpathDefaultNamespaceAttrOpt = new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "xpath-default-namespace",
                                           XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.WARNING);
    
    XSLTAttributeDef expandTextAttrOpt = new XSLTAttributeDef(null, "expand-text",
                                           XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.WARNING);
    
    XSLTAttributeDef xslExpandTextAttrOpt = new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "expand-text",
                                           XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.WARNING);
    
    // Required.
    // xsl:output-character                                     
    XSLTAttributeDef characterAttrRequired = new XSLTAttributeDef(null, "character",
    										   XSLTAttributeDef.T_UNICODE_CHAR, true, false, XSLTAttributeDef.ERROR);
    
    // Required.
    // xsl:output-character                                     
    XSLTAttributeDef stringAttrRequired = new XSLTAttributeDef(null, "string",
    							            XSLTAttributeDef.T_STRING, true, false, XSLTAttributeDef.ERROR);
    
    // xsl:output, xsl:result-document
    XSLTAttributeDef methodAttr = new XSLTAttributeDef(null, "method",
                                    XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef versionAttr = new XSLTAttributeDef(null, "version",
                                     XSLTAttributeDef.T_NMTOKEN, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef encodingAttr = new XSLTAttributeDef(null, "encoding",
                                      XSLTAttributeDef.T_CDATA, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef omitXmlDeclarationAttr = new XSLTAttributeDef(null,
                                                "omit-xml-declaration",
                                                XSLTAttributeDef.T_YESNO,
                                                false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef useCharacterMapsAttr = new XSLTAttributeDef(null, "use-character-maps",
                                                      XSLTAttributeDef.T_QNAMES, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef standaloneAttr = new XSLTAttributeDef(null,
                                        "standalone",
                                        XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef doctypePublicAttr = new XSLTAttributeDef(null,
                                           "doctype-public",
                                           XSLTAttributeDef.T_CDATA, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef doctypeSystemAttr = new XSLTAttributeDef(null,
                                           "doctype-system",
                                           XSLTAttributeDef.T_CDATA, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef cdataSectionElementsAttr = new XSLTAttributeDef(null,
                                                  "cdata-section-elements",
                                                  XSLTAttributeDef.T_QNAMES_RESOLVE_NULL,
                                                  false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef indentAttr = new XSLTAttributeDef(null, "indent",
                                    XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef mediaTypeAttr = new XSLTAttributeDef(null, "media-type",
                                       XSLTAttributeDef.T_CDATA, false, false, XSLTAttributeDef.ERROR);
                                       
                  
    // Required.
    // It is an error if the name attribute is not present on any of these elements
    // xsl:key, xsl:attribute-set, xsl:call-template, xsl:with-param, xsl:variable, xsl:param, 
    // xsl:function, xsl:character-map
    XSLTAttributeDef nameAttrRequired = new XSLTAttributeDef(null, "name",
                                          XSLTAttributeDef.T_QNAME, true, false, XSLTAttributeDef.ERROR);
    
	// Required.
    // Support AVT
    // xsl:element, xsl:attribute                                    
    XSLTAttributeDef nameAVTRequired = new XSLTAttributeDef(null, "name",
                                         XSLTAttributeDef.T_AVT_QNAME, true, true, XSLTAttributeDef.WARNING);
            

    // Required.
    // Support AVT
    // xsl:processing-instruction                                     
    XSLTAttributeDef nameAVT_NCNAMERequired = new XSLTAttributeDef(null, "name",
                                         XSLTAttributeDef.T_NCNAME, true, true, XSLTAttributeDef.WARNING);
                                        
    // Optional
    // Static error if invalid
    // xsl:template, xsl:decimal-format                                      
    XSLTAttributeDef nameAttrOpt_ERROR = new XSLTAttributeDef(null, "name",
                                     XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:merge-source
    XSLTAttributeDef nameAttrOpt = new XSLTAttributeDef(null, "name",
                                     XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.WARNING);
    
    // Optional
    // xsl:merge-source
    XSLTAttributeDef sortBeforeMergeAttrOpt = new XSLTAttributeDef(null, "sort-before-merge",
                                     XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.WARNING);
    
    // Optional
    // xsl:param                                      
    XSLTAttributeDef requiredAttrOpt = new XSLTAttributeDef(null, "required",
                                     XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);

    // xsl:key                                 
    XSLTAttributeDef useAttr = new XSLTAttributeDef(null, "use",
                                 XSLTAttributeDef.T_EXPR, true, false, XSLTAttributeDef.ERROR);
           
    // xsl:element, xsl:attribute                              
    XSLTAttributeDef namespaceAVTOpt = new XSLTAttributeDef(null,
                                         "namespace",XSLTAttributeDef.T_URL,
                                         false, true, XSLTAttributeDef.WARNING);
    // xsl:decimal-format                                     
    XSLTAttributeDef decimalSeparatorAttr = new XSLTAttributeDef(null,
                                              "decimal-separator",
                                              XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR, ".");
    XSLTAttributeDef infinityAttr = new XSLTAttributeDef(null, "infinity",
                                      XSLTAttributeDef.T_CDATA, false, XSLTAttributeDef.ERROR,"Infinity");
    XSLTAttributeDef minusSignAttr = new XSLTAttributeDef(null, "minus-sign",
                                       XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR,"-");
    XSLTAttributeDef NaNAttr = new XSLTAttributeDef(null, "NaN",
                                 XSLTAttributeDef.T_CDATA, false, XSLTAttributeDef.ERROR, "NaN");
    XSLTAttributeDef percentAttr = new XSLTAttributeDef(null, "percent",
                                     XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR, "%");
    XSLTAttributeDef perMilleAttr = new XSLTAttributeDef(null, "per-mille",
                                      XSLTAttributeDef.T_CHAR,
                                      false, false, XSLTAttributeDef.ERROR /* ,"&#x2030;" */);
    XSLTAttributeDef zeroDigitAttr = new XSLTAttributeDef(null, "zero-digit",
                                       XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR, "0");
    XSLTAttributeDef digitAttr = new XSLTAttributeDef(null, "digit",
                                   XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR, "#");
    XSLTAttributeDef patternSeparatorAttr = new XSLTAttributeDef(null,
                                              "pattern-separator",
                                              XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR, ";");
    // xsl:decimal-format                                         
    XSLTAttributeDef groupingSeparatorAttr = new XSLTAttributeDef(null,
                                               "grouping-separator",
                                               XSLTAttributeDef.T_CHAR, false, XSLTAttributeDef.ERROR,",");

                                              
    // xsl:element, xsl:attribute-set, xsl:copy                                           
    XSLTAttributeDef useAttributeSetsAttr = new XSLTAttributeDef(null,
                                              "use-attribute-sets",
                                              XSLTAttributeDef.T_QNAMES,
                                              false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:catch      
    XSLTAttributeDef errorsAttrOpt = new XSLTAttributeDef(null, "errors", XSLTAttributeDef.T_QNAMES,
                                                                                    false, false, XSLTAttributeDef.ERROR);
    
    // xsl:element, xsl:attribute, xsl:document                                          
    XSLTAttributeDef typeAttrOpt = new XSLTAttributeDef(null, "type",
            										XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
    
    // xsl:element, xsl:attribute, xsl:document                                         
    XSLTAttributeDef validationAttrOpt = new XSLTAttributeDef(null, "validation",
            										XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
        

    // xsl:if, xsl:when         
    XSLTAttributeDef testAttrRequired = new XSLTAttributeDef(null, "test",   
                                          XSLTAttributeDef.T_EXPR, true, false, XSLTAttributeDef.ERROR);
      
      
    // Required.                                       
    // xsl:for-each, xsl:copy-of, xsl:for-each-group, xsl:analyze-string, xsl:iterate, xsl:merge-source                            
    XSLTAttributeDef selectAttrRequired = new XSLTAttributeDef(null,
                                            "select",
                                            XSLTAttributeDef.T_EXPR, true, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:for-each-group
    XSLTAttributeDef compositeAttrOpt = new XSLTAttributeDef(null, "composite",
                                                      XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.WARNING);
    // Optional
    // xsl:for-each-group 
    XSLTAttributeDef collationAttrOpt = new XSLTAttributeDef(null, "collation", 
    	                                              XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:value-of
    XSLTAttributeDef separatorAttrOpt = new XSLTAttributeDef(null,
									                   "separator",
									                   XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
    
    // Required.
    // xsl:evaluate
    XSLTAttributeDef xpathAttrRequired = new XSLTAttributeDef(null,
								            "xpath",
								            XSLTAttributeDef.T_EXPR, true, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:evaluate
    XSLTAttributeDef contextItemAttrOpt = new XSLTAttributeDef(null,
									                   "context-item",
									                   XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    
    // Required.                                       
    // xsl:analyze-string
    XSLTAttributeDef regexAVTRequired = new XSLTAttributeDef(null, "regex",
                                              XSLTAttributeDef.T_AVT, true, true, XSLTAttributeDef.ERROR);
    
    // Optional                                       
    // xsl:analyze-string                              
    XSLTAttributeDef flagsAttrOpt = new XSLTAttributeDef(null,
                                             "flags",
                                             XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
    
    // Optional                                       
    // xsl:for-each-group                             
    XSLTAttributeDef groupByAttrOpt = new XSLTAttributeDef(null,
                                            "group-by",
                                            XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    
    // Optional                                       
    // xsl:for-each-group                             
    XSLTAttributeDef groupAdjacentAttrOpt = new XSLTAttributeDef(null,
                                                  "group-adjacent",
                                                  XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    
    // Optional                                       
    // xsl:for-each-group                             
    XSLTAttributeDef groupStartingWithAttrOpt = new XSLTAttributeDef(null,
                                                  "group-starting-with",
                                                  XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    
    // Optional                                       
    // xsl:for-each-group                             
    XSLTAttributeDef groupEndingWithAttrOpt = new XSLTAttributeDef(null,
                                                  "group-ending-with",
                                                  XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);

    // Optional                                          
    // xsl:variable, xsl:value-of, xsl:param, xsl:with-param, xsl:attribute, xsl:break, 
    // xsl:on-completion, xsl:sequence, xsl:try, xsl:catch                                       
    XSLTAttributeDef selectAttrOpt = new XSLTAttributeDef(null, "select",
                                       XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:variable, xsl:param, xsl:with-param, xsl:template, xsl:function, xsl:evaluate, xsl:context-item
    XSLTAttributeDef asAttrOpt = new XSLTAttributeDef(null, "as",
                                       XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:param, xsl:with-param 
    XSLTAttributeDef tunnelAttrOpt = new XSLTAttributeDef(null, "tunnel",
                                           XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);

    // Optional
    // Default: "node()"
    // xsl:apply-templates                                           
    XSLTAttributeDef selectAttrDefNode = new XSLTAttributeDef(null, "select",
                                           XSLTAttributeDef.T_EXPR, false, XSLTAttributeDef.ERROR, "node()");
    // Optional
    // Default: "."
    // xsl:sort                                        
    XSLTAttributeDef selectAttrDefDot = new XSLTAttributeDef(null, "select",
                                          XSLTAttributeDef.T_EXPR, false, XSLTAttributeDef.ERROR, ".");    
    
    // xsl:key                                      
    XSLTAttributeDef matchAttrRequired = new XSLTAttributeDef(null, "match",
                                           XSLTAttributeDef.T_PATTERN, true, false, XSLTAttributeDef.ERROR);
    // xsl:template                                       
    XSLTAttributeDef matchAttrOpt = new XSLTAttributeDef(null, "match",
                                      XSLTAttributeDef.T_PATTERN, false, false, XSLTAttributeDef.ERROR);
    // xsl:template                                  
    XSLTAttributeDef priorityAttr = new XSLTAttributeDef(null, "priority",
                                     XSLTAttributeDef.T_NUMBER, false, false, XSLTAttributeDef.ERROR);
                                     
    // xsl:template, xsl:apply-templates                                 
    XSLTAttributeDef modeAttr = new XSLTAttributeDef(null, "mode",
                                     XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
   
    XSLTAttributeDef spaceAttr = new XSLTAttributeDef(Constants.S_XMLNAMESPACEURI, "space", false, false, 
    		                             false, XSLTAttributeDef.WARNING,
                                         "default", Constants.ATTRVAL_STRIP, "preserve",
                                         Constants.ATTRVAL_PRESERVE);
    
    // Optional
    // Default: "no"
    // xsl:source-document
    XSLTAttributeDef streamableAttr = new XSLTAttributeDef(null, "streamable",
                                          XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // Default: "yes"
    // xsl:try
    XSLTAttributeDef rollbackOutputAttrOpt = new XSLTAttributeDef(null, "rollback-output",
                                          					XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
                                                    
    XSLTAttributeDef spaceAttrLiteral =
      new XSLTAttributeDef(Constants.S_XMLNAMESPACEURI, "space", 
                                          XSLTAttributeDef.T_URL, false, true, XSLTAttributeDef.ERROR);
    // xsl:namespace-alias                                      
    XSLTAttributeDef stylesheetPrefixAttr = new XSLTAttributeDef(null,
                                              "stylesheet-prefix",
                                              XSLTAttributeDef.T_CDATA, true, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef resultPrefixAttr = new XSLTAttributeDef(null,
                                          "result-prefix",
                                          XSLTAttributeDef.T_CDATA, true, false, XSLTAttributeDef.ERROR);
                                          
    // xsl:text, xsl:value-of                                      
    XSLTAttributeDef disableOutputEscapingAttr = new XSLTAttributeDef(null,
                                                   "disable-output-escaping",
                                                   XSLTAttributeDef.T_YESNO,
                                                   false, false, XSLTAttributeDef.ERROR);
                                                   
	// xsl:number                                                   
    XSLTAttributeDef levelAttr = new XSLTAttributeDef(null, "level", false, false, false, XSLTAttributeDef.ERROR,
                                   "single", Constants.NUMBERLEVEL_SINGLE,
                                   "multiple", Constants.NUMBERLEVEL_MULTI,
                                   "any", Constants.NUMBERLEVEL_ANY);
    levelAttr.setDefault("single");
    XSLTAttributeDef countAttr = new XSLTAttributeDef(null, "count",
                                   XSLTAttributeDef.T_PATTERN, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef fromAttr = new XSLTAttributeDef(null, "from",
                                  XSLTAttributeDef.T_PATTERN, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef valueAttr = new XSLTAttributeDef(null, "value",
                                   XSLTAttributeDef.T_EXPR, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef formatAttr = new XSLTAttributeDef(null, "format",
                                    XSLTAttributeDef.T_CDATA, false, true, XSLTAttributeDef.ERROR);
    formatAttr.setDefault("1");
    
    // xsl:number, xsl:sort
    XSLTAttributeDef langAttr = new XSLTAttributeDef(null, "lang",
                                  XSLTAttributeDef.T_NMTOKEN, false, true, XSLTAttributeDef.ERROR);
   
    // xsl:number
    XSLTAttributeDef letterValueAttr = new XSLTAttributeDef(null,
                                         "letter-value",
                                         false, true, false, XSLTAttributeDef.ERROR,
                                         "alphabetic", Constants.NUMBERLETTER_ALPHABETIC,
                                         "traditional", Constants.NUMBERLETTER_TRADITIONAL);
    // xsl:number
    XSLTAttributeDef groupingSeparatorAVT = new XSLTAttributeDef(null,
                                              "grouping-separator",
                                              XSLTAttributeDef.T_CHAR, false, true, XSLTAttributeDef.ERROR);
    // xsl:number
    XSLTAttributeDef groupingSizeAttr = new XSLTAttributeDef(null,
                                          "grouping-size",
                                          XSLTAttributeDef.T_NUMBER, false, true, XSLTAttributeDef.ERROR);
   
   // xsl:sort
    XSLTAttributeDef dataTypeAttr = new XSLTAttributeDef(null, "data-type", false, true, true, XSLTAttributeDef.ERROR,
                                    "text", Constants.SORTDATATYPE_TEXT ,"number", Constants.SORTDATATYPE_TEXT);
	dataTypeAttr.setDefault("text");
	
	// xsl:sort
    XSLTAttributeDef orderAttr = new XSLTAttributeDef(null, "order", false, true, false, XSLTAttributeDef.ERROR,
                                    "ascending", Constants.SORTORDER_ASCENDING, 
                                    "descending", Constants.SORTORDER_DESCENDING);
    orderAttr.setDefault("ascending");

    // xsl:sort                             
    XSLTAttributeDef caseOrderAttr = new XSLTAttributeDef(null, "case-order", false, true, false, XSLTAttributeDef.ERROR,
                                       "upper-first", Constants.SORTCASEORDER_UPPERFIRST ,
                                       "lower-first", Constants.SORTCASEORDER_LOWERFIRST);
	    
    // xsl:message                                   
    XSLTAttributeDef terminateAttr = new XSLTAttributeDef(null, "terminate",
                                       XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    terminateAttr.setDefault("no");

	// top level attributes
    XSLTAttributeDef xslExcludeResultPrefixesAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL,
                           "exclude-result-prefixes",
                           XSLTAttributeDef.T_PREFIXLIST, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef xslExtensionElementPrefixesAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL,
                           "extension-element-prefixes",
                           XSLTAttributeDef.T_PREFIX_URLLIST, false, false, XSLTAttributeDef.ERROR);
    // result-element-atts                       
    XSLTAttributeDef xslUseAttributeSetsAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "use-attribute-sets",
                           XSLTAttributeDef.T_QNAMES, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef xslVersionAttr =
      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "version",
                           XSLTAttributeDef.T_NMTOKEN, false, false, XSLTAttributeDef.ERROR);    
    XSLTAttributeDef xslTypeAttrOpt =
    	      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "type",
    	                           XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
    XSLTAttributeDef xslValidationAttrOpt =
    	      new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "validation",
    	                           XSLTAttributeDef.T_QNAME, false, false, XSLTAttributeDef.ERROR);
    // Optional
    // xsl:context-item
    XSLTAttributeDef useAttrOpt = new XSLTAttributeDef(null, "use",
                                                      XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:function
    XSLTAttributeDef overrideAttrOpt = new XSLTAttributeDef(null, "override",
                                                            XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:function
    XSLTAttributeDef overrideExtFunctionAttrOpt = new XSLTAttributeDef(null, "override-extension-function",
                                                                       XSLTAttributeDef.T_YESNO, false, false, XSLTAttributeDef.ERROR);
    
    // Optional
    // xsl:function
    XSLTAttributeDef newEachTimeAttrOpt = new XSLTAttributeDef(null, "new-each-time",
                                                                       XSLTAttributeDef.T_STRING, false, false, XSLTAttributeDef.ERROR);
                           
    XSLTElementDef charData = new XSLTElementDef(this, null, "text()",
                                null /*alias */, null /* elements */, null,  /* attributes */
                                new ProcessorCharacters(),
                                ElemTextLiteral.class /* class object */);

    charData.setType(XSLTElementDef.T_PCDATA);

    XSLTElementDef whiteSpaceOnly = new XSLTElementDef(this, null, "text()",
				                                       null /*alias */, null /* elements */,
				                                       null,  /* attributes */
				                                       null,
				                                       ElemTextLiteral.class /* should be null? -sb */);

    XSLTAttributeDef resultAttr = new XSLTAttributeDef(null, "*",
                                                       XSLTAttributeDef.T_AVT, false, true, 
                                                       XSLTAttributeDef.WARNING);
    XSLTAttributeDef xslResultAttr = new XSLTAttributeDef(Constants.S_XSLNAMESPACEURL, "*",
                                                          XSLTAttributeDef.T_CDATA, false, false, 
                                                          XSLTAttributeDef.WARNING);
                           
    XSLTElementDef[] templateElements = new XSLTElementDef[45];
    XSLTElementDef[] templateElementsAndParams = new XSLTElementDef[46];
    XSLTElementDef[] templateElementsAndSort = new XSLTElementDef[46];
    //exslt
    XSLTElementDef[] exsltFunctionElements = new XSLTElementDef[46];
    
    XSLTElementDef[] charTemplateElements = new XSLTElementDef[27];
    XSLTElementDef resultElement = new XSLTElementDef(this, null, "*",
                                     null /*alias */,
                                     templateElements /* elements */,
                                     new XSLTAttributeDef[]{
                                       spaceAttrLiteral, // special
                                       xslExcludeResultPrefixesAttr,
                                       xslExtensionElementPrefixesAttr,
                                       xslUseAttributeSetsAttr,
                                       xslVersionAttr,
                                       xslResultAttr,
                                       resultAttr,
                                       xslTypeAttrOpt,
                                       xslValidationAttrOpt,
                                       xslXpathDefaultNamespaceAttrOpt,
                                       xslExpandTextAttrOpt }, 
                                        new ProcessorLRE(),
                                     ElemLiteralResult.class /* class object */, 20, true);
    XSLTElementDef unknownElement =
      new XSLTElementDef(this, "*", "unknown", null /*alias */,
                         templateElementsAndParams /* elements */,
                         new XSLTAttributeDef[]{ xslExcludeResultPrefixesAttr,
                                                 xslExtensionElementPrefixesAttr,
                                                 xslUseAttributeSetsAttr,
                                                 xslVersionAttr,
                                                 xslResultAttr,
                                                 resultAttr }, 
                                                 new ProcessorUnknown(),
                                                 ElemUnknown.class /* class object */, 20, true);
    XSLTElementDef xslValueOf = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "value-of",
                                  null /*alias */, templateElements /* elements */,
                                  new XSLTAttributeDef[]{ selectAttrOpt,
                                		                  separatorAttrOpt,
                                                          disableOutputEscapingAttr,
                                                          xpathDefaultNamespaceAttrOpt,
                                                          expandTextAttrOpt }, 
                                               new ProcessorTemplateElem(),
                                  ElemValueOf.class /* class object */, 20, true);        
    
    XSLTElementDef xslCopyOf = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "copy-of",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ selectAttrRequired,
                                		                 typeAttrOpt,
                                		                 validationAttrOpt, xpathDefaultNamespaceAttrOpt,
                                		                 expandTextAttrOpt },
                                 new ProcessorTemplateElem(),
                                 ElemCopyOf.class /* class object */, 20, true);
    
    XSLTElementDef xslDocument = new XSLTElementDef(this,
					             Constants.S_XSLNAMESPACEURL, "document",
					             null /*alias */, templateElements /* elements */,
					             new XSLTAttributeDef[]{ typeAttrOpt, validationAttrOpt },
					             new ProcessorTemplateElem(),
					             ElemDocument.class /* class object */, 20, true);
    
    XSLTElementDef xslNumber = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "number",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ levelAttr,
                                                         countAttr,
                                                         fromAttr,
                                                         valueAttr,
                                                         formatAttr,
                                                         langAttr,
                                                         letterValueAttr,
                                                         groupingSeparatorAVT,
                                                         groupingSizeAttr,
                                                         xpathDefaultNamespaceAttrOpt,
                                                         expandTextAttrOpt }, 
                                        new ProcessorTemplateElem(),
                                 ElemNumber.class /* class object */, 20, true);

    // <!-- xsl:sort cannot occur after any other elements or
    // any non-whitespace character -->
    XSLTElementDef xslSort = new XSLTElementDef(this,
                                                Constants.S_XSLNAMESPACEURL,
                                                "sort", null /*alias */,
                                                null /* elements */,
                                                new XSLTAttributeDef[]{
                                                  selectAttrDefDot,
                                                  langAttr,
                                                  dataTypeAttr,
                                                  orderAttr,
                                                  caseOrderAttr }, 
                                       new ProcessorTemplateElem(),
                                                ElemSort.class/* class object */, 19, true );
    XSLTElementDef xslWithParam = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL,
                                    "with-param", null /*alias */,
                                    templateElements /* elements */,  // %template;>
                                    new XSLTAttributeDef[]{ nameAttrRequired,
                                                            selectAttrOpt, asAttrOpt, tunnelAttrOpt }, new ProcessorTemplateElem(),
                                                            ElemWithParam.class /* class object */, 19, true);
    XSLTElementDef xslApplyTemplates = new XSLTElementDef(this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "apply-templates", null /*alias */,
                                         new XSLTElementDef[]{ xslSort,
                                                               xslWithParam } /* elements */, new XSLTAttributeDef[]{
                                                                 selectAttrDefNode,
                                                                 modeAttr, xpathDefaultNamespaceAttrOpt,
                                                                 expandTextAttrOpt }, 
                                                                        new ProcessorTemplateElem(),
                                         ElemApplyTemplates.class /* class object */, 20, true);
    XSLTElementDef xslApplyImports =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "apply-imports",
                         null /*alias */, null /* elements */,
                         new XSLTAttributeDef[]{},
                         new ProcessorTemplateElem(),
                         ElemApplyImport.class /* class object */);
    XSLTElementDef xslForEach = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "for-each",
                                  null /*alias */, templateElementsAndSort,  // (#PCDATA %instructions; %result-elements; | xsl:sort)*
                                  new XSLTAttributeDef[]{ selectAttrRequired,
                                		                  xpathDefaultNamespaceAttrOpt, expandTextAttrOpt, 
                                		                  spaceAttr }, 
                                               new ProcessorTemplateElem(),
                                  ElemForEach.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslOutputCharacter = new XSLTElementDef(this,
					              Constants.S_XSLNAMESPACEURL, "output-character",
					              null /*alias */, templateElements,
					              new XSLTAttributeDef[]{ characterAttrRequired, stringAttrRequired }, 
					              new ProcessorTemplateElem(),
					              ElemOutputCharacter.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslForEachGroup = new XSLTElementDef(this,
                                             Constants.S_XSLNAMESPACEURL, "for-each-group",
                                             null /*alias */, templateElementsAndSort,
                                             new XSLTAttributeDef[]{ selectAttrRequired, groupByAttrOpt, groupAdjacentAttrOpt, 
                                                                     groupStartingWithAttrOpt, groupEndingWithAttrOpt, 
                                                                     compositeAttrOpt, collationAttrOpt, xpathDefaultNamespaceAttrOpt, 
                                                                     expandTextAttrOpt, spaceAttr }, 
                                             new ProcessorTemplateElem(),
                                             ElemForEachGroup.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslSourceDocument = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "source-document",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ hrefAVTRequired, streamableAttr, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemSourceDocument.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslResultDocument = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "result-document",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ hrefAVTRequired, methodAttr, 
								            		                 omitXmlDeclarationAttr, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemResultDocument.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslFork = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "fork",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemFork.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslMerge = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "merge",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemMerge.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslMergeSource = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "merge-source",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ nameAttrOpt, selectAttrRequired, 
								            		                 sortBeforeMergeAttrOpt, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemMergeSource.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslMergeKey = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "merge-key",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ selectAttrOpt, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemMergeKey.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslMergeAction = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "merge-action",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemMergeAction.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslTry = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "try",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ selectAttrOpt, rollbackOutputAttrOpt, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemTry.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslCatch = new XSLTElementDef(this,
								             Constants.S_XSLNAMESPACEURL, "catch",
								             null /*alias */, templateElements,
								             new XSLTAttributeDef[]{ selectAttrOpt, errorsAttrOpt, spaceAttr }, 
								             new ProcessorTemplateElem(),
								             ElemCatch.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslAnalyzeString = new XSLTElementDef(this,
                                                Constants.S_XSLNAMESPACEURL, "analyze-string",
                                                null /*alias */, templateElements,
                                                new XSLTAttributeDef[]{ regexAVTRequired, selectAttrRequired, 
                                                                        flagsAttrOpt, spaceAttr }, 
                                                new ProcessorTemplateElem(),
                                                ElemAnalyzeString.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslMatchingSubstring = new XSLTElementDef(this,
                                                Constants.S_XSLNAMESPACEURL, "matching-substring",
                                                null /*alias */, templateElements,
                                                new XSLTAttributeDef[] { }, 
                                                new ProcessorTemplateElem(),
                                                ElemMatchingSubstring.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslNonMatchingSubstring = new XSLTElementDef(this,
                                                Constants.S_XSLNAMESPACEURL, "non-matching-substring",
                                                null /*alias */, templateElements,
                                                new XSLTAttributeDef[] { }, 
                                                new ProcessorTemplateElem(),
                                                ElemNonMatchingSubstring.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslIterate = new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "iterate",
                                                   null /*alias */, templateElementsAndParams,
                                                   new XSLTAttributeDef[]{ selectAttrRequired,
                                                		                   xpathDefaultNamespaceAttrOpt, 
                                                		                   expandTextAttrOpt }, new ProcessorTemplateElem(),
                                                   ElemIterate.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslIterateOnCompletion = new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "on-completion",
                                                               null /*alias */, templateElements,
                                                               new XSLTAttributeDef[]{ selectAttrOpt }, new ProcessorTemplateElem(),
                                                               ElemIterateOnCompletion.class /* class object */, true, false, 
                                                               true, 20, true);
    
    XSLTElementDef xslIterateNextIteration = new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "next-iteration",
                                                                null /*alias */, 
                                                                new XSLTElementDef[]{ xslWithParam },
                                                                new XSLTAttributeDef[] { }, new ProcessorTemplateElem(),
                                                                ElemIterateNextIteration.class /* class object */, true, false, 
                                                                true, 20, true);
    
    XSLTElementDef xslIterateBreak = new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "break", null /*alias */, templateElements,
                                                        new XSLTAttributeDef[]{ selectAttrOpt }, new ProcessorTemplateElem(), 
                                                        ElemIterateBreak.class /* class object */, true, false, true, 20, true);
    
    XSLTElementDef xslEvaluate = new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "evaluate",
										            null /*alias */, new XSLTElementDef[]{
										                    xslWithParam } /* elements */,
										            new XSLTAttributeDef[]{ xpathAttrRequired, contextItemAttrOpt, asAttrOpt }, 
										                         new ProcessorTemplateElem(),
										            ElemEvaluate.class /* class object */, 20, true);
    
    XSLTElementDef xslIf = new XSLTElementDef(this,
                                              Constants.S_XSLNAMESPACEURL,
                                              "if", null /*alias */,
                                              templateElements /* elements */,  // %template;
                                              new XSLTAttributeDef[]{
                                                testAttrRequired, xpathDefaultNamespaceAttrOpt,
                                                expandTextAttrOpt, spaceAttr }, new ProcessorTemplateElem(),
                                                             ElemIf.class /* class object */, 20, true);
    XSLTElementDef xslWhen =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "when",
                         null /*alias */, templateElements /* elements */,  // %template;>
                                                new XSLTAttributeDef[]{
                                                  testAttrRequired, xpathDefaultNamespaceAttrOpt,
                                                  expandTextAttrOpt, spaceAttr }, new ProcessorTemplateElem(),
                                                               ElemWhen.class /* class object */,
                                                                                                false, true, 1, true);
    XSLTElementDef xslOtherwise = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL, "otherwise",
                                    null /*alias */,
                                    templateElements /* elements */,  // %template;>
                                    new XSLTAttributeDef[]{ xpathDefaultNamespaceAttrOpt, expandTextAttrOpt, spaceAttr },
                                    new ProcessorTemplateElem(),
                                    ElemOtherwise.class /* class object */,
                                                       false, false, 2, false);
    XSLTElementDef xslChoose = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "choose",
                                 null /*alias */,
                                 new XSLTElementDef[]{ xslWhen,
                                                       xslOtherwise } /* elements */, 
                                        new XSLTAttributeDef[]{ xpathDefaultNamespaceAttrOpt, expandTextAttrOpt, spaceAttr },
                                 new ProcessorTemplateElem(),
                                 ElemChoose.class /* class object */, true, false, true, 20, true);                                
    XSLTElementDef xslAttribute = new XSLTElementDef(this,
                                    Constants.S_XSLNAMESPACEURL, "attribute",
                                    null /*alias */,
                                    charTemplateElements /* elements */,  // %char-template;>
                                    new XSLTAttributeDef[]{ nameAVTRequired,
                                                            namespaceAVTOpt,
                                                            spaceAttr, selectAttrOpt, typeAttrOpt, 
                                                            validationAttrOpt, xpathDefaultNamespaceAttrOpt, 
                                                            expandTextAttrOpt }, 
                                    new ProcessorTemplateElem(),
                                    ElemAttribute.class /* class object */, 20, true);
    XSLTElementDef xslCallTemplate =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "call-template",
                         null /*alias */,
                         new XSLTElementDef[]{ xslWithParam } /* elements */,
                         new XSLTAttributeDef[]{ nameAttrRequired },
                         new ProcessorTemplateElem(),
                         ElemCallTemplate.class /* class object */, 20, true);
    XSLTElementDef xslVariable = new XSLTElementDef(this,
                                   Constants.S_XSLNAMESPACEURL, "variable",
                                   null /*alias */,
                                   templateElements /* elements */,  // %template;>
                                   new XSLTAttributeDef[]{ nameAttrRequired,
                                                           selectAttrOpt, asAttrOpt, xpathDefaultNamespaceAttrOpt, 
                                                           expandTextAttrOpt }, 
                                  new ProcessorTemplateElem(),
                                   ElemVariable.class /* class object */, 20, true);
    XSLTElementDef xslSequence = new XSLTElementDef(this,
                                                 Constants.S_XSLNAMESPACEURL, "sequence",
                                                 null /*alias */,
                                                 templateElements /* elements */,  // %template;>
                                                 new XSLTAttributeDef[]{ selectAttrOpt, xpathDefaultNamespaceAttrOpt,
                                                		                 expandTextAttrOpt }, 
                                                 new ProcessorTemplateElem(),
                                                 ElemSequence.class /* class object */, 20, true);
    XSLTElementDef xslParam = new XSLTElementDef(this,
                                Constants.S_XSLNAMESPACEURL, "param",
                                null /*alias */,
                                templateElements /* elements */,  // %template;>
                                new XSLTAttributeDef[]{ nameAttrRequired, selectAttrOpt, 
                                		                asAttrOpt, tunnelAttrOpt, requiredAttrOpt }, 
                                                        new ProcessorTemplateElem(),
                                                        ElemParam.class /* class object */, 19, true);
    XSLTElementDef xslText =
						       new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "text",
						                         null /*alias */,
						                         new XSLTElementDef[]{ charData } /* elements */,
						                         new XSLTAttributeDef[]{ disableOutputEscapingAttr, xpathDefaultNamespaceAttrOpt,
						                        		                 expandTextAttrOpt },
						                         new ProcessorText(),
						                         ElemText.class /* class object */, 20, true);
    
    
    XSLTElementDef xslProcessingInstruction =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL,
                         "processing-instruction", null /*alias */,
                         charTemplateElements /* elements */,  // %char-template;>
                         new XSLTAttributeDef[]{
                                                  nameAVT_NCNAMERequired,
                                                  spaceAttr, xpathDefaultNamespaceAttrOpt, 
                                                  expandTextAttrOpt }, 
                                        new ProcessorTemplateElem(),
                          ElemPI.class /* class object */, 20, true);
    XSLTElementDef xslElement = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "element",
                                  null /*alias */,
                                  templateElements /* elements */,  // %template;
                                  new XSLTAttributeDef[]{ nameAVTRequired,
                                                          namespaceAVTOpt,
                                                          useAttributeSetsAttr,
                                                          spaceAttr, typeAttrOpt, 
                                                          validationAttrOpt, xpathDefaultNamespaceAttrOpt, 
                                                          expandTextAttrOpt }, 
                                               new ProcessorTemplateElem(),
                                  ElemElement.class /* class object */, 20, true);
    XSLTElementDef xslComment = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "comment",
                                  null /*alias */,
                                  charTemplateElements /* elements */,  // %char-template;>
                                  new XSLTAttributeDef[]{ spaceAttr, xpathDefaultNamespaceAttrOpt, 
                                		                             expandTextAttrOpt },
                                  new ProcessorTemplateElem(),
                                  ElemComment.class /* class object */, 20, true);
    XSLTElementDef xslCopy =
      new XSLTElementDef(this, Constants.S_XSLNAMESPACEURL, "copy",
                         null /*alias */, templateElements /* elements */,  // %template;>
                          new XSLTAttributeDef[]{
                                                  spaceAttr,
                                                  useAttributeSetsAttr,
                                                  typeAttrOpt,
                         		                  validationAttrOpt, 
                         		                  xpathDefaultNamespaceAttrOpt, expandTextAttrOpt }, 
                                        new ProcessorTemplateElem(),
                          ElemCopy.class /* class object */, 20, true);
    XSLTElementDef xslMessage = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "message",
                                  null /*alias */,
                                  templateElements /* elements */,  // %template;>
                                  new XSLTAttributeDef[]{ terminateAttr },
                                  new ProcessorTemplateElem(),
                                  ElemMessage.class /* class object */, 20, true);
    XSLTElementDef xslFallback = new XSLTElementDef(this,
                                   Constants.S_XSLNAMESPACEURL, "fallback",
                                   null /*alias */,
                                   templateElements /* elements */,  // %template;>
                                   new XSLTAttributeDef[]{ spaceAttr },
                                   new ProcessorTemplateElem(),
                                   ElemFallback.class /* class object */, 20, true);
    //exslt
    XSLTElementDef exsltFunction =
                                  new XSLTElementDef(this, 
                                  Constants.S_EXSLT_FUNCTIONS_URL, 
                                  "function",
                                  null /*alias */,
                                  exsltFunctionElements /* elements */,
                                  new XSLTAttributeDef[]{ nameAttrRequired },
                                  new ProcessorExsltFunction(),
                                  ElemExsltFunction.class /* class object */);
    XSLTElementDef exsltResult =
                                  new XSLTElementDef(this, 
                                  Constants.S_EXSLT_FUNCTIONS_URL, 
                                  "result",
                                  null /*alias */,
                                  templateElements /* elements */,
                                  new XSLTAttributeDef[]{ selectAttrOpt },
                                  new ProcessorExsltFuncResult(),
                                  ElemExsltFuncResult.class  /* class object */);            
    
    XSLTElementDef importSchemaDef = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "import-schema",
                                  null /*alias */, templateElements /* elements */,  // EMPTY
                                  new XSLTAttributeDef[]{ namespaceAttr, schemaLocationAttr },
                                  new ProcessorImportSchema(),
                                  ElemImportSchema.class /* class object */, 1, true);
    
    XSLTElementDef xslContextItem = new XSLTElementDef(this,
					              Constants.S_XSLNAMESPACEURL, "context-item",
					              null /*alias */, null /* elements */,
					              new XSLTAttributeDef[]{ asAttrOpt, useAttrOpt },
					              new ProcessorTemplateElem(),
					              ElemContextItem.class /* class object */, 20, true);

    int i = 0;

    templateElements[i++] = charData;  // #PCDATA

    // char-instructions
    templateElements[i++] = xslApplyTemplates;
    templateElements[i++] = xslCallTemplate;
    templateElements[i++] = xslApplyImports;
    templateElements[i++] = xslForEach;
    templateElements[i++] = xslForEachGroup;
    templateElements[i++] = xslSourceDocument;
    templateElements[i++] = xslFork;
    templateElements[i++] = xslResultDocument;
    templateElements[i++] = xslAnalyzeString;
    templateElements[i++] = xslMerge;
    templateElements[i++] = xslMergeSource;
    templateElements[i++] = xslMergeKey;
    templateElements[i++] = xslMergeAction;
    templateElements[i++] = xslTry;
    templateElements[i++] = xslCatch;
    templateElements[i++] = xslMatchingSubstring;
    templateElements[i++] = xslNonMatchingSubstring;
    templateElements[i++] = xslIterate;
    templateElements[i++] = xslIterateBreak;
    templateElements[i++] = xslIterateOnCompletion;
    templateElements[i++] = xslIterateNextIteration;
    templateElements[i++] = xslValueOf;
    templateElements[i++] = xslEvaluate;
    templateElements[i++] = xslContextItem;
    templateElements[i++] = xslCopyOf;
    templateElements[i++] = xslDocument;
    templateElements[i++] = xslNumber;
    templateElements[i++] = xslChoose;
    templateElements[i++] = xslIf;
    templateElements[i++] = xslText;
    templateElements[i++] = xslCopy;
    templateElements[i++] = xslVariable;
    templateElements[i++] = xslSequence;
    templateElements[i++] = xslMessage;
    templateElements[i++] = xslFallback;

    // instructions
    templateElements[i++] = xslProcessingInstruction;
    templateElements[i++] = xslComment;
    templateElements[i++] = xslElement;
    templateElements[i++] = xslAttribute;
    templateElements[i++] = resultElement;
    templateElements[i++] = unknownElement;
    templateElements[i++] = exsltFunction;
    templateElements[i++] = exsltResult;
    templateElements[i++] = importSchemaDef;

    System.arraycopy(templateElements, 0, templateElementsAndParams, 0, i);
    System.arraycopy(templateElements, 0, templateElementsAndSort, 0, i);
    System.arraycopy(templateElements, 0, exsltFunctionElements, 0, i);
    
    templateElementsAndParams[i] = xslParam;
    templateElementsAndSort[i] = xslSort;
    exsltFunctionElements[i]   = xslParam;

    i = 0;
    charTemplateElements[i++] = charData;  // #PCDATA

    // char-instructions
    charTemplateElements[i++] = xslApplyTemplates;
    charTemplateElements[i++] = xslCallTemplate;
    charTemplateElements[i++] = xslApplyImports;
    charTemplateElements[i++] = xslForEach;
    charTemplateElements[i++] = xslForEachGroup;
    charTemplateElements[i++] = xslSourceDocument;
    charTemplateElements[i++] = xslFork;
    charTemplateElements[i++] = xslResultDocument;
    charTemplateElements[i++] = xslMerge;
    charTemplateElements[i++] = xslMergeSource;
    charTemplateElements[i++] = xslMergeKey;
    charTemplateElements[i++] = xslMergeAction;
    charTemplateElements[i++] = xslTry;
    charTemplateElements[i++] = xslCatch;
    charTemplateElements[i++] = xslValueOf;
    charTemplateElements[i++] = xslContextItem;
    charTemplateElements[i++] = xslCopyOf;
    charTemplateElements[i++] = xslDocument;
    charTemplateElements[i++] = xslNumber;
    charTemplateElements[i++] = xslChoose;
    charTemplateElements[i++] = xslIf;
    charTemplateElements[i++] = xslText;
    charTemplateElements[i++] = xslCopy;
    charTemplateElements[i++] = xslVariable;
    charTemplateElements[i++] = xslMessage;
    charTemplateElements[i++] = xslFallback;

    XSLTElementDef importDef = new XSLTElementDef(this,
                                 Constants.S_XSLNAMESPACEURL, "import",
                                 null /*alias */, null /* elements */,
                                 new XSLTAttributeDef[]{ hrefAttr },  // EMPTY
                                 new ProcessorImport(),
                                 null /* class object */,
                                        1, true);
    XSLTElementDef includeDef = new XSLTElementDef(this,
                                  Constants.S_XSLNAMESPACEURL, "include",
                                  null /*alias */, null /* elements */,  // EMPTY
                                  new XSLTAttributeDef[]{ hrefAttr },
                                  new ProcessorInclude(),
                                  null /* class object */,
                                               20, true);        
    
    XSLTAttributeDef[] scriptAttrs = new XSLTAttributeDef[]{
    					    new XSLTAttributeDef(null, "lang", XSLTAttributeDef.T_NMTOKEN,
                                                                 true, false, XSLTAttributeDef.WARNING),
                                            new XSLTAttributeDef(null, "src", XSLTAttributeDef.T_URL, 
                                            			 false, false, XSLTAttributeDef.WARNING)};

    XSLTAttributeDef[] componentAttrs = new XSLTAttributeDef[]{ 
                                            new XSLTAttributeDef(null, "prefix", XSLTAttributeDef.T_NMTOKEN, 
                                            			 true, false, XSLTAttributeDef.WARNING),
                                            new XSLTAttributeDef(null, "elements", XSLTAttributeDef.T_STRINGLIST, 
                                            			 false, false, XSLTAttributeDef.WARNING),
                                            new XSLTAttributeDef(null, "functions", XSLTAttributeDef.T_STRINGLIST, 
                                            			 false, false, XSLTAttributeDef.WARNING) };

    XSLTElementDef[] topLevelElements = new XSLTElementDef[]
                                 {includeDef,                                  	
                                  importDef,
                                  importSchemaDef,
                                  // resultElement,
                                  whiteSpaceOnly,
                                  unknownElement,
                                  new XSLTElementDef(
                                         this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "strip-space",
                                         null /*alias */,
                                         null /* elements */,
                                         new XSLTAttributeDef[]{
                                                elementsAttr },
                                                new ProcessorStripSpace(),
                                         null /* class object */, 20, true),
                                  new XSLTElementDef(
                                         this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "preserve-space",
                                         null /*alias */,
                                         null /* elements */,
                                         new XSLTAttributeDef[]{
                                                 elementsAttr },
                                                 new ProcessorPreserveSpace(),
                                         null /* class object */, 20, true),
                                  new XSLTElementDef(
                                         this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "output",
                                         null /*alias */,
                                         null /* elements */,
                                         new XSLTAttributeDef[]{
                                                  methodAttr,                                                  
                                                  versionAttr,
                                                  encodingAttr,
                                                  omitXmlDeclarationAttr,
                                                  useCharacterMapsAttr,
                                                  standaloneAttr,
                                                  doctypePublicAttr,
                                                  doctypeSystemAttr,
                                                  cdataSectionElementsAttr,
                                                  indentAttr,
                                                  mediaTypeAttr,
                                                  XSLTAttributeDef.m_foreignAttr }, 
                                          new ProcessorOutputElem(), null /* class object */, 20, true),
                                  new XSLTElementDef(
                                          this,
                                          Constants.S_XSLNAMESPACEURL,
                                          "character-map",
                                          null /*alias */,
                                          new XSLTElementDef[] { xslOutputCharacter } /* elements */,
                                          new XSLTAttributeDef[] { nameAttrRequired, useCharacterMapsAttr }, 
                                           new ProcessorCharacterMap(), ElemCharacterMap.class /* class object */, 20, true),
                                  new XSLTElementDef(
                                          this,
                                          Constants.S_XSLNAMESPACEURL,
                                          "key",
                                          null /*alias */,
                                          null /* elements */,  // EMPTY
                                          new XSLTAttributeDef[]{ nameAttrRequired,
                                                  matchAttrRequired,
                                                  useAttr }, 
                                          new ProcessorKey(), null /* class object */, 20, true),
                                  new XSLTElementDef(
                                          this,
                                          Constants.S_XSLNAMESPACEURL,
                                          "decimal-format",
                                          null /*alias */,
                                          null /* elements */,  // EMPTY
                                          new XSLTAttributeDef[]{
                                                  nameAttrOpt_ERROR,
                                                  decimalSeparatorAttr,
                                                  groupingSeparatorAttr,
                                                  infinityAttr,
                                                  minusSignAttr,
                                                  NaNAttr,
                                                  percentAttr,
                                                  perMilleAttr,
                                                  zeroDigitAttr,
                                                  digitAttr,
                                                  patternSeparatorAttr }, 
                                           new ProcessorDecimalFormat(),
                                           null /* class object */, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_XSLNAMESPACEURL,
                                           "attribute-set",
                                           null /*alias */,
                                           new XSLTElementDef[]{
                                                   xslAttribute } /* elements */,
                                           new XSLTAttributeDef[]{
                                                   nameAttrRequired,
                                                   useAttributeSetsAttr }, 
                                           new ProcessorAttributeSet(),
                                           null /* class object */, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_XSLNAMESPACEURL,
                                           "variable",
                                           null /*alias */,
                                           templateElements /* elements */,
                                           new XSLTAttributeDef[]{
                                                   nameAttrRequired,
                                                   selectAttrOpt, asAttrOpt }, 
                                           new ProcessorGlobalVariableDecl(),
                                           ElemVariable.class /* class object */, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_XSLNAMESPACEURL,
                                           "param",
                                           null /*alias */,
                                           templateElements /* elements */,
                                           new XSLTAttributeDef[]{
                                                   nameAttrRequired,
                                                   selectAttrOpt, asAttrOpt }, 
                                           new ProcessorGlobalParamDecl(),
                                           ElemParam.class /* class object */, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_XSLNAMESPACEURL,
                                           "template",
                                           null /*alias */,
                                           templateElementsAndParams /* elements */,
                                           new XSLTAttributeDef[]{
                                                   matchAttrOpt,
                                                   nameAttrOpt_ERROR,                                                   
                                                   priorityAttr,
                                                   modeAttr,
                                                   asAttrOpt,
                                                   xpathDefaultNamespaceAttrOpt,
                                                   expandTextAttrOpt,
                                                   spaceAttr }, 
                                           new ProcessorTemplate(), ElemTemplate.class /* class object */, true, 20, true),
                                  new XSLTElementDef(
                                          this,
                                          Constants.S_XSLNAMESPACEURL,
                                          "function",
                                          null /*alias */,
                                          templateElementsAndParams /* elements */,
                                          new XSLTAttributeDef[] {
                                                  nameAttrRequired,
                                                  asAttrOpt, xpathDefaultNamespaceAttrOpt, expandTextAttrOpt, 
                                                  overrideAttrOpt, overrideExtFunctionAttrOpt, newEachTimeAttrOpt }, 
                                          new ProcessorTemplate(), ElemFunction.class /* class object */, true, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_XSLNAMESPACEURL,
                                           "namespace-alias",
                                           null /*alias */,
                                          null /* elements */,  // EMPTY
                                           new XSLTAttributeDef[]{ 
                                                   stylesheetPrefixAttr,
                                                   resultPrefixAttr }, 
                                           new ProcessorNamespaceAlias(), null /* class object */, 20, true),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_BUILTIN_EXTENSIONS_URL,
                                           "component",
                                           null /*alias */,
                                           new XSLTElementDef[]{
                                                    new XSLTElementDef(
                                                        this,
                                                        Constants.S_BUILTIN_EXTENSIONS_URL,
                                                        "script",
                                                        null /*alias */,
                                                    	new XSLTElementDef[]{ 
                                                        charData } /* elements */,
                                                        scriptAttrs, 
                                                        new ProcessorLRE(),
                                                        ElemExtensionScript.class /* class object */, 20, true) },  // EMPTY
                                           componentAttrs, 
                                           new ProcessorLRE(), ElemExtensionDecl.class /* class object */),
                                  new XSLTElementDef(
                                           this,
                                           Constants.S_BUILTIN_OLD_EXTENSIONS_URL,
                                           "component",
                                           null /*alias */,
                                           new XSLTElementDef[]{
                                                    new XSLTElementDef(
                                                        this,
                                                        Constants.S_BUILTIN_OLD_EXTENSIONS_URL,
                                                        "script",
                                                        null /*alias */,
                                                    	new XSLTElementDef[]{ 
                                                        charData } /* elements */,
                                                        scriptAttrs, 
                                                        new ProcessorLRE(),
                                                        ElemExtensionScript.class /* class object */, 20, true) },  // EMPTY
                                           componentAttrs, 
                                           new ProcessorLRE(), ElemExtensionDecl.class /* class object */),
                                  exsltFunction}/* exslt */;  //end of topevelElements
    
    XSLTAttributeDef excludeResultPrefixesAttr =
      new XSLTAttributeDef(null, "exclude-result-prefixes",
                           XSLTAttributeDef.T_PREFIXLIST, false, false, XSLTAttributeDef.WARNING);
    XSLTAttributeDef extensionElementPrefixesAttr =
      new XSLTAttributeDef(null, "extension-element-prefixes",
                           XSLTAttributeDef.T_PREFIX_URLLIST, false, false, XSLTAttributeDef.WARNING);
    XSLTAttributeDef idAttr = new XSLTAttributeDef(null, "id",
                                XSLTAttributeDef.T_CDATA, false, false, XSLTAttributeDef.WARNING);    
    XSLTAttributeDef versionAttrRequired = new XSLTAttributeDef(null,
                                             "version",
                                             XSLTAttributeDef.T_NMTOKEN,
                                             true,false, XSLTAttributeDef.WARNING);
    
    XSLTElementDef stylesheetElemDef = new XSLTElementDef(this,
                                         Constants.S_XSLNAMESPACEURL,
                                         "stylesheet", "transform",
                                         topLevelElements,
                                         new XSLTAttributeDef[]{
                                           extensionElementPrefixesAttr,
                                           excludeResultPrefixesAttr,
                                           idAttr,
                                           versionAttrRequired,
                                           xpathDefaultNamespaceAttrOpt,
                                           expandTextAttrOpt,
                                           spaceAttr }, new ProcessorStylesheetElement(),  /* ContentHandler */
                                         null  /* class object */,
                                         true, -1, false);

    importDef.setElements(new XSLTElementDef[]{ stylesheetElemDef,
                                                resultElement,
                                                unknownElement });
    includeDef.setElements(new XSLTElementDef[]{ stylesheetElemDef,
                                                 resultElement,
                                                 unknownElement });
    build(null, null, null, new XSLTElementDef[]{ stylesheetElemDef,
                                                  whiteSpaceOnly,
                                                  resultElement,
                                                  unknownElement }, null,
                                                                    new ProcessorStylesheetDoc(),  /* ContentHandler */
                                                                    null  /* class object */
                                                                      );
  }

  /**
   * A hashtable of all available built-in elements for use by the element-available
   * function.
   * TODO:  When we convert to Java2, this should be a Set.
   */
  private HashMap m_availElems = new HashMap();
  
  /**
   * Get the table of available elements.
   * 
   * @return table of available elements, keyed by qualified names, and with 
   * values of the same qualified names.
   */
  public HashMap getElemsAvailable() 
  {
    return m_availElems;
  }

  /**
   * Adds a new element name to the Hashtable of available elements.
   * @param elemName The name of the element to add to the Hashtable of available elements.
   */
  void addAvailableElement(QName elemName)
  {
    m_availElems.put(elemName, elemName);
  }

  /**
   * Determines whether the passed element name is present in the list of available elements.
   * @param elemName The name of the element to look up.
   *
   * @return true if an element corresponding to elemName is available.
   */
  public boolean elementAvailable(QName elemName)
  {
    return m_availElems.containsKey(elemName);
  }
}

