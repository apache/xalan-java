package org.apache.xalan.xsltc.compiler.codemodel;

import java.util.List;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Gopal Sharma
 * @version 1.0
 */

public class CmSwitchStmt extends CmStatement {

  // Conditional expression for this switch
  private CmExpression _condition;

  // List of LabeledStmt
  private List _labelCase;

  // body of Switch
  private CmStatement _body;

  public CmSwitchStmt(CmExpression condition) {
    this(condition,CmEmptyStmt.instance);
  }

  public CmSwitchStmt(CmExpression condition, CmStatement body) {
    _condition = condition;
    _body = body;
  }

  public CmExpression getCondition() {
       return _condition;
  }

   public CmStatement getBody() {
       return _body;
   }

   public Object accept(CmVisitor visitor, Object data) {
       return visitor.visit(this, data);
   }

   public Object childrenAccept(CmVisitor visitor, Object data) {
       data = _condition.accept(visitor, data);
       return _body.accept(visitor, data);
   }
}