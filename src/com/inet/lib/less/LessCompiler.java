package com.inet.lib.less;

import com.inet.lib.less.debugging.sourcemap.SourceMapGeneratorFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author jfennell.
 */
public class LessCompiler {
    private CompilerOptions compilerOptions;

    private LessCompiler(CompilerOptions options) {
        this.compilerOptions = options;
    }

    /**
     * Create a new compiler instance using the specified options.
     *
     * @param options options to specify the behavior of the compiler.
     */
    public static LessCompiler configuredWith(CompilerOptions options) {
        return new LessCompiler(options);
    }

    /**
     * @param file the less you wish to compile.
     * @return css from the compiled Less.
     */
    public CompilationResult compile(File file) throws IOException {
        // todo - don't know if this is correct. Might fail if file is not utf-8.
        // see if possible to use the compilers' reader.
        String lessData = new String(Files
                .readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        CssFormatter formatter = CssFormatterFactory.getInstance(compilerOptions.getCompressionStatus());
        SourceMap sourceMap;
        String css;

        try {
            LessParser parser = new LessParser();
            parser.parse(file.toURI().toURL(), new StringReader(lessData),
                    compilerOptions
                            .getReaderFactory());
            StringBuilder builder = new StringBuilder();

            if (compilerOptions.sourceMapsEnabled())
            {
                sourceMap = SourceMap.fromGenerator(SourceMapGeneratorFactory.getInstance(compilerOptions.getSourceMapFormat()));
                formatter.setSourceMap(sourceMap);
            }
            parser.parseLazy(formatter);
            formatter.format(parser, file.toURI().toURL(), builder);
            css = builder.toString();
        } catch (LessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LessException(ex);
        }

        if (compilerOptions.sourceMapsEnabled())
        {
           return CompilationResult.withSourceMap(css, formatter.getSourceMap());
        }
        return CompilationResult.cssOnly(css);
    }
}
