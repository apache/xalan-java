package org.apache.xml.dtm.dom2dtm;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.StringReader;

import org.apache.xml.dtm.*;

/**
 * Simple unit test for DOM2DTM.
 */
public class UnitTest
{

  public UnitTest()
  {
  }
  
  // No namespaces, only elements & attributes
  static String s_doc1String1 = 
              "<?xml version=\"1.0\"?>" +
              "<far-north>" +
              " <north>" +
              "  <near-north>" +
              "   <far-west/>" +
              "   <west/>" +
              "   <near-west near-west-attr1='near-west-attr1-value'/>" +
              "   <center>" +
              "    <near-south>" +
              "     <south south-attr1='south-attr1-value' south-attr2='south-attr2-value'>" +
              "      <far-south/>" +
              "     </south>" +
              "    </near-south>" +
              "   </center>" +
              "   <near-east/>" +
              "   <east/>" +
              "   <far-east/>" +
              "  </near-north>" +
              " </north>" +
              "</far-north>";

  // With namespaces
  static String s_doc1String2 = 
              "<?xml version=\"1.0\"?>" +
              "<far-north>" +
              " <north xmlns:x='http://x.com'>" +
              "  <near-north>" +
              "   <far-west xmlns:y='http://y.com'/>" +
              "   <west/>" +
              "   <near-west near-west-attr1='near-west-attr1-value'/>" +
              "   <center xmlns:y='http://y.com'>" +
              "    <near-south xmlns:z='http://z.com' xmlns:v='http://v.com'>" +
              "     <south south-attr1='south-attr1-value' south-attr2='south-attr2-value'>" +
              "      <far-south/>" +
              "     </south>" +
              "    </near-south>" +
              "   </center>" +
              "   <near-east/>" +
              "   <east/>" +
              "   <far-east/>" +
              "  </near-north>" +
              " </north>" +
              "</far-north>";
              
    // Reproduces bug where things don't get processed after the last attribute.
  static String s_doc1String2a = 
              "<?xml version=\"1.0\"?>" +
              "<far-north>" +
                "<center>" +
                  "<near-south a='a' b='b'>" +
                    "<south c='c' d='d'/>" +
                  "</near-south>" +
                "</center>" +
                "<near-east/>" +
              "</far-north>";
                
  // namespaces and text
  static String s_doc1String3x = 
              "<?xml version=\"1.0\"?>" +
              "<far-north>a" +
              " <north xmlns:x='http://x.com'>b" +
              "  <near-north>c" +
              "   <far-west xmlns:y='http://y.com'>d</far-west>" +
              "   <west>e</west>" +
              "   <near-west near-west-attr1='near-west-attr1-value'>f</near-west>" +
              "   <center xmlns:y='http://y.com'>g" +
              "    <near-south xmlns:z='http://z.com' xmlns:v='http://v.com'>h" +
              "     <south south-attr1='south-attr1-value' south-attr2='south-attr2-value'>i" +
              "      <far-south>j</far-south>k" +
              "     </south>l" +
              "    </near-south>m" +
              "   </center>n" +
              "   <near-east>o</near-east>" +
              "   <east>p</east>" +
              "   <far-east>q</far-east>r" +
              "  </near-north>s" +
              " </north>t" +
              "</far-north>";
              
    static String s_doc1String3 = 
              "<?xml version=\"1.0\"?>" +
              "<docs>" +
              "  <doc xmlns:ext=\"http://somebody.elses.extension\">" +
              "    <section xmlns:foo=\"http://foo.com\">" +
              "      <inner xmlns:whiz=\"http://whiz.com/special/page\"/>" +
              "    </section>" +
              "  </doc>" +
              "</docs>";
  
  protected int run(String[] args)
    throws Exception
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    StringReader sr = new StringReader(s_doc1String3);
    
    Document doc = db.parse(new InputSource(sr));
    
    DTMManager dtmMgr = DTMManager.newInstance(
                 org.apache.xpath.objects.XMLStringFactoryImpl.getFactory());
    DTM dtm = dtmMgr.getDTM(new DOMSource(doc), true, null, true);
    
    int docHandle = dtm.getDocument();
    outputChildren(docHandle, dtm, 0);

//    TransformerFactory tf = TransformerFactory.newInstance();
//    Transformer t = tf.newTransformer();
//    t.setOutputProperty(OutputKeys.INDENT, "yes");
//    t.transform(new DOMSource(doc), new StreamResult(System.out));
    
    System.out.println("DOM2DTM Unit test done!");
    
    return 0;
  }
  
  protected void outputChildren(int handle, DTM dtm, int indentAmount)
  {
    for (handle = dtm.getFirstChild(handle); handle != DTM.NULL; 
         handle = dtm.getNextSibling(handle)) 
    {
      for (int i = 0; i < indentAmount; i++) 
      {
        System.out.print(' ');
      }
      
      System.out.print("node name: "+dtm.getNodeName(handle));
      System.out.println(", val: "+dtm.getStringValue(handle));
      
      for (int ns = dtm.getFirstNamespaceNode(handle, true); ns != DTM.NULL; 
           ns = dtm.getNextNamespaceNode(handle, ns, true)) 
      {
        System.out.print("ns decl: "+dtm.getNodeName(ns));
        System.out.println(", val: "+dtm.getStringValue(ns));
      }
      for (int attr = dtm.getFirstAttribute(handle); attr != DTM.NULL; 
           attr = dtm.getNextAttribute(attr)) 
      {
        System.out.print("attr: "+dtm.getNodeName(attr));
        System.out.println(", val: "+dtm.getStringValue(attr));
      }
      
      outputChildren(handle, dtm, indentAmount+1);
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    UnitTest unitTest1 = new UnitTest();
    unitTest1.run(args);
  }
}