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
package org.apache.xalan.lib.sql;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * Represents a column attribute on a column-header element.
 * Each column-header element can contain any of the following
 * attributes, depending on the ResultSetMetadata object returned with
 * the query result set.
 * <ul>
 *   <li>column-label</li>
 *   <li>column-name</li>
 *   <li>column-display-size</li>
 *   <li>column-type</li>
 *   <li>column-type-name</li>
 *   <li>precision</li>
 *   <li>scale</li>
 *   <li>catalogue-name</li>
 *   <li>schema-name</li>
 *   <li>table-name</li>
 *   <li>case-sensitive</li>
 *   <li>definitely-writable</li>
 *   <li>nullable</li>
 *   <li>signed</li>
 *   <li>writable</li>
 *   <li>searchable</li>
 * </ul>
 * @see org.apache.xalan.lib.sql.ColumnHeader.
 */
public class ColumnAttribute extends StreamableNode implements Attr
{

  /** NEEDSDOC Field m_owner          */
  Element m_owner;

  /** NEEDSDOC Field m_metadata          */
  ResultSetMetaData m_metadata;

  /** NEEDSDOC Field m_name          */
  String m_name;

  /** NEEDSDOC Field m_type          */
  int m_type;

  /** NEEDSDOC Field m_columnIndex          */
  int m_columnIndex;

  /** NEEDSDOC Field m_columnAttr          */
  ColumnAttribute m_columnAttr;

  /** NEEDSDOC Field S_ISTRUE          */
  static final String S_ISTRUE = "true";

  /** NEEDSDOC Field S_ISFALSE          */
  static final String S_ISFALSE = "false";

  /** NEEDSDOC Field CATALOGUE_NAME          */
  public static final int CATALOGUE_NAME = 0;

  /** NEEDSDOC Field DISPLAY_SIZE          */
  public static final int DISPLAY_SIZE = 1;

  /** NEEDSDOC Field COLUMN_LABEL          */
  public static final int COLUMN_LABEL = 2;

  /** NEEDSDOC Field COLUMN_NAME          */
  public static final int COLUMN_NAME = 3;

  /** NEEDSDOC Field COLUMN_TYPE          */
  public static final int COLUMN_TYPE = 4;

  /** NEEDSDOC Field COLUMN_TYPENAME          */
  public static final int COLUMN_TYPENAME = 5;

  /** NEEDSDOC Field PRECISION          */
  public static final int PRECISION = 6;

  /** NEEDSDOC Field SCALE          */
  public static final int SCALE = 7;

  /** NEEDSDOC Field SCHEMA_NAME          */
  public static final int SCHEMA_NAME = 8;

  /** NEEDSDOC Field TABLE_NAME          */
  public static final int TABLE_NAME = 9;

  /** NEEDSDOC Field CASESENSITIVE          */
  public static final int CASESENSITIVE = 10;

  /** NEEDSDOC Field DEFINATELYWRITABLE          */
  public static final int DEFINATELYWRITABLE = 11;

  /** NEEDSDOC Field ISNULLABLE          */
  public static final int ISNULLABLE = 12;

  /** NEEDSDOC Field ISSIGNED          */
  public static final int ISSIGNED = 13;

  /** NEEDSDOC Field ISWRITEABLE          */
  public static final int ISWRITEABLE = 14;

  /** NEEDSDOC Field ISSEARCHABLE          */
  public static final int ISSEARCHABLE = 15;

  /** NEEDSDOC Field NUMBER_ATTRIBUTES          */
  public static final int NUMBER_ATTRIBUTES = 16;

  /** NEEDSDOC Field S_CATALOGUE_NAME          */
  public static final String S_CATALOGUE_NAME = "catalogue-name";

  /** NEEDSDOC Field S_DISPLAY_SIZE          */
  public static final String S_DISPLAY_SIZE = "column-display-size";

  /** NEEDSDOC Field S_COLUMN_LABEL          */
  public static final String S_COLUMN_LABEL = "column-label";

  /** NEEDSDOC Field S_COLUMN_NAME          */
  public static final String S_COLUMN_NAME = "column-name";

  /** NEEDSDOC Field S_COLUMN_TYPE          */
  public static final String S_COLUMN_TYPE = "column-type";

  /** NEEDSDOC Field S_COLUMN_TYPENAME          */
  public static final String S_COLUMN_TYPENAME = "column-type-name";

