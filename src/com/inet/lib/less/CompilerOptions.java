package com.inet.lib.less;

/**
 * Configurable options to change the behavior of the compiler.
 *
 * @author jfennell.
 */
class CompilerOptions {
    private CompressionStatus compressionStatus;
    private ReaderFactory readerFactory;

    private CompilerOptions() {
    }

    public static Builder builder() {
        return new ConcreteBuilder();
    }

    public CompressionStatus getCompressionStatus() {
        return this.compressionStatus;
    }

    public ReaderFactory getReaderFactory() {
        return this.readerFactory;
    }

    public interface Builder {
        Builder setCompressionStatus(CompressionStatus status);
        Builder setReaderFactory(ReaderFactory readerFactory);
        CompilerOptions build();
    }

    private static class ConcreteBuilder
            implements Builder {
        private CompressionStatus compressionStatus;
        private ReaderFactory readerFactory;

        @Override
        public Builder setCompressionStatus(CompressionStatus status) {
            this.compressionStatus = status;
            return this;
        }

        @Override
        public Builder setReaderFactory(ReaderFactory readerFactory) {
            this.readerFactory = readerFactory;
            return this;
        }

        @Override
        public CompilerOptions build() {
            CompilerOptions options = new CompilerOptions();
            options.compressionStatus = this.compressionStatus ==
                    null ? CompressionStatus.UNCOMPRESSED : this.compressionStatus;
            options.readerFactory = this.readerFactory == null ? new ReaderFactory
                    () : this.readerFactory;
            return options;
        }
    }
}
