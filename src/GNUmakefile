#
# Basic variables
#
PWD		:= $(shell pwd)
TOP		:= $(XSLT)
SRC		:= $(TOP)/src
DOC		:= $(TOP)/doc
APIDOC		:= $(DOC)/api
ifdef PRODUCT
ifndef VIEW_ROOT
VIEW_ROOT	:= $(shell (echo $(PWD)) | sed -e 's,/vob.*,,')
export VIEW_ROOT
endif
ifndef JAVA.home
JAVA.home	:= $(shell type java | sed -e 's,[^/]*\(.*\)/bin/java$$,\1,')
export JAVA.home
endif
endif

CLASSDIR	:= $(TOP)/classes
JARSDIR		:= $(TOP)/jars
MAKEFILE	:= GNUmakefile

JAVA            := $(JAVA_HOME)/bin/java
JAVAC           := $(JAVA_HOME)/bin/javac
RMIC            := $(JAVA_HOME)/bin/rmic
JAR             := $(JAVA_HOME)/bin/jar

# treat top of the source tree specially
ifeq ($(PWD),$(SRC))
THISDIR		:= .
CLASSDIR.this	:= $(CLASSDIR)
PKG		:= 
else
THISDIR		:= $(PWD:$(SRC)/%=%)
CLASSDIR.this	:= $(CLASSDIR)/$(THISDIR)
PKG		:= $(subst /,.,$(THISDIR)).
endif

# buildable files
#
# We need to select only the stubs from this package and
# subpackages, which is why we filter SERVER.stubs to contain only
# things that at least have THISDIR.

SERVER.paths	= $(subst .,/,$(sort $(SERVER.class)))
SERVER.classes	= $(SERVER.paths:%=$(CLASSDIR)/%.class)
SERVER.stubs	= $(filter $(CLASSDIR.this)/%,$(SERVER.paths:%=$(CLASSDIR)/%_Stub.class))

FILES.raw	:= $(sort $(shell find . -name frag -prune -o -name SCCS -prune -o -name '*.java' -print))
FILES.java	:= $(FILES.raw:./%=%)
FILES.class	:= $(FILES.java:%.java=$(CLASSDIR.this)/%.class)

THIS.java	:= $(wildcard *.java)
THIS.class	:= $(THIS.java:%.java=$(CLASSDIR.this)/%.class)
THIS.stubs	:= $(patsubst /$(CLASSDIR.this)/%,,$(SERVER.stubs))

# THIS.class must contain only those stubs for this package, so we filter
# out anything from any subpackage.  I can't figure out how to do this using
# gmake string manipulation -- there must be a way...

PACKAGES.raw	:= $(subst /,.,$(sort $(dir $(FILES.java))))
PACKAGES.list	:= $(addprefix $(PKG),$(PACKAGES.raw))
PACKAGES	:= $(PACKAGES.list:%...=%)
PACKAGES	:= $(PACKAGES:%.=%)
PACKAGES.doc	= $(sort $(strip $(filter-out $(NO_DOC),$(PACKAGES))))

# The file that will hold the list of classes that need rebuilding

CLASS_LIST	:= /tmp/.class$(shell echo $$$$)
STUBS_LIST	:= $(subst class,stubs,$(CLASS_LIST))

# how to compile things
JAVAC.echo	= echo javac $(JAVACFLAGS)
JAVAC.exec	= $(JAVAC) -d $(CLASSDIR) $(JAVACFLAGS)
JAVAVERSION.exec= $(JAVA) -version

RMICFLAGS	= -v1.2
RMIC.echo	= echo rmic $(RMICFLAGS)
RMIC.exec	= $(RMIC) -d $(CLASSDIR) $(RMICFLAGS)

# The rest of the JDK we take from somplace else
#

# The tools directory
#

