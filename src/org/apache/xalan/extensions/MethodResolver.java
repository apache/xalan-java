package org.apache.xalan.extensions;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xalan.xpath.XObject;
import org.apache.xalan.xpath.XString;

/**
 * Utility class to help resolve method overloading with Xalan XSLT 
 * argument types.
 */
public class MethodResolver
{
  /**
   * Given a class, figure out the resolution of 
   * the Java Constructor from the XSLT argument types, and perform the 
   * conversion of the arguments.
   * @param classObj the Class of the object to be constructed.
   * @param argsIn An array of XSLT/XPath arguments.
   * @param argsOut An array of the exact size as argsIn, which will be 
   * populated with converted arguments if a suitable method is found.
   * @return A constructor that will work with the argsOut array.
   * @exception SAXException may be thrown for Xalan conversion
   * exceptions.
   */
  public static Constructor getConstructor(Class classObj, 
                                           Object[] argsIn, 
                                           Object[] argsOut)
    throws NoSuchMethodException,
           SecurityException,
           org.xml.sax.SAXException
  {
    Constructor bestConstructor = null;
    Class[] bestParamTypes = null;
    Constructor[] constructors = classObj.getConstructors();
    int nMethods = constructors.length;
    int bestScore = Integer.MAX_VALUE;
    for(int i = 0; i < nMethods; i++)
    {
      Constructor ctor = constructors[i];
      Class[] paramTypes = ctor.getParameterTypes();
      if(argsIn.length == paramTypes.length)
      {
        // then we have our candidate.
        int score = scoreMatch(paramTypes, argsIn);
        if(-1 == score)
          continue;
        if(score < bestScore)
        {
          bestConstructor = ctor;
          bestParamTypes = paramTypes;
          bestScore = score;
        }
      }
    }

    if(null == bestConstructor)
      throw new NoSuchMethodException(classObj.getName()); // Should give more info...
    else
      convertParams(argsIn, argsOut, bestParamTypes);
    
    return bestConstructor;
  }

  
  /**
   * Given the name of a method, figure out the resolution of 
   * the Java Method from the XSLT argument types, and perform the 
   * conversion of the arguments.
   * @param classObj The Class of the object that should have the method.
   * @param name The name of the method to be invoked.
   * @param argsIn An array of XSLT/XPath arguments.
   * @param argsOut An array of the exact size as argsIn, which will be 
   * populated with converted arguments if a suitable method is found.
   * @return A method that will work with the argsOut array.
   * @exception SAXException may be thrown for Xalan conversion
   * exceptions.
   */
  public static Method getMethod(Class classObj, String name, 
                                 Object[] argsIn, 
                                 Object[] argsOut)
    throws NoSuchMethodException,
           SecurityException,
           org.xml.sax.SAXException
  {
    Method bestMethod = null;
    Class[] bestParamTypes = null;
    Method[] methods = classObj.getMethods();
    int nMethods = methods.length;
    int bestScore = Integer.MAX_VALUE;
    for(int i = 0; i < nMethods; i++)
    {
      Method method = methods[i];
      if(method.getName().equals(name))
      {
        Class[] paramTypes = method.getParameterTypes();
        if(argsIn.length == paramTypes.length)
        {
          // then we have our candidate.
          int score = scoreMatch(paramTypes, argsIn);
          if(-1 == score)
            continue;
          if(score < bestScore)
          {
            bestMethod = method;
            bestParamTypes = paramTypes;
            bestScore = score;
          }
        }
      }
    }
    
    if(null == bestMethod)
      throw new NoSuchMethodException(name); // Should give more info...
    else
      convertParams(argsIn, argsOut, bestParamTypes);
    
    return bestMethod;
  }
  
  /**
   * Convert a set of parameters based on a set of paramTypes.
   * @param argsIn An array of XSLT/XPath arguments.
   * @param argsOut An array of the exact size as argsIn, which will be 
   * populated with converted arguments.
   * @param paramTypes An array of class objects, of the exact same 
   * size as argsIn and argsOut.
   * @exception SAXException may be thrown for Xalan conversion
   * exceptions.
   */
  public static void convertParams(Object[] argsIn, 
                     Object[] argsOut, Class[] paramTypes)
    throws org.xml.sax.SAXException
  {
    int nMethods = argsIn.length;
    for(int i = 0; i < nMethods; i++)
    {
      argsOut[i] = convert(argsIn[i], paramTypes[i]);
    }
  }
  