  /** NEEDSDOC Field S_PRECISION          */
  public static final String S_PRECISION = "precision";

  /** NEEDSDOC Field S_SCALE          */
  public static final String S_SCALE = "scale";

  /** NEEDSDOC Field S_SCHEMA_NAME          */
  public static final String S_SCHEMA_NAME = "schema-name";

  /** NEEDSDOC Field S_TABLE_NAME          */
  public static final String S_TABLE_NAME = "table-name";

  /** NEEDSDOC Field S_CASESENSITIVE          */
  public static final String S_CASESENSITIVE = "case-sensitive";

  /** NEEDSDOC Field S_DEFINATELYWRITABLE          */
  public static final String S_DEFINATELYWRITABLE = "definitely-writable";

  /** NEEDSDOC Field S_ISNULLABLE          */
  public static final String S_ISNULLABLE = "nullable";

  /** NEEDSDOC Field S_ISSIGNED          */
  public static final String S_ISSIGNED = "signed";

  /** NEEDSDOC Field S_ISWRITEABLE          */
  public static final String S_ISWRITEABLE = "writable";

  /** NEEDSDOC Field S_ISSEARCHABLE          */
  public static final String S_ISSEARCHABLE = "searchable";

  /** NEEDSDOC Field m_namelookup          */
  static java.util.Hashtable m_namelookup = new java.util.Hashtable();

  static
  {
    m_namelookup.put(S_CATALOGUE_NAME, new Integer(CATALOGUE_NAME));
    m_namelookup.put(S_DISPLAY_SIZE, new Integer(DISPLAY_SIZE));
    m_namelookup.put(S_COLUMN_LABEL, new Integer(COLUMN_LABEL));
    m_namelookup.put(S_COLUMN_NAME, new Integer(COLUMN_NAME));
    m_namelookup.put(S_COLUMN_TYPE, new Integer(COLUMN_TYPE));
    m_namelookup.put(S_COLUMN_TYPENAME, new Integer(COLUMN_TYPENAME));
    m_namelookup.put(S_PRECISION, new Integer(PRECISION));
    m_namelookup.put(S_SCALE, new Integer(SCALE));
    m_namelookup.put(S_SCHEMA_NAME, new Integer(SCHEMA_NAME));
    m_namelookup.put(S_TABLE_NAME, new Integer(TABLE_NAME));
    m_namelookup.put(S_CASESENSITIVE, new Integer(CASESENSITIVE));
    m_namelookup.put(S_DEFINATELYWRITABLE, new Integer(DEFINATELYWRITABLE));
    m_namelookup.put(S_ISNULLABLE, new Integer(ISNULLABLE));
    m_namelookup.put(S_ISSIGNED, new Integer(ISSIGNED));
    m_namelookup.put(S_ISWRITEABLE, new Integer(ISWRITEABLE));
    m_namelookup.put(S_ISSEARCHABLE, new Integer(ISSEARCHABLE));
  }

  /**
   * Constructor ColumnAttribute
   *
   *
   * NEEDSDOC @param statement
   * NEEDSDOC @param owner
   * NEEDSDOC @param columnIndex
   * NEEDSDOC @param type
   * NEEDSDOC @param metadata
   */
  public ColumnAttribute(XStatement statement, Element owner,
                         int columnIndex, int type,
                         ResultSetMetaData metadata)
  {

    super(statement);

    m_owner = owner;
    m_metadata = metadata;
    m_columnIndex = columnIndex;
    m_type = type;
  }

  /**
   * NEEDSDOC Method getAttrNameFromPos 
   *
   *
   * NEEDSDOC @param pos
   *
   * NEEDSDOC (getAttrNameFromPos) @return
   */
  static String getAttrNameFromPos(int pos)
  {

    switch (pos)
    {
    case CATALOGUE_NAME :
      return S_CATALOGUE_NAME;
    case DISPLAY_SIZE :
      return S_DISPLAY_SIZE;
    case COLUMN_LABEL :
      return S_COLUMN_LABEL;
    case COLUMN_NAME :
      return S_COLUMN_NAME;
    case COLUMN_TYPE :
      return S_COLUMN_TYPE;
    case COLUMN_TYPENAME :
      return S_COLUMN_TYPENAME;
    case PRECISION :
      return S_PRECISION;
    case SCALE :
      return S_SCALE;
    case SCHEMA_NAME :
      return S_SCHEMA_NAME;
    case TABLE_NAME :
      return S_TABLE_NAME;
    case CASESENSITIVE :
      return S_CASESENSITIVE;
    case DEFINATELYWRITABLE :
      return S_DEFINATELYWRITABLE;
    case ISNULLABLE :
      return S_ISNULLABLE;
    case ISSIGNED :
      return S_ISSIGNED;
    case ISWRITEABLE :
      return S_ISWRITEABLE;
    case ISSEARCHABLE :
      return S_ISSEARCHABLE;
    default :
      return null;
    }
  }