EXT             = $(XSLT)/External
JINI.lib	= $(EXT)/jini/lib
#JINI.jar	= $(JINI.lib)/jini-core.jar:$(JINI.lib)/jini-ext.jar:$(JINI.lib)/sun-util.jar
XML.jar	        = $(EXT)/xml-tr2/xml.jar
JAXP.jar	= $(EXT)/jaxp/jaxp.jar
BCEL.jar   = $(EXT)/BCEL/BCEL.jar
JAVA.tools	= $(CLASSPATH)
JLEX.jar        = $(EXT)/JLex/JLex.jar
JCUP            = $(EXT)/java_cup
JCUP.jar        = $(JCUP)/java_cup.jar
JCUP_RT.jar     = $(JCUP)/runtime/runtime.jar
SERVLET.jar     = $(EXT)/jswdk-1.0.1/servlet.jar
BRAZIL		= $(EXT)/brazil

CLASSDEP_EXEC   = $(JAVA) -cp $(JINI.lib)/classdep.jar com.sun.jini.tool.ClassDep -files
MANIFESTDIR     = $(SRC)/manifest

# Compilation flags
#
#JAVACFLAGS	= -J-Xmx48000000 $(JAVADEBUG) -O -deprecation
JAVACFLAGS	= -O -deprecation -classpath $(CLASSPATH)
JAVADOCLEVEL	=
#JAVADOCFLAGS	= -version -J-Xmx64000000 $(JAVADOCLEVEL)
JAVADOCFLAGS	= 
PSEDIR		= .
CLASSPATH	= $(CLASSDIR):$(XML.jar):$(JCUP_RT.jar):$(BCEL.jar):$(SERVLET.jar):$(EXT)/j2me_cldc/bin/api/classes:$(EXT)/ModifiedXt.jar:$(JAXP.jar):$(BRAZIL)
export CLASSPATH

# Fragment building
#
FRAG		= /vob/java_prog/src/Frags
FRAGS.dir	= $(SRC)/frag
CMDS.raw	:= $(sort $(shell find . -name frag -prune -o -name SCCS -prune -o -name '*.cmd' -print))
CMDS		:= $(CMDS.raw:./%=%)

# Generic build rules
#
.SUFFIXES:
.SUFFIXES: .class .java .zip

# explicit single-run builds

# simple .class->.java rule
$(CLASSDIR.this)/%.class: %.java
	@echo '$?' >> $(CLASS_LIST)


# stubs and skeletons -- we must get impl class name from dependency class
$(CLASSDIR.this)/%_Stub.class: $(CLASSDIR.this)/%.class
	@echo '$(subst /,.,$(?:$(CLASSDIR)/%.class=%))' >> $(STUBS_LIST)



# Class files for this package and all subpackages.
# Also, we put the target here so that just typing gmake
# will build the entire tree.
#
all: dirs parser classes # stubs


# Add the includes here. This way other people can add their
# own variables here, and they can either add too or completely
# modify the above variables
#

include $(SRC)/GNUmakefile.allinc

INCLUDES	= $(shell find . -name GNUmakefile.inc -print) /dev/null

ifdef INCLUDES
    include $(INCLUDES)
endif


# ============================================================
#				Targets
# ============================================================


# Just the class files for this package and all subpackages
#
classes: dirs init_java_list $(FILES.class) build_java_list

.PHONY: classes init_java_list build_java_list

init_java_list:
	@-rm -f $(CLASS_LIST)

# The recursion in this rule is because gmake is not always building all the
# necessary stubs if the stubs are built in the same run as the class that
# creates it.  This is odd, because when run with -d, gmake does the right
# thing.  This bug may be related to the one descirbed for the .DEFAULT rule

build_java_list:
	@set -e ;							\
	 if [ -f $(CLASS_LIST) ]; then					\
	    $(JAVAVERSION.exec) ;					\
	    files=`cat $(CLASS_LIST)` ;					\
	    num=`echo $$files | wc | awk '{ print $$2 }'` ;		\
	    $(JAVAC.echo) "[$$num files]:" ; 				\
	    sc=`expr $${COLUMNS:-80} - 4` ; 				\
	    COLUMNS=$$sc ls -C $$files | expand | sed -e 's/^/    /' ;	\
	    $(JAVAC.exec) $$files ; 					\
	    $(MAKE) --no-print-directory stubs ;			\
	    rm -f $(CLASS_LIST) ;					\
	 fi

# The stubs and skeletons for this package and all subpackages
#
stubs: init_stub_list $(SERVER.stubs) build_stub_list

