<h3>Eadgyth Programming Editor</h3>
<p>
A text editor which can be easily set up to run source code. So far, built-in
functions to run a program are available for code written in Java, Perl, Python, R and
HTML. Extending the source code of the editor to add support for other langauages can
be reasonably easily realized too. 
<br>
<p>
<h4>Features for editing text are ...</h4>
<ul>
<li>Find/replace in a dockable panel.</li>
<li>Clearing end-of-line (trailing) white spaces.</li>
<li>A dockable "exchange editor" view as a simple notepad next to the main editor
    and for exchanging text.</li>
<li>A basic syntax highlighting (for Java, Perl, Python, R, HTML, XML, CSS,
    Javascript, PHP).</li>
<li>Choosing between spaces and tabs for indentation.</li>
<li>Auto-indentation which distinguishes "curly-bracket-indentation".</li>
<li>Block-wise increase or decrease of the indentation.</li>
</ul>
<p>
<h4>Features for running source code are ...</h4>
<ul>
<li>A console panel for showing output/error during running (or compiling) a program.</li>
<li>Run a program by pre-defined functions or run self-chosen system commands.</li>
<li>Specify additional command options/arguments for running a program</li>
<li>Compile and run Java code that is organized in packages and create an executabe jar
    file of a Java program. Also, specify external Libraries.</li>
<li>View HTML code in the default Browser.</li>
<li>Set several coding projects which can be switched between.</li>
<li>Retrieval of projects after newly starting the program.</li>
</ul>
<p>
A little more about it and an example how to set up the editor for running code is found
<a href="https://eadgyth.github.io/Programming-Editor/">here</a>.
To try the program the executable jar file in a
<a href="https://github.com/Eadgyth/Programming-Editor/releases">release</a> may be used.
<br>
<h4>REQUIREMENTS</h4>
<p>
Running and compiling the program requires Java 8 or higher. Building an executable jar
file of the editor can be done, for example, after creating a Java project with existing
sources in Netbeans using the src folder from the repository as sources directory.
<p>
If this editor shall be used for compiling Java code by the built-in compile option it must
be made sure that it is run using the JRE contained in a JDK (and not the public JRE).
<p>
For using the built-in function to run code the path variables that point to the executables
of a programming language may have to be set in the OS (they have to under Windows).
<br>
<h4>LIMITATIONS</h4>
<p>
Countless...
<p>
Output to the console panel does not work as expected in cases where a run process buffers
its output if it is not sent to the standard output. To display output correctly buffering
would have to be disabled by a corresponding command-line switch or by switch in a script
itself. Currently, this limitation applies to interactive Perl scripts and may apply to
custom commands.
<br>
<h4>ACKNOWLEDGEMENTS</h4>
<p>
Special thanks go to William Gilreath for his advice, comments and enthusiasm.
<br>
<p>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<br>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
<p>
<h4>SCREENSHOT</h4>
<p>
An example Java "project" run in the editor:
<br>
<br>
<img src="docs/images/ExampleProject.png" width="800"/><br><br>
