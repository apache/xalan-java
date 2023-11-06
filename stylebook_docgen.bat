rem Stylebook execution extracted from old Ant build
rem Some of the Xalan (especially xsltc) documentation was written in
rem stylebook. It isn't clear that keeping it as source and rendering
rem it at buildtime, rather than checking in the generated files, is
rem actually a win -- not least because those files are probably pretty
rem far out of date at this point.
rem
rem This, or something equivalent, should be being invoked as part of our
rem maven "site" build.

set stylebook_dir=".\stylebook"
set stylebook_book="%stylebook_dir%\sources\xalan-jlocal.xml"
set stylebook_style="%stylebook_dir%\style"
set doc_generator="org.apache.stylebook.StyleBook"
set doc_generator_styletar="%stylebook_dir%\xml-site-style.tar.gz"
set xalan_cmdline_class="org.apache.xalan.xslt.Process"

set stylebook_class_path="xml-apis.jar";"runtime.jar";"stylebook\stylebook-1.0-b3_xalan-2.jar";tools\*.jar;"serializer\target\classes";"xalan\target\classes"


rem Note --directory affects any following options, so put it at EOL here
rem Also note that this really should be unpacking into a scratch directory,
rem or not unpacking at all except during execution; Oh Well
tar -xf %doc_generator_styletar% --directory %stylebook_dir%

echo "Generate Xalan-J 2.x design document"
java -cp %stylebook_class_path% %doc_generator% loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=.\target\site\design\ .\stylebook\sources\xalandesign.xml .\stylebook\style


rem Generate a PDF file, via collation, xml2fo.xsl, and FOP (if we have it)
rem In Fact, this whole sequence is not being called in our normal Ant-driven builds either,
rem so I've disabled it for now.
if 0==1 (
    java -cp %stylebook_class_path% %xalan_cmdline_class% -xsl .\stylebook\sources\xalan-collate.xsl -out .\stylebook\sources\xalan\xalan-collate.xml
    java -cp %stylebook_class_path% %xalan_cmdline_class% -in .\stylebook\sources\xalan\xalan-collate.xml -xsl .\stylebook\style\stylesheets\xml2fo.xsl  -param resourceFile ..\..\sources\xalan\resources.xml -param project Xalan-Java -out .\target\site\sources\xalan\xalan-collate.fo
)

rem NOTE; Without the loaderConfig, we get complaints from
rem book2project.xsl that it can't find
rem org/apache/xerces/dom/DocumentImpl. loaderDesign is just saying that
rem xslt shouild be called with design2project.xsl, so that seems
rem harmless.

echo "Generate XSLTC Architectural documentation"
java -cp %stylebook_class_path% %doc_generator% loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=.\target\site\xsltc\ .\stylebook\sources\xsltc.xml .\stylebook\style

rem Diff tells me that the -jlocal output is almost identical to the
rem -jsite output despite the slight difference in the .xml files used
rem as their sources. (The only effective difference appears to be that
rem -jlocal doesn't produce the index or charter documents.)
rem I'm not convinced that's enough difference to merit generating both, but
rem until I better grok why this duplication was done in the first place I'm
rem hesitant to remove it. -- jkesselm, 20231105
echo "Generate xalan-jlocal documentation"
java -cp %stylebook_class_path% %doc_generator% loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=.\target\site\xalan\local .\stylebook\sources\xalan-jlocal.xml .\stylebook\style

echo "autodocs equivalent"
mkdir .\target\site\xalan
java -cp %stylebook_class_path% %doc_generator% loaderConfig=sbk:/style/loaderdesign.xml targetDirectory=.\target\site\xalan .\stylebook\sources\xalan-jsite.xml .\stylebook\style

mkdir .\target\site\xsltc
xcopy /s stylebook\sources\xsltc\README.x* target\site\xsltc\
