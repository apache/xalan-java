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
  Element m_owner;
  ResultSetMetaData m_metadata;
  String m_name;
  int m_type;
  int m_columnIndex;
  ColumnAttribute m_columnAttr;
  static final String S_ISTRUE = "true";
  static final String S_ISFALSE = "false";
  
  public static final int CATALOGUE_NAME = 0;
  public static final int DISPLAY_SIZE = 1;
  public static final int COLUMN_LABEL = 2;
  public static final int COLUMN_NAME = 3;
  public static final int COLUMN_TYPE = 4;
  public static final int COLUMN_TYPENAME = 5;
  public static final int PRECISION = 6;
  public static final int SCALE = 7;
  public static final int SCHEMA_NAME = 8;
  public static final int TABLE_NAME = 9;
  public static final int CASESENSITIVE = 10;
  public static final int DEFINATELYWRITABLE = 11;
  public static final int ISNULLABLE = 12;
  public static final int ISSIGNED = 13;
  public static final int ISWRITEABLE = 14;
  public static final int ISSEARCHABLE = 15;
  
  public static final int NUMBER_ATTRIBUTES = 16;

  public static final String S_CATALOGUE_NAME = "catalogue-name";
  public static final String S_DISPLAY_SIZE = "column-display-size";
  public static final String S_COLUMN_LABEL = "column-label";
  public static final String S_COLUMN_NAME = "column-name";
  public static final String S_COLUMN_TYPE = "column-type";
  public static final String S_COLUMN_TYPENAME = "column-type-name";
  public static final String S_PRECISION = "precision";
  public static final String S_SCALE = "scale";
  public static final String S_SCHEMA_NAME = "schema-name";
  public static final String S_TABLE_NAME = "table-name";
  public static final String S_CASESENSITIVE = "case-sensitive";
  public static final String S_DEFINATELYWRITABLE = "definitely-writable";
  public static final String S_ISNULLABLE = "nullable";
  public static final String S_ISSIGNED = "signed";
  public static final String S_ISWRITEABLE = "writable";
  public static final String S_ISSEARCHABLE = "searchable";
  
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

  public ColumnAttribute(XStatement statement, Element owner,
                         int columnIndex, int type, ResultSetMetaData metadata)
  {
    super(statement);
    m_owner = owner;
    m_metadata = metadata;
    m_columnIndex = columnIndex;
    m_type = type;
  }
  
  static String getAttrNameFromPos(int pos)
  {
    switch(pos)
    {
    case  CATALOGUE_NAME:
      return S_CATALOGUE_NAME;
    case  DISPLAY_SIZE:
      return S_DISPLAY_SIZE;
    case  COLUMN_LABEL:
      return S_COLUMN_LABEL;
    case  COLUMN_NAME:
      return S_COLUMN_NAME;
    case  COLUMN_TYPE:
      return S_COLUMN_TYPE;
    case  COLUMN_TYPENAME:
      return S_COLUMN_TYPENAME;
    case  PRECISION:
      return S_PRECISION;
    case  SCALE:
      return S_SCALE;
    case  SCHEMA_NAME:
      return S_SCHEMA_NAME;
    case  TABLE_NAME:
      return S_TABLE_NAME;
    case  CASESENSITIVE:
      return S_CASESENSITIVE;
    case  DEFINATELYWRITABLE:
      return S_DEFINATELYWRITABLE;
    case  ISNULLABLE:
      return S_ISNULLABLE;
    case  ISSIGNED:
      return S_ISSIGNED;
    case  ISWRITEABLE:
      return S_ISWRITEABLE;
    case  ISSEARCHABLE:
      return S_ISSEARCHABLE;
    default:
      return null;
    }
  }

  
  static int getAttrPosFromName(String name)
  {
    Integer intObj = (Integer)m_namelookup.get(name);
    if(null != intObj)
    {
      return intObj.intValue();
    }
    else
      return -1;
  }
  
  public boolean setName(String name)
  {
    m_name = name;
    int i = getAttrPosFromName(name);
    return (i >= 0);
  }
  
  public String getNodeName()
  {
    return m_name;
  }

  public String getName() 
  {
    return m_name;
  }

  public boolean getSpecified()
  {
    return true;
  }
  
  public String getNodeValue()
  {
    return getValue();
  }

  public String getValue()
  {
    int i = m_columnIndex+1; // sql counts by 1
    try
    {
      // System.out.println("m_type: "+m_type);
      switch(m_type)
      {
      case  CATALOGUE_NAME:
        return m_metadata.getCatalogName(i);
      case  DISPLAY_SIZE:
        return Integer.toString(m_metadata.getColumnDisplaySize(i));
      case  COLUMN_LABEL:
        return m_metadata.getColumnLabel(i);
      case  COLUMN_NAME:
        return m_metadata.getColumnName(i);
      case  COLUMN_TYPE:
        return Integer.toString(m_metadata.getColumnType(i));
      case  COLUMN_TYPENAME:
        return m_metadata.getColumnTypeName(i);
      case  PRECISION:
        return Integer.toString(m_metadata.getPrecision(i));
      case  SCALE:
        return Integer.toString(m_metadata.getScale(i));
      case  SCHEMA_NAME:
        return m_metadata.getSchemaName(i);
      case  TABLE_NAME:
        return m_metadata.getTableName(i);
      case  CASESENSITIVE:
        return m_metadata.isCaseSensitive(i) ? S_ISTRUE : S_ISFALSE;
      case  DEFINATELYWRITABLE:
        return m_metadata.isDefinitelyWritable(i) ? S_ISTRUE : S_ISFALSE;
      case  ISNULLABLE:
        return Integer.toString(m_metadata.isNullable(i));
      case  ISSIGNED:
        return m_metadata.isSigned(i) ? S_ISTRUE : S_ISFALSE;
      case  ISWRITEABLE:
        return m_metadata.isWritable(i) ? S_ISTRUE : S_ISFALSE;
      case  ISSEARCHABLE:
        return m_metadata.isSearchable(i) ? S_ISTRUE : S_ISFALSE;
      default:
        return "";
      }
    }
    catch(SQLException sqle)
    {
      return "SQL ERROR!";
    }
  }
  
  public void setValue(String value)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  public Element getOwnerElement()
  {
    return m_owner;
  }

  public Node getParentNode()
  {
    return null;
  }
  
  /**
   * Return Node.ATTRIBUTE_NODE.
   */
  public short getNodeType()
  {
    return Node.ATTRIBUTE_NODE;
  }


}
