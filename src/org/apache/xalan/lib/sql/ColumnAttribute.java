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
 * @see org.apache.xalan.lib.sql.ColumnHeader
 */
public class ColumnAttribute extends StreamableNode implements Attr
{

  /** Column owning this attribute          */
  Element m_owner;

  /** Meta data (column header)          */
  ResultSetMetaData m_metadata;

  /** Attribute name          */
  String m_name;

  /** Attribute type          */
  int m_type;

  /** Owner Column index          */
  int m_columnIndex;

  /** Column attribute         */
  ColumnAttribute m_columnAttr;

  /** Constant for ISTRUE          */
  static final String S_ISTRUE = "true";

  /** Constant for ISFALSE          */
  static final String S_ISFALSE = "false";

  /** Constant for CATALOGUE_NAME          */
  public static final int CATALOGUE_NAME = 0;

  /** Constnat for DISPLAY_SIZE          */
  public static final int DISPLAY_SIZE = 1;

  /** Constant for COLUMN_LABEL          */
  public static final int COLUMN_LABEL = 2;

  /** Constant for COLUMN_NAME          */
  public static final int COLUMN_NAME = 3;

  /** Constant for COLUMN_TYPE          */
  public static final int COLUMN_TYPE = 4;

  /** Constant for COLUMN_TYPENAME          */
  public static final int COLUMN_TYPENAME = 5;

  /** Constant for PRECISION          */
  public static final int PRECISION = 6;

  /** Constant for SCALE          */
  public static final int SCALE = 7;

  /** Constant for SCHEMA_NAME          */
  public static final int SCHEMA_NAME = 8;

  /** Constant for TABLE_NAME          */
  public static final int TABLE_NAME = 9;

  /** Constant for CASESENSITIVE          */
  public static final int CASESENSITIVE = 10;

  /** Constant for DEFINATELYWRITABLE          */
  public static final int DEFINATELYWRITABLE = 11;

  /** Constant for ISNULLABLE          */
  public static final int ISNULLABLE = 12;

  /** Constant for ISSIGNED          */
  public static final int ISSIGNED = 13;

  /** Constant for ISWRITEABLE          */
  public static final int ISWRITEABLE = 14;

  /** Constant for ISSEARCHABLE          */
  public static final int ISSEARCHABLE = 15;

  /** Constant for NUMBER_ATTRIBUTES          */
  public static final int NUMBER_ATTRIBUTES = 16;

  /** Constant for S_CATALOGUE_NAME          */
  public static final String S_CATALOGUE_NAME = "catalogue-name";

  /** Constant for S_DISPLAY_SIZE          */
  public static final String S_DISPLAY_SIZE = "column-display-size";

  /** Constant for S_COLUMN_LABEL          */
  public static final String S_COLUMN_LABEL = "column-label";

  /** Constant for S_COLUMN_NAME          */
  public static final String S_COLUMN_NAME = "column-name";

  /** Constant for S_COLUMN_TYPE          */
  public static final String S_COLUMN_TYPE = "column-type";

  /** Constant for S_COLUMN_TYPENAME          */
  public static final String S_COLUMN_TYPENAME = "column-type-name";

  /** Constant for S_PRECISION          */
  public static final String S_PRECISION = "precision";

  /** Constant for S_SCALE          */
  public static final String S_SCALE = "scale";

  /** Constant for S_SCHEMA_NAME          */
  public static final String S_SCHEMA_NAME = "schema-name";

  /** Constant for S_TABLE_NAME          */
  public static final String S_TABLE_NAME = "table-name";

  /** Constant for S_CASESENSITIVE          */
  public static final String S_CASESENSITIVE = "case-sensitive";

  /** Constant for S_DEFINATELYWRITABLE          */
  public static final String S_DEFINATELYWRITABLE = "definitely-writable";

  /** Constant for S_ISNULLABLE          */
  public static final String S_ISNULLABLE = "nullable";

  /** Constant for S_ISSIGNED          */
  public static final String S_ISSIGNED = "signed";

  /** Constant for S_ISWRITEABLE          */
  public static final String S_ISWRITEABLE = "writable";

  /** Constant for S_ISSEARCHABLE          */
  public static final String S_ISSEARCHABLE = "searchable";

  /** Table of column attribute names           */
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
   * @param statement Owning document
   * @param owner Column owning this attribute
   * @param columnIndex Owning column index
   * @param type attribute type
   * @param metadata Column header
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
   * Get column attribute name using constant value
   *
   *
   * @param pos Constant value of attribute
   *
   * @return Attribute name corresponding to the given value
   * or null if not found
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
   * Get attribute constant value from name
   *
   *
   * @param name Name of attribute
   *
   * @return Attribute value or -1 if not found
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
   * Set column name
   *
   *
   * @param name column attribute name
   *
   * @return True if the name is found in the lookup table
   */
  public boolean setName(String name)
  {

    m_name = name;

    int i = getAttrPosFromName(name);

    return (i >= 0);
  }

  /**
   * Get the column name
   *
   *
   * @return the column name
   */
  public String getNodeName()
  {
    return m_name;
  }

  /**
   * Get the column name
   *
   *
   * @return the column name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Return Specified
   *
   *
   * @return true
   */
  public boolean getSpecified()
  {
    return true;
  }

  /**
   * Return column value
   *
   *
   * @return column value
   */
  public String getNodeValue()
  {
    return getValue();
  }

  /**
   * Return column value
   *
   *
   * @return column value
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
    catch(Exception e)
    {
      // Catch all other execptions also
      // JCG 4/1/2001
      return "Attribute Not Supported";
    }
  }

  /**
   * setValue - Not supported
   *
   *
   * @param value column value to set
   *
   * @throws DOMException
   */
  public void setValue(String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Return column owner
   *
   *
   * @return Column owner
   */
  public Element getOwnerElement()
  {
    return m_owner;
  }

  /**
   * Get parent node
   *
   *
   * @return null
   */
  public Node getParentNode()
  {
    return null;
  }

  /**
   * Return Node.ATTRIBUTE_NODE.
   *
   * @return ATTRIBUTE_NODE type
   */
  public short getNodeType()
  {
    return Node.ATTRIBUTE_NODE;
  }
}
