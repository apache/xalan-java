package org.apache.xpath.patterns;

/**
 * This interface should be implemented by Nodes and/or iterators, 
 * when they need to know what the node test is before get they do 
 * getNextChild, etc.
 */
public interface NodeTestFilter
{ 
  void setNodeTest(NodeTest nodeTest);
}
