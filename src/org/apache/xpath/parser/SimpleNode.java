package org.apache.xpath.parser;

import java.util.Hashtable;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.axes.UnionPathIterator;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.functions.*;
import org.apache.xpath.objects.XDecimal;
import org.apache.xpath.objects.XDouble;
import org.apache.xpath.objects.XInteger;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.FunctionPattern;
import org.apache.xpath.patterns.StepPattern;
import org.apache.xpath.seqctor.ExprSequence;
import org.apache.xpath.seqctor.FLWRExpr;
import org.apache.xpath.types.InstanceofExpr;

/**
 * This is the most generic syntax node, which implements the JJTree 
 * interface "Node" methods.  This class assumes that it's derived 
 * class is org.apache.xpath.Expression, and so does a fair amount 
 * of downcasting.  The only reason this class exists is to separate 
 * out the jjXXX methods, to act as a more generic interface to 
 * the JavaCC/JJTree parser, and also to act as a factory class for 
 * the concrete implementations of the expressions.
 * 
 * @author sboag
 */
public class SimpleNode implements Node
{

  /**
   * Construct a new SimpleNode.
   * @see java.lang.Object#Object()
   */
  public SimpleNode()
  {
  }

  public static Hashtable m_builtInFunctions;

  static {
    // For user installed functions, can we really have a single static table?
    // Answer: no, each parser instance will need to copy the table.
    // Then the question is, can all instances of a user Function be shared among 
    // processes?  Answer: I think that would cause regressions.
    m_builtInFunctions = new Hashtable();
    m_builtInFunctions.put(new QName("current"), new FuncCurrent());
    m_builtInFunctions.put(
      new QName("last"),
      new org.apache.xpath.functions.FuncLast());
    m_builtInFunctions.put(
      new QName("position"),
      new org.apache.xpath.functions.FuncPosition());
    m_builtInFunctions.put(new QName("count"), new FuncCount());
    m_builtInFunctions.put(new QName("id"), new FuncId());
    m_builtInFunctions.put(
      new QName("key"),
      new org.apache.xalan.templates.FuncKey());
    m_builtInFunctions.put(new QName("local-name"), new FuncLocalPart());
    m_builtInFunctions.put(new QName("namespace-uri"), new FuncNamespace());
    m_builtInFunctions.put(new QName("name"), new FuncQname());
    m_builtInFunctions.put(new QName("generate-id"), new FuncGenerateId());
    m_builtInFunctions.put(new QName("not"), new FuncNot());
    m_builtInFunctions.put(new QName("true"), new FuncTrue());
    m_builtInFunctions.put(new QName("false"), new FuncFalse());
    m_builtInFunctions.put(new QName("boolean"), new FuncBoolean());
    m_builtInFunctions.put(new QName("lang"), new FuncLang());
    m_builtInFunctions.put(new QName("number"), new FuncNumber());
    m_builtInFunctions.put(new QName("floor"), new FuncFloor());
    m_builtInFunctions.put(new QName("ceiling"), new FuncCeiling());
    m_builtInFunctions.put(new QName("round"), new FuncRound());
    m_builtInFunctions.put(new QName("sum"), new FuncSum());
    m_builtInFunctions.put(new QName("string"), new FuncString());
    m_builtInFunctions.put(new QName("starts-with"), new FuncStartsWith());
    m_builtInFunctions.put(new QName("contains"), new FuncContains());
    m_builtInFunctions.put(
      new QName("substring-before"),
      new FuncSubstringBefore());
    m_builtInFunctions.put(
      new QName("substring-after"),
      new FuncSubstringAfter());
    m_builtInFunctions.put(
      new QName("normalize-space"),
      new FuncNormalizeSpace());
    m_builtInFunctions.put(new QName("translate"), new FuncTranslate());
    m_builtInFunctions.put(new QName("concat"), new FuncConcat());
    m_builtInFunctions.put(
      new QName("system-property"),
      new FuncSystemProperty());
    m_builtInFunctions.put(
      new QName("function-available"),
      new FuncExtFunctionAvailable());
    m_builtInFunctions.put(
      new QName("element-available"),
      new FuncExtElementAvailable());
    m_builtInFunctions.put(new QName("substring"), new FuncSubstring());
    m_builtInFunctions.put(new QName("string-length"), new FuncStringLength());
    m_builtInFunctions.put(
      new QName("unparsed-entity-uri"),
      new FuncUnparsedEntityURI());
    m_builtInFunctions.put(
      new QName("document-location"),
      new FuncDoclocation());
    // Proprietary
	// XPATH2 experimental
    m_builtInFunctions.put(
      new QName("data"),
      new FuncData());    
    m_builtInFunctions.put(
      new QName("current-dateTime"),
      new FuncCurrentDateTime());
    m_builtInFunctions.put(
      new QName("current-date"),
      new FuncCurrentDate());
    m_builtInFunctions.put(
      new QName("dateTime"),
      new FuncDateTime());
    m_builtInFunctions.put(
      new QName("date"),
      new FuncDate());
    m_builtInFunctions.put(
      new QName("duration"),
      new FuncDuration());
    m_builtInFunctions.put(
      new QName("time"),
      new FuncTime());
      m_builtInFunctions.put(
      new QName("yearMonthDuration"),
      new FuncYMDuration());
      m_builtInFunctions.put(
      new QName("yearMonthDuration-from-months"),
      new FuncYMDurationFromMonths());
      m_builtInFunctions.put(
      new QName("dayTimeDuration"),
      new FuncDTDuration());
      m_builtInFunctions.put(
      new QName("dayTimeDuration-from-seconds"),
      new FuncDTDurationFromSecs());
      m_builtInFunctions.put(
      new QName("add-dayTimeDuration"),
      new FuncAddDayTimeDuration());
      m_builtInFunctions.put(
      new QName("subtract-dayTimeDuration"),
      new FuncSubDayTimeDuration());
      m_builtInFunctions.put(
      new QName("multiply-dayTimeDuration"),
      new FuncMultDayTimeDuration());
      m_builtInFunctions.put(
      new QName("divide-dayTimeDuration"),
      new FuncDivDayTimeDuration());
      m_builtInFunctions.put(
      new QName("add-yearMonthDuration"),
      new FuncAddYearMonthDuration()); 
      m_builtInFunctions.put(
      new QName("subtract-yearMonthDuration"),
      new FuncSubYearMonthDuration());
      m_builtInFunctions.put(
      new QName("multiply-yearMonthDuration"),
      new FuncMultYearMonthDuration());
      m_builtInFunctions.put(
      new QName("divide-yearMonthDuration"),
      new FuncDivYearMonthDuration());
      m_builtInFunctions.put(
      new QName("get-yearMonthDuration"),
      new FuncGetYMDuration());
      m_builtInFunctions.put(
      new QName("get-dayTimeDuration"),
      new FuncGetDTDuration());
      m_builtInFunctions.put(
      new QName("add-yearMonthDurationToDateTime"),
      new FuncAddYMDurationToDT());
      m_builtInFunctions.put(
      new QName("subtract-yearMonthDurationFromDateTime"),
      new FuncSubtractYMDurationFromDT());
      m_builtInFunctions.put(
      new QName("add-yearMonthDurationToDate"),
      new FuncAddYMDurationToDate());
      m_builtInFunctions.put(
      new QName("subtract-yearMonthDurationFromDate"),
      new FuncSubtractYMDurationFromDate());
      m_builtInFunctions.put(
      new QName("add-dayTimeDurationToDateTime"),
      new FuncAddDTDurationToDT());
      m_builtInFunctions.put(
      new QName("subtract-dayTimeDurationFromDateTime"),
      new FuncSubtractDTDurationFromDT());
      m_builtInFunctions.put(
      new QName("add-dayTimeDurationToDate"),
      new FuncAddDTDurationToDate());
      m_builtInFunctions.put(
      new QName("add-dayTimeDurationToTime"),
      new FuncAddDTDurationToTime());
      m_builtInFunctions.put(
      new QName("subtract-dayTimeDurationFromDate"),
      new FuncSubtractDTDurationFromDate());
      m_builtInFunctions.put(
      new QName("subtract-dayTimeDurationFromTime"),
      new FuncSubtractDTDurationFromTime());
      m_builtInFunctions.put(
      new QName("add-days"),
      new FuncAddDays());
      m_builtInFunctions.put(
      new QName("gYearMonth"),
      new FuncGYearMonth());
      m_builtInFunctions.put(
      new QName("gYear"),
      new FuncGYear());
      m_builtInFunctions.put(
      new QName("gMonth"),
      new FuncGMonth());
      m_builtInFunctions.put(
      new QName("gMonthDay"),
      new FuncGMonthDay());
      m_builtInFunctions.put(
      new QName("gDay"),
      new FuncGDay());
m_builtInFunctions.put(
      new QName("get-day-from-date"),
      new FuncGetDFromDate());
m_builtInFunctions.put(
      new QName("get-day-from-dateTime"),
      new FuncGetDFromDT());
m_builtInFunctions.put(
      new QName("get-days-from-dayTimeDuration"),
      new FuncGetDFromDTDuration());
m_builtInFunctions.put(
      new QName("get-hours-from-dateTime"),
      new FuncGetHFromDT());
m_builtInFunctions.put(
      new QName("get-hours-from-dayTimeDuration"),
      new FuncGetHFromDTDuration());
m_builtInFunctions.put(
      new QName("get-hours-from-time"),
      new FuncGetHFromTime());
m_builtInFunctions.put(
      new QName("get-month-from-date"),
      new FuncGetMFromDate());
m_builtInFunctions.put(
      new QName("get-month-from-dateTime"),
      new FuncGetMFromDT());
m_builtInFunctions.put(
      new QName("get-months-from-yearMonthDuration"),
      new FuncGetMFromYMDuration());
m_builtInFunctions.put(
      new QName("get-minutes-from-dateTime"),
      new FuncGetMnFromDT());
m_builtInFunctions.put(
      new QName("get-minutes-from-dayTimeDuration"),
      new FuncGetMnFromDTDuration());
m_builtInFunctions.put(
      new QName("get-minutes-from-time"),
      new FuncGetMnFromTime());
m_builtInFunctions.put(
      new QName("get-seconds-from-dateTime"),
      new FuncGetSFromDT());
m_builtInFunctions.put(
      new QName("get-seconds-from-dayTimeDuration"),
      new FuncGetSFromDTDuration());
m_builtInFunctions.put(
      new QName("get-seconds-from-time"),
      new FuncGetSFromTime());
m_builtInFunctions.put(
      new QName("get-timezone-from-datetime"),
      new FuncGetTZFromDT());
m_builtInFunctions.put(
      new QName("get-timezone-from-date"),
      new FuncGetTZFromDate());
m_builtInFunctions.put(
      new QName("get-timezone-from-time"),
      new FuncGetTZFromTime());
m_builtInFunctions.put(
      new QName("get-year-from-date"),
      new FuncGetYFromDate());
m_builtInFunctions.put(
      new QName("get-year-from-dateTime"),
      new FuncGetYFromDT());
m_builtInFunctions.put(
      new QName("get-years-from-yearMonthDuration"),
      new FuncGetYFromYMDuration());
m_builtInFunctions.put(
      new QName("duration-equal"),
      new FuncDurationEqual());
m_builtInFunctions.put(
      new QName("yearMonthDuration-equal"),
      new FuncYMDurationEqual());
m_builtInFunctions.put(
      new QName("yearMonthDuration-greater-than"),
      new FuncYMDurationGT());
m_builtInFunctions.put(
      new QName("yearMonthDuration-less-than"),
      new FuncYMDurationLT());
m_builtInFunctions.put(
      new QName("dayTimeDuration-equal"),
      new FuncDTDurationEqual());
m_builtInFunctions.put(
      new QName("dayTimeDuration-greater-than"),
      new FuncDTDurationGT());
m_builtInFunctions.put(
      new QName("dayTimeDuration-less-than"),
      new FuncDTDurationLT());
m_builtInFunctions.put(
      new QName("dateTime-equal"),
      new FuncDateTimeEq());
m_builtInFunctions.put(
      new QName("dateTime-less-than"),
      new FuncDateTimeLT());
m_builtInFunctions.put(
      new QName("dateTime-greater-than"),
      new FuncDateTimeGT());
m_builtInFunctions.put(
      new QName("node-kind"),
      new FuncNodeKind());
m_builtInFunctions.put(
      new QName("node-name"),
      new FuncNodeName());
m_builtInFunctions.put(
      new QName("base-uri"),
      new FuncBaseURI());
m_builtInFunctions.put(
      new QName("unique-ID"),
      new FuncUniqueID());
m_builtInFunctions.put(
      new QName("compare"),
      new FuncCompare());
m_builtInFunctions.put(
      new QName("matches"),
      new FuncMatches());
m_builtInFunctions.put(
      new QName("replace"),
      new FuncReplace());
m_builtInFunctions.put(
      new QName("tokenize"),
      new FuncTokenize());    
  }

