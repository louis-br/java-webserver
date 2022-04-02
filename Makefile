all:
	rm -f WebServer.class
	javac -d classes WebServer.java
run: all
	cd classes && java WebServer