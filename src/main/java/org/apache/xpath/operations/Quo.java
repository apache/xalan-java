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
/*
 * $Id$
 */
package org.apache.xpath.operations;

import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

/**
 * The 'quo' operation expression executer. (no longer supported by XPath).
 * @deprecated
 */
public class Quo extends XPathOperator
{
    static final long serialVersionUID = 693765299196169905L;

  // Actually, this is no longer supported by xpath...

    /**
     * Apply an XPath operator to its two operands, and return the result.
     *
     * @param left  non-null reference to an XPath operator's evaluated 
     *              first operand.              
     * @param right non-null reference to an XPath operator's evaluated 
     *              second operand.
     *
     * @return non-null reference to an XObject object instance, that 
     *         represents the result of XPath operator evaluation. 
     *
     * @throws javax.xml.transform.TransformerException
     */
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {
    return new XNumber((int) (left.num() / right.num()));
  }
}