  /**
   * Simple class to hold information about allowed conversions 
   * and their relative scores, for use by the table below.
   */
  static class ConversionInfo
  {
    ConversionInfo(Class cl, int score)
    {
      m_class = cl;
      m_score = score;
    }
    
    Class m_class;  // Java class to convert to.
    int m_score; // Match score, closer to zero is more matched.
  }
  
  /**
   * Specification of conversions from XSLT type CLASS_UNKNOWN
   * (i.e. some unknown Java object) to allowed Java types.
   */
  static ConversionInfo[] m_javaObjConversions = {
    new ConversionInfo(java.lang.Object.class, 0),
    new ConversionInfo(Double.TYPE, 1),
    new ConversionInfo(Float.TYPE, 2),
    new ConversionInfo(Long.TYPE, 3),
    new ConversionInfo(Integer.TYPE, 4),
    new ConversionInfo(Short.TYPE, 5),
    new ConversionInfo(Character.TYPE, 6),
    new ConversionInfo(Byte.TYPE, 7),
    new ConversionInfo(java.lang.String.class, 8)
  };
    
  /**
   * Specification of conversions from XSLT type CLASS_BOOLEAN
   * to allowed Java types.
   */
  static ConversionInfo[] m_booleanConversions = {
    new ConversionInfo(Boolean.TYPE, 0),
    new ConversionInfo(java.lang.Boolean.class, 1),
    new ConversionInfo(java.lang.Object.class, 1),
    new ConversionInfo(java.lang.String.class, 2)
    };

  /**
   * Specification of conversions from XSLT type CLASS_NUMBER
   * to allowed Java types.
   */
  static ConversionInfo[] m_numberConversions = {
    new ConversionInfo(Double.TYPE, 0),
    new ConversionInfo(java.lang.Double.class, 1),
    new ConversionInfo(Float.TYPE, 3),
    new ConversionInfo(Long.TYPE, 4),
    new ConversionInfo(Integer.TYPE, 5),
    new ConversionInfo(Short.TYPE, 6),
    new ConversionInfo(Character.TYPE, 7),
    new ConversionInfo(Byte.TYPE, 8),
    new ConversionInfo(java.lang.String.class, 9),
    new ConversionInfo(java.lang.Object.class, 10)
  };

  /**
   * Specification of conversions from XSLT type CLASS_STRING
   * to allowed Java types.
   */
  static ConversionInfo[] m_stringConversions = {
    new ConversionInfo(java.lang.String.class, 0),
    new ConversionInfo(java.lang.Object.class, 1),
    new ConversionInfo(Character.TYPE, 2),
    new ConversionInfo(Double.TYPE, 3),
    new ConversionInfo(Float.TYPE, 3),
    new ConversionInfo(Long.TYPE, 3),
    new ConversionInfo(Integer.TYPE, 3),
    new ConversionInfo(Short.TYPE, 3),
    new ConversionInfo(Byte.TYPE, 3)
  };

  /**
   * Specification of conversions from XSLT type CLASS_RTREEFRAG
   * to allowed Java types.
   */
  static ConversionInfo[] m_rtfConversions = {
    new ConversionInfo(org.w3c.dom.traversal.NodeIterator.class, 0),
    new ConversionInfo(org.w3c.dom.Node.class, 1),
    new ConversionInfo(java.lang.String.class, 2),
    new ConversionInfo(Boolean.TYPE, 3),
    new ConversionInfo(java.lang.Object.class, 4),
    new ConversionInfo(Character.TYPE, 5),
    new ConversionInfo(Double.TYPE, 6),
    new ConversionInfo(Float.TYPE, 6),
    new ConversionInfo(Long.TYPE, 6),
    new ConversionInfo(Integer.TYPE, 6),
    new ConversionInfo(Short.TYPE, 6),
    new ConversionInfo(Byte.TYPE, 6)
  };
  
