
/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.trax;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.DOMWSFilter;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;

import org.xml.sax.SAXException;

public final class XSLTCSource implements Source {

    private String     _systemId = null;
    private Source     _source   = null;
    private ThreadLocal _dom     = new ThreadLocal();

    /**
     * Create a new XSLTC-specific source from a system ID 
     */
    public XSLTCSource(String systemId) 
    {
        _systemId = systemId;
    }

    /**
     * Create a new XSLTC-specific source from a JAXP Source
     */
    public XSLTCSource(Source source) 
    {
        _source = source;
    }

    /**
     * Implements javax.xml.transform.Source.setSystemId()
     * Set the system identifier for this Source. 
     * This Source can get its input either directly from a file (in this case
     * it will instanciate and use a JAXP parser) or it can receive it through
     * ContentHandler/LexicalHandler interfaces.
     * @param systemId The system Id for this Source
     */
    public void setSystemId(String systemId) {
        _systemId = systemId;
        if (_source != null) {
            _source.setSystemId(systemId);
        }
    }

    /**
     * Implements javax.xml.transform.Source.getSystemId()
     * Get the system identifier that was set with setSystemId.
     * @return The system identifier that was set with setSystemId,
     *         or null if setSystemId was not called.
     */
    public String getSystemId() {
	if (_source != null) {
	    return _source.getSystemId();
	}
	else {
	    return(_systemId);
	}
    }
    
    /**
     * Internal interface which returns a DOM for a given DTMManager and translet.
     */
    protected DOM getDOM(XSLTCDTMManager dtmManager, AbstractTranslet translet)
        throws SAXException
    {
        SAXImpl idom = (SAXImpl)_dom.get();
                
        if (idom != null) {
            if (dtmManager != null) {
                idom.migrateTo(dtmManager);
            }
        }
        else {
            Source source = _source;
            if (source == null) {
                if (_systemId != null && _systemId.length() > 0) {
                    source = new StreamSource(_systemId);
                }
                else {
                    ErrorMsg err = new ErrorMsg(ErrorMsg.XSLTC_SOURCE_ERR);
                    throw new SAXException(err.toString());
                }
            }
            
            DOMWSFilter wsfilter = null;
            if (translet != null && translet instanceof StripFilter) {
                wsfilter = new DOMWSFilter(translet);
            }
                
            boolean hasIdCall = (translet != null) ? translet.hasIdCall() : false;
            
            if (dtmManager == null) {
                dtmManager = XSLTCDTMManager.newInstance();
            }
            
            idom = (SAXImpl)dtmManager.getDTM(source, true, wsfilter, false, false, hasIdCall);
            
            String systemId = getSystemId();
            if (systemId != null) {
                idom.setDocumentURI(systemId);
            }
            _dom.set(idom);
        }
        return idom;
    }

}