  /**
   * NEEDSDOC Method getAttrPosFromName 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (getAttrPosFromName) @return
   */
  static int getAttrPosFromName(String name)
  {

    Integer intObj = (Integer) m_namelookup.get(name);

    if (null != intObj)
    {
      return intObj.intValue();
    }
    else
      return -1;
  }

  /**
   * NEEDSDOC Method setName 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (setName) @return
   */
  public boolean setName(String name)
  {

    m_name = name;

    int i = getAttrPosFromName(name);

    return (i >= 0);
  }

  /**
   * NEEDSDOC Method getNodeName 
   *
   *
   * NEEDSDOC (getNodeName) @return
   */
  public String getNodeName()
  {
    return m_name;
  }

  /**
   * NEEDSDOC Method getName 
   *
   *
   * NEEDSDOC (getName) @return
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * NEEDSDOC Method getSpecified 
   *
   *
   * NEEDSDOC (getSpecified) @return
   */
  public boolean getSpecified()
  {
    return true;
  }

  /**
   * NEEDSDOC Method getNodeValue 
   *
   *
   * NEEDSDOC (getNodeValue) @return
   */
  public String getNodeValue()
  {
    return getValue();
  }

  /**
   * NEEDSDOC Method getValue 
   *
   *
   * NEEDSDOC (getValue) @return
   */
  public String getValue()
  {

    int i = m_columnIndex + 1;  // sql counts by 1

    try
    {

      // System.out.println("m_type: "+m_type);
      switch (m_type)
      {
      case CATALOGUE_NAME :
        return m_metadata.getCatalogName(i);
      case DISPLAY_SIZE :
        return Integer.toString(m_metadata.getColumnDisplaySize(i));
      case COLUMN_LABEL :
        return m_metadata.getColumnLabel(i);
      case COLUMN_NAME :
        return m_metadata.getColumnName(i);
      case COLUMN_TYPE :
        return Integer.toString(m_metadata.getColumnType(i));
      case COLUMN_TYPENAME :
        return m_metadata.getColumnTypeName(i);
      case PRECISION :
        return Integer.toString(m_metadata.getPrecision(i));
      case SCALE :
        return Integer.toString(m_metadata.getScale(i));
      case SCHEMA_NAME :
        return m_metadata.getSchemaName(i);
      case TABLE_NAME :
        return m_metadata.getTableName(i);
      case CASESENSITIVE :
        return m_metadata.isCaseSensitive(i) ? S_ISTRUE : S_ISFALSE;
      case DEFINATELYWRITABLE :
        return m_metadata.isDefinitelyWritable(i) ? S_ISTRUE : S_ISFALSE;
      case ISNULLABLE :
        return Integer.toString(m_metadata.isNullable(i));
      case ISSIGNED :
        return m_metadata.isSigned(i) ? S_ISTRUE : S_ISFALSE;
      case ISWRITEABLE :
        return m_metadata.isWritable(i) ? S_ISTRUE : S_ISFALSE;
      case ISSEARCHABLE :
        return m_metadata.isSearchable(i) ? S_ISTRUE : S_ISFALSE;
      default :
        return "";
      }
    }
    catch (SQLException sqle)
    {
      return "SQL ERROR!";
    }
  }

  /**
   * NEEDSDOC Method setValue 
   *
   *
   * NEEDSDOC @param value
   *
   * @throws DOMException
   */
  public void setValue(String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * NEEDSDOC Method getOwnerElement 
   *
   *
   * NEEDSDOC (getOwnerElement) @return
   */
  public Element getOwnerElement()
  {
    return m_owner;
  }

  /**
   * NEEDSDOC Method getParentNode 
   *
   *
   * NEEDSDOC (getParentNode) @return
   */
  public Node getParentNode()
  {
    return null;
  }

  /**
   * Return Node.ATTRIBUTE_NODE.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.ATTRIBUTE_NODE;
  }
}
