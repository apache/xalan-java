/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xml.dtm.dom2dtm2;
import org.w3c.dom.Node;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/** An object which can map a Node in a specific DOM into a 
 * unique-within-document identifying integer. Different
 * DOMs may require different solutions, so most of the
 * actual behavior will occur in subclasses; the
 * method <code>NodeDTMIDResolverFactory.createResolverForDOM</code> 
 * can be used to (attempt to?) automatically select an appropriate
 * implementation.
 * 
 * @author keshlam
 * @since Sep 6, 2002
 */
public interface NodeDTMIDResolver
{
	/* @return a resolver iff this resolver believes it can support
	 * the given DOM, else return null.
	 * 
	 * PROBLEM: Interfaces can't declare static methods, and implementations
	 * can't override to static. Grrrrrr!
	 * 
	public NodeDTMIDResolver getInstance(DOMImplementation impl,Document doc);
	 * */
	
	/** Given a DOM node, return its unique-within-Document ID number.
	 * 
	 * This version  is safe under all conditions -- equivalent to
	 * findID(n,true), which see.
	 * 
	 * This is implemented uniquely in each concrete resolver class.
	 * Approaches may range from using the DOM Level 3 userData hooks
	 * to leveraging a particular implementation's custom features to
	 * (possibly extremely painful!) isSameNode table searches.
	 * 
	 * Note that some mapping from DOM to XPath should be performed
	 * -- specifically, findID should "skip" EntityReference nodes,
	 * and seek to the first of a logically-adjacent set of Text nodes.
	 * */
	public int findID(Node n);

	/** Given a DOM node, return its unique-within-Document ID number.
	 * 
	 * This version is used within DOM2DTM2, as an optimization; if fixupTextNodes
	 * is false, the caller is asserting that the node will never be a Text
	 * node which "logically follows" another Text node, and thus that we don't
	 * have to spend cycles walking backward to test that case. If you hand us a text
	 * node which does not meet those constraints and do not set fixupTextNodes
	 * to true, erroneous results are the best you can expect; if in doubt,
	 * burn the cycles and do the fixup.
	 * 
	 * This is implemented uniquely in each concrete resolver class.
	 * Approaches may range from using the DOM Level 3 userData hooks
	 * to leveraging a particular implementation's custom features to
	 * (possibly extremely painful!) isSameNode table searches.
	 * 
	 * Note that some mapping from DOM to XPath should be performed
	 * -- specifically, findID should "skip" EntityReference nodes,
	 * and (if fixupTextNodes is true) seek to the first of a 
	 * logically-adjacent set of Text nodes.
	 * */
	public int findID(Node n,boolean fixupTextNodes);

	/** Given unique-within-Document ID number, return its Node
	 * This is implemented uniquely in each concrete resolver class,
	 * most commonly via some form of reverse table/vector.
	 * 
	 * Out of range values will throw exceptions.
	 * */
	public Node findNode(int id);	
	

	/** Given two node objects, report whether they are the same DOM node.
	 * This is implemented uniquely in each concrete resolver class,
	 * most portably via DOM3 isSameNode() but some DOMs require/permit
	 * other solutions.
	 * */
	public boolean isSameNode(Node n1,Node n2);	
	
	/** Given a Node, return a Vector of all the namespace
	 * declaration attributes in scope at that point.
	 * 
	 * Implementations will probably want to cache this information,
	 * to improve performance of the DTM's getNextNamespaceNode operation.
	 * %REVIEW% However, there are questions about how long it should
	 * be cached. Set flush true to cause it to be recomputed.
	 * THIS IS SUBJECT TO REDESIGN.
	 * */
    public java.util.Vector getNamespacesInScope(Node n,boolean flush);
    
  /**
   * Figure out whether nodeHandle2 should be considered as being later
   * in the document than nodeHandle1, in Document Order as defined
   * by the DOM model. This may not agree with the ordering defined
   * by other XML applications; XPath in particular needs to do a
   * separate test for ordering of Namespace Nodes versus Attributes
   * within a single element.
   * <p>
   * Performance is likely to be lousy. DOM Level 3 is introducing
   * a method which may be better optimized (in some DOMs).
   * <p>
   * There are some cases where ordering isn't defined.
   * Our convention is that since we can't definitively say it
   * <strong>is</strong> ordered, we return <code>false</code>.
   *
   * @param nodeHandle1 Node handle to perform position comparison on.
   * @param nodeHandle2 Second Node handle to perform position comparison on .
   *
   * @return true if node1 comes before node2, otherwise return false.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public boolean isNodeOrder(Node n1, Node n2);
}
