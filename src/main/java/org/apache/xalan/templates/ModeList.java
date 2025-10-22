/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.xml.utils.QName;

/**
 * A class definition, to encapsulate an XSL mode list indentified 
 * by xsl:mode instruction(s), and helps locate individual mode 
 * declarations.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ModeList implements java.io.Serializable
{

  private static final long serialVersionUID = -6677705125375244140L;

  /**
   * Construct a ModeList object. Needs to be public so it can
   * be invoked from the CompilingStylesheetHandler.
   */
  public ModeList()
  {
    super();
  }
  
  /**
   * A java.util.Map object, containing xsl:mode declarations 
   * keyed by their names.
   * 
   * REVISIT : To handle xsl:mode declarations, that don't 
   *           have an attribute 'name'.
   */
  private Map<QName, ElemMode> m_elem_mode_map = new HashMap<QName, ElemMode>();

  /**
   * Add a mode to the table of mode declarations.
   *
   * @param ElemMode				An ElemMode object instance
   */
  public void setElemMode(ElemMode elemMode)
  {    	      
	  m_elem_mode_map.put(elemMode.getName(), elemMode);
  }

  /**
   * Locate a mode declaration.
   *
   * @param qname             Qualified name of the mode
   *
   * @return 	              An xsl:mode definition object instance, or null 
   *                          if not found.
   */
  public ElemMode getElemMode(QName qname)
  {
	  return m_elem_mode_map.get(qname);
  }

}
