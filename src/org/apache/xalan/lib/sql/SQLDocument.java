/**
 * @(#) SQLDocument.java
 *
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
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 * not be used to endorse or promote products derived from this
 * software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
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
 *
 */

package org.apache.xalan.lib.sql;

import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.NodeList;
import java.sql.ResultSet;
import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.apache.xml.dtm.ref.DTMDefaultBaseIterators;
import org.xml.sax.ext.*;
import org.xml.sax.*;
import org.apache.xml.utils.*;

import org.apache.xpath.XPathContext;
import org.apache.xalan.extensions.ExpressionContext;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.w3c.dom.Node;

/**
 * The SQL Document is the main controlling class the executesa SQL Query
 */
public class SQLDocument extends DTMDefaultBaseIterators
{
  private boolean DEBUG = true;
  /**
   *
   */
  private Connection  m_Connection = null;
  private Statement   m_Statement = null;
  private ResultSet   m_ResultSet = null;

  private static final String S_NAMESPACE = null;

  private static final String S_ATTRIB_NOT_SUPPORTED="Not Supported";
  private static final String S_ISTRUE="true";
  private static final String S_ISFALSE="false";

  private static final String S_COLUMN_HEADER = "column-header";
  private static final String S_ROW_SET = "row-set";
  private static final String S_ROW = "row";
  private static final String S_COL = "col";

  private static final String S_CATALOGUE_NAME = "catalogue-name";
  private static final String S_DISPLAY_SIZE = "column-display-size";
  private static final String S_COLUMN_LABEL = "column-label";
  private static final String S_COLUMN_NAME = "column-name";
  private static final String S_COLUMN_TYPE = "column-type";
  private static final String S_COLUMN_TYPENAME = "column-typename";
  private static final String S_PRECISION = "precision";
  private static final String S_SCALE = "scale";
  private static final String S_SCHEMA_NAME = "schema-name";
  private static final String S_TABLE_NAME = "table-name";
  private static final String S_CASESENSITIVE = "case-sensitive";
  private static final String S_DEFINITLEYWRITABLE = "definitley-writable";
  private static final String S_ISNULLABLE = "nullable";
  private static final String S_ISSIGNED = "signed";
  private static final String S_ISWRITEABLE = "writable";
  private static final String S_ISSEARCHABLE = "searchable";

  private int         m_ColumnHeader_TypeID = 0;
  private int         m_RowSet_TypeID = 0;
  private int         m_Row_TypeID = 0;
  private int         m_Col_TypeID = 0;

  private int         m_ColAttrib_CATALOGUE_NAME_TypeID = 0;
  private int         m_ColAttrib_DISPLAY_SIZE_TypeID = 0;
  private int         m_ColAttrib_COLUMN_LABEL_TypeID = 0;
  private int         m_ColAttrib_COLUMN_NAME_TypeID = 0;
  private int         m_ColAttrib_COLUMN_TYPE_TypeID = 0;
  private int         m_ColAttrib_COLUMN_TYPENAME_TypeID = 0;
  private int         m_ColAttrib_PRECISION_TypeID = 0;
  private int         m_ColAttrib_SCALE_TypeID = 0;
  private int         m_ColAttrib_SCHEMA_NAME_TypeID = 0;
  private int         m_ColAttrib_TABLE_NAME_TypeID = 0;
  private int         m_ColAttrib_CASESENSITIVE_TypeID = 0;
  private int         m_ColAttrib_DEFINITLEYWRITEABLE_TypeID = 0;
  private int         m_ColAttrib_ISNULLABLE_TypeID = 0;
  private int         m_ColAttrib_ISSIGNED_TypeID = 0;
  private int         m_ColAttrib_ISWRITEABLE_TypeID = 0;
  private int         m_ColAttrib_ISSEARCHABLE_TypeID = 0;

  /**
   * Store the SQL Data in this growable array
   */
  private ObjectArray m_ObjectArray = new ObjectArray();

