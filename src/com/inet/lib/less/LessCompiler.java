package com.inet.lib.less;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
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
     * @param lessData the less you wish to compile.
     * @param baseUrl  the base url from which to import external less data.
     * @return css from the compiled Less.
     */
    public String compile(String lessData, URL baseUrl) {
        return performCompilation(lessData, baseUrl);
    }

    /**
     * @param file the less you wish to compile.
     * @return css from the compiled Less.
     */
    public String compile(File file) throws IOException {
        String lessData = new String(Files
                .readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        return performCompilation(lessData, file.toURI().toURL());
    }

    private String performCompilation(String lessData, URL baseUrl) {
        try {
            LessParser parser = new LessParser();
            parser.parse(baseUrl, new StringReader(lessData),
                    compilerOptions
                            .getReaderFactory());

            StringBuilder builder = new StringBuilder();
            CssFormatter formatter = compilerOptions
                    .getCompressionStatus() == CompressionStatus.COMPRESSED ? new
                    CompressCssFormatter() : new
                    CssFormatter();
            parser.parseLazy(formatter);
            formatter.format(parser, baseUrl, builder);
            return builder.toString();
        } catch (LessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new LessException(ex);
        }
    }
}