  /**
   * Specification of conversions from XSLT type CLASS_NODESET
   * to allowed Java types.  (This is the same as for CLASS_RTREEFRAG)
   */
  static ConversionInfo[] m_nodesetConversions = {
    new ConversionInfo(org.w3c.dom.traversal.NodeIterator.class, 0),
    new ConversionInfo(org.w3c.dom.Node.class, 1),
    new ConversionInfo(java.lang.String.class, 2),
    new ConversionInfo(Boolean.TYPE, 3),
    new ConversionInfo(java.lang.Object.class, 4),
    new ConversionInfo(Character.TYPE, 5),
    new ConversionInfo(Double.TYPE, 6),
    new ConversionInfo(Float.TYPE, 6),
    new ConversionInfo(Long.TYPE, 6),
    new ConversionInfo(Integer.TYPE, 6),
    new ConversionInfo(Short.TYPE, 6),
    new ConversionInfo(Byte.TYPE, 6)
  };
  
  /**
   * Order is significant in the list below, based on 
   * XObject.CLASS_XXX values.
   */
  static ConversionInfo[][] m_conversions = 
  {
    m_javaObjConversions, // CLASS_UNKNOWN = 0;
    m_booleanConversions, // CLASS_BOOLEAN = 1;
    m_numberConversions,  // CLASS_NUMBER = 2;
    m_stringConversions,  // CLASS_STRING = 3;
    m_nodesetConversions, // CLASS_NODESET = 4;
    m_rtfConversions      // CLASS_RTREEFRAG = 5;
  };
  
  /**
   * Score the conversion of a set of XSLT arguments to a 
   * given set of Java parameters.
   * If any invocations of this function for a method with 
   * the same name return the same positive value, then a conflict 
   * has occured, and an error should be signaled.
   * @param javeParamTypes Must be filled with valid class names, and 
   * of the same length as xsltArgs.
   * @param xsltArgs Must be filled with valid object instances, and 
   * of the same length as javeParamTypes.
   * @return -1 for no allowed conversion, or a positive score 
   * that is closer to zero for more preferred, or further from 
   * zero for less preferred.
   */
  public static int scoreMatch(Class[] javeParamTypes, 
                               Object[] xsltArgs)
  {
    int nParams = xsltArgs.length;
    int score = 0;
    for(int i = 0; i < nParams; i++)
    {
      Object xsltObj = xsltArgs[i];
      int xsltClassType = (xsltObj instanceof XObject) 
                          ? ((XObject)xsltObj).getType() 
                            : XObject.CLASS_UNKNOWN;
      Class javaClass = javeParamTypes[i];
      
      if(xsltClassType == XObject.CLASS_NULL)
      {
        // In Xalan I have objects of CLASS_NULL, though I'm not 
        // sure they're used any more.  For now, do something funky.
        if(!javaClass.isPrimitive())
        {
          // Then assume that a null can be used, but give it a low score.
          score += 10;
          continue;
        }
        else
          return -1;  // no match.
      }
      
      ConversionInfo[] convInfo = m_conversions[xsltClassType];
      int nConversions = convInfo.length;
      int k;
      for(k = 0; k < nConversions; k++)
      {
        ConversionInfo cinfo = convInfo[k];
        if(javaClass.isAssignableFrom(cinfo.m_class))
        {
          score += cinfo.m_score;
          break; // from k loop
        }
      }
      if(k == nConversions)
        return -1; // no match
    }
    return score;
  }
  
