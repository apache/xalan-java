package org.apache.xalan.xsltc.compiler.codemodel;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Gopal Sharma
 * @version 1.0
 */

public class CmVoidType extends CmType {

  public static CmVoidType instance = new CmVoidType();

  public CmVoidType() {
  }

  public Object accept(CmVisitor visitor, Object data) {
        return visitor.visit(this, data);
  }
}