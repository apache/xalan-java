# Stylebook execution extracted from old Ant build
# Some of the Xalan (especially xsltc) documentation was written in
# stylebook. It isn't clear that keeping it as source and rendering
# it at buildtime, rather than checking in the generated files, is
# actually a win -- not least because those files are probably pretty
# far out of date at this point.
#
# This, or something equivalent, should be being invoked as part of our
# maven "site" build.

stylebook_dir="./stylebook"
stylebook_book="${stylebook_dir}/sources/xalan-jlocal.xml"
stylebook_style="${stylebook_dir}/style"
doc_generator="org.apache.stylebook.StyleBook"
doc_generator_styletar="${stylebook_dir}/xml-site-style.tar.gz"
xalan_cmdline_class="org.apache.xalan.xslt.Process"

stylebook_class_path="xml-apis.jar":"runtime.jar":"stylebook/stylebook-1.0-b3_xalan-2.jar":tools/xalan2jdoc.jar:"serializer/target/classes":"xalan/target/classes"
stylebook_class_path="stylebook/stylebook-1.0-b3_xalan-2.jar":tools/xalan2jdoc.jar:"serializer/target/classes":"xalan/target/classes"


# Note --directory affects any following options, so put it at EOL here
# Also note that this really should be unpacking into a scratch directory,
# or not unpacking at all except during execution; Oh Well
#tar -xf ${doc_generator_styletar} --directory ${stylebook_dir}

echo "Generate Xalan-J 2.x design document"
java -cp ${stylebook_class_path} ${doc_generator} loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=./target/site/design/ ./stylebook/sources/xalandesign.xml ./stylebook/style


# Generate a PDF file, via collation, xml2fo.xsl, and FOP (if we have it)
# In Fact, this whole sequence is not being called in our normal Ant-driven builds either.
if false; then 
    java -cp ${stylebook_class_path} ${xalan_cmdline_class} -xsl ./stylebook/sources/xalan-collate.xsl -out ./stylebook/sources/xalan/xalan-collate.xml
    java -cp ${stylebook_class_path} ${xalan_cmdline_class} -in ./stylebook/sources/xalan/xalan-collate.xml -xsl ./stylebook/style/stylesheets/xml2fo.xsl  -param resourceFile ../../sources/xalan/resources.xml -param project Xalan-Java -out ./target/site/sources/xalan/xalan-collate.fo
fi

# NOTE: Without the loaderConfig, we get complaints from
# book2project.xsl that it can't find
# org/apache/xerces/dom/DocumentImpl. loaderDesign is just saying that
# xslt shouild be called with design2project.xsl, so that seems
# harmless.
echo "Generate XSLTC Architectural documentation"
java -cp ${stylebook_class_path} ${doc_generator} loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=./target/site/xsltc/ ./stylebook/sources/xsltc.xml ./stylebook/style

# Diff tells me that the -jlocal output is almost identical to the
# -jsite output despite the slight difference in the .xml files used
# as their sources. (The only effective difference appears to be that
# -jlocal doesn't produce the index or charter documents.)
# I'm not convinced that's enough difference to merit generating both, but
# until I better grok why this duplication was done in the first place I'm
# hesitant to remove it. -- jkesselm, 20231105
echo "Generate xalan-jlocal documentation"
java -cp ${stylebook_class_path} ${doc_generator} loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=./target/site/xalan/local ./stylebook/sources/xalan-jlocal.xml ./stylebook/style

echo "autodocs equivalent"
mkdir -p ./target/site/xalan
java -cp ${stylebook_class_path} ${doc_generator} loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=./target/site/xalan ./stylebook/sources/xalan-jsite.xml ./stylebook/style

mkdir -p ./target/site/xsltc
cp stylebook/sources/xsltc/README.x* target/site/xsltc/
