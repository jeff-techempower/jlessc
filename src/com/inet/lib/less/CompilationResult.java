package com.inet.lib.less;

public class CompilationResult {
    private String compiledCode;
    private SourceMap sourceMap;

    private CompilationResult(String compiledCode, SourceMap sourceMap)
    {
        this.compiledCode = compiledCode;
        this.sourceMap = sourceMap;
    }

    public static CompilationResult withSourceMap(String compiledCode, SourceMap sourceMap)
    {
        return new CompilationResult(compiledCode, sourceMap);
    }

    public static CompilationResult cssOnly(String css)
    {
        return new CompilationResult(css, null);
    }

    public String getCompiledCode() {
        return compiledCode;
    }

    public SourceMap getSourceMap() {
        return this.sourceMap;
    }
}
