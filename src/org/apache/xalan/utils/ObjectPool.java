package org.apache.xalan.utils;

import java.util.*;

public class ObjectPool implements java.io.Serializable
{
  private final Class objectType;
  private final Vector freeStack;

  public ObjectPool(Class type) {
    objectType = type;
    freeStack = new Vector();
  }

  public ObjectPool(Class type, int size) {
    objectType = type;
    freeStack = new Vector(size);
  }
  
  public ObjectPool() 
  {
    objectType = null;
    freeStack = new Vector();
  }
  
  public synchronized Object getInstanceIfFree() 
  {
    // Check if the pool is empty.
    if (!freeStack.isEmpty()) 
    {
      // Remove object from end of free pool.
      Object result = freeStack.lastElement();
      freeStack.setSize(freeStack.size() - 1);
      return result;
    }
    return null;
  }
  
  public synchronized Object getInstance() 
  {

    // Check if the pool is empty.
    if (freeStack.isEmpty()) {

      // Create a new object if so.
      try {
        return objectType.newInstance();
      } catch (InstantiationException ex) {}
        catch (IllegalAccessException ex) {}

      // Throw unchecked exception for error in pool configuration.
      throw new RuntimeException("exception creating new instance for pool");

    } else {

      // Remove object from end of free pool.
      Object result = freeStack.lastElement();
      freeStack.setSize(freeStack.size() - 1);
      return result;
    }
  }

  public synchronized void freeInstance(Object obj) 
  {
    // Make sure the object is of the correct type.
    if (objectType.isInstance(obj)) {
      freeStack.addElement(obj);
    } else {
      throw new IllegalArgumentException("argument type invalid for pool");
    }
  }
}


