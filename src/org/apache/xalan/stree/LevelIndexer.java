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
package org.apache.xalan.stree;

import org.w3c.dom.*;

import java.util.*;

import java.io.*;

import java.lang.Object;

import org.apache.xml.utils.IntVector;

/**
 * <meta name="usage" content="general"/>
 * The XPath class represents the semantic parse tree of the XPath pattern.
 * It is the representation of the grammar which filters out
 * the choice for replacement order of the production rules.
 * In order to conserve memory and reduce object creation, the
 * tree is represented as an array of integers:
 *    [op code][length][...]
 * where strings are represented within the array as
 * indexes into the token tree.
 */
public class LevelIndexer
{
  
  /** 
   * Global variable to store the subtype of the elements 
   * added to the table of nodes          
   */
  int m_subtype;

  /** Default size of a nodesList to be added to the level array            */
  int m_defaultSize = 3;  // this is the default value.

  /** 
   * Hashtable of element types that is keyed
   * on multiple keys.          
   */
  MultiKeyTable m_elemTypes = new MultiKeyTable();  

  /** 
   * Array of levels in the tree.
   * Each element of this array is a nodesList element.
   */
  Object[] m_levelArray;  // array of levels in the tree. These are used

  // to build the elemPostings table.

  /**
   * Create a LevelIndexer object.
   */
  public LevelIndexer()
  {
    m_levelArray = new Object[10];
  }

