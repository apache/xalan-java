#!/bin/sh
#
# Bourne shell script to create xalan-j documentation pages
#
# SET THE LIBRARY PATH FOR YOUR OPERATING SYSTEM, REQUIRED BY "Xalan" PROGRAM
#
#  SOLARIS AND LINUX
# export LD_LIBRARY_PATH=/usr/local/lib
#
#  AIX AND BSD
# export LIBPATH=/usr/local/lib
#
#  HPUX
# export SHLIB_PATH=/usr/local/lib
#
#  MAC OS/X
# export DYLD_LIBRARY_PATH=/usr/local/lib
#
#  CYGWIN AND MINGW
# export PATH=$PATH;/usr/local/lib
#

umask 0002
mkdir -p ../../build/docs/xalan-j/resources
mkdir -p ../../build/docs/xalan-j/xsltc/resources
mkdir -p ../../build/docs/xalan-j/design/resources

make-jsite.sh bugreporting
make-jsite.sh builds
make-jsite.sh charter
make-jsite.sh commandline
make-jsite.sh commandline_xsltc
make-jsite.sh contact_us
make-jsite.sh downloads
make-jsite.sh dtm
make-jsite.sh extensionslib
make-jsite.sh extensions
make-jsite.sh extensions_xsltc
make-jsite.sh faq
make-jsite.sh features
make-jsite.sh getstarted
make-jsite.sh history
make-jsite.sh index
make-jsite.sh overview
make-jsite.sh public_apis
make-jsite.sh readme
make-jsite.sh resources
make-jsite.sh samples
make-jsite.sh trax
make-jsite.sh usagepatterns
make-jsite.sh whatsnew
make-jsite.sh xpath_apis
make-jsite.sh xsltc_history
make-jsite.sh xsltc_usage

make-design.sh design2_0_0

make-xsltc.sh index
make-xsltc.sh xsl_choose_design
make-xsltc.sh xsl_comment_design
make-xsltc.sh xsl_if_design
make-xsltc.sh xsl_include_design
make-xsltc.sh xsl_key_design
make-xsltc.sh xsl_lang_design
make-xsltc.sh xsl_sort_design
make-xsltc.sh xsltc_compiler
make-xsltc.sh xsltc_dom
make-xsltc.sh xsltc_iterators
make-xsltc.sh xsltc_namespace
make-xsltc.sh xsltc_native_api
make-xsltc.sh xsltc_overview
make-xsltc.sh xsltc_performance
make-xsltc.sh xsltc_predicates
make-xsltc.sh xsltc_runtime
make-xsltc.sh xsltc_trax_api
make-xsltc.sh xsltc_trax
make-xsltc.sh xsl_unparsed_design
make-xsltc.sh xsl_variable_design
make-xsltc.sh xsl_whitespace_design

#cp xalan-graphic/*.* ../../build/docs/xalan-j
cp ../xslt-resources/*.* ../../build/docs/xalan-j/resources
cp ../xslt-resources/*.* ../../build/docs/xalan-j/xsltc/resources
cp ../xslt-resources/*.* ../../build/docs/xalan-j/design/resources
cp xalan/*.gif        ../../build/docs/xalan-j
cp design/*.gif       ../../build/docs/xalan-j/design
cp xsltc/*.gif        ../../build/docs/xalan-j/xsltc



