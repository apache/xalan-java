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
package org.apache.xml.dtm.ref;

/**
 * The class ExtendedType represents an extended type object used by
 * ExpandedNameTable.
 */
public final class ExtendedType
{
    private int nodetype;
    private String namespace;
    private String localName;
    private int hash;

    /**
     * Create an ExtendedType object from node type, namespace and local name.
     * The hash code is calculated from the node type, namespace and local name.
     * 
     * @param nodetype Type of the node
     * @param namespace Namespace of the node
     * @param localName Local name of the node
     */
    public ExtendedType (int nodetype, String namespace, String localName)
    {
      this.nodetype = nodetype;
      this.namespace = namespace;
      this.localName = localName;
      this.hash = nodetype + namespace.hashCode() + localName.hashCode();
    }

    /**
     * Create an ExtendedType object from node type, namespace, local name
     * and a given hash code.
     * 
     * @param nodetype Type of the node
     * @param namespace Namespace of the node
     * @param localName Local name of the node
     * @param hash The given hash code
     */
    public ExtendedType (int nodetype, String namespace, String localName, int hash)
    {
      this.nodetype = nodetype;
      this.namespace = namespace;
      this.localName = localName;
      this.hash = hash;
    }

    /** 
     * Redefine this ExtendedType object to represent a different extended type.
     * This is intended to be used ONLY on the hashET object. Using it elsewhere
     * will mess up existing hashtable entries!
     */
    protected void redefine(int nodetype, String namespace, String localName)
    {
      this.nodetype = nodetype;
      this.namespace = namespace;
      this.localName = localName;
      this.hash = nodetype + namespace.hashCode() + localName.hashCode();
    }

    /** 
     * Redefine this ExtendedType object to represent a different extended type.
     * This is intended to be used ONLY on the hashET object. Using it elsewhere
     * will mess up existing hashtable entries!
     */
    protected void redefine(int nodetype, String namespace, String localName, int hash)
    {
      this.nodetype = nodetype;
      this.namespace = namespace;
      this.localName = localName;
      this.hash = hash;
    }

    /**
     * Override the hashCode() method in the Object class
     */
    public int hashCode()
    {
      return hash;
    }

    /**
     * Test if this ExtendedType object is equal to the given ExtendedType.
     * 
     * @param other The other ExtendedType object to test for equality
     * @return true if the two ExtendedType objects are equal.
     */
    public boolean equals(ExtendedType other)
    {
      try
      {
        return other.nodetype == this.nodetype &&
                other.localName.equals(this.localName) &&
                other.namespace.equals(this.namespace);
      }
      catch(NullPointerException e)
      {
        return false;
      }
    }
    
    /**
     * Return the node type
     */
    public int getNodeType()
    {
      return nodetype;
    }
    
    /**
     * Return the local name
     */
    public String getLocalName()
    {
      return localName;
    }
    
    /**
     * Return the namespace
     */
    public String getNamespace()
    {
      return namespace;
    }

}
