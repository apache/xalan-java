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

import java.io.IOException;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;

/**
 * The SQL Document is the main controlling class the executesa SQL Query
 */
public class SQLDocument extends DTMDefaultBaseIterators
{
  private boolean DEBUG = false;

  private static final String S_NAMESPACE = null;

  private static final String S_ATTRIB_NOT_SUPPORTED="Not Supported";
  private static final String S_ISTRUE="true";
  private static final String S_ISFALSE="false";

  private static final String S_DOCUMENT = "#root";
  private static final String S_TEXT_NODE = "#text";
  private static final String S_ELEMENT_NODE   = "#element";

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

  private int         m_Document_TypeID = 0;
  private int         m_TextNode_TypeID = 0;

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
   * The DBMS Connection used to produce this SQL Document.
   * Will be used to clear free up the database resources on
   * close.
   */
  private Connection  m_Connection = null;

  /**
   * The Statement used to extract the data from the Database connection.
   * We really don't need the connection, but it is NOT defined from
   * JDBC Driver to driver what happens to the ResultSet if the statment
   * is closed prior to reading all the data needed. So as long as we are
   * using the ResultSet, we will track the Statement used to produce it.
   */
  private Statement   m_Statement = null;

  /**
   * The conduit to our data that will be used to fill the document.
   */
  private ResultSet   m_ResultSet = null;

  /**
   * Store the SQL Data in this growable array
   */
  private ObjectArray m_ObjectArray = new ObjectArray();

  /**
   * For each element node, there can be zero or more attributes. If Attributes
   * are assigned, the first attribute for that element will be use here.
   * Subsequent elements will use the m_nextsib, m_prevsib array. The sibling
   * arrays are not meeant to hold indexes to attribute information but as
   * long as there is not direct connection back into the main DTM tree
   * we should be OK.
   */
  protected SuballocatedIntVector m_attribute;

  /**
   * The Document Index will most likely be 0, but we will reference it
   * by variable in case that paradigm falls through.
   */
  private int       m_DocumentIdx;

  /**
   * As the column header array is built, keep the node index
   * for each Column.
   *
   * The primary use of this is to locate the first attribute for
   * each column in each row as we add records.
   */
  private int[]     m_ColHeadersIdx;

  /**
   * An indicator on how many columns are in this query
   */
  private int       m_ColCount;
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

  public SQLDocument(
    DTMManager mgr, int ident,
    Connection con, Statement stmt, ResultSet data)
    throws SQLException
  {
    super(mgr, null, ident,
      null, mgr.getXMLStringFactory(), true);

    m_Connection = con;
    m_Statement  = stmt;
    m_ResultSet  = data;

    m_attribute = new SuballocatedIntVector(m_initialblocksize);

    createExpandedNameTable();
    extractSQLMetaData(m_ResultSet.getMetaData());

    // Let's see what we have
    dumpDTM();
  }


