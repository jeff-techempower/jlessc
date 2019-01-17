package com.inet.lib.less;

import com.inet.lib.less.debugging.sourcemap.SourceMapFormat;

/**
 * Configurable options to change the behavior of the compiler.
 *
 */
class CompilerOptions {
    private CompressionStatus compressionStatus;
    private ReaderFactory readerFactory;
    private SourceMapStatus sourceMapStatus;
    private SourceMapFormat sourceMapFormat;

    private CompilerOptions() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public CompressionStatus getCompressionStatus() {
        return this.compressionStatus;
    }

    public ReaderFactory getReaderFactory() {
        return this.readerFactory;
    }

    public SourceMapStatus getSourceMapStatus()
    {
      return this.sourceMapStatus;
    }

    public boolean sourceMapsEnabled()
    {
        return this.getSourceMapStatus() == SourceMapStatus.INCLUDE_SOURCEMAPS;
    }

    public SourceMapFormat getSourceMapFormat()
    {
      return this.sourceMapFormat;
    }

    public static class Builder
    {
        private CompressionStatus compressionStatus;
        private ReaderFactory readerFactory;
        private SourceMapStatus sourceMapStatus;
        private SourceMapFormat sourceMapFormat;

        private Builder()
        {
            //only allow instantiation through static factory method
        }

        public Builder setCompressionStatus(CompressionStatus status) {
            this.compressionStatus = status;
            return this;
        }

        public Builder setReaderFactory(ReaderFactory readerFactory) {
            this.readerFactory = readerFactory;
            return this;
        }

        public Builder setSourceMapStatus( SourceMapStatus status ) {
            this.sourceMapStatus = status;
            return this;
        }

        public Builder setSourceMapFormat(SourceMapFormat format)
        {
            this.sourceMapFormat = format;
            return this;
        }

        public CompilerOptions build() {
            CompilerOptions options = new CompilerOptions();
            options.compressionStatus = this.compressionStatus ==
                    null ? CompressionStatus.COMPRESSED : this.compressionStatus;
            options.readerFactory = this.readerFactory == null ? new ReaderFactory
                    () : this.readerFactory;
            options.sourceMapStatus = this.sourceMapStatus == null ?
                SourceMapStatus.NO_SOURCEMAPS : options.sourceMapStatus;
            options.sourceMapFormat = this.sourceMapFormat == null ? SourceMapFormat.DEFAULT : this.sourceMapFormat;
            return options;
        }
    }

    /**
     * Indicates whether or not the compiler is configured to produce sourcemaps for target files.
     */
    public enum SourceMapStatus {
        INCLUDE_SOURCEMAPS,
        NO_SOURCEMAPS
    }

    /**
     * Indicates whether or not code generated should be compressed.
     */
    public enum CompressionStatus {
        DEFAULT,
        UNCOMPRESSED,
        COMPRESSED
    }
}