  /**
   * As the column header array is built, keep the node index
   * for each Column.
   *
   * The primary use of this is to locate the first attribute for
   * each column in each row as we add records.
   */
  private int[]     m_ColHeadersIdx;

  /**
   * The index of the Row Set node. This is the sibling directly after
   * the last Column Header.
   */
  private int       m_RowSetIdx = DTM.NULL;

  /**
   * Demark the first row element where we started adding rows into the
   * Document.
   */
  private int     m_FirstRowIdx = DTM.NULL;

  /**
   * Keep track of the Last row inserted into the DTM from the ResultSet.
   * This will be used as the index of the parent Row Element when adding
   * a row.
   */
  private int     m_LastRowIdx = DTM.NULL;

  public SQLDocument(DTMManager mgr, int ident, Connection con, Statement stmt, ResultSet data)
    throws SQLException
  {
    super(mgr, null, ident,
      null, mgr.getXMLStringFactory(), true);

    // DTMManager mgr, Source source, int dtmIdentity,
    // DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory,
    // boolean doIndexing
    m_Connection = con;
    m_Statement  = stmt;
    m_ResultSet  = data;

    createExpandedNameTable();
    extractSQLMetaData(m_ResultSet.getMetaData());
  }


  /**
   * Extract the Meta Data and build the Column Attribute List.
   * @return
   */
  private void extractSQLMetaData(ResultSetMetaData meta)
  {
    int colCount = 0;
    // Build the Node Tree, just add the Column Header
    // branch now, the Row & col elements will be added
    // on request.

    // Add in the row-set Element
    m_RowSetIdx = addNode(null, 0, m_RowSet_TypeID, DTM.NULL, DTM.NULL);
    try
    {
      colCount = meta.getColumnCount();
      m_ColHeadersIdx = new int[colCount];
    }
    catch(Exception e)
    {
      error("ERROR Extracting Metadata");
    }

    // The idx will represent the previous sibling for all the
    // attribute nodes
    int idx;

    // The ColHeaderIdx will be used to keep track of the
    // Element entries for the individual Column Header.
    int lastColHeaderIdx = DTM.NULL;

    // JDBC Columms Start at 1
    for (int i=1; i<= colCount; i++)
    {
      idx = DTM.NULL;

      m_ColHeadersIdx[i-1] =
        addNode(
          null, 1,
          m_ColumnHeader_TypeID, m_RowSetIdx, lastColHeaderIdx);

      lastColHeaderIdx = m_ColHeadersIdx[i-1];
      // A bit brute force, but not sure how to clean it up

      try
      {
        idx = addNode(
          new Integer(meta.getColumnDisplaySize(i)), 1,
          m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          new Integer(meta.getColumnDisplaySize(i)), 1,
          m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.getColumnLabel(i), 1,
          m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.getColumnName(i), 1,
          m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          new Integer(meta.getColumnType(i)), 1,
          m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.getColumnTypeName(i), 1,
          m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          new Integer(meta.getPrecision(i)), 1,
          m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx, idx);
      }
      try
      {
        idx = addNode(
          new Integer(meta.getScale(i)), 1,
          m_ColAttrib_SCALE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_SCALE_TypeID, lastColHeaderIdx, idx);
      }
      try
      {
        idx = addNode(
          meta.getSchemaName(i), 1,
          m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx, idx);
      }
      try
      {
        idx = addNode(
          meta.getTableName(i), 1,
          m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx, idx);
      }
      try
      {
        idx = addNode(
          meta.isCaseSensitive(i) ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.isDefinitelyWritable(i) ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_DEFINITLEYWRITEABLE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_DEFINITLEYWRITEABLE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.isNullable(i) != 0 ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.isSigned(i) ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.isWritable(i) == true ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx, idx);
      }

      try
      {
        idx = addNode(
          meta.isSearchable(i) == true ? S_ISTRUE : S_ISFALSE, 1,
          m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx, idx);
      }
      catch(Exception e)
      {
        idx = addNode(
          S_ATTRIB_NOT_SUPPORTED, 1,
          m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx, idx);
      }
    }
  }