  /**
   * Factory method for creating parse tree nodes, which is called by 
   * the JJTree created parser.
   * @param p The JJTree created XPath parser.
   * @param id The ID of the node.
   * @return Node The new node, which may not be null.
   */
  public static Node jjtCreate(XPath p, int id)
  {
    SimpleNode newNode = null;
    // To be done:  If function on stack, assume param.
    switch (id)
    {
      case XPathTreeConstants.JJTXPATH2 :
        // When I try to use #void to cancel out this node, 
        // I get an error.  So I guess 
        // the top of the tree can't be void.  So I create a dummy
        // node.  This makes the called have to call 
        // ((SimpleNode)tree.jjtGetChild(0)) to get the real root.
        newNode = new RootOfRoot(p);
        break;

        // === MATCH EXPRESSIONS ===	
      case XPathTreeConstants.JJTMATCHPATTERN :
        // See comment above.
        // TBD: If the pattern is a MatchPattern, then RootOfRoot should 
        // rebuild the children according to the structure needed 
        // by the patterns package.
        newNode = new RootOfRootPattern(p);
        break;
      case XPathTreeConstants.JJTPATTERN :
        // This should be optimized away if there is only one pattern
        newNode = new org.apache.xpath.patterns.UnionPattern();
        break;
      case XPathTreeConstants.JJTPATHPATTERN :
        // This is a temporary node, for construction purposes only,
        // Since the patterns package constructs the StepPatterns as 
        // a linked list.
        newNode = new Pattern(p);
        break;
      case XPathTreeConstants.JJTPATTERNSTEP :
        {
          StepPattern spat = new StepPattern();
          // At this point, the PatternAxis, NodeTest, and Predicates 
          // should be already on the stack?
          newNode = spat;
        }
        break;

      case XPathTreeConstants.JJTROOT :
        {
          if (!p.m_isMatchPattern || p.m_predLevel > 0)
          {
            newNode = new StepExpr(p);
            PatternAxis patAxis =
              new PatternAxis(org.apache.xml.dtm.Axis.ROOT, p);
            patAxis.m_value = "root::"; // for diagnostics
            NodeTest nt =
              new NodeTest(
                org.apache.xml.dtm.DTMFilter.SHOW_DOCUMENT
                  | org.apache.xml.dtm.DTMFilter.SHOW_DOCUMENT_FRAGMENT,
                p);
            Predicates preds = new Predicates(p);
            newNode.jjtAddChild(patAxis, 0);
            newNode.jjtAddChild(nt, 1);
            newNode.jjtAddChild(preds, 2);
          }
          else
          {
            StepPattern spat = new StepPattern();
            spat.setWhatToShow(
              DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT);
            spat.setAxis(org.apache.xml.dtm.Axis.PARENT);
            // spat.setT(org.apache.xpath.patterns.StepPattern.PSEUDONAME_ROOT);
            newNode = spat;
          }
        }
        break;
      case XPathTreeConstants.JJTROOTDESCENDANTS :
        {
          if (!p.m_isMatchPattern || p.m_predLevel > 0)
          {
            newNode = new StepExpr(p);
            PatternAxis patAxis =
              new PatternAxis(
                org.apache.xml.dtm.Axis.DESCENDANTSORSELFFROMROOT,
                p);
            patAxis.m_value = "descendants-from-root::"; // for diagnostics
            NodeTest nt =
              new NodeTest(org.apache.xml.dtm.DTMFilter.SHOW_ALL, p);
            nt.setTotallyWild(true);
            // nt.setLocalName(org.apache.xpath.patterns.StepPattern.PSEUDONAME_ROOT);
            Predicates preds = new Predicates(p);
            newNode.jjtAddChild(patAxis, 0);
            newNode.jjtAddChild(nt, 1);
            newNode.jjtAddChild(preds, 2);
          }
          else
          {
            // see comment for JJTROOT.
            StepPattern spat = new StepPattern();
            spat.setWhatToShow(
              DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT);
            spat.setAxis(org.apache.xml.dtm.Axis.ANCESTOR);
            newNode = spat;
          }
        }
        break;

      case XPathTreeConstants.JJTSLASH :
        newNode = new SlashOrSlashSlash(false, p);
        break;
      case XPathTreeConstants.JJTSLASHSLASH :
        newNode = new SlashOrSlashSlash(true, p);
        break;

        // === NODE TESTS ===	
      case XPathTreeConstants.JJTNODETEST :
        newNode = new NodeTest(p);
        break;
      case XPathTreeConstants.JJTNAMETEST :
        newNode = new NameTest(p);
        break;
      case XPathTreeConstants.JJTQNAME :
        newNode = new org.apache.xpath.parser.QName(p);
        break;
      case XPathTreeConstants.JJTSTAR : // Wildcard
        newNode = new Star(p);
        break;
      case XPathTreeConstants.JJTNCNAMECOLONSTAR :
        newNode = new NCNameColonStar(p);
        break;
      case XPathTreeConstants.JJTSTARCOLONNCNAME :
        newNode = new StarColonNCName(p);
        break;

      case XPathTreeConstants.JJTKINDTEST :
        newNode = new KindTest(p);
        break;
      case XPathTreeConstants.JJTPROCESSINGINSTRUCTIONTEST :
        newNode = new ProcessingInstructionTest(p);
        break;
      case XPathTreeConstants.JJTCOMMENTTEST :
        newNode = new CommentTest(p);
        break;
      case XPathTreeConstants.JJTTEXTTEST :
        newNode = new TextTest(p);
        break;
      case XPathTreeConstants.JJTANYKINDTEST :
        newNode = new AnyKindTest(p);
        break;

      case XPathTreeConstants.JJTLBRACK :
        newNode = new LbrackOrRbrack(p);
        p.m_predLevel++;
        break;
      case XPathTreeConstants.JJTRBRACK :
        newNode = new LbrackOrRbrack(p);
        p.m_predLevel--;
        break;
      case XPathTreeConstants.JJTPREDICATES :
        newNode = new Predicates(p);
        break;

        // === AXES, ETC. ===	
      case XPathTreeConstants.JJTAXISDESCENDANT :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.DESCENDANT, p);
        break;
      case XPathTreeConstants.JJTAXISSELF :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.SELF, p);
        break;
      case XPathTreeConstants.JJTAXISDESCENDANTORSELF :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.DESCENDANTORSELF, p);
        break;
      case XPathTreeConstants.JJTAXISFOLLOWINGSIBLING :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.FOLLOWINGSIBLING, p);
        break;
      case XPathTreeConstants.JJTAXISFOLLOWING :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.FOLLOWING, p);
        break;
      case XPathTreeConstants.JJTAXISNAMESPACE :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.NAMESPACE, p);
        break;
      case XPathTreeConstants.JJTAXISPARENT :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.PARENT, p);
        break;
      case XPathTreeConstants.JJTAXISANCESTOR :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.ANCESTOR, p);
        break;
      case XPathTreeConstants.JJTAXISPRECEDINGSIBLING :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.PRECEDINGSIBLING, p);
        break;
      case XPathTreeConstants.JJTAXISPRECEDING :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.PRECEDING, p);
        break;
      case XPathTreeConstants.JJTAXISANCESTORORSELF :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.ANCESTORORSELF, p);
        break;
      case XPathTreeConstants.JJTAXISCHILD :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.CHILD, p);
        break;
      case XPathTreeConstants.JJTAXISATTRIBUTE :
      case XPathTreeConstants.JJTAT :
        newNode = new PatternAxis(org.apache.xml.dtm.Axis.ATTRIBUTE, p);
        break;

      case XPathTreeConstants.JJTABBREVIATEDFORWARDSTEP :
        {
          PatternAxis patAxis =
            new PatternAxis(org.apache.xml.dtm.Axis.CHILD, p);
          patAxis.m_value = "child::"; // for diagnostics
          newNode = patAxis;
        }
        break;
      case XPathTreeConstants.JJTDOT :
        {
          PatternAxis patAxis =
            new PatternAxis(org.apache.xml.dtm.Axis.SELF, p);
          patAxis.m_value = "self::"; // for diagnostics
          newNode = patAxis;
          NodeTest nt = new NodeTest(org.apache.xml.dtm.DTMFilter.SHOW_ALL, p);
          newNode.jjtAddChild(nt, 0);
        }
        break;
      case XPathTreeConstants.JJTDOTDOT :
        {
          PatternAxis patAxis =
            new PatternAxis(org.apache.xml.dtm.Axis.PARENT, p);
          patAxis.m_value = "parent::"; // for diagnostics
          newNode = patAxis;
          NodeTest nt = new NodeTest(org.apache.xml.dtm.DTMFilter.SHOW_ALL, p);
          newNode.jjtAddChild(nt, 0);
        }
        break;

        // === PATH EXPRESSIONS ===	
      case XPathTreeConstants.JJTPATHEXPR :
        newNode = new PathExpr(p);
        break;
      case XPathTreeConstants.JJTSTEPEXPR :
        newNode = new StepExpr(p);
        break;

        // === SEQUENCE UNIONS AND INTERSECTIONS ===	
      case XPathTreeConstants.JJTUNIONEXPR :
        newNode = new UnionPathIterator();
        break;
      case XPathTreeConstants.JJTINTERSECTEXCEPTEXPR :
        newNode = new NonExecutableExpression(p, "JJTUNIONEXPR");
        break;
        //			case XPathTreeConstants.JJTUNION:
        //				newNode = new SimpleNode();
        //				break;
        //			case XPathTreeConstants.JJTVBAR:
        //				newNode = new SimpleNode();
        //				break;
        //			case XPathTreeConstants.JJTRELATIVEPATHPATTERN:
        //				newNode = new SimpleNode();
        //				break;

        // === FUNCTIONS ===	
      case XPathTreeConstants.JJTIDKEYPATTERN :
        {
          FunctionPattern fpat = new FunctionPattern();
          p.m_matchFunc = fpat; // short lived.
          newNode = fpat;
        }
        break;
      case XPathTreeConstants.JJTFUNCTIONCALL :
        {
          FunctionPattern fpat = new TempFunctionHolder();
          p.m_matchFunc = fpat; // short lived.
          newNode = fpat;
        }
        break;
      case XPathTreeConstants.JJTQNAMELPAR :
        try
        {

          org.apache.xpath.parser.QName qname =
            new org.apache.xpath.parser.QName(p);
          if (null != p.m_matchFunc)
          {
            qname.processToken(p.getToken(0));
            QName funcName = qname.getQName();
            String ns = funcName.getNamespaceURI();
            if (null != ns && ns.length() > 0)
            {
              String uniqueKey =
                String.valueOf(
                  ((SourceLocator) p.m_prefixResolver).getLineNumber())
                  + String.valueOf(funcName.hashCode())
                  + String.valueOf(System.currentTimeMillis());
              Function extension =
                new FuncExtFunction(ns, funcName.getLocalPart(), uniqueKey);
              p.m_matchFunc.setFunctionExpression(extension);
              p.m_matchFunc = null;
            }
            else
            {
              Expression exp = (Expression) m_builtInFunctions.get(funcName);
              if (null != exp)
              {
                exp = (Expression) exp.getClass().newInstance();
                p.m_matchFunc.setFunctionExpression(exp);
                p.m_matchFunc = null;
              }
              else
              {
                // TBD: a proper error throw.  -sb
                throw new RuntimeException(
                  "Function not found: " + funcName + "!");
              }
            }
          }
          newNode = qname;
        }
        catch (InstantiationException iae)
        {
          throw new org.apache.xml.utils.WrappedRuntimeException(iae);
        }
        catch (IllegalAccessException iae)
        {
          throw new org.apache.xml.utils.WrappedRuntimeException(iae);
        }

        break;

        // === BINARY OPERATORS ===	

      case XPathTreeConstants.JJTOREXPR :
        newNode = new org.apache.xpath.operations.Or();
        break;
      case XPathTreeConstants.JJTANDEXPR :
        newNode = new org.apache.xpath.operations.And();
        break;
      case XPathTreeConstants.JJTCOMPARISONEXPR :
        {
          Token operator = (Token) p.binaryTokenStack.peek();
          switch (operator.kind)
          {
            case XPathConstants.Equals :
              newNode = new org.apache.xpath.operations.Equals();
              break;
            case XPathConstants.NotEquals :
              newNode = new org.apache.xpath.operations.NotEquals();
              ;
              break;
            case XPathConstants.Lt :
              newNode = new org.apache.xpath.operations.Lt();
              break;
            case XPathConstants.LtEquals :
              newNode = new org.apache.xpath.operations.Lte();
              break;
            case XPathConstants.Gt :
              newNode = new org.apache.xpath.operations.Gt();
              break;
            case XPathConstants.GtEquals :
              newNode = new org.apache.xpath.operations.Gte();
              break;
            case XPathConstants.FortranEq :
              newNode = new org.apache.xpath.operations.FortranEq();
              break;
            case XPathConstants.FortranNe :
              newNode = new org.apache.xpath.operations.FortranNe();
              break;
            case XPathConstants.FortranLt :
              newNode = new org.apache.xpath.operations.FortranLt();
              break;
            case XPathConstants.FortranLe :
              newNode = new org.apache.xpath.operations.FortranLe();
              break;
            case XPathConstants.FortranGt :
              newNode = new org.apache.xpath.operations.FortranGt();
              break;
            case XPathConstants.FortranGe :
              newNode = new org.apache.xpath.operations.FortranGe();
              break;
            case XPathConstants.Is :
              newNode = new org.apache.xpath.operations.Is();
              break;
            case XPathConstants.IsNot :
              newNode = new org.apache.xpath.operations.IsNot();
              break;
            case XPathConstants.LtLt :
              newNode = new org.apache.xpath.operations.LtLt();
              break;
            case XPathConstants.GtGt :
              newNode = new org.apache.xpath.operations.GtGt();
              break;
            case XPathConstants.Precedes :
              newNode = new org.apache.xpath.operations.Precedes();
              break;
            case XPathConstants.Follows :
              newNode = new org.apache.xpath.operations.Follows();
              break;
          }
        }
        break;
      case XPathTreeConstants.JJTRANGEEXPR :
        newNode = new org.apache.xpath.seqctor.RangeExpr();
        break;
      case XPathTreeConstants.JJTADDITIVEEXPR :
        {
          Token operator = (Token) p.binaryTokenStack.peek();
          switch (operator.kind)
          {
            case XPathConstants.Plus :
              newNode = new org.apache.xpath.operations.Add();
              break;
            case XPathConstants.Minus :
              newNode = new org.apache.xpath.operations.Subtract();
              ;
              break;
          }
        }
        break;
      case XPathTreeConstants.JJTMULTIPLICATIVEEXPR :
        {
          Token operator = (Token) p.binaryTokenStack.peek();
          switch (operator.kind)
          {
            case XPathConstants.Multiply :
              newNode = new org.apache.xpath.operations.Mult();
              break;
            case XPathConstants.Div :
              newNode = new org.apache.xpath.operations.Div();
              ;
              break;
            case XPathConstants.Idiv :
              newNode = new org.apache.xpath.operations.Idiv();
              break;
            case XPathConstants.Mod :
              newNode = new org.apache.xpath.operations.Mod();
              ;
              break;
          }
        }
        break;

        // === UNARY OPERATORS ===	
      case XPathTreeConstants.JJTUNARYEXPR :
        newNode = new UnaryExpr(p);
        break;
      case XPathTreeConstants.JJTPLUS :
        newNode = new org.apache.xpath.operations.Pos();
        break;
      case XPathTreeConstants.JJTMINUS :
        newNode = new org.apache.xpath.operations.Neg();
        break;

        // === VARIABLES ===	
      case XPathTreeConstants.JJTVARNAME :
        {
          // We'll have to check to make sure the node is 
          // really on the stack.
          Variable var = new Variable();
          Token varToken = p.getToken(0);
          String varName = varToken.image;
          QName varQName = new QName(varName, p.m_prefixResolver);
          var.setQName(varQName);
          newNode = var;
        }
        break;

        // === LITERALS ===	
      case XPathTreeConstants.JJTSTRINGLITERAL :
        newNode = new org.apache.xpath.objects.XString();
        break;
      case XPathTreeConstants.JJTINTEGERLITERAL :
        if(p.getVersion() >= 2.0)
          newNode = new XInteger();
        else
          newNode = new XDouble();
        break;
      case XPathTreeConstants.JJTDECIMALLITERAL :
        newNode = new XDecimal();
        break;
      case XPathTreeConstants.JJTDOUBLELITERAL :
        newNode = new XDouble();
        break;

        // === SEQUENCE CONSTRUCTION ===	
      case XPathTreeConstants.JJTEXPRSEQUENCE :
        newNode = new ExprSequence();
        break;

        // === QUANTIFIED EXPRESSIONS ===	
      case XPathTreeConstants.JJTQUANTIFIEDEXPR :
        newNode = new QuantifiedExpr(p);
        break;
      case XPathTreeConstants.JJTSOME :
        newNode = new org.apache.xpath.quantified.Some();
        break;
      case XPathTreeConstants.JJTEVERY :
        newNode = new org.apache.xpath.quantified.Every();
        break;
      case XPathTreeConstants.JJTSATISFIES :
        newNode = new org.apache.xpath.quantified.Satisfies();
        break;
        
      case XPathTreeConstants.JJTIN : // Both for ForClause and Quantified.
        newNode = new In(p);
        break;

        // === CONDITIONAL EXPRESSIONS ===	
      case XPathTreeConstants.JJTIFEXPR :
        newNode = new IfExpr(p);
        break;
      case XPathTreeConstants.JJTIFLPAR :
        newNode = new org.apache.xpath.conditional.If();
        break;
      case XPathTreeConstants.JJTTHEN :
        newNode = new org.apache.xpath.conditional.Then();
        break;
      case XPathTreeConstants.JJTELSE :
        newNode = new org.apache.xpath.conditional.Else();
        break;

        // === ITTERATION ===	
      case XPathTreeConstants.JJTFLWREXPR :
        newNode = new FLWRExpr();
        break;
      case XPathTreeConstants.JJTFORCLAUSE :
        newNode = new org.apache.xpath.parser.ForClause(p);
        break;
      case XPathTreeConstants.JJTRETURN :
        newNode = new org.apache.xpath.parser.Return(p);
        break;

        // === TYPE HANDLING ===	
      case XPathTreeConstants.JJTINSTANCEOFEXPR :
        newNode = new InstanceofExpr();
        break;
      case XPathTreeConstants.JJTINSTANCEOF :
        newNode = new Instanceof(p);
        break;
      case XPathTreeConstants.JJTVALIDATEEXPR :
        newNode = new NonExecutableExpression(p, "JJTVALIDATEEXPR");
        break;
      case XPathTreeConstants.JJTVALIDATE :
        newNode = new NonExecutableExpression(p, "JJTVALIDATE");
        break;
      case XPathTreeConstants.JJTLBRACE :
        newNode = new NonExecutableExpression(p, "JJTLBRACE");
        break;
      case XPathTreeConstants.JJTRBRACE :
        newNode = new NonExecutableExpression(p, "JJTRBRACE");
        break;
      case XPathTreeConstants.JJTCASTEXPR :
        newNode = new NonExecutableExpression(p, "JJTCASTEXPR");
        break;
      case XPathTreeConstants.JJTCASTAS :
        newNode = new NonExecutableExpression(p, "JJTCASTAS");
        break;
      case XPathTreeConstants.JJTTREATAS :
        newNode = new NonExecutableExpression(p, "JJTTREATAS");
        break;
      case XPathTreeConstants.JJTSCHEMACONTEXT :
        newNode = new SchemaContext(p, "JJTSCHEMACONTEXT");
        break;
      case XPathTreeConstants.JJTSCHEMAGLOBALCONTEXT :
        newNode = new SchemaGlobalContext(p, "JJTSCHEMAGLOBALCONTEXT");
        break;
      case XPathTreeConstants.JJTTYPE :
        newNode = new NonExecutableExpression(p, "JJTTYPE");
        break;
      case XPathTreeConstants.JJTSCHEMACONTEXTSTEP :
        newNode = new SchemaContextStep(p);
        break;
      case XPathTreeConstants.JJTSEQUENCETYPE :
        newNode = new SequenceType(p, "JJTSEQUENCETYPE");
        break;
      case XPathTreeConstants.JJTEMPTY :
        newNode = new Empty(p, "JJTEMPTY");
        break;
      case XPathTreeConstants.JJTITEMTYPE :
        newNode = new ItemType(p, "JJTITEMTYPE");
        break;
      case XPathTreeConstants.JJTELEMENTTYPE :
        newNode = new NodeTestType(p, DTMFilter.SHOW_ELEMENT);
        break;
      case XPathTreeConstants.JJTATTRIBUTETYPE :
        newNode = new NodeTestType(p, DTMFilter.SHOW_ATTRIBUTE);
        break;
      case XPathTreeConstants.JJTNODE :
        newNode = new NodeTestType(p, DTMFilter.SHOW_ALL);
        break;
      case XPathTreeConstants.JJTPROCESSINGINSTRUCTION :
        newNode = new NodeTestType(p, DTMFilter.SHOW_PROCESSING_INSTRUCTION);
        break;
      case XPathTreeConstants.JJTCOMMENT :
        newNode = new NodeTestType(p, DTMFilter.SHOW_COMMENT);
        break;
      case XPathTreeConstants.JJTTEXT :
        newNode = new NodeTestType(p, DTMFilter.SHOW_TEXT);
        break;
      case XPathTreeConstants.JJTDOCUMENT :
        newNode = new NodeTestType(p, DTMFilter.SHOW_DOCUMENT);
        break;
      case XPathTreeConstants.JJTITEM :
        newNode = new NodeTestType(p, DTMFilter.SHOW_ITEM);
        break;
      case XPathTreeConstants.JJTUNTYPED :
        newNode = new NodeTestType(p, DTMFilter.SHOW_UNTYPED);
        break;
