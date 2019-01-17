package com.inet.lib.less;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ErrorTest {
    private static final String TEST_FILE_LOCATION = "../test/com/inet/lib/less/samples/error/";

    private void assertLessException( String relativeTestFileLocation, String expectedErrorMessage ) {
        try {
            File testFile = getTestFile(relativeTestFileLocation);
            this.getDefaultCompiler().compile(testFile);
            fail( "LessException expected" );
        }
        catch (IOException e){
            fail("Problem compilingZ file" + relativeTestFileLocation);
        }
        catch( LessException lex ) {
            String message = lex.getMessage();
            System.out.println("COMPILATION SUCCEEDED");
            assertEquals( expectedErrorMessage, message.substring( 0, message.indexOf( System.lineSeparator() ) ) );
        }
    }

    private LessCompiler getDefaultCompiler()
    {
        CompilerOptions options = CompilerOptions.builder()
            .setCompressionStatus( CompilerOptions.CompressionStatus.UNCOMPRESSED ).build();
        return LessCompiler.configuredWith( options );
    }

    @Test
    public void parenthesisWithComma() {
       File file = getTestFile("valid-parentheses.less");
        try {
            this.getDefaultCompiler().compile(file);
        }
        catch (IOException e)
        {
            fail(e.getMessage());
        }
        assertLessException("invalid-parentheses.less", "Unrecognized input" );
    }

    @Test
    public void maxDiffTypes() {
        assertLessException( "max-diff-types.less", "Incompatible types" );
    }

    @Test
    public void minDiffTypes() {
        assertLessException( "min-diff-types.less", "Incompatible types" );
    }

    @Test
    public void unrecognizedInput1() {
        assertLessException( "unrecognized-input-1.less", "Unrecognized input: '>'" );
    }

    @Test
    public void unrecognizedInput2() {
        assertLessException( "unrecognized-input-2.less", "Unrecognized input: '>'" );
    }

    @Test
    public void unrecognizedInput3() {
        assertLessException( "unrecognized-input-3.less", "Unrecognized input: 'a:}'" );
    }

    @Test
    public void unrecognizedInput4() {
        assertLessException( "unrecognized-input-4.less", "Unrecognized input: 'a:)'" );
    }

    @Test
    public void unrecognizedInput5() {
        assertLessException( "unrecognized-input-5.less", "Unrecognized input: '/*comment'" );
    }

    @Test
    public void unrecognizedInput6() {
        assertLessException( "unrecognized-input-6.less", "Unrecognized input: '@{a;'" );
    }

    @Test
    public void unrecognizedInput7() {
        assertLessException( "unrecognized-input-7.less", "Unrecognized input: 'xyz{'" );
    }

    @Test
    public void unknownImportKeyword() {
        assertLessException( "unknown-import-keyword.less", "Unknown @import keyword: xyz" );
    }

    @Test
    public void undefinedVariableInSelectorInput() {
        assertLessException( "undefined-variable-in-selector-input.less", "Undefined Variable: @b in a@{b}c" );
    }

    @Test
    public void unexpectedEOF1() {
        assertLessException( "unexpected-eof-1.less", "Unexpected end of Less data" );
    }

    @Test
    public void unexpectedEOF2() {
        assertLessException( "unexpected-eof-2.less", "Unexpected end of Less data" );
    }

    @Test
    public void propsInRoot() {
        assertLessException( "props-in-root.less", "Properties must be inside selector blocks, they cannot be in the root." );
    }

    /**
     * test for a JIT error
     * https://github.com/i-net-software/jlessc/issues/20
     */
    @Test
    public void colorConst() {
        for( int i = 0; i < 10000; i++ ) {
            CompilerOptions options = CompilerOptions
                .builder()
                .setCompressionStatus( CompilerOptions.CompressionStatus.COMPRESSED ).build();
            LessCompiler compiler = LessCompiler.configuredWith( options );
            File file = getTestFile( "jit-error.less");
            try {
                assertEquals( Integer.toString( i ),".t{c:#d3d3d3}",
                        compiler.compile( file ).getCompiledCode());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    private File getTestFile(String fileName) {
        File testFile = new File("");
        try {
            String resolvedTestFileLocation = ErrorTest.TEST_FILE_LOCATION  + fileName;
            URL url = ClassLoader.getSystemResource(resolvedTestFileLocation);
            if (url == null) {
                fail("Expected test file " + fileName + " does not exist.");
            }
            testFile = new File(url.toURI());
            if(!testFile.exists()) {
                fail("Expected test file " + fileName + " does not exist.");;
            }
        } catch (URISyntaxException e) {
            fail("Expected test file " + fileName + " does not exist.");
        }
        return testFile;
    }
}
