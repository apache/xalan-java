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
 */
package org.apache.xalan.xsltc.compiler;
import java_cup.runtime.Symbol;


class XPathLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 65536;
	private final int YY_EOF = 65537;
	public final int YYEOF = -1;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	XPathLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	XPathLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private XPathLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	static private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NOT_ACCEPT,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NOT_ACCEPT,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NOT_ACCEPT,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NOT_ACCEPT,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NOT_ACCEPT,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NOT_ACCEPT,
		/* 169 */ YY_NOT_ACCEPT,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NOT_ACCEPT,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR,
		/* 176 */ YY_NO_ANCHOR,
		/* 177 */ YY_NO_ANCHOR,
		/* 178 */ YY_NO_ANCHOR,
		/* 179 */ YY_NO_ANCHOR,
		/* 180 */ YY_NO_ANCHOR,
		/* 181 */ YY_NO_ANCHOR,
		/* 182 */ YY_NO_ANCHOR,
		/* 183 */ YY_NO_ANCHOR,
		/* 184 */ YY_NO_ANCHOR,
		/* 185 */ YY_NO_ANCHOR,
		/* 186 */ YY_NO_ANCHOR,
		/* 187 */ YY_NO_ANCHOR,
		/* 188 */ YY_NO_ANCHOR,
		/* 189 */ YY_NO_ANCHOR,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NO_ANCHOR,
		/* 192 */ YY_NO_ANCHOR,
		/* 193 */ YY_NO_ANCHOR,
		/* 194 */ YY_NO_ANCHOR,
		/* 195 */ YY_NO_ANCHOR,
		/* 196 */ YY_NO_ANCHOR,
		/* 197 */ YY_NO_ANCHOR,
		/* 198 */ YY_NO_ANCHOR,
		/* 199 */ YY_NO_ANCHOR,
		/* 200 */ YY_NO_ANCHOR,
		/* 201 */ YY_NO_ANCHOR,
		/* 202 */ YY_NO_ANCHOR,
		/* 203 */ YY_NO_ANCHOR,
		/* 204 */ YY_NO_ANCHOR,
		/* 205 */ YY_NO_ANCHOR,
		/* 206 */ YY_NO_ANCHOR,
		/* 207 */ YY_NO_ANCHOR,
		/* 208 */ YY_NO_ANCHOR,
		/* 209 */ YY_NO_ANCHOR,
		/* 210 */ YY_NO_ANCHOR,
		/* 211 */ YY_NO_ANCHOR,
		/* 212 */ YY_NO_ANCHOR,
		/* 213 */ YY_NO_ANCHOR,
		/* 214 */ YY_NO_ANCHOR,
		/* 215 */ YY_NO_ANCHOR
	};
	static private int yy_cmap[] = unpackFromString(1,65538,
"53:9,58:2,53,58:2,53:18,58,17,52,53,15,53:2,54,25,26,1,3,11,4,13,2,55:10,10" +
",53,18,16,19,53,12,43,56:3,45,56:3,50,56:4,47,51,42,56,46,49,44,56:3,48,56:" +
"2,40,53,41,53,57,53,34,37,28,5,21,38,32,35,6,56,20,36,8,27,9,29,56,30,31,23" +
",33,7,39,24,22,56,53,14,53:65411,0:2")[0];

	static private int yy_rmap[] = unpackFromString(1,216,
"0,1:2,2,1:2,3,4,1,5,6,1:3,7,8,1:4,9,1:2,10:2,1:3,11,1:5,12,10,1,10:5,1:2,10" +
",13,1,10,1,14,10,15,16,1,10:4,1,17,18,19,20,21,22,23,24,1,22,10,25:2,26,5,2" +
"7,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,5" +
"2,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,7" +
"7,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101" +
",102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,10" +
",120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,13" +
"8,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,1" +
"57,158,159,160,161,162,163,164,165,166,167")[0];

	static private int yy_nxt[][] = unpackFromString(168,59,
"1,2,3,4,5,6,60,167,186,63,7,8,9,10,11,12,13,61,14,15,193,167:2,197,167,16,1" +
"7,200,202,203,167,204,167:2,205,167:3,206,167,18,19,167:10,64,67,70,20,167:" +
"2,21,-1:61,22,-1:60,167:2,66,167:3,59,-1:2,69,-1:6,167,72,167:3,-1:2,167:13" +
",-1:2,167:10,-1:3,69,167,69,-1:11,25,-1:49,26,-1:70,27,-1:41,28,-1:19,30,-1" +
":25,62,-1:2,65,-1:29,31,-1:55,34,-1:41,20,-1:7,167:6,59,-1:2,69,-1:6,167:5," +
"-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:56,28,-1:58,34,-1:7,138,167:5,59," +
"-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,191,167:5,59" +
",-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,215,167:5,5" +
"9,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,141,167:5," +
"59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:2,36,-1:3,9" +
"6:5,-1:2,73,-1:7,96:5,-1:2,96:13,-1:2,96:10,-1:4,96:2,-1:5,167,23,167:4,59," +
"-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:17,29,-1:85,75" +
",-1:19,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:3,24,167:9,-1:2,167:10,-1:3,69," +
"167,69,-1:2,68:51,32,68:6,-1:48,77,-1:14,167:3,35,167:2,59,-1:2,69,-1:6,167" +
":5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:2,71:53,33,71:4,-1:4,167:6,59," +
"-1:2,69,-1:6,167:5,-1:2,167:4,98,167:8,-1:2,167:10,-1:3,69,167,69,-1:5,167," +
"37,167:4,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:45" +
",168,-1:18,167:6,59,-1:2,69,-1:6,167:2,38,167:2,-1:2,167:13,-1:2,167:10,-1:" +
"3,69,167,69,-1:43,79,-1:20,167:6,59,-1:2,69,-1:6,167:4,173,-1:2,167:13,-1:2" +
",167:10,-1:3,69,167,69,-1:47,169,-1:16,167,100,167:4,59,-1:2,69,-1:6,167:5," +
"-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:46,87,-1:17,167:4,175,167,59,-1:2" +
",69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:27,42,-1:36,167:2," +
"187,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:2" +
"7,43,-1:36,167:5,174,59,-1:2,69,-1:6,167,210,167:3,-1:2,167:13,-1:2,167:10," +
"-1:3,69,167,69,-1:47,91,-1:16,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:3,188,16" +
"7:9,-1:2,167:10,-1:3,69,167,69,-1:50,171,-1:13,167:6,59,-1:2,69,-1:6,167:5," +
"-1:2,167:9,104,167:3,-1:2,167:10,-1:3,69,167,69,-1:48,93,-1:15,167:6,59,-1:" +
"2,69,-1:6,167:3,106,167,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:20,46,-1:" +
"43,167,39,167:4,59,-1:2,69,-1:6,167:5,-1:2,167,194,167:11,-1:2,167:10,-1:3," +
"69,167,69,-1:51,99,-1:12,96:6,-1:3,96,-1:6,96:5,-1:2,96:13,-1:2,96:10,-1:3," +
"96:3,-1:27,48,-1:36,167:6,59,-1:2,69,-1:6,167:5,-1:2,167,198,167:11,-1:2,16" +
"7:10,-1:3,69,167,69,-1:52,101,-1:11,167:6,59,-1:2,69,-1:6,167,109,167:3,-1:" +
"2,167:13,-1:2,167:10,-1:3,69,167,69,-1:48,103,-1:15,167:4,111,167,59,-1:2,6" +
"9,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:20,53,-1:43,167:6,59" +
",-1:2,69,-1:6,167:5,-1:2,167:11,40,167,-1:2,167:10,-1:3,69,167,69,-1:27,58," +
"-1:36,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:3,114,167:9,-1:2,167:10,-1:3,69," +
"167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:9,115,167:3,-1:2,167:10,-1" +
":3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,83,-1,167:13,-1:2,167:10,-1:3" +
",69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,85,-1,167:13,-1:2,167:10,-1:3,6" +
"9,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:4,116,167:8,-1:2,167:10," +
"-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,190,167:3,-1:2,167:13,-1:2,16" +
"7:10,-1:3,69,167,69,-1:5,167,41,167:4,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:" +
"2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,119,167:12,-1" +
":2,167:10,-1:3,69,167,69,-1:5,167:2,120,167:3,59,-1:2,69,-1:6,167:5,-1:2,16" +
"7:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:5,122,59,-1:2,69,-1:6,167:5,-1:2,1" +
"67:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:" +
"2,123,167:10,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1" +
":2,167:4,179,167:8,-1:2,167:10,-1:3,69,167,69,-1:5,167,124,167:4,59,-1:2,69" +
",-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1" +
":6,167:3,44,167,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,6" +
"9,-1:6,167:5,-1:2,167:10,125,167:2,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59" +
",-1:2,69,-1:6,167:3,126,167,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167" +
":6,59,-1:2,69,-1:6,167:5,-1:2,167:12,207,-1:2,167:10,-1:3,69,167,69,-1:5,16" +
"7:6,59,-1:2,69,-1:6,167:5,-1:2,167:7,127,167:5,-1:2,167:10,-1:3,69,167,69,-" +
"1:5,167:2,129,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,1" +
"67,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:6,130,167:6,-1:2,167:10,-1:" +
"3,69,167,69,-1:5,167:5,131,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1" +
":3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167,132,167:11,-1:2,167:" +
"10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,97,-1,167:13,-1:2,167:10" +
",-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,133,167:12,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:3,134,167,-1:2,167:13,-1:2," +
"167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:3,45,167:9," +
"-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,47,167:3,-1:2,167" +
":13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:5," +
"49,167:7,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,50,167:3" +
",-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1" +
":2,167:5,51,167:7,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167" +
":3,52,167,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6" +
",167:5,-1:2,167:5,139,167:7,-1:2,167:10,-1:3,69,167,69,-1:5,167:5,140,59,-1" +
":2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,142,167:5,59,-" +
"1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2" +
",69,-1:6,167:5,-1:2,167:3,143,167:9,-1:2,167:10,-1:3,69,167,69,-1:5,167:5,1" +
"44,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:2," +
"145,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5" +
",195,167:5,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:" +
"5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:3,208,167:9,-1:2,167:10,-1:3,69,167," +
"69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,199,167:12,-1:2,167:10,-1:3,69,167" +
",69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:10,147,167:2,-1:2,167:10,-1:3" +
",69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:9,150,167:3,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,151,167:3,-1:2,167:13,-1:2," +
"167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:3,153,167,-1:2,167:13," +
"-1:2,167:10,-1:3,69,167,69,-1:5,167:2,154,167:3,59,-1:2,69,-1:6,167:5,-1:2," +
"167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167" +
":9,155,167:3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,156," +
"167:3,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167" +
":5,-1:2,167:3,157,167:9,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1" +
":6,167:5,-1:2,158,167:12,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-" +
"1:6,167:5,-1:2,167:11,54,167,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2," +
"69,-1:6,167:5,-1:2,167:9,160,167:3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59" +
",-1:2,69,-1:6,167:5,-1:2,167:6,161,167:6,-1:2,167:10,-1:3,69,167,69,-1:5,16" +
"7:6,59,-1:2,69,-1:6,167:5,-1:2,167:5,55,167:7,-1:2,167:10,-1:3,69,167,69,-1" +
":5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:5,56,167:7,-1:2,167:10,-1:3,69,167," +
"69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:11,57,167,-1:2,167:10,-1:3,69," +
"167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167,162,167:11,-1:2,167:10,-1:" +
"3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:3,163,167,-1:2,167:13,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:2,164,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:" +
"2,167:10,-1:3,69,167,69,-1:5,167:5,165,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1" +
":2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,166,167:12,-" +
"1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,105,-1,167:13,-1" +
":2,167:10,-1:3,69,167,69,-1:45,81,-1:59,89,-1:17,167:4,102,167,59,-1:2,69,-" +
"1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:50,95,-1:13,167:6,59,-1" +
":2,69,-1:6,167:5,-1:2,167:9,107,167:3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6" +
",59,-1:2,69,-1:6,167:3,108,167,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5," +
"167:6,59,-1:2,69,-1:6,167:5,-1:2,167,177,167:11,-1:2,167:10,-1:3,69,167,69," +
"-1:5,167:6,59,-1:2,69,-1:6,167,110,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,16" +
"7,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:4,121,167:8,-1:2,167:10,-1:3" +
",69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,117,167:3,-1:2,167:13,-1:2,167:10" +
",-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,180,167:12,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:4,211,167:8,-1:2" +
",167:10,-1:3,69,167,69,-1:5,167,182,167:4,59,-1:2,69,-1:6,167:5,-1:2,167:13" +
",-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:3,128,167,-1:2,1" +
"67:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:" +
"7,192,167:5,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:" +
"2,135,167:12,-1:2,167:10,-1:3,69,167,69,-1:5,167:2,146,167:3,59,-1:2,69,-1:" +
"6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,1" +
"67:5,-1:2,159,167:12,-1:2,167:10,-1:3,69,167,69,-1:5,167:5,74,59,-1:2,69,-1" +
":6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6," +
"167:5,-1:2,167:9,112,167:3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69" +
",-1:6,167,113,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1" +
":2,69,-1:6,167,118,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6," +
"59,-1:2,69,-1:6,167:5,-1:2,181,167:12,-1:2,167:10,-1:3,69,167,69,-1:5,167:6" +
",59,-1:2,69,-1:6,167:5,-1:2,167:4,184,167:8,-1:2,167:10,-1:3,69,167,69,-1:5" +
",167:6,59,-1:2,69,-1:6,167:5,-1:2,136,167:12,-1:2,167:10,-1:3,69,167,69,-1:" +
"5,167:6,59,-1:2,69,-1:6,167,76,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,167,69" +
",-1:5,167:6,59,-1:2,69,-1:6,167,176,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,1" +
"67,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:4,148,167:8,-1:2,167:10,-1:" +
"3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,137,167:12,-1:2,167:10,-1" +
":3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,78,167:3,-1:2,167:13,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167,178,167:3,-1:2,167:13,-1:2," +
"167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:4,149,167:8" +
",-1:2,167:10,-1:3,69,167,69,-1:5,167:5,80,59,-1:2,69,-1:6,167:5,-1:2,167:7," +
"82,167:5,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,1" +
"67:4,152,167:8,-1:2,167:10,-1:3,69,167,69,-1:5,167:5,170,59,-1:2,69,-1:6,16" +
"7:5,-1:2,167:8,84,167:4,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1" +
":6,167:5,-1:2,167:3,86,167:3,88,167:5,-1:2,167:10,-1:3,69,167,69,-1:5,167:6" +
",59,-1:2,69,-1:6,167,90,167:3,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,1" +
"67:6,59,-1:2,69,-1:6,167:3,92,167,-1:2,94,167:12,-1:2,167:10,-1:3,69,167,69" +
",-1:5,167:5,172,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,6" +
"9,-1:5,167:2,183,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,6" +
"9,167,69,-1:5,201,167:5,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3," +
"69,167,69,-1:5,167:2,185,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:13,-1:2,167:1" +
"0,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167,189,167:11,-1:2," +
"167:10,-1:3,69,167,69,-1:5,167:2,196,167:3,59,-1:2,69,-1:6,167:5,-1:2,167:1" +
"3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,167:9,20" +
"9,167:3,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6,167:5,-1:2,16" +
"7:10,212,167:2,-1:2,167:10,-1:3,69,167,69,-1:5,167:2,213,167:3,59,-1:2,69,-" +
"1:6,167:5,-1:2,167:13,-1:2,167:10,-1:3,69,167,69,-1:5,167:6,59,-1:2,69,-1:6" +
",167:5,-1:2,167:4,214,167:8,-1:2,167:10,-1:3,69,167,69,-1");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException, 
Exception

		{
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return new Symbol(sym.STAR); }
					case -3:
						break;
					case 3:
						{ return new Symbol(sym.SLASH); }
					case -4:
						break;
					case 4:
						{ return new Symbol(sym.PLUS); }
					case -5:
						break;
					case 5:
						{ return new Symbol(sym.MINUS); }
					case -6:
						break;
					case 6:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -7:
						break;
					case 7:
						{ throw new Exception(yytext()); }
					case -8:
						break;
					case 8:
						{ return new Symbol(sym.COMMA); }
					case -9:
						break;
					case 9:
						{ return new Symbol(sym.ATSIGN); }
					case -10:
						break;
					case 10:
						{ return new Symbol(sym.DOT); }
					case -11:
						break;
					case 11:
						{ return new Symbol(sym.VBAR); }
					case -12:
						break;
					case 12:
						{ return new Symbol(sym.DOLLAR); }
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.EQ); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.LT); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.GT); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.LPAREN); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.RPAREN); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.LBRACK); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.RBRACK); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.INT, new Integer(yytext())); }
					case -21:
						break;
					case 21:
						{ /* ignore white space. */ }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.DSLASH); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.ID); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.OR); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.DCOLON); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.DDOT); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.REAL, new Double(yytext())); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.NE); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.LE); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym.GE); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym.Literal,
			      yytext().substring(1, yytext().length() - 1)); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym.REAL, new Double(yytext())); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.DIV); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym.MOD); }
					case -38:
						break;
					case 38:
						{ return new Symbol(sym.KEY); }
					case -39:
						break;
					case 39:
						{ return new Symbol(sym.AND); }
					case -40:
						break;
					case 40:
						{ return new Symbol(sym.SELF); }
					case -41:
						break;
					case 41:
						{ return new Symbol(sym.CHILD); }
					case -42:
						break;
					case 42:
						{ return new Symbol(sym.TEXT); }
					case -43:
						break;
					case 43:
						{ return new Symbol(sym.NODE); }
					case -44:
						break;
					case 44:
						{ return new Symbol(sym.PARENT); }
					case -45:
						break;
					case 45:
						{ return new Symbol(sym.ANCESTOR); }
					case -46:
						break;
					case 46:
						{ return new Symbol(sym.PATTERN); }
					case -47:
						break;
					case 47:
						{ return new Symbol(sym.NAMESPACE); }
					case -48:
						break;
					case 48:
						{ return new Symbol(sym.COMMENT); }
					case -49:
						break;
					case 49:
						{ return new Symbol(sym.PRECEDING); }
					case -50:
						break;
					case 50:
						{ return new Symbol(sym.ATTRIBUTE); }
					case -51:
						break;
					case 51:
						{ return new Symbol(sym.FOLLOWING); }
					case -52:
						break;
					case 52:
						{ return new Symbol(sym.DESCENDANT); }
					case -53:
						break;
					case 53:
						{ return new Symbol(sym.EXPRESSION); }
					case -54:
						break;
					case 54:
						{ return new Symbol(sym.ANCESTORORSELF); }
					case -55:
						break;
					case 55:
						{ return new Symbol(sym.PRECEDINGSIBLING); }
					case -56:
						break;
					case 56:
						{ return new Symbol(sym.FOLLOWINGSIBLING); }
					case -57:
						break;
					case 57:
						{ return new Symbol(sym.DESCENDANTORSELF); }
					case -58:
						break;
					case 58:
						{ return new Symbol(sym.PI); }
					case -59:
						break;
					case 60:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -60:
						break;
					case 61:
						{ throw new Exception(yytext()); }
					case -61:
						break;
					case 63:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -62:
						break;
					case 64:
						{ throw new Exception(yytext()); }
					case -63:
						break;
					case 66:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -64:
						break;
					case 67:
						{ throw new Exception(yytext()); }
					case -65:
						break;
					case 69:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -66:
						break;
					case 70:
						{ throw new Exception(yytext()); }
					case -67:
						break;
					case 72:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -68:
						break;
					case 74:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -69:
						break;
					case 76:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -70:
						break;
					case 78:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -71:
						break;
					case 80:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -72:
						break;
					case 82:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -73:
						break;
					case 84:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -74:
						break;
					case 86:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -75:
						break;
					case 88:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -76:
						break;
					case 90:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -77:
						break;
					case 92:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -78:
						break;
					case 94:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -79:
						break;
					case 96:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -80:
						break;
					case 98:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -81:
						break;
					case 100:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -82:
						break;
					case 102:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -83:
						break;
					case 104:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -84:
						break;
					case 106:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -85:
						break;
					case 107:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -86:
						break;
					case 108:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -87:
						break;
					case 109:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -88:
						break;
					case 110:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -89:
						break;
					case 111:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -90:
						break;
					case 112:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -91:
						break;
					case 113:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -92:
						break;
					case 114:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -93:
						break;
					case 115:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -94:
						break;
					case 116:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -95:
						break;
					case 117:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -96:
						break;
					case 118:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -97:
						break;
					case 119:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -98:
						break;
					case 120:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -99:
						break;
					case 121:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -100:
						break;
					case 122:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -101:
						break;
					case 123:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -102:
						break;
					case 124:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -103:
						break;
					case 125:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -104:
						break;
					case 126:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -105:
						break;
					case 127:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -106:
						break;
					case 128:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -107:
						break;
					case 129:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -108:
						break;
					case 130:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -109:
						break;
					case 131:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -110:
						break;
					case 132:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -111:
						break;
					case 133:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -112:
						break;
					case 134:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -113:
						break;
					case 135:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -114:
						break;
					case 136:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -115:
						break;
					case 137:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -116:
						break;
					case 138:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -117:
						break;
					case 139:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -118:
						break;
					case 140:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -119:
						break;
					case 141:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -120:
						break;
					case 142:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -121:
						break;
					case 143:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -122:
						break;
					case 144:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -123:
						break;
					case 145:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -124:
						break;
					case 146:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -125:
						break;
					case 147:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -126:
						break;
					case 148:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -127:
						break;
					case 149:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -128:
						break;
					case 150:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -129:
						break;
					case 151:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -130:
						break;
					case 152:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -131:
						break;
					case 153:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -132:
						break;
					case 154:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -133:
						break;
					case 155:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -134:
						break;
					case 156:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -135:
						break;
					case 157:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -136:
						break;
					case 158:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -137:
						break;
					case 159:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -138:
						break;
					case 160:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -139:
						break;
					case 161:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -140:
						break;
					case 162:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -141:
						break;
					case 163:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -142:
						break;
					case 164:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -143:
						break;
					case 165:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -144:
						break;
					case 166:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -145:
						break;
					case 167:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -146:
						break;
					case 170:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -147:
						break;
					case 172:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -148:
						break;
					case 173:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -149:
						break;
					case 174:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -150:
						break;
					case 175:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -151:
						break;
					case 176:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -152:
						break;
					case 177:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -153:
						break;
					case 178:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -154:
						break;
					case 179:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -155:
						break;
					case 180:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -156:
						break;
					case 181:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -157:
						break;
					case 182:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -158:
						break;
					case 183:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -159:
						break;
					case 184:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -160:
						break;
					case 185:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -161:
						break;
					case 186:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -162:
						break;
					case 187:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -163:
						break;
					case 188:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -164:
						break;
					case 189:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -165:
						break;
					case 190:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -166:
						break;
					case 191:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -167:
						break;
					case 192:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -168:
						break;
					case 193:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -169:
						break;
					case 194:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -170:
						break;
					case 195:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -171:
						break;
					case 196:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -172:
						break;
					case 197:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -173:
						break;
					case 198:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -174:
						break;
					case 199:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -175:
						break;
					case 200:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -176:
						break;
					case 201:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -177:
						break;
					case 202:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -178:
						break;
					case 203:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -179:
						break;
					case 204:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -180:
						break;
					case 205:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -181:
						break;
					case 206:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -182:
						break;
					case 207:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -183:
						break;
					case 208:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -184:
						break;
					case 209:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -185:
						break;
					case 210:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -186:
						break;
					case 211:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -187:
						break;
					case 212:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -188:
						break;
					case 213:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -189:
						break;
					case 214:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -190:
						break;
					case 215:
						{ return new Symbol(sym.QNAME, yytext()); }
					case -191:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