.PHONY: classes init_stub_list build_stub_list

# Otherwise gmake thinks that these were only built to get at the stubs,
# and are therefore removable intermediate files (!)
.PRECIOUS: $(SERVER.classes)

init_stub_list:
	@-rm -f $(STUBS_LIST)

v:
	echo $(SERVER.classes)
	echo $(STUBS_LIST)

build_stub_list:
	@set -e ;							\
	 if [ -f $(STUBS_LIST) ]; then					\
	    files=`cat $(STUBS_LIST)` ;					\
	    num=`echo $$files | awk '{ print NF }'` ;			\
	    if [ $$num -ne 0 ]; then					\
		$(RMIC.echo) "[$$num files]:" ; 			\
		sort $(STUBS_LIST) | sed -e 's/^/    /' ;		\
		$(RMIC.exec) $$files ; 					\
	    fi ;							\
	    rm -f $(STUBS_LIST) ;					\
	 fi

# Class files for this package
#
this: dirs init_java_list $(THIS.class) build_java_list
.PHONY: this

dirs: $(CLASSDIR)
.PHONY: dirs

$(CLASSDIR) $(JARSDIR) $(APIDOC):
	mkdir -p $@
	@test -d $@ || (echo no $@ ; exit 1)

jartools:
	@cd $(JARTOOLSDIR); $(MAKE)

#include $(TOOLSDIR)/java/GNUmakefile

# javadoc files
#
#JAVADOC	= CLASSPATH=$(CLASSPATH):../javadoc java sun.tools.javadoc.Main

doc: $(APIDOC)/index.html
.PHONY: doc

# The exit status of javadoc is ignored because of a bug -- it complains
# about a private interface that rmic and javac accept.  Unfortunately, if
# other errors show up this will just skip over them, but generally javadoc
# only generates warnings -- errors are for source code errors, which should
# have already been caught during compilation.

$(APIDOC)/index.html: $(FILES.java) $(MAKEFILE)
	@rm -fr $(APIDOC)
	@mkdir -p $(APIDOC)
	@echo javadoc $(JAVADOCFLAGS) -d $(APIDOC) \\
	@echo $(PACKAGES.doc) | awk '{ for (i = 1; i <= NF; i++) print "   ", $$i; }'
	@-$(JAVADOC) $(JAVADOCFLAGS) -d $(APIDOC) $(PACKAGES.doc)

# Clean all class files for this package and all subpackages
#
clean clobber clean.all clobber.all::
	-rm -r $(CLASSDIR.this)/

clean.jars::
	-rm -f $(JARS:%=$(JARSDIR)/%)

clean.doc clobber clobber.all::
	-rm -rf $(APIDOC)

.PHONY: clean clobber clean.all clobber.all

