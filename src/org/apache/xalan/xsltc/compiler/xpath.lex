#/*
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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java_cup.runtime.Symbol;
%%
%cup
%unicode
%class XPathLexer
%yyeof

%eofval{
return new Symbol(sym.EOF);
%eofval}

%yylexthrow{
Exception
%yylexthrow}

Digit=[0-9]
Letter=[A-Za-z]
NCNameChar=({Letter}|{Digit}|"."|"-"|"_")
NCName=({Letter}|"_")({NCNameChar})*
%%

"*"                      { return new Symbol(sym.STAR); }
"/"                      { return new Symbol(sym.SLASH); } 
"+"                      { return new Symbol(sym.PLUS); }
"-"                      { return new Symbol(sym.MINUS); }
"div"                    { return new Symbol(sym.DIV); }
"mod"                    { return new Symbol(sym.MOD); }
"::"                     { return new Symbol(sym.DCOLON); }
","                      { return new Symbol(sym.COMMA); }
"@"                      { return new Symbol(sym.ATSIGN); }
".."                     { return new Symbol(sym.DDOT); }
"|"                      { return new Symbol(sym.VBAR); }
"$"                      { return new Symbol(sym.DOLLAR); }
"//"                     { return new Symbol(sym.DSLASH); }
"="                      { return new Symbol(sym.EQ); }
"!="                     { return new Symbol(sym.NE); }
"<"                      { return new Symbol(sym.LT); } 
">"                      { return new Symbol(sym.GT); }
"<="                     { return new Symbol(sym.LE); }
">="                     { return new Symbol(sym.GE); }
"id"                     { return new Symbol(sym.ID); }
"key"                    { return new Symbol(sym.KEY); }
"text()"                 { return new Symbol(sym.TEXT); }
"text"+[ \t\r\n\f]+"()"  { return new Symbol(sym.TEXT); }
"node()"                 { return new Symbol(sym.NODE); }
"node"+[ \t\r\n\f]+"()"  { return new Symbol(sym.NODE); }
"comment()"                 { return new Symbol(sym.COMMENT); }
"comment"+[ \t\r\n\f]+"()"  { return new Symbol(sym.COMMENT); }
"processing-instruction" { return new Symbol(sym.PIPARAM); }
"processing-instruction()"                { return new Symbol(sym.PI); }
"processing-instruction"+[ \t\r\n\f]+"()" { return new Symbol(sym.PI); }
"or"                     { return new Symbol(sym.OR); }
"and"                    { return new Symbol(sym.AND); }
"child"                  { return new Symbol(sym.CHILD); }
"attribute"              { return new Symbol(sym.ATTRIBUTE); }
"ancestor"               { return new Symbol(sym.ANCESTOR); }
"ancestor-or-self"       { return new Symbol(sym.ANCESTORORSELF); }
"descendant"             { return new Symbol(sym.DESCENDANT); }
"descendant-or-self"     { return new Symbol(sym.DESCENDANTORSELF); }
"following"              { return new Symbol(sym.FOLLOWING); }
"following-sibling"      { return new Symbol(sym.FOLLOWINGSIBLING); }
"namespace"              { return new Symbol(sym.NAMESPACE); }
"parent"                 { return new Symbol(sym.PARENT); }
"preceding"              { return new Symbol(sym.PRECEDING); }
"preceding-sibling"      { return new Symbol(sym.PRECEDINGSIBLING); }
"self"                   { return new Symbol(sym.SELF); }
"["                      { return new Symbol(sym.LBRACK); }
"]"                      { return new Symbol(sym.RBRACK); }
"("                      { return new Symbol(sym.LPAREN); }
")"                      { return new Symbol(sym.RPAREN); }
"<PATTERN>"              { return new Symbol(sym.PATTERN); }
"<EXPRESSION>"           { return new Symbol(sym.EXPRESSION); }
\"[^\"]*\"               { return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
\'[^\']*\'               { return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
{Digit}+               	 { return new Symbol(sym.INT, new Long(yytext())); }
{Digit}+("."{Digit}*)? 	 { return new Symbol(sym.REAL, new Double(yytext())); }
"."{Digit}+            	 { return new Symbol(sym.REAL, new Double(yytext())); }
"."                      { return new Symbol(sym.DOT); }
({NCName}":")?{NCName}   { return new Symbol(sym.QNAME, yytext()); }
({NCName}":")?"*"        { return new Symbol(sym.QNAME, yytext()); }
({NCName}":")?"@*"       { return new Symbol(sym.QNAME, yytext()); }
[ \t\r\n\f]              { /* ignore white space. */ }
.                        { throw new Exception(yytext()); }
