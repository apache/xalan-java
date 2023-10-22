/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
package org.apache.xpath;

import javax.xml.transform.SourceLocator;

/**
 * A class that implements this interface can construct expressions, 
 * give information about child and parent expressions,
 * and give the originating source information.  A class that implements 
 * this interface does not lay any claim to being directly executable.
 * 
 * <p>Note: This interface should not be considered stable.  Only exprSetParent 
 * and exprGetParent can be counted on to work reliably.  Work in progress.</p>
 */
public interface ExpressionNode extends SourceLocator
{
  /** @param n ExpressionNode to be set as parent of this one
    */
  public void exprSetParent(ExpressionNode n);
  /** @return ExpressionNode currently set as parent of this one
    */
  public ExpressionNode exprGetParent();

  /** This method tells the node to add its argument to the node's
    list of children.  
    @param n ExpressionNode to be added to the list of children
    @param i Insertion offset within that list
  */
  public void exprAddChild(ExpressionNode n, int i);

  /** This method returns a child node.  The children are numbered
     from zero, left to right. 
     @param i Index of child to be returned
     @return ExpressionNode at that child offset, or null if none found
  */
  public ExpressionNode exprGetChild(int i);

  /** @return the number of children this ExpressionNode has. */
  public int exprGetNumChildren();
}