# Clean all class files for this package
#
clean.this clobber.this::
	-rm -f $(CLASSDIR.this)/*.class core

.PHONY: clean.this clobber.this

# Print out the classpath to use (export CLASSPATH=`gmake classpath`)
#
classpath:
	@echo $(CLASSPATH)

.PHONY: classpath

# Print out this message
#
what:
	@$(TOOLS)/targets 

.PHONY: what

vars:
	@echo PKG $(PKG)
	@echo PACKAGES.raw $(PACKAGES.raw)
	@echo PACKAGES.list $(PACKAGES.list)
	@echo PACKAGES $(PACKAGES)

.PHONY: vars

.DEFAULT:
	@echo "*** Unknown target: $@ ('make what' shows targets)";

java-version:
	@$(JAVAVERSION.exec)

frags:: all $(FRAGS.dir)
	$(FRAG) -d $(FRAGS.dir) $(FILES.java) $(CMDS)
	@for src in $(FILES.java); do					\
	    dir=$(FRAGS.dir)/src/$(THISDIR)/`dirname $$src`;		\
	    frag=$$dir/`basename $$src`;				\
	    mkdir -p $$dir;						\
	    rm -f $$frag;						\
	    egrep -v '// *\^\^|// *vv|// *\.\.\.' $$src >$$frag;	\
	 done

$(FRAGS.dir):
	mkdir -p $(FRAGS.dir)

clean clobber clean.all clobber.all::
	-rm -rf $(FRAGS.dir)


####
# GTM: added this block to do javadoc for our classes
#
JDDEST=$(XSLT)/doc/api
JDSRC=$(XSLT)/src
JDCLZES=$(XSLT)/classes
JDCP=$(JDCLZES):$(XML.jar):$(BCEL.jar):$(JCUP.jar):$(JCUP_RT.jar):$(JAXP.jar)
JDPKGS=\
	org.apache.xalan.xsltc \
	org.apache.xalan.xsltc.compiler \
	org.apache.xalan.xsltc.compiler.util \
	org.apache.xalan.xsltc.dom \
	org.apache.xalan.xsltc.runtime \
	org.apache.xalan.xsltc.util \
	org.apache.xalan.xsltc.util.getopt

jdoc:
	rm -rf $(XSLT)/doc/api; mkdir -p $(XSLT)/doc/api/;\
	javadoc -sourcepath $(JDSRC) -classpath $(JDCP) -d $(JDDEST) $(JDPKGS)

pjdoc:
	rm -rf $(XSLT)/doc/api; mkdir -p $(XSLT)/doc/api/;\
	javadoc -private -sourcepath $(JDSRC) -classpath $(JDCP) -d $(JDDEST) $(JDPKGS)

#
# End GTM.	
############

# GTM: apache target
TARFILES=\
	External/BCEL\
	External/java_cup\
	External/xml-tr2\
	External/JLex\
	bin/xslt\
	bin/xsltc\
	src/GNUmakefile\
	src/GNUmakefile.allinc\
	src/docs/README\
	src/docs/README.api\
	src/docs/README.xslt\
	src/docs/README.xsltc\
	src/docs/README.APACHE\
	src/org/apache/xalan/xsltc/*.java\
	src/org/apache/xalan/xsltc/compiler\
	src/org/apache/xalan/xsltc/dom\
	src/org/apache/xalan/xsltc/runtime\
	src/org/apache/xalan/xsltc/util\
	doc

TMPDEST=/tmp/xsltc_apache
JARDEST=/tmp
APACHE.jar=apache.jar
XLATE=$(XSLT)/src/xlate
COPYRIGHT=$(XSLT)/bin/apache_sun4u

apache:	
	(rm -rf $(TMPDEST);\
	 mv -f $(JARDEST)/APACHE.jar $(JARDEST)/APACHE.jar%;\
	 mkdir $(TMPDEST);\
	 cd $(XSLT);tar cvf - $(TARFILES)|(cd $(TMPDEST); tar xfpB -);\
	 cd $(TMPDEST)/src;mkdir -p org/apache/xalan;\
	 cp -R org/apache/xalan/xsltc org/apache/xalan;\
	 mv org/apache/xalan/xslt org/apache/xalan/xsltc;\
	 rm -rf com;\
	 find $(TMPDEST)/src -name "*.java" -exec $(XLATE) {} \;;\
	 find $(TMPDEST)/src -name "*.lex" -exec $(XLATE) {} \;;\
	 find $(TMPDEST)/src -name "*.cup" -exec $(XLATE) {} \;;\
	 find $(TMPDEST)/bin -name "xsl*" -exec $(XLATE) {} \;;\
	 cd $(TMPDEST)/src;$(XLATE) GNUmakefile; $(XLATE) GNUmakefile.allinc;\
	 cd $(TMPDEST)/src;$(COPYRIGHT) org;\
	 cp $(XSLT)/src/docs/README.APACHE $(TMPDEST)/README;\
	 cd $(TMPDEST);jar cvf $(JARDEST)/$(APACHE.jar) *;\
	)


zip: parser classes
	(rm -rf /tmp/xsltc;\
	 rm -f ../Releases/*.zip;\
	 mkdir /tmp/xsltc;\
	 mkdir /tmp/xsltc/bin /tmp/xsltc/lib /tmp/xsltc/src /tmp/xsltc/demo /tmp/xsltc/samples;\
	 mkdir -p /tmp/xsltc/samples/jdExample/;\
	 mkdir -p /tmp/xsltc/src/org/apache/xalan/xsltc/runtime;\
	 mkdir -p /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 mkdir -p /tmp/xsltc/src/org/apache/xalan/xsltc/demo/servlet;\
	 cd ../classes;\
	 jar xvf ../External/java_cup/runtime/runtime.jar;\
	 jar cvf /tmp/xsltc/lib/xsltc.jar org/apache/xalan/xsltc/compiler java_cup org/apache/xalan/xsltc/util/getopt;\
	 jar cvf /tmp/xsltc/lib/xsltcrt.jar org/apache/xalan/xsltc/demo/applet/*.class org/apache/xalan/xsltc/*.class org/apache/xalan/xsltc/util org/apache/xalan/xsltc/runtime org/apache/xalan/xsltc/dom;\
	 cp ../src/docs/LICENSE.txt /tmp/xsltc/;\
	 cp ../src/docs/XSLTCprn_lg.txt /tmp/xsltc/;\
	 cp ../src/docs/README /tmp/xsltc/;\
	 cp ../src/docs/README.api /tmp/xsltc/;\
	 cp ../src/docs/README.xsltc /tmp/xsltc/;\
	 cp ../src/docs/README.xslt /tmp/xsltc/;\
	 cp ../External/xml-tr2/xml.jar /tmp/xsltc/lib/;\
	 cp ../src/org/apache/xalan/xsltc/demo/palm/README /tmp/xsltc/demo;\
	 cp ../src/org/apache/xalan/xsltc/demo/palm/TransletDemo.prc /tmp/xsltc/demo;\
	 cp ../bin/xsltc-release /tmp/xsltc/bin/xsltc;\
	 cp ../bin/xslt-release /tmp/xsltc/bin/xslt;\
	 cp ../bin/xsltc.bat /tmp/xsltc/bin/xsltc.bat;\
	 cp ../bin/xslt.bat /tmp/xsltc/bin/xslt.bat;\
	 cp ../src/org/apache/xalan/xsltc/DOMCache.java /tmp/xsltc/src/org/apache/xalan/xsltc;\
	 cp ../src/org/apache/xalan/xsltc/Translet.java /tmp/xsltc/src/org/apache/xalan/xsltc;\
	 cp ../src/org/apache/xalan/xsltc/TransletException.java /tmp/xsltc/src/org/apache/xalan/xsltc;\
	 cp ../src/org/apache/xalan/xsltc/TransletOutputHandler.java /tmp/xsltc/src/org/apache/xalan/xsltc;\
	 cp ../src/org/apache/xalan/xsltc/runtime/DefaultRun.java /tmp/xsltc/src/org/apache/xalan/xsltc/runtime;\
	 cp ../src/org/apache/xalan/xsltc/runtime/DefaultSAXOutputHandler.java /tmp/xsltc/src/org/apache/xalan/xsltc/runtime;\
	 cp ../src/org/apache/xalan/xsltc/runtime/dom/DocumentCache.java /tmp/xsltc/src/org/apache/xalan/xsltc/dom;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/TransformApplet.java /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/xslt_demo.html /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/xslt_demo_plays.html /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/hamlet.xml /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/macbeth.xml /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/playShort.xsl /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/playFull.xsl /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/playSpeakers.xsl /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/plays.jar /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/sunlogodot-blu.gif /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/servlet/Transform.java /tmp/xsltc/src/org/apache/xalan/xsltc/demo/servlet;\
	 cp ../src/org/apache/xalan/xsltc/demo/applet/README /tmp/xsltc/src/org/apache/xalan/xsltc/demo/applet;\
	 cp ../src/org/apache/xalan/xsltc/demo/servlet/README /tmp/xsltc/src/org/apache/xalan/xsltc/demo/servlet;\
	 chmod +x /tmp/xsltc/bin/*;\
	 cp $(XSLT)/test/Samples/*.* /tmp/xsltc/samples/;\
	 cp $(XSLT)/test/Samples/jdExample/*.* /tmp/xsltc/samples/jdExample/;\
	 cd /tmp;\
	 zip -r $(XSLT)/Releases/xsltc-1_0-alpha5-bin.zip xsltc/)



