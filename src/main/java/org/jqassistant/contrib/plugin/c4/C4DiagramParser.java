package org.jqassistant.contrib.plugin.c4;

import org.antlr.v4.runtime.*;
import org.jqassistant.contrib.plugin.c4.antlr.C4Lexer;
import org.jqassistant.contrib.plugin.c4.antlr.C4Parser;
import org.jqassistant.contrib.plugin.c4.data.C4Diagram;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Parser for C4 diagrams.
 *
 * @author Stephan Pirnbaum
 */
public class C4DiagramParser {

    public C4Diagram parseDiagram(InputStream is) throws IOException {
        C4Lexer l = new C4Lexer(new ANTLRInputStream(is));
        C4Parser p = new C4Parser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });

        C4ParseListener c4ParseListener = new C4ParseListener(new ArrayList<>(), new ArrayList<>());
        p.addParseListener(c4ParseListener);
        p.c4();

        return new C4Diagram(c4ParseListener.getC4Elements(), c4ParseListener.getC4ElementRelations());
    }
}
