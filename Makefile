###########################################################
# Makefile for xml-xalan, an XSLT Processor
#
# Please note the following assumptions!
#  - See make.include for system tools definitions. We assume
#    that each of these tools are on the path and properly setup.
#  
#  - Use GNU make or an equivalent.  On Win32, you can see
#    http://sourceware.cygnus.com/cygwin for a port. 
#  
#  - With JDK 1.1.7B or earlier, a 'make all' may fail the first
#    time. Try running it again twice, and everything should work.
#    Note that when using JDK 1.2.x, it all works fine. We'll work on it.
#  
#  - Note the several attempts to determine if we're on WinXX or Unix:
#    ifeq (,$(findstring usr,$(PATH)))
#    Feel free to modify to work for your machine, and suggest a better
#    way to create a platform-independent makefile.
#  
#  - The docs directory currently requires javadoc from the JDK 1.2.x
#    You may need to edit make.include 'JAVADOC12' for your system.
#
#  - Yes, we plan to move to an 'Ant' based make system soon
#    (Ant is from jakarta.apache.org and is a Java-based make)
###########################################################

include make.include

SUBDIRS = src

###########################################################
# Main targets definitions
#
# Note that make.include also defines common targets
###########################################################
all: makesubdirs jars makesamples makedocs

build: makesubdirs jars
# To change debug/release options, see make.include for JAVADEBUG flag

# Create a distribution module
dist: makedist

docs: makedocs

samples: makesamples

jars: makesubdirs $(JARNAME)

compat: makecompat compatjars

PROPPATH = org$(PATHSEP)apache$(PATHSEP)xalan$(PATHSEP)res
$(JARNAME)::
	-mkdir src/$(CLASS_DIR)/META-INF;
	-mkdir src/$(CLASS_DIR)/META-INF/services;
	$(CP) src/$(PROPPATH)/*.properties src/$(CLASS_DIR)/$(PROPPATH)/.; \
	$(CP) src/org/apache/serialize/*.properties src/$(CLASS_DIR)/org/apache/serialize/.; \
	$(CPR) src/META-INF/* src/$(CLASS_DIR)/META-INF/.; \
	echo -n "Jarring ../bin/$@ .. "; \
	cd src/$(CLASS_DIR); $(JAR) $(JARFLAGS) ../../bin/$@ META-INF javax org; \
	echo "done"

compatjars:
	echo -n "Jarring ../bin/$@ .. "; \
	cd src/$(CLASS_DIR); $(JAR) $(JARFLAGS) ../../bin/$@.jar org/apache/xalan/xslt org/apache/xalan/xpath; \
	echo "done"

# Note: When making dist, copy the built docs up one level
# Note: Create both a super-jar and a tar.gz archive
DISTDIR  = $(PRODUCT_NAME)$(VERSION)
.PHONY: makedist
makedist:
	echo Prepare creating $(DIST_NAME)
	-mkdir $(DISTDIR)
	$(CP) * $(DISTDIR);\
	$(CPR) xdocs $(DISTDIR);\
	$(CPR) samples $(DISTDIR);\
	$(CPR) src $(DISTDIR);\
	$(CPR) build/docs $(DISTDIR);\
	echo Create $(JARDISTNAME);\
	$(JAR) -cf $(JARDISTNAME) $(DISTDIR);\
	echo Create $(TARDISTNAME);\
	$(TARGZ) $(TARDISTNAME) $(DISTDIR);\
	echo Create $(ZIPDISTNAME);\
	$(JAR) -cMf $(ZIPDISTNAME) $(DISTDIR);\
	echo Done creating $(JARDISTNAME) etc., you should sign this with PGP before posting

# Subsidiary targets are defined in make.include
clean:: cleansubdirs cleandocs cleansamples

compatclean:: cleancompat

