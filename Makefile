# Makefile for Smart Snake Game

# Compiler and packaging configurations
JAVAC = javac
JAVA = java
JAR = jar
SRC_DIR = src/project
OUT_DIR = out/production/Project
DIST_DIR = dist
JAR_FILE = $(DIST_DIR)/SmartSnakeGame.jar

.PHONY: all compile package run clean

all: compile package

compile:
	mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(SRC_DIR)/*.java

package:
	mkdir -p $(DIST_DIR)
	$(JAR) --create --file $(JAR_FILE) --main-class project.Project -C $(OUT_DIR) project

run: all
	$(JAVA) -jar $(JAR_FILE)

clean:
	rm -rf out dist
