package org.jqassistant.contrib.plugin.c4;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.jqassistant.contrib.plugin.c4.antlr.C4Lexer;
import org.jqassistant.contrib.plugin.c4.antlr.C4Parser;
import org.jqassistant.contrib.plugin.c4.data.C4Diagram;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for C4 diagrams.
 *
 * @author Stephan Pirnbaum
 */
public class C4DiagramParser {

    /**
     * Parses the given C4 diagram and maps it to the internal representation.
     *
     * @param is The C4 diagram as a {@link InputStream}.
     * @param fallbackName The diagram name to use when no is given in the .puml file.
     *
     * @return The {@link C4Diagram}.
     *
     * @throws IOException If the diagram could not be parsed due to file access.
     */
    public C4Diagram parseDiagram(InputStream is, String fallbackName) throws IOException {
        C4Lexer l = new C4Lexer(new ANTLRInputStream(is));
        C4Parser p = new C4Parser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
            }
        });

        C4ParseListener c4ParseListener = new C4ParseListener();
        p.addParseListener(c4ParseListener);
        p.c4();

        return new C4Diagram(c4ParseListener.getName() != null ? c4ParseListener.getName() : fallbackName, c4ParseListener.getC4Elements(), c4ParseListener.getC4ElementRelations());
    }

}