  /**
   * <meta name="usage" content="internal"/>
   * Insert a node in the nodesList by level, by parent and by type.
   *
   * @param child Node to be inserted in nodesList
   */
  public void insertNode(Child child)
  {

    boolean updateParent = true;

    // first assign a subtype to the element and add it to the
    // m_elemTypes table.
    int type = addToTable(child);

    // int uid = child.getUid();
    int level = child.getLevel();

    // Nothing there yet
    if (m_levelArray[level] == null)
    {
      Object[] nodesList = new Object[m_defaultSize];

      m_levelArray[level] = nodesList;
      nodesList[0] = child;

      ((IndexedElem) child.getParentNode()).setIndex(0);
    }

    // Add to the existing list
    else
    {
      Object[] nodesList = (Object[]) m_levelArray[level];
      int structIndex = 0;

      while (structIndex < nodesList.length
             && (nodesList[structIndex] != null))
      {
        structIndex++;
      }

      int lastUsed = structIndex - 1;

      // TODO: cleanup
      // Need to reallocate?? 2 is the max slots we could
      // need to add plus 1 to indicate end...
      if (nodesList.length < lastUsed + 3)
      {
        nodesList = allocateNewList(nodesList);
        m_levelArray[level] = nodesList;
      }

      structIndex = 0;

      while (structIndex < nodesList.length
             && (nodesList[structIndex] != null))
      {
        int next = structIndex + 1;
        Child node = (Child) nodesList[structIndex];

        if (child.getParentNode().equals(node.getParentNode()))
        {

          // There is already at least one node with this parent at this level        
          // This parent already has a pointer to its children.
          updateParent = false;

          if (getType(node) == type)
          {

            // This parent already has children of this type.
            // Add this child to the end of the list for this type.
            if (nodesList[next] != null)
            {

              // Slide down one
              int i;

              for (i = lastUsed; i >= next; i--)
              {
                if (getType((Child) nodesList[i]) != type)
                  nodesList[i + 1] = nodesList[i];
              }

              nodesList[i] = child;
            }
            else
            {
              nodesList[next] = child;
            }

            break;
          }

          structIndex = structIndex + 1;
        }

        // Keep looking for this parent and this type
        else
        {
          structIndex = structIndex + 1;
        }
      }  // end while

      // First node for this parent of this type at this level
      if (nodesList[structIndex] == null)
      {
        nodesList[structIndex] = child;

        if (updateParent)
          ((IndexedElem) child.getParentNode()).setIndex(structIndex);
      }
    }  //end else add to existing list
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get a list of nodes in the level array by level and by type.
   *
   * @param level Level of the nodes in the document tree 
   *
   * @return List of nodes at the given level in the document tree.
   */
  public Object[] getNodesList(int level)
  {

    if (level > m_levelArray.length - 1 || m_levelArray[level] == null)
      return null;
    else
    {
      return (Object[]) m_levelArray[level];
    }
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get a list of nodes in the level array by level and by type.
   *
   * @param nodesList Existing list of nodes to expand
   *
   * @return Larger list of nodes, including the nodes from original list
   */
  public Object[] allocateNewList(Object[] nodesList)
  {

    int len = nodesList.length;
    Object[] newlist = new Object[len + m_defaultSize];

    System.arraycopy(nodesList, 0, newlist, 0, len);

    for (int i = len; i < len + m_defaultSize; i++)
    {
      newlist[i] = null;
    }

    return newlist;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get index pointing to nodes of a certain type in the nodeslist.
   *
   * @param child Node for which type we're looking for an index
   * @param type Type of node to look for in nodesList
   * @param nodesList List of nodes at a certain level in document tree
   *
   * @return index pointing to nodes of a certain type in the given nodeslist
   */
  public int getIndexForType(Node child, int type, Object[] nodesList)
  {

    int structIndex = 0;

    if (type == TYPEANY)
      return structIndex;

    while (structIndex < nodesList.length && nodesList[structIndex] != null)
    {

      // int next = structIndex + 1; 
      Child node = (Child) nodesList[structIndex];

      if (child.getParentNode().equals(node.getParentNode()))
      {
        if (getType(node) == type)
        {
          return structIndex;
        }

        structIndex = structIndex + 1;
      }

      // Keep looking for this type
      else
      {
        structIndex = structIndex + 1;
      }
    }

    return -1;  // not found
  }

  /**
   * <meta name="usage" content="internal"/>
   * Add a node to the types table. Each entry is a (node, type) pair.
   * The type is an assigned integer value. Attribute nodes
   * will have "@::" prepended to the element name. The name
   * is a combination of the URI and the actual element name
   * seperated by a double colon "::"
   * Note that m_subtype is a global variable that gets incremented
   * for the next time it is used.
   *
   * @param child Node to be added to the subtypes table.
   * @return return the element subtype
   */
  public int addToTable(Child child)
  {

    String uri = child.getNamespaceURI();
    String name = child.getNodeName();
    String prepend = null;

    // Keep track of attribute nodes
    if (child.getNodeType() == Node.ATTRIBUTE_NODE)
      prepend = "@";

    // Only add new types to the table    
    int type = m_elemTypes.get(name, uri, prepend);

    if (type < 0)
    {
      type = m_subtype;

      m_elemTypes.put(name, uri, prepend, type);

      m_subtype++;
    }

    return type;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get subtype of a given node.
   *
   * @param name Node name
   * @param namespace Namespace of node
   * @param prepend String prepended to attribute node names.
   *
   * @return Subtype for the given node.
   */
  public short getType(String name, String namespace, String prepend)
  {

    int type = m_elemTypes.get(name, namespace, prepend);

    return (new Integer(type).shortValue());
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get subtype of a given node.
   *
   * @param node Node which subtype we're looking for.
   *
   * @return Subtype for the given node.
   */
  public short getType(Node node)
  {

    String uri = node.getNamespaceURI();
    String name = node.getNodeName();
    String prepend = null;

    // set up for attribute nodes
    if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      prepend = "@";

    return getType(name, uri, prepend);
  }

  /**
   * The value to use for "//" and the like, where the level is
   * to the bottom of the tree, but we don't know how many levels
   * the tree is.
   */
  static final int MAXDEPTH = 2000;

  /**
   * The value to use for wildcards and the like
   */
  static final int TYPEANY = 2000;

  /**
   * Implement a structure extending from Hashtable that is keyed
   * on multiple keys.
   */
  protected class MultiKeyTable  //extends Hashtable
  {

    /**
     * Constructor MultiKeyTable
     *
     */
    protected MultiKeyTable()
    {
      super();
    }

    /** Hashtable keyed by node name          */
    private Hashtable m_nameTable;

    /** Hashtable keyed by namespaceURI          */
    private Hashtable m_uriTable;

    /**
     * Add a node to this table. 
     *
     *
     * @param name Name of the node 
     * @param namespace namespace of the node
     * @param prepend String to prepend for attribute nodes
     * @param value Node subtype
     */
    public void put(String name, String namespace, String prepend, int value)
    {

      IntVector nameList, uriList;

      if (prepend != null)
        name = prepend + "::" + name;

      if (name != null)
      {
        if (m_nameTable == null)
          m_nameTable = new Hashtable();

        nameList = (IntVector) m_nameTable.get(name);

        if (nameList == null)
          nameList = new IntVector();

        nameList.addElement(value);
        m_nameTable.put(name, nameList);
      }

      if (namespace == null)
        namespace = "";

      if (m_uriTable == null)
        m_uriTable = new Hashtable();

      uriList = (IntVector) m_uriTable.get(namespace);

      if (uriList == null)
        uriList = new IntVector();

      uriList.addElement(value);
      m_uriTable.put(namespace, uriList);
    }

    /**
     * Get the subtype of a node from the table 
     *
     *
     * @param name Node name to look up 
     * @param namespace Namespace of node to look up
     * @param prepend String that was prepended to the node name 
     *
     * @return The subtype for the given node
     */
    public int get(String name, String namespace, String prepend)
    {

      IntVector nameList, uriList = null;

      if (m_nameTable == null)
        return -1;

      if (prepend != null)
        name = prepend + "::" + name;

      nameList = (IntVector) m_nameTable.get(name);

      if (nameList == null)
        return -1;

      if (namespace == null)
        namespace = "";

      if (m_uriTable != null)
      {
        uriList = (IntVector) m_uriTable.get(namespace);

        if (uriList == null)
          return -1;
      }

      // Return the element that is common to both lists 
      for (int i = 0; i < nameList.size(); i++)
      {
        for (int j = 0; j < uriList.size(); j++)
        {
          if (nameList.elementAt(i) == uriList.elementAt(j))
            return nameList.elementAt(i);
        }
      }

      // Not found.
      return -1;
    }
  }
}
