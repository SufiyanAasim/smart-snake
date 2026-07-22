# Makefile for Smart Snake Game

# Compiler and packaging configurations
JAVAC = javac
JAVA = java
JAR = jar
SRC_DIR = src/project
OUT_DIR = out
DIST_DIR = dist
JAR_FILE = $(DIST_DIR)/SmartSnake.jar

# Detect OS for correct classpath separator (semicolon for Windows, colon for Unix)
ifeq ($(OS),Windows_NT)
    CP_SEP = ;
else
    CP_SEP = :
endif

CLASSPATH = "lib/sqlite-jdbc.jar$(CP_SEP)lib/slf4j-api.jar$(CP_SEP)lib/slf4j-simple.jar"
RUN_CLASSPATH = "$(JAR_FILE)$(CP_SEP)lib/sqlite-jdbc.jar$(CP_SEP)lib/slf4j-api.jar$(CP_SEP)lib/slf4j-simple.jar"

.PHONY: all compile package run clean

all: compile package

compile:
	mkdir -p $(OUT_DIR)
	$(JAVAC) -cp $(CLASSPATH) -d $(OUT_DIR) $(SRC_DIR)/*.java

package:
	mkdir -p $(DIST_DIR)
	$(JAR) --create --file $(JAR_FILE) --main-class project.Project -C $(OUT_DIR) project

run: all
	$(JAVA) -cp $(RUN_CLASSPATH) project.Project

clean:
	rm -rf out dist
