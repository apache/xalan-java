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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ResourceBundle;

public final class ErrorMessages_no extends ErrorMessages {
    
    // Disse feilmeldingene maa korrespondere med konstantene som er definert
    // i kildekoden til ErrorMsg.
    private static final String errorMessages[] = { 
	// MULTIPLE_STYLESHEET_ERR
	"En fil kan bare innehold ett stilark.",
	// TEMPLATE_REDEF_ERR	
	"<xsl:template> ''{0}'' er allerede definert i dette stilarket.",
	// TEMPLATE_UNDEF_ERR
	"<xsl:template> ''{0}'' er ikke definert i dette stilarket.",
	// VARIABLE_REDEF_ERR	
	"Variabel ''{0}'' er allerede definert.",
	// VARIABLE_UNDEF_ERR
	"Variabel eller parameter ''{0}'' er ikke definert.",
	// CLASS_NOT_FOUND_ERR
	"Finner ikke klassen ''{0}''.",
	// METHOD_NOT_FOUND_ERR
	"Finner ikke ekstern funksjon ''{0}'' (m\u00e5 v\00e6re deklarert b\u00e5de 'static' og 'public').",
	// ARGUMENT_CONVERSION_ERR
	"Kan ikke konvertere argument/retur type i kall til funksjon ''{0}''",
	// FILE_NOT_FOUND_ERR
	"Finner ikke fil eller URI ''{0}''.",
	// INVALID_URI_ERR
	"Ugyldig URI ''{0}''.",
	// FILE_ACCESS_ERR
	"Kan ikke \u00e5pne fil eller URI ''{0}''.",
	// MISSING_ROOT_ERR
	"Forvented <xsl:stylesheet> eller <xsl:transform> element.",
	// NAMESPACE_UNDEF_ERR
	"Prefiks ''{0}'' er ikke deklarert.",
	// FUNCTION_RESOLVE_ERR
	"Kunne ikke resolvere kall til funksjon ''{0}''.",
	// NEED_LITERAL_ERR
	"Argument til ''{0}'' m\u00e5 v\00e6re ordrett tekst.",
	// XPATH_PARSER_ERR
	"Kunne ikke tolke XPath uttrykk ''{0}''.",
	// REQUIRED_ATTR_ERR
	"N\u00f8dvendig attributt ''{0}'' er ikke deklarert.",
	// ILLEGAL_CHAR_ERR
	"Ugyldig bokstav/tegn ''{0}'' i XPath uttrykk.",
	// ILLEGAL_PI_ERR
	"Ugyldig navn ''{0}'' for prosesserings-instruksjon.",
	// STRAY_ATTRIBUTE_ERR
	"Attributt ''{0}'' utenfor element.",
	// ILLEGAL_ATTRIBUTE_ERR
	"Ugyldig attributt ''{0}''.",
	// CIRCULAR_INCLUDE_ERR
	"Sirkul \00e6 import/include; stilark ''{0}'' er alt lest.",
	// RESULT_TREE_SORT_ERR
	"Result-tre fragmenter kan ikke sorteres (<xsl:sort> elementer vil "+
	"bli ignorert). Du m\u00e5 sortere nodene mens du bygger treet.",
	// SYMBOLS_REDEF_ERR
	"Formatterings-symboler ''{0}'' er alt definert.",
	// XSL_VERSION_ERR
	"XSL versjon ''{0}'' er ikke st\u00f8ttet av XSLTC.",
	// CIRCULAR_VARIABLE_ERR
	"Sirkul\00e6r variabel/parameter referanse i ''{0}''.",
	// ILLEGAL_BINARY_OP_ERR
	"Ugyldig operator for bin\00e6rt uttrykk.",
	// ILLEGAL_ARG_ERR
	"Ugyldig parameter i funksjons-kall.",
	// DOCUMENT_ARG_ERR
	"Andre argument til document() m\u00e5 v\00e6re et node-sett.",
	// MISSING_WHEN_ERR
	"Du m\u00e5 deklarere minst ett <xsl:when> element innenfor <xsl:choose>.",
	// MULTIPLE_OTHERWISE_ERR
	"Kun ett <xsl:otherwise> element kan deklareres innenfor <xsl:choose>.",
	// STRAY_OTHERWISE_ERR
	"<xsl:otherwise> kan kun benyttes innenfor <xsl:choose>.",
	// STRAY_WHEN_ERR
	"<xsl:when> kan kun benyttes innenfor <xsl:choose>.",
	// WHEN_ELEMENT_ERR	
	"Kun <xsl:when> og <xsl:otherwise> kan benyttes innenfor <xsl:choose>.",
	// UNNAMED_ATTRIBSET_ERR
	"<xsl:attribute-set> element manger 'name' attributt.",
	// ILLEGAL_CHILD_ERR
	"Ugyldig element.",
	// ILLEGAL_ELEM_NAME_ERR
	"''{0}'' er ikke et gyldig navn for et element.",
	// ILLEGAL_ATTR_NAME_ERR
	"''{0}'' er ikke et gyldig navn for et attributt.",
	// ILLEGAL_TEXT_NODE_ERR
	"Du kan ikke plassere tekst utenfor et <xsl:stylesheet> element.",
	// SAX_PARSER_CONFIG_ERR
	"JAXP parser er ikke korrekt konfigurert.",
	// INTERNAL_ERR
	"XSLTC-intern feil: ''{0}''",
	// UNSUPPORTED_XSL_ERR
	"St\u00f8tter ikke XSL element ''{0}''.",
	// UNSUPPORTED_EXT_ERR
	"XSLTC st\u00f8tter ikke utvidet funksjon ''{0}''.",
	// MISSING_XSLT_URI_ERR
	"Dette dokumentet er ikke et XSL stilark "+
	"(xmlns:xsl='http://www.w3.org/1999/XSL/Transform' er ikke deklarert).",
	// MISSING_XSLT_TARGET_ERR
	"Kan ikke finne stilark ved navn ''{0}'' i dette dokumentet.",
	// NOT_IMPLEMENTED_ERR
	"Ikke implementert/gjenkjent: ''{0}''.",
	// NOT_STYLESHEET_ERR
	"Dokumentet inneholder ikke et XSL stilark",
	// ELEMENT_PARSE_ERR
	"Kan ikke tolke element ''{0}''",
	// KEY_USE_ATTR_ERR
	"'use'-attributtet i <xsl:key> m\u00e5 v\00e6re node, node-sett, tekst eller nummer.",
	// OUTPUT_VERSION_ERR
	"Det genererte XML dokumentet m\u00e5 gis versjon 1.0",
	// ILLEGAL_RELAT_OP_ERR
	"Ugyldig operator for relasjons-uttrykk.",
	// ATTRIBSET_UNDEF_ERR
	"Finner ikke <xsl:attribute-set> element med navn ''{0}''.",
	// ATTR_VAL_TEMPLATE_ERR
	"Kan ikke tolke attributt ''{0}''.",
	// UNKNOWN_SIG_TYPE_ERR
	"Ukjent data-type i signatur for klassen ''{0}''.",
	// DATA_CONVERSION_ERR
	"Kan ikke oversette mellom data-type ''{0}'' og ''{1}''.",

	// NO_TRANSLET_CLASS_ERR
	"Dette Templates objected inneholder ingen translet klasse definisjon.",
	// NO_MAIN_TRANSLET_ERR
	"Dette Templates objected inneholder ingen klasse ved navn ''{0}''.",
	// TRANSLET_CLASS_ERR
	"Kan ikke laste translet-klasse ''{0}''.",
	// TRANSLET_OBJECT_ERR
	"Translet klassen er lastet man kan instansieres.",
	// ERROR_LISTENER_NULL_ERR
	"ErrorListener for ''{0}'' fors\u00f8kt satt til 'null'.",
	// JAXP_UNKNOWN_SOURCE_ERR
	"Kun StreamSource, SAXSource og DOMSOurce er st\u00f8ttet av XSLTC",
	// JAXP_NO_SOURCE_ERR
	"Source objekt sendt til ''{0}'' har intet innhold.",
	// JAXP_COMPILE_ERR
	"Kan ikke kompilere stilark.",
	// JAXP_INVALID_ATTR_ERR
	"TransformerFactory gjenkjenner ikke attributtet ''{0}''.",
	// JAXP_SET_RESULT_ERROR
	"setResult() m\u00e5 kalles f\u00f8r startDocument().",
	// JAXP_NO_TRANSLET_ERR
	"Transformer objektet inneholder ikken noen translet instans.",
	// JAXP_NO_HANDLER_ERR
	"Ingen 'handler' er satt for \u00e5 ta imot generert dokument.",
	// JAXP_NO_RESULT_ERR
	"Result objektet sendt til ''{0}'' er ikke gyldig.",
	// JAXP_UNKNOWN_PROP_ERR
	"Fors\u00f8ker \u00e5 lese ugyldig attributt ''{0}'' fra Transformer.",
	// SAX2DOM_ADAPTER_ERR
	"Kan ikke instansiere SAX2DOM adapter: ''{0}''.",
	// XSLTC_SOURCE_ERR
	"XSLTCSource.build() kalt uten at 'systemId' er definert.",

	// COMPILE_STDIN_ERR
	"Du kan ikke bruke -i uten \u00e5 ogs\u00e5 angi klasse-navn med -o.",
	// COMPILE_USAGE_STR
	"Bruk:\n" + 
	"   xsltc [-o <klasse>] [-d <katalog>] [-j <arkiv>]\n"+
	"         [-p <pakke>] [-x] [-s] [-u] <stilark>|-i\n\n"+
	"   Der:  <klasse> er navnet du vil gi den kompilerte java klassen.\n"+
	"         <stilark> er ett eller flere XSL stilark, eller dersom -u\n"+
	"         er benyttet, en eller flere URL'er til stilark.\n"+
	"         <katalog> katalog der klasse filer vil plasseres.\n"+
	"         <arkiv> er en JAR-fil der klassene vil plasseres\n"+
	"         <pakke> er an Java 'package' klassene vil legges i.\n\n"+
	"   Annet:\n"+
	"         -i tvinger kompilatoren til \u00e5 lese fra stdin.\n"+
	"         -o ignoreres dersom flere enn ett silark kompileres.\n"+
	"         -x sl\u00e5r p\u00e5 debug meldinger.\n"+
	"         -s blokkerer alle kall til System.exit().",
	// TRANSFORM_USAGE_STR
	"Bruk: \n" +
	"   xslt  [-j <arkiv>] {-u <url> | <dokument>} <klasse>\n"+
	"         [<param>=<verdi> ...]\n\n" +
	"   Der:  <dokument> er XML dokumentet som skal behandles.\n" +
	"         <url> er en URL til XML dokumentet som skal behandles.\n" +
	"         <klasse> er Java klassen som skal benyttes.\n" +
	"         <arkiv> er en JAR-fil som klassen leses fra.\n"+
	"   Annet:\n"+
	"         -x sl\u00e5r p\u00e5 debug meldinger.\n"+
	"         -s blokkerer alle kall til System.exit().",

	// STRAY_SORT_ERR
	"<xsl:sort> kan bare brukes under <xsl:for-each> eller <xsl:apply-templates>.",
	// UNSUPPORTED_ENCODING
	"Karaktersett ''{0}'' er ikke st\u00f8ttet av denne JVM.",
	// SYNTAX_ERR
	"Syntax error in ''{0}''."  // TODO: How do you say "syntax error" in norwegian?
    };

    public Object handleGetObject(String key) {
	if (key == null) return null;
	if (key.equals(ErrorMsg.ERROR_MESSAGES_KEY))
	    return errorMessages;
 	else if (key.equals(ErrorMsg.COMPILER_ERROR_KEY))
	    return "Kompilator-feil: ";
	else if (key.equals(ErrorMsg.COMPILER_WARNING_KEY))
	    return "Advarsel : ";	    
 	else if (key.equals(ErrorMsg.RUNTIME_ERROR_KEY))
	    return "Kj\u00f8refeil: ";
	return(null);
    }

}
