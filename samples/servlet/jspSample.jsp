<%@ page language="java" contentType="text/html" %>
<%@ page import="javax.xml.transform.*"%>
<%@ page import="javax.xml.transform.stream.*"%>
<html>
<head>
<title>JSP sample passing a parameter to XSL</title>
</head>
<body>
<%
/**
 * This JSP uses PMA to set param1 in the
*  foo.xsl stylesheet before using the
 * stylesheet to transform foo.xml
 * and outputing the result.
 *
 * Invoke the jsp from the appropriate
 * context for your servlet/jsp server.
 * For example: http://localhost:8080/samples/jspSample.jsp?HellowWorld!&XML=foo.xml&XSL=foo.xsl
 * This example assumes that foo.xsl and foo.xml
 * are in the same directory. 
 * Output should be Hello (from foo.xsml) and HelloWorld!
 * (value of param1 in foo.xsl).

 *@author Paul Campbell seapwc@halcyon.com
 *@version $Id$
 */
 

String paramValue = request.getParameter("PMA");
String xmlFile    = request.getParameter("XML");
String xslFile    = request.getParameter("XSL");

TransformerFactory tFactory = 
	TransformerFactory.newInstance();
Transformer transformer =
	tFactory.newTransformer(new StreamSource(xslFile));
	transformer.setParameter("param1", paramValue);
	transformer.transform(
		 new StreamSource(xmlFile), new StreamResult(out));
%>
</body>
</html>