//      case XPathTreeConstants.JJTATOMICVALUE :
//        newNode = new AtomicType(p);
//        break;
      case XPathTreeConstants.JJTELEMORATTRTYPE :
        newNode = new ElemOrAttrType(p, "JJTELEMORATTRTYPE");
        break;
      case XPathTreeConstants.JJTSCHEMATYPE :
        newNode = new SchemaType(p);
        break;
      case XPathTreeConstants.JJTOFTYPE :
        newNode = new OfType(p, "JJTOFTYPE");
        break;
      case XPathTreeConstants.JJTATOMICTYPE :
        newNode = new AtomicType(p, "JJTATOMICTYPE");
        break;
      case XPathTreeConstants.JJTOCCURRENCEINDICATOR :
        newNode = new OccurrenceIndicator(p, "JJTOCCURRENCEINDICATOR");
        break;
      case XPathTreeConstants.JJTMULTIPLY :
        // Leave as NEE
        newNode = new NonExecutableExpression(p, "JJTMULTIPLY");
        break;
      case XPathTreeConstants.JJTQMARK :
        // Leave as NEE
        newNode = new NonExecutableExpression(p, "JJTQMARK");
        break;

      default :
        newNode = new NonExecutableExpression(p, "(case default: " + id + ")");
    }
    return newNode;
  }

  /**
   * Tell if this node is part of a PathExpr chain.  For instance:
   * <pre>
   * 	|UnaryExpr
   * 	|   PathExpr
   * 	|      StepExpr
   * 	|         AxisChild child::
   * 	|         NodeTest
   * 	|            NameTest
   * 	|               QName foo
   * 	|         Predicates   * 
   * </pre><br/>
   * In this example, UnaryExpr, PathExpr, and StepExpr should all return true.
   */
  public boolean isPathExpr()
  {
    return false;
  }

  /**
   * Tell if this node should have it's PathExpr ancestory reduced.
   */
  public boolean isPathExprReduced()
  {
    return false;
  }

  /**
   * Tell if this node should have it's parent reduced.
   */
  public boolean shouldReduceIfOneChild()
  {
    return false;
  }

  /**
   * This function checks the integrity of the tree, after it has been fully built and 
   * is ready for execution.
   * @return boolean true if the tree has integrity, false otherwise.
   */
  public boolean checkTreeIntegrity()
  {
    return checkTreeIntegrity(0, 0, true);
  }

  /**
   * Tell the user there is a problem with the tree.
   * @param s The string to show the user.
   * @return boolean false always.
   */
  protected boolean flagProblem(String s)
  {
    // System.err.println("checkTreeIntegrity failed: " + s);
    throw new RuntimeException("checkTreeIntegrity failed: " + s);
    // return false;
  }

  /**
   * This function checks the integrity of the tree, after it has been fully built and 
   * is ready for execution.  Derived classes can overload this function to check 
   * their own assumptions.
   * 
   * @param levelCount The current tree level.
   * @param childNumber The current child index.
   * @param parentOK The parent's integrity flag.
   * @return boolean true the node does not have integrity, otherwise 
   *          return the parentOK value.
   */
  public boolean checkTreeIntegrity(
    int levelCount,
    int childNumber,
    boolean parentOK)
  {
    boolean isOK;
//    try
    {
      isOK = parentOK;
      if (levelCount == 0)
      {
        if (!(null == jjtGetParent()))
          isOK =
            flagProblem("The root node has a parent?? parent: " + jjtGetParent());
      }
      else
      {
        Node parent = jjtGetParent();
        if (null == parent)
          isOK =
            flagProblem(
              toString() + " has a null parent and is not the root node! "+
                this.getClass().getName());
        else
        {
          if ( (childNumber >= parent.jjtGetNumChildren())
            || (parent.jjtGetChild(childNumber) != this))
          {
            isOK =
              flagProblem(
                toString() + " has a parent that it is not a child of!");
          }
        }
      
      }
      if (this instanceof NonExecutableExpression)
        isOK = flagProblem(toString() + " is a NonExecutableExpression!");
      
      int childCount = jjtGetNumChildren();
      for (int i = 0; i < childCount; i++)
      {
        Node child = jjtGetChild(i);
        if(null == child)
          isOK = flagProblem(toString() + " has a null child!");
        else
          isOK = ((SimpleNode) child).checkTreeIntegrity(levelCount + 1, i, isOK);
      }
    }
//    catch (RuntimeException e)
//    {
//      isOK = flagProblem(toString() + e.getMessage());
//      e.printStackTrace();
//    }

    return isOK;
  }

  /**
   * This ugly little function strips the extranious PathExpr/RelativePathExpr/StepExpr
   * from XObjects, recursively calling the node's child at index zero, if 
   * the node is a path expression.  It should be called from 
   * jjtAddChild functions.
   * 
   * @param n The node to fix up.
   * @return Node The fixed up node that should be added as a child, which 
   *               may well not be the same node that was passed in.
   */
  protected org.apache.xpath.parser.Node fixupPrimaryRecursive(
    org.apache.xpath.parser.Node n)
  {
    if (((SimpleNode) n).isPathExpr() && n.jjtGetNumChildren() > 0)
    {
      SimpleNode newRoot = (SimpleNode) fixupPrimaryRecursive(n.jjtGetChild(0));

      if (newRoot.isPathExprReduced() && !newRoot.isPathExpr())
      {
        return newRoot;
      }
    }
    return n;
  }

  /**
   * Set this to false if you don't want the PathExprs rewritten to LocationPaths.
   * For Diagnostic purposes.
   */
  public static boolean m_rewritePathExprs = true;

  /**
   * This ugly little function strips the extranious PathExpr/RelativePathExpr/StepExpr
   * from XObjects.  I'm not sure what I'm going to do with the predicates of 
   * these expressions, or with variables, yet.  It should be called from 
   * jjtAddChild functions.
   * 
   * @param n The node to fix up.
   * @return Node The fixed up node that should be added as a child, which 
   *               may well not be the same node that was passed in.
   */
  protected org.apache.xpath.parser.Node fixupPrimarys(
    org.apache.xpath.parser.Node n)
  {
    if (((SimpleNode) n).shouldReduceIfOneChild())
    {
      n = n.jjtGetChild(0);
      n.jjtSetParent(this);
      return n;
    }
    Node orig = n;
    if ((n instanceof NonExecutableExpression) && n.jjtGetNumChildren() > 0)
    {
      n = fixupPrimaryRecursive(n);
    }
    if (n instanceof PathExpr)
    {
      Node mightBeStepExprOrSomethingElse = n.jjtGetChild(0);
      if (!(mightBeStepExprOrSomethingElse instanceof StepExpr))
        n = mightBeStepExprOrSomethingElse; // is something else, so reduce
    }
    if (m_rewritePathExprs && n instanceof PathExpr)
    {
      boolean isTopLevel = (((PathExpr) n).m_parser.m_predLevel == 0);
      try
      {
        n = (Node) WalkerFactory.newDTMIterator((PathExpr) n, isTopLevel);
      }
      catch (javax.xml.transform.TransformerException te)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(te);
      }
    }
    if (n != orig)
      n.jjtSetParent(this); // because jjtree can't!
    return n;
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtOpen()
   */
  public void jjtOpen()
  {
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtClose()
   */
  public void jjtClose()
  {
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtSetParent(Node)
   */
  public void jjtSetParent(Node n)
  {
    ((Expression) this).exprSetParent((ExpressionNode) n);
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetParent()
   */
  public Node jjtGetParent()
  {
    return (Node) ((Expression) this).exprGetParent();
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    n = fixupPrimarys(n);
    ((Expression) this).exprAddChild((ExpressionNode) n, i);
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetChild(int)
   */
  public Node jjtGetChild(int i)
  {
    return (Node) ((Expression) this).exprGetChild(i);
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtGetNumChildren()
   */
  public int jjtGetNumChildren()
  {
    return ((Expression) this).exprGetNumChildren();
  }

  /**
   * @see org.apache.xpath.parser.Node#jjtAccept(XPathVisitor, Object)
   */
  public Object jjtAccept(XPathVisitor visitor, Object data)
  {
    return visitor.visit(this, data);
  }

  /**
   * Call jjtAccept for each child of the node.
   * @param visitor The visitor that will visit the child node.
   * @param data A data object to be handed to that visitor.
   * @return Object The data object handed in, possibly  
   *                 modified by jjtAccept.
   */
  public Object childrenAccept(XPathVisitor visitor, Object data)
  {
    int nChildren = jjtGetNumChildren();
    for (int i = 0; i < nChildren; ++i)
    {
      jjtGetChild(i).jjtAccept(visitor, data);
    }
    return data;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return this.getClass().getName();
  }

  /**
   * Method toString.
   * @param prefix
   * @return String
   */
  public String toString(String prefix)
  {
    return prefix + toString();
  }

  public void processToken(Token t)
  {
    // abstract
  }

  /**
   * Dump a diagnostic representation of the node to System.out.
   * @param prefix The prefix string to be added before the 
   *                to each line of output.
   */
  public void dump(String prefix)
  {
    dump(prefix, System.out);
  }

  /**
   * Method for subclasses to override to print the diagnostic 
   * <i>value</i> of the node.
   * @param ps The PrintStream to write the value to.
   */
  public void printValue(java.io.PrintStream ps)
  {
  }

  /**
   * Dump a diagnostic representation of the node to a PrintStream.
   * 
   * @param prefix The prefix string to be added before the 
   *                to each line of output.
  
   * @param ps The PrintStream where the representation will 
   *            be written.
   */
  public void dump(String prefix, java.io.PrintStream ps)
  {
    ps.print(toString(prefix));
    printValue(ps);
    ps.println();
    int nChildren = jjtGetNumChildren();
    for (int i = 0; i < nChildren; ++i)
    {
      SimpleNode n = (SimpleNode) jjtGetChild(i);
      if (n != null)
      {
        n.dump(prefix + "   ", ps);
      }
    }
  }

}
