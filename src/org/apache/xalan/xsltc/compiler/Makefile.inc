####################################################################
#                                                                  #
# Makefile.inc for XSLT compiler                                   #
#                                                                  #
####################################################################

####################################################################
# Useful macros                                                    #
####################################################################

JAVAC = javac
#JAVAC_FLAGS = -g -d $(XSLT)/src/classes
JAVAC_FLAGS = -sourcepath $(XSLT)/src
JAVACC = javacc
JAVACC_FLAGS = -static=FALSE 
JAVACUP = javacup
JAVACUP_FLAGS =
JAVALEX = jlex
JAVALEX_FLAGS =

####################################################################
#  Explicit rules                                                  #
####################################################################

.SUFFIXES: .java .class .jj .lex .cup

.java.class:
	$(JAVAC) $(JAVAC_FLAGS) $<

.jj.java:
	$(JAVACC) $(JAVACC_FLAGS) $<

.cup.java:
	$(JAVACUP) $(JAVACUP_FLAGS) $<

.lex.java:
	$(JAVALEX) $(JAVALEX_FLAGS) $<

