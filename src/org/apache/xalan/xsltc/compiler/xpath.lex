/*
 * @(#)$Id$
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
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
"node()"                 { return new Symbol(sym.NODE); }
"comment()"              { return new Symbol(sym.COMMENT); }
"processing-instruction()" { return new Symbol(sym.PI); }
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
{Digit}+               	 { return new Symbol(sym.INT, new Integer(yytext())); }
{Digit}+("."{Digit}*)? 	 { return new Symbol(sym.REAL, new Double(yytext())); }
"."{Digit}+            	 { return new Symbol(sym.REAL, new Double(yytext())); }
"."                      { return new Symbol(sym.DOT); }
({NCName}":")?{NCName}   { return new Symbol(sym.QNAME, yytext()); }
({NCName}":")?"*"        { return new Symbol(sym.QNAME, yytext()); }
({NCName}":")?"@*"       { return new Symbol(sym.QNAME, yytext()); }
[ \t\r\n\f]              { /* ignore white space. */ }
.                        { throw new Exception(yytext()); }
