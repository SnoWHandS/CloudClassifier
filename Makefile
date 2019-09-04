# Assignment 3 Makefile
# Dillon Heald
# 4 September 2019

JAVAC=/usr/bin/javac
JAVADOC=/usr/bin/javadoc
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -sourcepath $(SRCDIR) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= CloudData.class Cloudscapes.class

JAVAS= CloudData.java Cloudscapes.java

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class
	
javadoc:
	#No idea why this wont work. running it manually in ./src does work. - I accept defeat.
	$(JAVADOC) -d ./Docs -sourcepath $(SRCDIR) $(JAVAS)

run:
	java -cp bin Cloudscapes "simplesample_input.txt"
	
largeRun:
	java -cp bin Cloudscapes "largesample_input.txt"