  /**
   * Populate the Expanded Name Table with the Node that we will use.
   * Keep a reference of each of the types for access speed.
   *
   */
  private void createExpandedNameTable()
  {

    m_ColumnHeader_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_HEADER, DTM.ELEMENT_NODE);
    m_RowSet_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ROW_SET, DTM.ELEMENT_NODE);
    m_Row_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ROW, DTM.ELEMENT_NODE);
    m_Col_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COL, DTM.ELEMENT_NODE);


    m_ColAttrib_CATALOGUE_NAME_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_CATALOGUE_NAME, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_DISPLAY_SIZE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_DISPLAY_SIZE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_COLUMN_LABEL_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_LABEL, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_COLUMN_NAME_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_NAME, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_COLUMN_TYPE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_TYPE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_COLUMN_TYPENAME_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_COLUMN_TYPENAME, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_PRECISION_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_PRECISION, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_SCALE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SCALE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_SCHEMA_NAME_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_SCHEMA_NAME, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_TABLE_NAME_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_TABLE_NAME, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_CASESENSITIVE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_CASESENSITIVE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_DEFINITLEYWRITEABLE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_DEFINITLEYWRITABLE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_ISNULLABLE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISNULLABLE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_ISSIGNED_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISSIGNED, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_ISWRITEABLE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISWRITEABLE, DTM.ATTRIBUTE_NODE);
    m_ColAttrib_ISSEARCHABLE_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_ISSEARCHABLE, DTM.ATTRIBUTE_NODE);
  }

  private int addNode(Object o, int level, int extendedType, int parent, int prevsib)
  {
    int node = DTM.NULL;

    // Need to keep this counter going even if we don't use it.
    m_size++;

    try
    {
      // Add the Node and adjust its Extended Type
      node = m_ObjectArray.append(o);
      m_exptype.setElementAt(extendedType, node);
      m_prevsib.setElementAt(prevsib, node);
      m_parent.setElementAt(parent, node);

      // Fixup the previous sibling
      // So if there was a previous sibling, chain into them.

      // As a precaution, always set the next sibling
      m_nextsib.setElementAt(DTM.NULL, node);

      if (prevsib != DTM.NULL)
      {
        // If the previous sibling is already assigned, then we are
        // inserting a value into the chain.
        if (m_nextsib.elementAt(prevsib) != DTM.NULL)
          m_nextsib.setElementAt(m_nextsib.elementAt(prevsib), node);

        // Tell the proevious sibling that they have a new bother/sister.
        m_nextsib.setElementAt(node, prevsib);
      }

      // Set this value even if we change it later
      m_parent.setElementAt(parent, node);

      // Fix up the Parent, Since we don't track f ththe last child, then
      // So if we have a valid parent and the new node ended up being first
      // in the list, i.e. no prevsib, then set the new node up as the
      // first child of the parent. Since we chained the node in the list,
      // there should be no reason to worry about the current first child
      // of the parent node.
      if ((parent != DTM.NULL) && (m_prevsib.elementAt(node) == DTM.NULL))
      {
        m_firstch.setElementAt(node, parent);
      }
    }
    catch(Exception e)
    {
      // Let's just return DTM.NULL now
      error("");
    }

    return node;
  }

  private boolean addRowToDTMFromResultSet()
  {
    try
    {
      if ( ! m_ResultSet.next()) return false;

      // If this is the first time here, start the new level
      if (m_FirstRowIdx == DTM.NULL)
      {
        m_FirstRowIdx = addNode(null,2, m_Row_TypeID, m_RowSetIdx, DTM.NULL);
        m_LastRowIdx = m_FirstRowIdx;
      }
      else
      {
        m_LastRowIdx = addNode(null,2, m_Row_TypeID, m_RowSetIdx, m_LastRowIdx);
      }
    }
    catch(Exception e)
    {
      error("SQL Error Fetching next row [" + e.getLocalizedMessage() + "]");
    }

    return false;
  }


  public void close()
  {
    if (DEBUG) System.out.println("close()");

    try
    {
      if (null != m_ResultSet) m_ResultSet.close();
    }
    catch(Exception e)
    {
      /* Empty */
    }

    try
    {
      if (null != m_Statement) m_Statement.close();
    }
    catch(Exception e)
    {
      /* Empty */
    }

    try
    {
      if (null != m_Connection) m_Connection.close();
    }
    catch(Exception e)
    {
      /* Empty */
    }
  }


  /**
   * @param parm1
   * @return
   */
  protected int getNextNodeIdentity( int parm1 )
  {
    if (DEBUG) System.out.println("getNextNodeIdenty(" + parm1 + ")");
    return DTM.NULL;
  }

  /**
   * @param parm1
   * @param parm2
   * @param parm3
   * @return
   */
  public int getAttributeNode( int parm1, String parm2, String parm3 )
  {
    if (DEBUG)
    {
      System.out.println(
        "getAttributeNode(" +
        parm1 + "," +
        parm2 + "," +
        parm3 + ")");
    }
    return DTM.NULL;
  }

  /**
   * @param parm1
   * @return
   */
  public String getLocalName( int parm1 )
  {
    int exID = this.getExpandedTypeID( parm1 & NODEIDENTITYBITS );

    if (DEBUG)
    {
      DEBUG = false;
      System.out.print("getLocalName(" + parm1 + ") -> ");
      System.out.println("..." + getLocalNameFromExpandedNameID(exID) );
      DEBUG = true;
    }

    return getLocalNameFromExpandedNameID(exID);
  }

  /**
   * @param parm1
   * @return
   */
  public String getNodeName( int parm1 )
  {
    int exID = this.getExpandedTypeID( parm1 & NODEIDENTITYBITS );
    if (DEBUG)
    {
      DEBUG = false;
      System.out.print("getLocalName(" + parm1 + ") -> ");
      System.out.println("..." + getLocalNameFromExpandedNameID(exID) );
      DEBUG = true;
    }
    return getLocalNameFromExpandedNameID(exID);
  }

  /**
   * @param parm1
   * @return
   */
  public int getElementById( String parm1 )
  {
    if (DEBUG) System.out.println("getElementByID("+parm1+")");
    return DTM.NULL;
  }

  /**
   * @return
   */
  public DeclHandler getDeclHandler( )
  {
    if (DEBUG) System.out.println("getDeclHandler()");
    return null;
  }

  /**
   * @return
   */
  public ErrorHandler getErrorHandler( )
  {
    if (DEBUG) System.out.println("getErrorHandler()");
    return null;
  }

  /**
   * @return
   */
  public String getDocumentTypeDeclarationSystemIdentifier( )
  {
    if (DEBUG) System.out.println("get_DTD-SID()");
    return null;
  }

  /**
   * @return
   */
  protected int getNumberOfNodes( )
  {
    if (DEBUG) System.out.println("getNumberOfNodes()");
    return 0;
  }


  /**
   * @param parm1
   * @return
   */
  public String getNodeValue( int parm1 )
  {
    if (DEBUG) System.out.println("getNodeValue(" + parm1 + ")");
    return "";
  }

  /**
   * @param parm1
   * @return
   */
  public boolean isAttributeSpecified( int parm1 )
  {
    if (DEBUG) System.out.println("isAttributeSpecified(" + parm1 + ")");
    return false;
  }

  /**
   * @param parm1
   * @return
   */
  public String getUnparsedEntityURI( String parm1 )
  {
    if (DEBUG) System.out.println("getUnparsedEntityURI(" + parm1 + ")");
    return "";
  }

  /**
   * @return
   */
  public DTDHandler getDTDHandler( )
  {
    if (DEBUG) System.out.println("getDTDHandler()");
    return null;
  }

  /**
   * @param parm1
   * @return
   */
  public String getPrefix( int parm1 )
  {
    if (DEBUG) System.out.println("getPrefix(" + parm1  + ")");
    return "";
  }

  /**
   * @return
   */
  public EntityResolver getEntityResolver( )
  {
    if (DEBUG) System.out.println("getEntityResolver()");
    return null;
  }

  /**
   * @return
   */
  public String getDocumentTypeDeclarationPublicIdentifier( )
  {
    if (DEBUG) System.out.println("get_DTD_PubId()");
    return "";
  }

  /**
   * @return
   */
  protected boolean nextNode( )
  {
    if (DEBUG) System.out.println("nextNode()");
    return addRowToDTMFromResultSet();
  }

  /**
   * @return
   */
  public LexicalHandler getLexicalHandler( )
  {
    if (DEBUG) System.out.println("getLexicalHandler()");
    return null;
  }

  /**
   * @param parm1
   * @return
   */
  public XMLString getStringValue( int parm1 )
  {
    if (DEBUG) System.out.println("getStringValue(" + parm1 + ")");
    return null;
  }

  /**
   * @return
   */
  public boolean needsTwoThreads( )
  {
    if (DEBUG) System.out.println("needsTwoThreads()");
    return false;
  }

  /**
   * @return
   */
  public ContentHandler getContentHandler( )
  {
    if (DEBUG) System.out.println("getContentHandler()");
    return null;
  }

  /**
   * @param parm1
   * @param parm2
   * @return
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents( int parm1, ContentHandler parm2 )
    throws org.xml.sax.SAXException
  {
    if (DEBUG)
    {
      System.out.println(
      "dispathcToEvents(" +
      parm1 + "," +
      parm2 + ")");
    }
    return;
  }

  /**
   * @param parm1
   * @return
   */
  public String getNamespaceURI( int parm1 )
  {
    if (DEBUG) System.out.println("getNamespaceURI(" +parm1+")");
    return "";
  }

  /**
   * @param parm1
   * @param parm2
   * @param parm3
   * @return
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents( int parm1, ContentHandler parm2, boolean parm3 )
    throws org.xml.sax.SAXException
  {
    if (DEBUG)
    {
      System.out.println("dispatchCharacterEvents(" +
      parm1 + "," +
      parm2 + "," +
      parm3 + ")");
    }

    return;
  }

  /**
   * Event overriding for Debug
   */

  public boolean supportsPreStripping()
  {
    if (DEBUG) System.out.println("supportsPreStripping()");
    return super.supportsPreStripping();
  }

  protected int _exptype(int parm1)
  {
    if (DEBUG) System.out.println("_exptype(" + parm1 + ")");
    return super._exptype( parm1);
  }

  protected SuballocatedIntVector findNamespaceContext(int parm1)
  {
    if (DEBUG) System.out.println("SuballocatedIntVector(" + parm1 + ")");
    return super.findNamespaceContext( parm1);
  }

  protected int _prevsib(int parm1)
  {
    if (DEBUG) System.out.println("_prevsib(" + parm1+ ")");
    return super._prevsib( parm1);
  }


  protected short _type(int parm1)
  {
    if (DEBUG) System.out.println("_type(" + parm1 + ")");
    return super._type( parm1);
  }

  public Node getNode(int parm1)
  {
    if (DEBUG) System.out.println("getNode(" + parm1 + ")");
    return super.getNode( parm1);
  }

  public int getPreviousSibling(int parm1)
  {
    if (DEBUG) System.out.println("getPrevSib(" + parm1 + ")");
    return super.getPreviousSibling( parm1);
  }

  public String getDocumentStandalone(int parm1)
  {
    if (DEBUG) System.out.println("getDOcStandAlone(" + parm1 + ")");
    return super.getDocumentStandalone( parm1);
  }

  public String getNodeNameX(int parm1)
  {
    if (DEBUG) System.out.println("getNodeNameX(" + parm1 + ")");
    return super.getNodeNameX( parm1);
  }

  public void setFeature(String parm1, boolean parm2)
  {
    if (DEBUG)
    {
      System.out.println(
        "setFeature(" +
        parm1 + "," +
        parm2 + ")");
    }
    super.setFeature( parm1,  parm2);
  }

  protected int _parent(int parm1)
  {
    if (DEBUG) System.out.println("_parent(" + parm1 + ")");
    return super._parent( parm1);
  }

  protected void indexNode(int parm1, int parm2)
  {
    if (DEBUG) System.out.println("indexNode("+parm1+","+parm2+")");
    super.indexNode( parm1,  parm2);
  }

  protected boolean getShouldStripWhitespace()
  {
    if (DEBUG) System.out.println("getShouldStripWS()");
    return super.getShouldStripWhitespace();
  }

  protected void popShouldStripWhitespace()
  {
    if (DEBUG) System.out.println("popShouldStripWS()");
    super.popShouldStripWhitespace();
  }

  public boolean isNodeAfter(int parm1, int parm2)
  {
    if (DEBUG) System.out.println("isNodeAfter(" + parm1 + "," + parm2 + ")");
    return super.isNodeAfter( parm1,  parm2);
  }

  public int getNamespaceType(int parm1)
  {
    if (DEBUG) System.out.println("getNamespaceType(" + parm1 + ")");
    return super.getNamespaceType( parm1);
  }

  protected int _level(int parm1)
  {
    if (DEBUG) System.out.println("_level(" + parm1 + ")");
    return super._level( parm1);
  }


  protected void pushShouldStripWhitespace(boolean parm1)
  {
    if (DEBUG) System.out.println("push_ShouldStripWS(" + parm1 + ")");
    super.pushShouldStripWhitespace( parm1);
  }

  public String getDocumentVersion(int parm1)
  {
    if (DEBUG) System.out.println("getDocVer("+parm1+")");
    return super.getDocumentVersion( parm1);
  }

  public boolean isSupported(String parm1, String parm2)
  {
    if (DEBUG) System.out.println("isSupported("+parm1+","+parm2+")");
    return super.isSupported( parm1,  parm2);
  }

  public void dumpDTM()
  {
    if (DEBUG) System.out.println("dumpDTM()");
    super.dumpDTM();
  }

  protected void setShouldStripWhitespace(boolean parm1)
  {
    if (DEBUG) System.out.println("set_ShouldStripWS("+parm1+")");
    super.setShouldStripWhitespace( parm1);
  }

  protected void ensureSizeOfIndex(int parm1, int parm2)
  {
    if (DEBUG) System.out.println("ensureSizeOfIndex("+parm1+","+parm2+")");
    super.ensureSizeOfIndex( parm1,  parm2);
  }

  protected void ensureSize(int parm1)
  {
    if (DEBUG) System.out.println("ensureSize("+parm1+")");
    super.ensureSize( parm1);
  }

  public String getDocumentEncoding(int parm1)
  {
    if (DEBUG) System.out.println("getDocumentEncoding("+parm1+")");
    return super.getDocumentEncoding( parm1);
  }

  public void appendChild(int parm1, boolean parm2, boolean parm3)
  {
    if (DEBUG)
    {
      System.out.println(
        "appendChild(" +
        parm1 + "," +
        parm2 + "," +
        parm3 + ")");
    }
    super.appendChild( parm1,  parm2,  parm3);
  }

  public short getLevel(int parm1)
  {
    if (DEBUG) System.out.println("getLevel("+parm1+")");
    return super.getLevel( parm1);
  }

  public String getDocumentBaseURI()
  {
    if (DEBUG) System.out.println("getDocBaseURI()");
    return super.getDocumentBaseURI();
  }

  public int getNextNamespaceNode(int parm1, int parm2, boolean parm3)
  {
    if (DEBUG)
    {
      System.out.println(
      "getNextNamesapceNode(" +
      parm1 + "," +
      parm2 + "," +
      parm3 + ")");
    }
    return super.getNextNamespaceNode( parm1,  parm2,  parm3);
  }

  public void appendTextChild(String parm1)
  {
    if (DEBUG) System.out.println("appendTextChild(" + parm1 + ")");
    super.appendTextChild( parm1);
  }

  protected int findGTE(int[] parm1, int parm2, int parm3, int parm4)
  {
    if (DEBUG)
    {
      System.out.println(
      "findGTE("+
      parm1 + "," +
      parm2 + "," +
      parm3 + ")");
    }
    return super.findGTE( parm1,  parm2,  parm3,  parm4);
  }

  public int getFirstNamespaceNode(int parm1, boolean parm2)
  {
    if (DEBUG) System.out.println("getFirstNamespaceNode()");
    return super.getFirstNamespaceNode( parm1,  parm2);
  }

  public int getStringValueChunkCount(int parm1)
  {
    if (DEBUG) System.out.println("getStringChunkCount(" + parm1 + ")");
    return super.getStringValueChunkCount( parm1);
  }

  public int getLastChild(int parm1)
  {
    if (DEBUG) System.out.println("getLastChild(" + parm1 + ")");
    return super.getLastChild( parm1);
  }

  public boolean hasChildNodes(int parm1)
  {
    if (DEBUG) System.out.println("hasChildNodes(" + parm1 + ")");
    return super.hasChildNodes( parm1);
  }

  public short getNodeType(int parm1)
  {
    if (DEBUG)
    {
      DEBUG=false;
      System.out.print("getNodeType(" + (parm1 & NODEIDENTITYBITS) + ") ");
      int exID = this.getExpandedTypeID( parm1 & NODEIDENTITYBITS );
      String name = getLocalNameFromExpandedNameID(exID);
      System.out.println(
        ".. Node name [" + name + "]" +
        "[" + getNodeType( parm1) + "]");

      DEBUG=true;
    }

    return super.getNodeType( parm1);
  }

  public int getNextAttribute(int parm1)
  {
    if (DEBUG) System.out.println("getNextAttribute(" + parm1 + ")");
    return super.getNextAttribute( parm1);
  }

  public boolean isCharacterElementContentWhitespace(int parm1)
  {
    if (DEBUG) System.out.println("isCharacterElementContentWhitespace(" + parm1 +")");
    return super.isCharacterElementContentWhitespace( parm1);
  }

  public int getFirstChild(int parm1)
  {
    if (DEBUG) System.out.println("getFirstChild(" + parm1 + ")");
    return super.getFirstChild( parm1);
  }

  public String getDocumentSystemIdentifier(int parm1)
  {
    if (DEBUG) System.out.println("getDocSysID(" + parm1 + ")");
    return super.getDocumentSystemIdentifier( parm1);
  }

  protected void declareNamespaceInContext(int parm1, int parm2)
  {
    if (DEBUG) System.out.println("declareNamespaceContext("+parm1+","+parm2+")");
    super.declareNamespaceInContext( parm1,  parm2);
  }

  public String getNamespaceFromExpandedNameID(int parm1)
  {
    if (DEBUG)
    {
      DEBUG = false;
      System.out.print("getNamespaceFromExpandedNameID("+parm1+")");
      System.out.println("..." + super.getNamespaceFromExpandedNameID( parm1) );
      DEBUG = true;
    }
    return super.getNamespaceFromExpandedNameID( parm1);
  }

  public String getLocalNameFromExpandedNameID(int parm1)
  {
    if (DEBUG)
    {
      DEBUG = false;
      System.out.print("getLocalNameFromExpandedNameID("+parm1+")");
      System.out.println("..." + super.getLocalNameFromExpandedNameID( parm1));
      DEBUG = true;
    }
    return super.getLocalNameFromExpandedNameID( parm1);
  }

  public int getExpandedTypeID(int parm1)
  {
    if (DEBUG) System.out.println("getExpandedTypeID("+parm1+")");
    return super.getExpandedTypeID( parm1);
  }

  public int getDocument()
  {
    if (DEBUG) System.out.println("getDocument()");
    return super.getDocument();
  }


  protected int findInSortedSuballocatedIntVector(SuballocatedIntVector parm1, int parm2)
  {
    if (DEBUG)
    {
      System.out.println(
      "findInSortedSubAlloctedVector(" +
      parm1 + "," +
      parm2 + ")");
    }
    return super.findInSortedSuballocatedIntVector( parm1,  parm2);
  }

  public boolean isDocumentAllDeclarationsProcessed(int parm1)
  {
    if (DEBUG) System.out.println("isDocumentAllDeclProc("+parm1+")");
    return super.isDocumentAllDeclarationsProcessed( parm1);
  }

  protected void error(String parm1)
  {
    if (DEBUG) System.out.println("error("+parm1+")");
    super.error( parm1);
  }

  public int getFirstAttribute(int parm1)
  {
    if (DEBUG) System.out.println("getFirstAttribute("+parm1+")");
    return super.getFirstAttribute( parm1);
  }

  protected int _firstch(int parm1)
  {
    if (DEBUG) System.out.println("_firstch("+parm1+")");
    return super._firstch( parm1);
  }

  public int getOwnerDocument(int parm1)
  {
    if (DEBUG) System.out.println("getOwnerDoc("+parm1+")");
    return super.getOwnerDocument( parm1);
  }

  protected int _nextsib(int parm1)
  {
    if (DEBUG) System.out.println("_nextSib("+parm1+")");
    return super._nextsib( parm1);
  }

  public int getNextSibling(int parm1)
  {
    if (DEBUG) System.out.println("getNextSibling("+parm1+")");
    return super.getNextSibling( parm1);
  }


  public boolean getDocumentAllDeclarationsProcessed()
  {
    if (DEBUG) System.out.println("getDocAllDeclProc()");
    return super.getDocumentAllDeclarationsProcessed();
  }

  public int getParent(int parm1)
  {
    if (DEBUG) System.out.println("getParent("+parm1+")");
    return super.getParent( parm1);
  }

  public int getExpandedTypeID(String parm1, String parm2, int parm3)
  {
    if (DEBUG) System.out.println("getExpandedTypeID()");
    return super.getExpandedTypeID( parm1,  parm2,  parm3);
  }

  public void setDocumentBaseURI(String parm1)
  {
    if (DEBUG) System.out.println("setDocBaseURI()");
    super.setDocumentBaseURI( parm1);
  }

  public char[] getStringValueChunk(int parm1, int parm2, int[] parm3)
  {
    if (DEBUG)
    {
      System.out.println("getStringChunkValue(" +
      parm1 + "," +
      parm2 + ")");
    }
    return super.getStringValueChunk( parm1,  parm2,  parm3);
  }

  public DTMAxisTraverser getAxisTraverser(int parm1)
  {
    if (DEBUG) System.out.println("getAxixTraverser("+parm1+")");
    return super.getAxisTraverser( parm1);
  }

  public DTMAxisIterator getTypedAxisIterator(int parm1, int parm2)
  {
    if (DEBUG) System.out.println("getTypedAxisIterator("+parm1+","+parm2+")");
    return super.getTypedAxisIterator( parm1,  parm2);
  }

  public DTMAxisIterator getAxisIterator(int parm1)
  {
    if (DEBUG) System.out.println("getAxisIterator("+parm1+")");
    return super.getAxisIterator( parm1);
  }
  
  /**
   * For the moment all the run time properties are ignored by this
   * class.
   *
   * @param property a <code>String</code> value
   * @param value an <code>Object</code> value
   */
  public void setProperty(String property, Object value)
  {
  }
  
  /**
   * No source information is available for DOM2DTM, so return
   * <code>null</code> here.
   *
   * @param node an <code>int</code> value
   * @return null
   */
  public javax.xml.transform.SourceLocator getSourceLocatorFor(int node)
  {
    return null;
  }


}
