/*
 * The Apache Software License, Version 1.1
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
 * originally based on software copyright (c) 2002, International
 * Business Machines Corporation., http://www.ibm.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml;

import java.io.Serializable;


public class QName implements Serializable {

    private String namespaceURI;

    private String localPart;

    private String prefix;

    public QName(String namespaceURI, String localPart) {
        this(namespaceURI, localPart, XMLConstants.DEFAULT_NS_PREFIX);
    }

    public QName(String namespaceURI, String localPart, String prefix) {
        if (namespaceURI == null) {
            this.namespaceURI = XMLConstants.DEFAULT_NS_URI;
        } else {
            this.namespaceURI = namespaceURI;
        }
        
        if (localPart == null || localPart.length() == 0) {
            throw new IllegalArgumentException(
                "local part cannot be \"null\" or \"\" when creating a QName");
        }
        this.localPart = localPart;
        
        if (prefix == null) {
            throw new IllegalArgumentException(
                "prefix cannot be \"null\" when creating a QName");
        }
        this.prefix = prefix;
    }

    public QName(String localPart) {
        this(XMLConstants.DEFAULT_NS_URI,
             localPart,
             XMLConstants.DEFAULT_NS_PREFIX);
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getPrefix() {
        return prefix;
    }
   
    public boolean equals(Object objectToTest) {
        if (objectToTest == null || !(objectToTest instanceof QName)) {
            return false;
        }
    
        QName qName = (QName) objectToTest;
        
        return namespaceURI.equals(qName.namespaceURI)
            && localPart.equals(qName.localPart);
    }

    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    public String toString() {
        if (namespaceURI.equals(XMLConstants.DEFAULT_NS_URI)) {
            return localPart;
        } else {
            return "{" + namespaceURI + "}" + localPart;
        }
    }

    public static QName valueOf(String qNameAsString) {
        if (qNameAsString == null || qNameAsString.length() == 0) {
            throw new IllegalArgumentException(
              "cannot create QName from \"null\" or \"\" String");
        }

        // local part only?
        if (qNameAsString.charAt(0) != '{') {
            return new QName(XMLConstants.DEFAULT_NS_URI,
                             qNameAsString,
                             XMLConstants.DEFAULT_NS_PREFIX);
        }

        // specifies Namespace URI and local part
        int endOfNamespaceURI = qNameAsString.indexOf('}');
        if (endOfNamespaceURI == -1) {
            throw new IllegalArgumentException(
                "cannot create QName from \""
                + qNameAsString + "\", missing closing \"}\"");
        }
        if (endOfNamespaceURI == qNameAsString.length() - 1) {
            throw new IllegalArgumentException(
                "cannot create QName from \""
                + qNameAsString + "\", missing local part");
        }
        return new QName(qNameAsString.substring(1, endOfNamespaceURI),
                         qNameAsString.substring(endOfNamespaceURI + 1),
                         XMLConstants.DEFAULT_NS_PREFIX);
    }
}
