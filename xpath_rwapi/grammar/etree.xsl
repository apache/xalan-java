<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:g="http://www.w3.org/2001/03/XPath/grammar">

  <xsl:import href="jjtree.xsl"/>

  <!-- override jjtree.xsl -->
	<xsl:template name="javacc-options">
	  <!-- xsl:apply-imports/ -->
    STATIC = false;
    MULTI=false;
    // NODE_PACKAGE="org.apache.xpath.operations";
    NODE_PREFIX="";
    NODE_FACTORY=true; 
    VISITOR=true;     // invokes the JJTree Visitor support
    NODE_SCOPE_HOOK=false;
    NODE_USES_PARSER=true;
	</xsl:template>

	<xsl:template name="set-parser-package">
package org.apache.xpath.rwapi.impl.parser;
	</xsl:template>

    <xsl:template name="extra-parser-code">
      org.apache.xml.utils.PrefixResolver m_prefixResolver;
      org.apache.xpath.patterns.FunctionPattern m_matchFunc = null; // short lived.


	  /**
	   * Node factory for customized parser
       */
   	 NodeFactory m_nodeFactory;

      int m_predLevel = 0;

      public void setPrefixResolver(org.apache.xml.utils.PrefixResolver resolver)
      {
        m_prefixResolver = resolver;
      }

      public org.apache.xml.utils.PrefixResolver getPrefixResolver()
      {
        return m_prefixResolver;
      }
      
      /**
       * Sets the node factory
       */
      public void setNodeFactory(NodeFactory nodeFactory) {      		
			m_nodeFactory = nodeFactory;
	  }
	
	  /**
	   * Returns the node factory.
	   * @return NodeFactory
	   */
	  public NodeFactory getNodeFactory() {
	 	return m_nodeFactory;
	  }
      
	  /**
	   * The "version" property.
	   * @serial
	   */
	  private double m_version;
	
	  /**
	   * Set the "version" property for XPath.
	   *
	   * @param v Value for the "version" property.
	   */
	  public void setVersion(double v)
	  {
	    m_version = v;
	  }
	
	  /**
	   * Get the "version" property of XPath.
	   *
	   * @return The value of the "version" property.
	   */
	  public double getVersion()
	  {
	    return m_version;
	  }

    </xsl:template>

</xsl:stylesheet>
