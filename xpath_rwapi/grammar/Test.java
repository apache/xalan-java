// ONLY EDIT THIS FILE IN THE GRAMMAR ROOT DIRECTORY!
// THE ONE IN THE ${spec}-src DIRECTORY IS A COPY!!!

import java.io.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Test {

  public Test() {
  }
  public static void main(String[] args)
  {
      try
      {
        final boolean dumpTree = (args.length == 2 && args[1].equals("-dump"))?
                             true : false;
        
        /*
        String filename = null;
        boolean dumpTree = false;
        String expression = null;
        boolean isQueryFile = false;
        for(int i = 0; i < args.length; i++)
				{
						if(args[i].equals("-dump"))
						{
								dumpTree = true;
						}
						else if(args[i].equals("-f"))
						{
								i++;
								filename = args[i];
						}
            else if(args[i].endsWith(".xquery"))
						{
								filename = args[i];
                isQueryFile = true;
						}
				}
        */

        if(args[0].endsWith(".xquery"))
        {
          System.out.println("Running test for: "+args[0]);
          File file = new File(args[0]);
          FileInputStream fis = new FileInputStream(file);
          XPath parser = new XPath(fis);
          SimpleNode tree = parser.XPath2();
          if(dumpTree)
            tree.dump("|") ;
        }
        else
        {
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.parse(args[0]);
          Element tests = doc.getDocumentElement();
          NodeList testElems = tests.getChildNodes();
          int nChildren = testElems.getLength();
          int testid=0;
          for (int i = 0; i < nChildren; i++) {
            org.w3c.dom.Node node = testElems.item(i);
            if(org.w3c.dom.Node.ELEMENT_NODE == node.getNodeType())
            {
              testid++;
              String xpathString = ((Element)node).getAttribute("value");
              if(dumpTree)
									System.err.println("Test["+ testid+"]: "+xpathString);
              XPath parser = new XPath(new java.io.StringBufferInputStream(xpathString));
              SimpleNode tree = parser.XPath2();
              if(dumpTree)
                tree.dump("|") ;
            }
          }
        }
        System.out.println("Test successful!!!");
      }
      catch(Exception e)
      {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
  }

}
