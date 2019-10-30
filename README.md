<h3>Eadgyth Programming-Editor</h3>
<p>
This is a text editor which can be used for coding and which can be easily set up to run
code by using built-in functions. So far, built-in functions are included to run code
written in Java, Perl, Python, R and HTML.
<br>
<p>Some features for editing text are...
<ul>
<li>Undo/redo.</li>
<li>A basic find and find/replace function.</li>
<li>Clearing end-of-line (trailing) white spaces.</li>
<li>An "exchange editor" pane to edit text in a separate view and to facilitate
    the exchange of text within a file or between files.</li>
<li>A basic syntax highlighting (for Java, Perl, Python, R, HTML, XML, CSS,
    Javascript, PHP).</li>
<li>A basic auto-indentation which distinguishes "curly-bracket-indentation".</li>
<li>Block-wise increase or decrease of the indentation.</li>
</ul>
<p>
<Further features are ...
<ul>
<li>A basic console for showing output/error during running (or compiling) a program</li>
<li>Run a project by pre-defined functions or run self-chosen system commands</li>
<li>Set a number of coding projects which can be switched between.</li>
<li>Retrieval of already defined projects after newly starting the program.</li>
</ul>
<p>
To try the program the executable jar file in a
<a href="https://github.com/Eadgyth/Programming-Editor/releases">release</a> may be used.
A help site which contains some information about setting up the program to run
code is accessible in the '?' menu.
<br>
<p>
The image below shows an example Java "project" run in the editor.
<br>
<br>
<img src="docs/images/ExampleProject.png" width="700"/><br><br>
<h4>REQUIREMENTS</h4>
<p>
Running and compiling the program requires Java 8 or higher. Building an executable jar
file can be done, for example, after creating a Java project with existing sources in
Netbeans using the src folder from the repository as sources directory.
<p>
If this editor shall be used for compiling Java code by the built-in compile option it must
be made sure that it is run using the JRE contained in the JDK (and not the public JRE).
<p>
For using the built-in function to run code the path variables that point to the executables
of a programming language may have to be set in the OS (they have to under Windows).
<br>
<h4>LIMITATIONS</h4>
<p>
The editor includes a basic console to show output/error messages from a tested program
or from freely defined system commands and also allows to run interactive command-line
programms. However, the console does not work as expected if a process buffers its
output until completion in the case that the output is not to the terminal of the OS
(PERL, for example, but not Java). To display output correctly block-buffering would
have to be disabled if this option is available for a language (by a corresponding
command option which can be entered in the project settings or by a switch in a script
itself).
<br>
<h4>ACKLOWLEDGEMENT</h4>
<p>
The program uses icons from
<a href="https://github.com/Distrotech/tango-icon-theme">Tango Desktop Project</a>.
<br>
<h4>LICENSE</h4>
<p>
MIT, see LICENSE<br>
