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
package org.apache.xml.utils;

import java.util.Hashtable;

/**
 * <meta name="usage" content="internal"/>
 * This class is in support of FormatterToHTML, and acts as a sort
 * of element representative for HTML elements.
 */
class ElemDesc
{

  /** Element's flags (See below for posible values          */
  int m_flags;

  /** Table of attributes for the element          */
  Hashtable m_attrs = null;

  /** EMPTY flag          */
  static final int EMPTY = (1 << 1);

  /** FLOW flag          */
  static final int FLOW = (1 << 2);

  /** BLOCK flag          */
  static final int BLOCK = (1 << 3);

  /** BLOCKFORM  flag         */
  static final int BLOCKFORM = (1 << 4);

  /** BLOCKFORMFIELDSET flag          */
  static final int BLOCKFORMFIELDSET = (1 << 5);

  /** CDATA flag         */
  static final int CDATA = (1 << 6);

  /** PCDATA flag          */
  static final int PCDATA = (1 << 7);

  /** RAW flag         */
  static final int RAW = (1 << 8);

  /** INLINE flag          */
  static final int INLINE = (1 << 9);

  /** INLINEA flag          */
  static final int INLINEA = (1 << 10);

  /** INLINELABEL flag          */
  static final int INLINELABEL = (1 << 11);

  /** FONTSTYLE flag          */
  static final int FONTSTYLE = (1 << 12);

  /** PHRASE flag          */
  static final int PHRASE = (1 << 13);

  /** FORMCTRL flag         */
  static final int FORMCTRL = (1 << 14);

  /** SPECIAL flag         */
  static final int SPECIAL = (1 << 15);

  /** ASPECIAL flag         */
  static final int ASPECIAL = (1 << 16);

  /** HEADMISC flag         */
  static final int HEADMISC = (1 << 17);

  /** HEAD flag         */
  static final int HEAD = (1 << 18);

  /** LIST flag         */
  static final int LIST = (1 << 19);

  /** PREFORMATTED flag         */
  static final int PREFORMATTED = (1 << 20);

  /** WHITESPACESENSITIVE flag         */
  static final int WHITESPACESENSITIVE = (1 << 21);

  /** ATTRURL flag         */
  static final int ATTRURL = (1 << 1);

  /** ATTREMPTY flag         */
  static final int ATTREMPTY = (1 << 2);

  /**
   * Constructor ElemDesc
   *
   *
   * @param flags Element flags
   */
  ElemDesc(int flags)
  {
    m_flags = flags;
  }

  /**
   * NEEDSDOC Method is 
   *
   *
   * @param flags flags to compare this element to
   *
   * @return true if these flags match the element's
   */
  boolean is(int flags)
  {

    // int which = (m_flags & flags);
    return (m_flags & flags) != 0;
  }

  /**
   * Set a new attribute for this element 
   *
   *
   * @param name Attribute name
   * @param flags Attibute flags
   */
  void setAttr(String name, int flags)
  {

    if (null == m_attrs)
      m_attrs = new Hashtable();

    m_attrs.put(name, new Integer(flags));
  }

  /**
   * Find out if a flag is set in a given attribute of this element 
   *
   *
   * @param name Attribute name
   * @param flags Flag to check
   *
   * @return True if the flag is set in the attribute. Returns false
   * if the attribute is not found 
   */
  boolean isAttrFlagSet(String name, int flags)
  {

    if (null != m_attrs)
    {
      Integer _flags = (Integer) m_attrs.get(name);

      if (null != _flags)
      {
        return (_flags.intValue() & flags) != 0;
      }
    }

    return false;
  }
}
