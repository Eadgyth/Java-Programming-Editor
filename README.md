# Java-Programming-Editor / Eadgyth
A text editor written in Java that helps programming (currently in Java) and that is extensible by plugins
which may be developed to do different types of work with text files.
<p>
Eadgyth features opening files in tabs, a file tree view with already a little functionality, a simple
console, syntax coloring, auto indentation (still simple) and line numbering. Java code can be compiled and
run within the program and a jar file can be created (containing only .class files, though).
<p>
REQUIREMENTS FOR TESTING <br>
Running the program requires JAVA 8 (JRE 8) and, for compiling java source files,
installing the JDK (I have used the JDK by Oracle). I did not have the opportunity to test
the program on operating systems other than windows Vista, 7 and 10.<p>
The program must be found in the same folder as the two .properties files and the Resources
folder, just like in the 'EadgythProgram' folder in this repository.<p>
Plugins must be stored in the folder 'Plugins' in the same folder as the program and be available
as single jar files.<p>
To compile the program the source files (packages) must be placed in a directory that also
includes the ".properties" files and the 'Resources' folder.<p>
A guide how to configure a project and how to use the console is provided in the Resources
folder (Help.html) and is adressable from the '?' menu. 
<p>
DOCUMENTATION <br>
The javadoc of the program is found in this repository (Ead-JavaDoc).
<p>
IDEA FOR FURTHER DEVELOPMENT AND USAGE <br>
The program includes an interface that defines an object type that can be used by the
Editor to configure and run a (programming) project. This interface is called
'ProjectActions' in the package 'eg.projects'. As an example for using the interface
to implement another "Actions", the 'HtmlActions' class is added (which simply executes
html code writen in the editor in a browser, though). Therefore, the program may be
adapted to special needs or be genarally extended.<br>
<p>
The pogram includes the possiblity to add plugins as jar files. A plugin must implement
the interface 'Pluggable' found in 'eg.plugin' package. Such plugin has access to the text
document in the currently selected tab and can have a graphical view that is integrated in
the main window. A template for writing a plugin is included<br>
<p>
LIMITATIONS <br>
1) The Undo/redo is a place holder for a real undo/redo as it simply undoes/redoes single
characters.
<br>
2) The console has a function to run system commands (defined the class 'ProcessStarter'
in the package 'eg.console'). Interactive programs that then require reading from and
writing to the started process by typing in the console are not guaranteed to work.
Interactive programs in Java seem to work fine.
<br>
3) The built in packing of a jar file bundles only .class files.
<br>
<p>
PROBLEMS <br>
4) The rendering of the GUI has gone wrong after the computer was in sleep mode when
the system look and feel was selected.
<br>
5) Removing/replacing chunks of text leads under some condition to the coloring of text 
right above block comments in the block comment color (class eg.document.Coloring).
<p>
CONTACT<br>
Malte Bussiek<br>
m.bussiek@web.de<br>
<p>
CONTRIBUTORS<br>
I hope for contributions and critiques.<br>
<p>
LICENSE: MIT, see LICENSE<br>
<p>
SCREENSHOT<br>
Under Windows 10, setting the system look and feel<br>
<img src="Screenshots/Windows10SystemLAF.png" width="600"/>