  /**
   * Extract the Meta Data and build the Column Attribute List.
   * @return
   */
  private void extractSQLMetaData(ResultSetMetaData meta)
  {
    // Build the Node Tree, just add the Column Header
    // branch now, the Row & col elements will be added
    // on request.

    // Start the document here
    m_DocumentIdx = addElement(0, m_Document_TypeID, DTM.NULL, DTM.NULL);
    // Add in the row-set Element
    m_RowSetIdx = addElement(1, m_RowSet_TypeID,  m_DocumentIdx, DTM.NULL);
    try
    {
      m_ColCount = meta.getColumnCount();
      m_ColHeadersIdx = new int[m_ColCount];
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
    int i = 1;
    for (i=1; i<= m_ColCount; i++)
    {
      m_ColHeadersIdx[i-1] =
        addElement(2,m_ColumnHeader_TypeID, m_RowSetIdx, lastColHeaderIdx);

      lastColHeaderIdx = m_ColHeadersIdx[i-1];
      // A bit brute force, but not sure how to clean it up

      try
      {
        addAttributeToNode(
          meta.getColumnName(i),
          m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_COLUMN_NAME_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.getColumnLabel(i),
          m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_COLUMN_LABEL_TypeID, lastColHeaderIdx);
      }

/*
      try
      {
        addAttributeToNode(
          meta.getCatalogName(i),
          m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_CATALOGUE_NAME_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          new Integer(meta.getColumnDisplaySize(i)),
          m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_DISPLAY_SIZE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          new Integer(meta.getColumnType(i)),
          m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_COLUMN_TYPE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.getColumnTypeName(i),
          m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_COLUMN_TYPENAME_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          new Integer(meta.getPrecision(i)),
          m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_PRECISION_TypeID, lastColHeaderIdx);
      }
      try
      {
        addAttributeToNode(
          new Integer(meta.getScale(i)),
          m_ColAttrib_SCALE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_SCALE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.getSchemaName(i),
          m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_SCHEMA_NAME_TypeID, lastColHeaderIdx);
      }
      try
      {
        addAttributeToNode(
          meta.getTableName(i),
          m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_TABLE_NAME_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isCaseSensitive(i) ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_CASESENSITIVE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isDefinitelyWritable(i) ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_DEFINITLEYWRITEABLE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_DEFINITLEYWRITEABLE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isNullable(i) != 0 ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_ISNULLABLE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isSigned(i) ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_ISSIGNED_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isWritable(i) == true ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_ISWRITEABLE_TypeID, lastColHeaderIdx);
      }

      try
      {
        addAttributeToNode(
          meta.isSearchable(i) == true ? S_ISTRUE : S_ISFALSE,
          m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx);
      }
      catch(Exception e)
      {
        addAttributeToNode(
          S_ATTRIB_NOT_SUPPORTED,
          m_ColAttrib_ISSEARCHABLE_TypeID, lastColHeaderIdx);
      }
*/
    }

  }

  /**
   * Populate the Expanded Name Table with the Node that we will use.
   * Keep a reference of each of the types for access speed.
   *
   */
  private void createExpandedNameTable()
  {

    m_Document_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_DOCUMENT, DTM.DOCUMENT_NODE);

    m_TextNode_TypeID =
      m_expandedNameTable.getExpandedTypeID(S_NAMESPACE, S_TEXT_NODE, DTM.TEXT_NODE);

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

  /**
   * A common routine that allocates an Object from the Object Array.
   * One of the common bugs in this code was to allocate an Object and
   * not incerment m_size, using this method will assure that function.
   */
  private int allocateNodeObject(Object o)
  {
    // Need to keep this counter going even if we don't use it.
    m_size++;
    return m_ObjectArray.append(o);
  }

  private int addElementWithData(Object o, int level, int extendedType, int parent, int prevsib)
  {
    int elementIdx = addElement(level,extendedType,parent,prevsib);

    int data = allocateNodeObject(o);
    m_firstch.setElementAt(data,elementIdx);

    m_exptype.setElementAt(m_TextNode_TypeID, data);
    m_level.setElementAt((byte)(level), data);
    m_parent.setElementAt(elementIdx, data);

    m_prevsib.setElementAt(DTM.NULL, data);
    m_nextsib.setElementAt(DTM.NULL, data);
    m_attribute.setElementAt(DTM.NULL, data);
    m_firstch.setElementAt(DTM.NULL, data);

    return elementIdx;
  }

  private int addElement(int level, int extendedType, int parent, int prevsib)
  {
    int node = DTM.NULL;

    try
    {
      // Add the Node and adjust its Extended Type
      node = allocateNodeObject(S_ELEMENT_NODE);

      m_exptype.setElementAt(extendedType, node);
      m_nextsib.setElementAt(DTM.NULL, node);
      m_prevsib.setElementAt(prevsib, node);

      m_parent.setElementAt(parent, node);
      m_firstch.setElementAt(DTM.NULL, node);
      m_level.setElementAt((byte)level, node);
      m_attribute.setElementAt(DTM.NULL, node);

      if (prevsib != DTM.NULL)
      {
        // If the previous sibling is already assigned, then we are
        // inserting a value into the chain.
        if (m_nextsib.elementAt(prevsib) != DTM.NULL)
          m_nextsib.setElementAt(m_nextsib.elementAt(prevsib), node);

        // Tell the proevious sibling that they have a new bother/sister.
        m_nextsib.setElementAt(node, prevsib);
      }

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

  /**
   * Link an attribute to a node, if the node already has one or more
   * attributes assigned, then just link this one to the attribute list.
   * The first attribute is attached to the Parent Node (pnode) through the
   * m_attribute array, subsequent attributes are linked through the
   * m_prevsib, m_nextsib arrays.
   *
   */
  private int addAttributeToNode(
    Object o, int extendedType, int pnode)
  {

    int attrib = DTM.NULL;
    int prevsib = DTM.NULL;
    int lastattrib = DTM.NULL;
    int value = DTM.NULL;

    try
    {
      // Add the Node and adjust its Extended Type
      attrib = allocateNodeObject(o);

      m_attribute.setElementAt(DTM.NULL, attrib);
      m_exptype.setElementAt(extendedType, attrib);
      m_level.setElementAt((byte)0, attrib);


      // Clear the sibling references
      m_nextsib.setElementAt(DTM.NULL, attrib);
      m_prevsib.setElementAt(DTM.NULL,attrib);
      // Set the parent, although the was we are using attributes
      // in the SQL extension this reference will more than likly
      // be wrong
      m_parent.setElementAt(pnode, attrib);
      m_firstch.setElementAt(DTM.NULL, attrib);

      if (m_attribute.elementAt(pnode) != DTM.NULL)
      {
        // OK, we already have an attribute assigned to this
        // Node, Insert us into the head of the list.
        lastattrib = m_attribute.elementAt(pnode);
        m_nextsib.setElementAt(lastattrib, attrib);
        m_prevsib.setElementAt(attrib, lastattrib);
      }
      // Okay set the new attribute up as the first attribute
      // for the node.
      m_attribute.setElementAt(attrib, pnode);
    }
    catch(Exception e)
    {
      // Let's just return DTM.NULL now
      error("");
    }

    return attrib;
  }

  /**
   * Allow two nodes to share the same set of attributes. There may be some
   * problems because the parent of any attribute will be the original node
   * they were assigned to. Need to see how the attribute walker works, then
   * we should be able to fake it out.
   */
  private void cloneAttributeFromNode(int toNode, int fromNode)
  {
   try
    {
      if (m_attribute.elementAt(toNode) != DTM.NULL)
      {
        error("Cloneing Attributes, where from Node already had addtibures assigned");
      }

      m_attribute.setElementAt(m_attribute.elementAt(fromNode), toNode);
    }
    catch(Exception e)
    {
      // Let's just return DTM.NULL now
      error("Cloning attributes");
    }
  }

  private boolean addRowToDTMFromResultSet()
  {
    try
    {
      if ( ! m_ResultSet.next()) return false;

      // If this is the first time here, start the new level
      if (m_FirstRowIdx == DTM.NULL)
      {
        m_FirstRowIdx =
          addElement(2, m_Row_TypeID, m_RowSetIdx, m_ColHeadersIdx[m_ColCount-1]);
        m_LastRowIdx = m_FirstRowIdx;
      }
      else
      {
        m_LastRowIdx = addElement(2, m_Row_TypeID, m_RowSetIdx, m_LastRowIdx);
      }

      int colID = DTM.NULL;

      // Columns in JDBC Start at 1 and go to the Extent
      for (int i=1; i<= m_ColCount; i++)
      {
        // Just grab the Column Object Type, we will convert it to a string
        // later.
        Object o = m_ResultSet.getObject(i);
        colID = addElementWithData(o,3,m_Col_TypeID, m_LastRowIdx, colID);
        cloneAttributeFromNode(colID, m_ColHeadersIdx[i-1]);
      }
    }
    catch(Exception e)
    {
      error("SQL Error Fetching next row [" + e.getLocalizedMessage() + "]");
    }

    // Only do a single row...
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

  public int getFirstAttribute(int parm1)
  {
    if (DEBUG) System.out.println("getFirstAttribute("+ (parm1&NODEIDENTITYBITS)+")");
    int nodeIdx = parm1 & NODEIDENTITYBITS;
    if (nodeIdx != DTM.NULL) return m_attribute.elementAt(nodeIdx);
    else return DTM.NULL;
  }

 /**
   * @param parm1
   * @return
   */
  public String getNodeValue( int parm1 )
  {
    if (DEBUG) System.out.println("getNodeValue(" + parm1 + ")");
    try
    {
      Object o = m_ObjectArray.getAt(parm1 & NODEIDENTITYBITS);
      if (o != null)
      {
        return o.toString();
      }
      else
      {
        return "";
      }
    }
    catch(Exception e)
    {
      error("Getting String Value");
      return null;
    }
  }


  /**
   * @param parm1
   * @return
   */
  public XMLString getStringValue( int parm1 )
  {
    if (DEBUG) System.out.println("getStringValue(" + parm1 + ")");
    try
    {
      Object o = m_ObjectArray.getAt(parm1 & NODEIDENTITYBITS);
      if (o != null)
      {
        return m_xstrf.newstr(o.toString());
      }
      else
      {
        return m_xstrf.emptystr();
      }
    }
    catch(Exception e)
    {
      error("Getting String Value");
      return null;
    }
  }


  public int getNextAttribute(int parm1)
  {
    if (DEBUG) System.out.println("getNextAttribute(" + parm1 + ")");
    int nodeIdx = parm1 & NODEIDENTITYBITS;
    if (nodeIdx != DTM.NULL) return m_nextsib.elementAt(nodeIdx);
    else return DTM.NULL;
  }


  /**
   * @return
   */
  protected int getNumberOfNodes( )
  {
    if (DEBUG) System.out.println("getNumberOfNodes()");
    return m_size;
  }

  /**
   * @return
   */
  protected boolean nextNode( )
  {
    if (DEBUG) System.out.println("nextNode()");
    return addRowToDTMFromResultSet();
  }


  public void dumpDTM()
  {
    try
    {
//      File f = new File("DTMDump"+((Object)this).hashCode()+".txt");
      File f = new File("DTMDump.txt");
      System.err.println("Dumping... "+f.getAbsolutePath());
      PrintStream ps = new PrintStream(new FileOutputStream(f));

      while (nextNode()){}

      int nRecords = m_size;

      ps.println("Total nodes: " + nRecords);

      for (int i = 0; i < nRecords; i++)
      {
        ps.println("=========== " + i + " ===========");
        ps.println("NodeName: " + getNodeName(i));
        ps.println("NodeNameX: " + getNodeNameX(i));
        ps.println("LocalName: " + getLocalName(i));
        ps.println("NamespaceURI: " + getNamespaceURI(i));
        ps.println("Prefix: " + getPrefix(i));

        int exTypeID = getExpandedTypeID(i);

        ps.println("Expanded Type ID: "
                           + Integer.toHexString(exTypeID));

        int type = getNodeType(i);
        String typestring;

        switch (type)
        {
        case DTM.ATTRIBUTE_NODE :
          typestring = "ATTRIBUTE_NODE";
          break;
        case DTM.CDATA_SECTION_NODE :
          typestring = "CDATA_SECTION_NODE";
          break;
        case DTM.COMMENT_NODE :
          typestring = "COMMENT_NODE";
          break;
        case DTM.DOCUMENT_FRAGMENT_NODE :
          typestring = "DOCUMENT_FRAGMENT_NODE";
          break;
        case DTM.DOCUMENT_NODE :
          typestring = "DOCUMENT_NODE";
          break;
        case DTM.DOCUMENT_TYPE_NODE :
          typestring = "DOCUMENT_NODE";
          break;
        case DTM.ELEMENT_NODE :
          typestring = "ELEMENT_NODE";
          break;
        case DTM.ENTITY_NODE :
          typestring = "ENTITY_NODE";
          break;
        case DTM.ENTITY_REFERENCE_NODE :
          typestring = "ENTITY_REFERENCE_NODE";
          break;
        case DTM.NAMESPACE_NODE :
          typestring = "NAMESPACE_NODE";
          break;
        case DTM.NOTATION_NODE :
          typestring = "NOTATION_NODE";
          break;
        case DTM.NULL :
          typestring = "NULL";
          break;
        case DTM.PROCESSING_INSTRUCTION_NODE :
          typestring = "PROCESSING_INSTRUCTION_NODE";
          break;
        case DTM.TEXT_NODE :
          typestring = "TEXT_NODE";
          break;
        default :
          typestring = "Unknown!";
          break;
        }

        ps.println("Type: " + typestring);

        int firstChild = _firstch(i);

        if (DTM.NULL == firstChild)
          ps.println("First child: DTM.NULL");
        else if (NOTPROCESSED == firstChild)
          ps.println("First child: NOTPROCESSED");
        else
          ps.println("First child: " + firstChild);

        int prevSibling = _prevsib(i);

        if (DTM.NULL == prevSibling)
          ps.println("Prev sibling: DTM.NULL");
        else if (NOTPROCESSED == prevSibling)
          ps.println("Prev sibling: NOTPROCESSED");
        else
          ps.println("Prev sibling: " + prevSibling);

        int nextSibling = _nextsib(i);

        if (DTM.NULL == nextSibling)
          ps.println("Next sibling: DTM.NULL");
        else if (NOTPROCESSED == nextSibling)
          ps.println("Next sibling: NOTPROCESSED");
        else
          ps.println("Next sibling: " + nextSibling);

        int parent = _parent(i);

        if (DTM.NULL == parent)
          ps.println("Parent: DTM.NULL");
        else if (NOTPROCESSED == parent)
          ps.println("Parent: NOTPROCESSED");
        else
          ps.println("Parent: " + parent);

        int level = _level(i);

        ps.println("Level: " + level);
        ps.println("Node Value: " + getNodeValue(i));
        ps.println("String Value: " + getStringValue(i));

        ps.println("First Attribute Node: " + m_attribute.elementAt(i));
      }

    }
    catch(IOException ioe)
    {
      ioe.printStackTrace(System.err);
      System.exit(-1);
    }
  }

  /*********************************************************************/
  /*********************************************************************/
  /******************* End of Functions we Wrote ***********************/
  /*********************************************************************/
  /*********************************************************************/


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
  public LexicalHandler getLexicalHandler( )
  {
    if (DEBUG) System.out.println("getLexicalHandler()");
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
    //return super.getNodeNameX( parm1);
    return getNodeName(parm1);

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


}
