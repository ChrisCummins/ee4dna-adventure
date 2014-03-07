SRC  = $(shell ls *.java)
DOCS = index.html
LOG = run/RoomServer.log

all help:
	@echo ""
	@echo "Compilation:"
	@echo ""
	@echo "make compile        - Compile the IDL and all Java source code files"
	@echo ""
	@echo "Invocation:"
	@echo ""
	@echo "make start          - Start the server"
	@echo "make stop           - Stop the server"
	@echo "make status         - Get the current status of the server"
	@echo ""
	@echo "Cleanup:"
	@echo ""
	@echo "make clean          - delete temporary files and cleanup directory"
	@echo "make clean-log      - clear the runtime log"
	@echo ""
	@echo "General:"
	@echo ""
	@echo "make docs           - Generate JavaDoc documentation"
	@echo "make test           - Execute the automated tests"

compile:
	[ -d classes ] || mkdir classes
	[ -d idl ] || mkdir idl
	idlj -fall -td idl -verbose adventure.idl
	javac -cp classes -d classes -g idl/adventure/*.java
	javac -cp classes -d classes -g *.java

start:
	bash ./RoomServer start

stop:
	bash ./RoomServer stop

status:
	bash ./RoomServer status

clean: clean-log
	@rm -rvf classes idl *~ $(DOCS) *.html

# JavaDocs
doc docs documentation: $(DOCS)

$(DOCS): $(SRC)
	javadoc *.java

# Log file
.PHONY: log clean-log

log:
	@[ ! -f $(LOG) ] || cat $(LOG)

clean-log:
	@rm -fv $(LOG)

# Test suite
.PHONY: test tests

test:
	@./test/run
