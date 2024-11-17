package net.sourceforge.plantuml.nassidiagram.command;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.regex.IRegex;
import net.sourceforge.plantuml.regex.RegexConcat;
import net.sourceforge.plantuml.regex.RegexLeaf;
import net.sourceforge.plantuml.regex.RegexResult;
import net.sourceforge.plantuml.nassidiagram.NassiDiagram;
import net.sourceforge.plantuml.nassidiagram.element.NassiConnector;
import net.sourceforge.plantuml.nassidiagram.element.NassiIf;
import net.sourceforge.plantuml.nassidiagram.element.NassiWhile;
import net.sourceforge.plantuml.utils.LineLocation;
import net.sourceforge.plantuml.command.ParserPass;
import net.sourceforge.plantuml.nassidiagram.NassiElement;

public class CommandNassiConnector extends SingleLineCommand2<NassiDiagram> {

    public CommandNassiConnector() {
        super(getRegexConcat());
    }

    static IRegex getRegexConcat() {
        return RegexConcat.build(CommandNassiConnector.class.getName(),
                RegexLeaf.start(),
                new RegexLeaf("connector"),
                RegexLeaf.spaceOneOrMore(),
                new RegexLeaf("CONTENT", "\"([^\"]+)\""),
                RegexLeaf.end());
    }

    @Override
    protected CommandExecutionResult executeArg(NassiDiagram diagram, LineLocation location, RegexResult arg, ParserPass pass) {
        String content = arg.get("CONTENT", 0);
        NassiConnector connector = new NassiConnector(content);
        
        // Get current control structure
        NassiElement current = diagram.getCurrentControlStructure();
        
        // Handle nesting
        if (current != null) {
            if (current instanceof NassiIf) {
                // Inside an if statement
                NassiIf parentIf = (NassiIf) current;
                parentIf.addToCurrentBranch(connector);
                connector.setParent(parentIf);
            } else if (current instanceof NassiWhile) {
                // Inside a while loop
                NassiWhile parentWhile = (NassiWhile) current;
                parentWhile.addBodyElement(connector);
                connector.setParent(parentWhile);
            } else {
                // Unknown parent type - add to diagram
                diagram.addElement(connector);
            }
        } else {
            // Root level connector
            diagram.addElement(connector);
        }
        
        return CommandExecutionResult.ok();
    }
} 