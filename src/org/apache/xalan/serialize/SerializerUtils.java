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
package org.apache.xalan.serialize;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

/**
 * <meta name="usage" content="internal"/>
 * Class that contains only static methods that are used to "serialize",
 * these methods are used by Xalan and are not in org.apache.xml.serializer
 * because they have dependancies on the packages org.apache.xpath or org.
 * apache.xml.dtm or org.apache.xalan.transformer. The package org.apache.xml.
 * serializer should not depend on Xalan or XSLTC.
 */
public class SerializerUtils
{

    /**
     * Copy an DOM attribute to the created output element, executing
     * attribute templates as need be, and processing the xsl:use
     * attribute.
     *
     * @param handler SerializationHandler to which the attributes are added.
     * @param attr Attribute node to add to SerializationHandler.
     *
     * @throws TransformerException
     */
    public static void addAttribute(SerializationHandler handler, int attr)
        throws TransformerException
    {

        TransformerImpl transformer =
            (TransformerImpl) handler.getTransformer();
        DTM dtm = transformer.getXPathContext().getDTM(attr);

        if (SerializerUtils.isDefinedNSDecl(handler, attr, dtm))
            return;

        String ns = dtm.getNamespaceURI(attr);

        if (ns == null)
            ns = "";

        // %OPT% ...can I just store the node handle?
        try
        {
            handler.addAttribute(
                ns,
                dtm.getLocalName(attr),
                dtm.getNodeName(attr),
                "CDATA",
                dtm.getNodeValue(attr));
        }
        catch (SAXException e)
        {
            // do something?
        }
    } // end copyAttributeToTarget method

    /**
     * Copy DOM attributes to the result element.
     *
     * @param src Source node with the attributes
     *
     * @throws TransformerException
     */
    public static void addAttributes(SerializationHandler handler, int src)
        throws TransformerException
    {

        TransformerImpl transformer =
            (TransformerImpl) handler.getTransformer();
        DTM dtm = transformer.getXPathContext().getDTM(src);

        for (int node = dtm.getFirstAttribute(src);
            DTM.NULL != node;
            node = dtm.getNextAttribute(node))
        {
            addAttribute(handler, node);
        }
    }

    /**
     * Given a result tree fragment, walk the tree and
     * output it to the SerializationHandler.
     *
     * @param obj Result tree fragment object
     * @param support XPath context for the result tree fragment
     *
     * @throws org.xml.sax.SAXException
     */
    public static void outputResultTreeFragment(
        SerializationHandler handler,
        XObject obj,
        XPathContext support)
        throws org.xml.sax.SAXException
    {

        int doc = obj.rtf();
        DTM dtm = support.getDTM(doc);

        if (null != dtm)
        {
            for (int n = dtm.getFirstChild(doc);
                DTM.NULL != n;
                n = dtm.getNextSibling(n))
            {
                handler.flushPending();
                // I think. . . . This used to have a (true) arg
                // to flush prefixes, will that cause problems ???
                if (dtm.getNamespaceURI(n) == null)
                    handler.startPrefixMapping("", "");
                dtm.dispatchToEvents(n, handler);
            }
        }
    }

    /**
     * Copy <KBD>xmlns:</KBD> attributes in if not already in scope.
     *
     * As a quick hack to support ClonerToResultTree, this can also be used
     * to copy an individual namespace node.
     *
     * @param src Source Node
     * NEEDSDOC @param type
     * NEEDSDOC @param dtm
     *
     * @throws TransformerException
     */
    public static void processNSDecls(
        SerializationHandler handler,
        int src,
        int type,
        DTM dtm)
        throws TransformerException
    {

        try
        {
            if (type == DTM.ELEMENT_NODE)
            {
                for (int namespace = dtm.getFirstNamespaceNode(src, true);
                    DTM.NULL != namespace;
                    namespace = dtm.getNextNamespaceNode(src, namespace, true))
                {

                    // String prefix = dtm.getPrefix(namespace);
                    String prefix = dtm.getNodeNameX(namespace);
                    String desturi = handler.getNamespaceURIFromPrefix(prefix);
                    //            String desturi = getURI(prefix);
                    String srcURI = dtm.getNodeValue(namespace);

                    if (!srcURI.equalsIgnoreCase(desturi))
                    {
                        handler.startPrefixMapping(prefix, srcURI, false);
                    }
                }
            }
            else if (type == DTM.NAMESPACE_NODE)
            {
                String prefix = dtm.getNodeNameX(src);
                // bjm - some changes here to get desturi
                String desturi = handler.getNamespaceURIFromPrefix(prefix);
                String srcURI = dtm.getNodeValue(src);

                if (!srcURI.equalsIgnoreCase(desturi))
                {
                    handler.startPrefixMapping(prefix, srcURI, false);
                }
            }
        }
        catch (org.xml.sax.SAXException se)
        {
            throw new TransformerException(se);
        }
    }

    /**
     * Returns whether a namespace is defined
     *
     *
     * @param attr Namespace attribute node
     * @param dtm The DTM that owns attr.
     *
     * @return True if the namespace is already defined in
     * list of namespaces
     */
    public static boolean isDefinedNSDecl(
        SerializationHandler serializer,
        int attr,
        DTM dtm)
    {

        if (DTM.NAMESPACE_NODE == dtm.getNodeType(attr))
        {

            // String prefix = dtm.getPrefix(attr);
            String prefix = dtm.getNodeNameX(attr);
            String uri = serializer.getNamespaceURIFromPrefix(prefix);
            //      String uri = getURI(prefix);

            if ((null != uri) && uri.equals(dtm.getStringValue(attr)))
                return true;
        }

        return false;
    }

    /**
     * This function checks to make sure a given prefix is really
     * declared.  It might not be, because it may be an excluded prefix.
     * If it's not, it still needs to be declared at this point.
     * TODO: This needs to be done at an earlier stage in the game... -sb
     *
     * @param ns Namespace URI of the element
     * @param rawName Raw name of element (with prefix)
     *
     * NEEDSDOC @param dtm
     * NEEDSDOC @param namespace
     *
     * @throws org.xml.sax.SAXException
     */
    public static void ensureNamespaceDeclDeclared(
        SerializationHandler handler,
        DTM dtm,
        int namespace)
        throws org.xml.sax.SAXException
    {

        String uri = dtm.getNodeValue(namespace);
        String prefix = dtm.getNodeNameX(namespace);

        if ((uri != null && uri.length() > 0) && (null != prefix))
        {
            String foundURI;
            NamespaceMappings ns = handler.getNamespaceMappings();
            if (ns != null)
            {

                foundURI = ns.lookupNamespace(prefix);
                if ((null == foundURI) || !foundURI.equals(uri))
                {
                    handler.startPrefixMapping(prefix, uri, false);
                }
            }
        }
    }
}
