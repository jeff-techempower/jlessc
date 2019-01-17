package com.inet.lib.less;

public final class CssFormatterFactory {
    public static CssFormatter getInstance(CompilerOptions.CompressionStatus compressionStatus) {
        switch (compressionStatus) {
            case DEFAULT:
            case UNCOMPRESSED:
                return new CssFormatter();
            case COMPRESSED:
                return new CompressCssFormatter();
            default:
                throw new IllegalStateException("Unsupported compression status.");
        }
    }
}
