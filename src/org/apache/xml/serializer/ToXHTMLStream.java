/*
 * Created on Sep 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.apache.xml.serializer;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author minchau
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ToXHTMLStream extends ToXMLStream
{
    /** True if the META tag should be omitted. */
    private boolean m_omitMetaTag = false;

    private boolean m_inBlockElem = false;

    public void startElement(
        String namespaceURI,
        String localName,
        String name,
        Attributes atts)
        throws org.xml.sax.SAXException
    {

        if (m_inEntityRef)
            return;
        final ElemContext elemContext = m_elemContext;
        final ElemDesc elemDesc = ToHTMLStream.getElemDesc(name);
        final int elemFlags = elemDesc.getFlags();
        final boolean isHeadElement = (elemFlags & ElemDesc.HEADELEM) != 0;
        
//        if ( ((elemFlags & ElemDesc.HTMLELEM) != 0)
//            && (namespaceURI == null || namespaceURI.equals(EMPTYSTRING))) 
//        {
//            this.startPrefixMapping(EMPTYSTRING,"http://www.w3.org/1999/xhtml");
//        }

        if (m_needToCallStartDocument)
        {
            startDocumentInternal();
            m_needToCallStartDocument = false;
        }
        else if (m_cdataTagOpen)
            closeCDATA();
        try
        {
            if ((true == m_needToOutputDocTypeDecl)
                && (null != getDoctypeSystem()))
            {
                outputDocTypeDecl(name, true);
            }

            m_needToOutputDocTypeDecl = false;

            /* before we over-write the current elementLocalName etc.
             * lets close out the old one (if we still need to)
             */
            if (m_elemContext.m_startTagOpen)
            {
                closeStartTag();
                m_elemContext.m_startTagOpen = false;
            }

            if (namespaceURI != null)
                ensurePrefixIsDeclared(namespaceURI, name);

            m_ispreserve = false;

            // deal with indentation issues first
            if (m_doIndent)
            {

                boolean isBlockElement = (elemFlags & ElemDesc.BLOCK) != 0;
                if (m_ispreserve)
                    m_ispreserve = false;
                else if (
                    (null != elemContext.m_elementName)
                    && (!m_inBlockElem
                        || isBlockElement) /* && !isWhiteSpaceSensitive */
                    )
                {
                    m_startNewLine = true;

                    indent();

                }
                m_inBlockElem = !isBlockElement;
            }

            m_startNewLine = true;

            final java.io.Writer writer = m_writer;
            writer.write('<');
            writer.write(name);

            // process the attributes now, because after this SAX call they might be gone
            if (atts != null)
                addAttributes(atts);
            if (m_tracer != null)
                firePseudoAttributes();

            final ElemContext newElemContext = m_elemContext.push(namespaceURI, localName, name);
            m_elemContext = newElemContext;
            newElemContext.m_elementDesc = elemDesc;
            newElemContext.m_isRaw = (elemFlags & ElemDesc.RAW) != 0;

            m_isprevtext = false;

            if ((elemFlags & ElemDesc.HEADELEM) != 0)
            {
                // This is the <HEAD> element, do some special processing
                closeStartTag();
                newElemContext.m_startTagOpen = false;
                if (!m_omitMetaTag)
                {
                    if (m_doIndent)
                        indent();
                    writer.write(
                        "<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
                    String encoding = getEncoding();
                    String encode = Encodings.getMimeEncoding(encoding);
                    writer.write(encode);
                    writer.write("\">");
                }
            }
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }
    }

    public final void endElement(
        final String namespaceURI,
        final String localName,
        final String name)
        throws org.xml.sax.SAXException
    {
        try
        {

            if (m_inEntityRef)
                return;

            // deal with any pending issues
            if (m_cdataTagOpen)
                closeCDATA();

            // namespaces declared at the current depth are no longer valid
            // so get rid of them    
            m_prefixMap.popNamespaces(m_elemContext.m_currentElemDepth, null);
            final java.io.Writer writer = m_writer;
            final ElemContext elemContext = m_elemContext;
            final ElemDesc elemDesc = elemContext.m_elementDesc;
            final int elemFlags = elemDesc.getFlags();
            final boolean elemEmpty = (elemFlags & ElemDesc.EMPTY) != 0;

            // deal with any indentation issues
            if (m_doIndent)
            {
                final boolean isBlockElement =
                    (elemFlags & ElemDesc.BLOCK) != 0;
                boolean shouldIndent = false;

                if (m_ispreserve)
                {
                    m_ispreserve = false;
                }
                else if (m_doIndent && (!m_inBlockElem || isBlockElement))
                {
                    m_startNewLine = true;
                    shouldIndent = true;
                }
                if (!elemContext.m_startTagOpen && shouldIndent)
                    indent(elemContext.m_currentElemDepth - 1);
                m_inBlockElem = !isBlockElement;
            }

            if (m_elemContext.m_startTagOpen)
            {
                if (m_tracer != null)
                    super.fireStartElem(m_elemContext.m_elementName);
                int nAttrs = m_attributes.getLength();
                if (nAttrs > 0)
                {
                    processAttributes(m_writer, nAttrs);
                    // clear attributes object for re-use with next element
                    m_attributes.clear();
                }

                if (!elemEmpty)
                {
                    writer.write("></");
                    writer.write(name);
                    writer.write('>');
                }
                else
                {
                    writer.write(' ');
                    writer.write('/');
                    writer.write('>');
                }

            }
            else
            {
                writer.write('<');
                writer.write('/');
                writer.write(name);
                writer.write('>');
            }
            // clean up because the element has ended
            if (!m_elemContext.m_startTagOpen && m_doIndent)
            {
                m_ispreserve =
                    m_preserves.isEmpty() ? false : m_preserves.pop();
            }
            if ((elemFlags & ElemDesc.WHITESPACESENSITIVE) != 0)
                m_ispreserve = true;
            m_isprevtext = false;

            // fire off the end element event
            if (m_tracer != null)
                super.fireEndElem(name);

            m_elemContext = elemContext.m_prev;
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }

    }

}
