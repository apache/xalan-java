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
package org.apache.xml.utils;

/** CharacterBlockEnumeration yields a series of {char[], int, int} triplets.
 * Its primary use is returning a low-level representation of text scattered
 * across multiple storage units, eg one or more FastStringBuffer "chunks",
 * so they can be processed without first converting them to a single Java
 * String or StringBuffer.
 * 
 * Note that the values are displayed on this object rather than being
 * returned as elements as in the standard Java Enumeration
 * When constructed, the CharacterBlockEnumeration will immediately
 * display the first available set of values;  hasMoreElements() can be
 * used to test whether another set is available, and nextElement() can
 * be used to step to the next set.
 * 
 * This class directly implements the basic functionality for a single 
 * string or character array; it can be subclassed to handle non-contiguous
 * types such as FastStringBuffer. Obviously, in those cases it is the user's
 * responsibility not to alter the data structure while it is being
 * enumerated, except in ways that may (or may not) be explicitly permitted
 * by those specific implementation classes. Even for this simple version,
 * alterations to the source after enumeration begins are not guaranteed 
 * to be correctly reflected in the enumerated view.
 * 
 * %REVIEW% An argument can be made for making this
 * an interface. Since it's a transient, I'm not sure the extra fields
 * are really enough of a nuisance to justify doing so.
 * */
public class CharacterBlockEnumeration 
// implements java.util.Enumeration, but see nextElement's comments
{
	protected char[] _chars=null;
	protected int _start;
	protected int _length;
	protected String _string=null;
	
	static final protected char[] EMPTY=new char[0];
	
	/** Create an empty enumeration. */
	public CharacterBlockEnumeration()
	{
	}
	
	/** Construct a CharacterBlockEnumeration to represent the content
	 * of a Java string.
	 * 
	 * @param s The String whose content is to be returned.
	 * */
	public CharacterBlockEnumeration(String s)
	{
		_string=s;
		_length=s.length();
	}
	
	/** Construct a CharacterBlockEnumeration to represent part of the content
	 * of a Java string.
	 * 
	 * @param s The String whose content is to be returned.
	 * @param start Starting offset of the substring. If greater than
	 *   or equal to the length of the string, we will deliver zero characters
	 * @param length Number of characters in the substring. This will be
	 *   truncated to not exceed the length of the string.
	 * */
	public CharacterBlockEnumeration(String s, int start, int length)
	{
		_string=s;
		int l=s.length();
		if(start<=l)
		{
			_start=start;
			int max=l-start;
			_length=(length<max) ? length : max;
		}
	}
	
	/** Construct a CharacterBlockEnumeration to represent the content
	 * of a Java character array
	 * 
	 * @param ch The char array whose content is to be returned.
	 * */
	public CharacterBlockEnumeration(char[] ch)
	{
		_chars=ch;
		_length=ch.length;
	}


	/** Construct a CharacterBlockEnumeration to represent part of the content
	 * of a Java character array
	 * 
	 * @param ch The char array whose content is to be returned.
	 * @param start Starting offset of the substring. If greater than
	 *   or equal to the length of the array, we will deliver zero characters
	 * @param length Number of characters in the substring. This will be
	 *   truncated to not exceed the length of the array.
	 * */
	public CharacterBlockEnumeration(char[] ch, int start, int length)
	{
		_chars=ch;
		int l=ch.length;
		if(start<=l)
		{
			_start=start;
			int max=l-start;
			_length=(length<max) ? length : max;
		}
	}
	
	/** @return true if another character block can be accessed by calling
	 * nextElement()
	 */
	public boolean hasMoreElements()
	{
		return false;
	}
	
	/** Advance to the next character block. 
	 * 
	 * @returns either this CharacterBlockEnumeration object (as a
	 * transient accessor to the "element") or null if no more elements are available.
	 * This is a bit of a kluge, but it allows us to claim that we
	 * implement the Java Enumeration interface if we want to do so, and
	 * it seems to be as good or bad as any other return value.
	 * */
	public Object nextElement()
	{
		_chars=null;
		_string=null;
		return null;
	}
	
	
	/** @return the starting offset in the current block's character array
	 * */
	public int getStart()
	{
		return _start;
	}

	/** @return the length of the the current block
	 * */
	public int getLength()
	{
		return _length;
	}

	/** 
	 * @return the current block's character array. Data will begin at
	 * offset {start}.
	 * */
	public char[] getChars()
	{
		// Implementation note for this particular version of the class: 
		// In a JVM which optimizes String.toCharArray() by deferring 
		// dissociation of the array from the String (sharing storage until
		// and unless the user attempts to modify the array), this may be 
		// significantly faster than the version which returns the selected 
		// characters into a user-supplied array. In others, it may be 
		// significantly slower. 

		if(_chars==null)
		{
			if(_string!=null)
				_chars=_string.toCharArray();
			else
				_chars=EMPTY;
		}
		return _chars;		
	}

	/** @param target A char[] to be copied into. If a buffer is not supplied
	 * we will create one.
	 *
	 * @param targetStart Offset in the target at which copying should begin.
	 * 
	 * @return the buffer, filled with {length} characters starting at offset
	 * {targetStart}. Characters before or after that block should be unaffected.
	 * */
	public char[] getChars(char[] target, int targetStart)
	{
		// See performance issues discussion above. But note that they
		// apply only to this version of the class, not necessarily to
		// subclasses such as the FSB version.
		if(target==null)
			target=new char[targetStart+_length];
			
		if(_chars==null)
			_string.getChars(_start,_start+_length,
				target,targetStart);
		else
			System.arraycopy(_chars,_start,target,targetStart,_length);
			
		return target;
	}
	
}