  /**
   * Convert the given XSLT object to an object of 
   * the given class.
   * @param xsltObj The XSLT object that needs conversion.
   * @param javaClass The type of object to convert to.
   * @returns An object suitable for passing to the Method.invoke 
   * function in the args array, which may be null in some cases.
   * @exception SAXException may be thrown for Xalan conversion
   * exceptions.
   */
  static Object convert(Object xsltObj, Class javaClass)
    throws org.xml.sax.SAXException
  {
    if(xsltObj instanceof XObject)
    {
      XObject xobj = ((XObject)xsltObj);
      int xsltClassType = xobj.getType();

      switch(xsltClassType)
      {
      case XObject.CLASS_NULL:
        return null;
        
      case XObject.CLASS_BOOLEAN:
        {
          if(javaClass == java.lang.String.class)
            return xobj.str();
          else
            return new Boolean(xobj.bool());
        }
        // break; Unreachable
      case XObject.CLASS_NUMBER:
        {
          if(javaClass == java.lang.String.class)
            return xobj.str();
          else 
          {
            return convertDoubleToNumber(xobj.num(), javaClass);
          }
        }
        // break; Unreachable
        
      case XObject.CLASS_STRING:
        {
          if((javaClass == java.lang.String.class) ||
             (javaClass == java.lang.Object.class))
            return xobj.str();
          else if(javaClass == Character.TYPE)
          {
            String str = xobj.str();
            if(str.length() > 0)
              return new Character(str.charAt(0));
            else
              return null; // ??
          }
          else 
          {
            return convertDoubleToNumber(xobj.num(), javaClass);
          }
        }
        // break; Unreachable
        
      case XObject.CLASS_RTREEFRAG:
        {
          if((javaClass.isAssignableFrom(NodeIterator.class)) ||
             (javaClass == java.lang.Object.class))
          {
            // This will fail in Xalan right now, since RTFs aren't 
            // convertable to node-sets.
            return xobj.nodeset();
          }
          else if(javaClass.isAssignableFrom(Node.class))
          {
            // This will return a Document fragment in Xalan right 
            // now, which isn't what the we specify.
            return xobj.rtree();
          }
          else if(javaClass == java.lang.String.class)
          {
            return xobj.str();
          }
          else if(javaClass == Boolean.TYPE)
          {
            return new Boolean(xobj.bool());
          }
          else
          {
            return convertDoubleToNumber(xobj.num(), javaClass);
          }
        }
        // break; Unreachable
        
      case XObject.CLASS_NODESET:
        {
          if((javaClass.isAssignableFrom(NodeIterator.class)) ||
             (javaClass == java.lang.Object.class))
          {
            // This will fail in Xalan right now, since RTFs aren't 
            // convertable to node-sets.
            return xobj.nodeset();
          }
          else if(javaClass.isAssignableFrom(Node.class))
          {
            // Xalan ensures that nodeset() always returns an
            // iterator positioned at the beginning.
            NodeIterator ni = xobj.nodeset();
            return ni.nextNode(); // may be null.
          }
          else if(javaClass == java.lang.String.class)
          {
            return xobj.str();
          }
          else if(javaClass == Boolean.TYPE)
          {
            return new Boolean(xobj.bool());
          }
          else
          {
            return convertDoubleToNumber(xobj.num(), javaClass);
          }
        }
        // break; Unreachable
      
        // No default:, fall-through on purpose
      } // end switch
      xsltObj = xobj.object();
      
    } // end if if(xsltObj instanceof XObject)
    
    // At this point, we have a raw java object.
    if(javaClass == java.lang.String.class)
    {
      return xsltObj.toString();
    }
    else if(javaClass.isPrimitive())
    {
      // Assume a number conversion
      XString xstr = new XString(xsltObj.toString());
      double num = xstr.num();
      return convertDoubleToNumber(num, javaClass);
    }
    else
    {
      // Just pass the object directly, and hope for the best.
      return xsltObj;
    }
  }
  
  /**
   * Do a standard conversion of a double to the specified type.
   * @param num The number to be converted.
   * @param javaClass The class type to be converted to.
   * @return An object specified by javaClass, or a Double instance.
   */
  static Object convertDoubleToNumber(double num, Class javaClass)
  {
    // In the code below, I don't check for NaN, etc., instead 
    // using the standard Java conversion, as I think we should 
    // specify.  See issue-runtime-errors.
    if((javaClass == Double.TYPE) ||
       (javaClass == java.lang.Double.class))
      return new Double(num);
    else if(javaClass == Float.TYPE)
      return new Float(num);
    else if(javaClass == Long.TYPE)
    {
      // Use standard Java Narrowing Primitive Conversion
      // See http://java.sun.com/docs/books/jls/html/5.doc.html#175672
      return new Long((long)num);
    }
    else if(javaClass == Integer.TYPE)
    {
      // Use standard Java Narrowing Primitive Conversion
      // See http://java.sun.com/docs/books/jls/html/5.doc.html#175672
      return new Integer((int)num);
    }
    else if(javaClass == Short.TYPE)
    {
      // Use standard Java Narrowing Primitive Conversion
      // See http://java.sun.com/docs/books/jls/html/5.doc.html#175672
      return new Short((short)num);
    }
    else if(javaClass == Character.TYPE)
    {
      // Use standard Java Narrowing Primitive Conversion
      // See http://java.sun.com/docs/books/jls/html/5.doc.html#175672
      return new Character((char)num);
    }
    else if(javaClass == Byte.TYPE)
    {
      // Use standard Java Narrowing Primitive Conversion
      // See http://java.sun.com/docs/books/jls/html/5.doc.html#175672
      return new Byte((byte)num);
    }
    else
    {
      // Should never get here??
      return new Double(num);
    }
  }

}
