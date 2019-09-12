# Assignment 3 Makefile
# Dillon Heald
# 4 September 2019

#For use on cluster
#JAVAC=/home/dheald/apps/java/jdk-11.0.4/bin/javac
#JAVA=/home/dheald/apps/java/jdk-11.0.4/bin/java

#For use on laptop
JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
JAVADOC=/usr/bin/javadoc
.SUFFIXES: .java .class

SRCDIR=src
BINDIR=bin
DOC=doc
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
	#$(JAVADOC) -d ./Docs -sourcepath $(SRCDIR) $(JAVAS)
	$(JAVADOC) -d $(DOC) $(SRCDIR)/*.java

run:
	$(JAVA) -cp bin Cloudscapes "simplesample_input.txt" "Mysimplesample_output.txt"
difSizerun:
	$(JAVA) -cp bin Cloudscapes "1-2xsimplesample_input.txt" "1-2xsimplesample_output.txt"
	$(JAVA) -cp bin Cloudscapes "1-4xsimplesample_input.txt" "1-4xsimplesample_output.txt"
	$(JAVA) -cp bin Cloudscapes "1-8xsimplesample_input.txt" "1-8xsimplesample_output.txt"
	$(JAVA) -cp bin Cloudscapes "1-16xsimplesample_input.txt" "1-16xsimplesample_output.txt"
	$(JAVA) -cp bin Cloudscapes "1-32xsimplesample_input.txt" "1-32xsimplesample_output.txt"

largeRun:
	$(JAVA) -cp bin Cloudscapes "largesample_input.txt" "Mylargesample_output.txt"

optimisedRun:
	$(JAVA) -cp bin Cloudscapes "bestSplit" "Mylargesample_output.txt"
