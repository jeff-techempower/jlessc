/*
 * Copyright 2009 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inet.lib.less;

import com.inet.lib.less.debugging.sourcemap.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import jdk.nashorn.internal.codegen.*;

/**
 * Collects information mapping the generated (compiled) source back to
 * its original source for debugging purposes.
 *
 * @see CodeGenerator
 *
 * @author johnlenz@google.com (John Lenz)
 */
public final class SourceMap {
  /**
   * An enumeration of available source map formats
   */
  public static enum Format {
    DEFAULT {
      @Override SourceMap getInstance() {
        return new SourceMap(
            SourceMapGeneratorFactory.getInstance(SourceMapFormat.DEFAULT));
      }
    },
    V3 {
      @Override SourceMap getInstance() {
        return new SourceMap(
            SourceMapGeneratorFactory.getInstance(SourceMapFormat.V3));
      }
    };
    abstract SourceMap getInstance();
  }

  /**
   * Function that maps a "destination" location to use within the source
   * map. Should return null
   * if the value is not mapped.
   */
  @FunctionalInterface
  public interface LocationMapping {
    /**
     * @param location the location to transform
     * @return the transformed location or null if not transformed
     */
    @Nullable
    String map(String location);
  }

  /**
   * Simple {@link LocationMapping} that strips a prefix from a location.
   */
  public static final class PrefixLocationMapping implements LocationMapping {
    final String prefix;
    final String replacement;
    public PrefixLocationMapping(String prefix, String replacement) {
      this.prefix = prefix;
      this.replacement = replacement;
    }

    @Override
    public String map(String location) {
      if (location.startsWith(prefix)) {
        return replacement + location.substring(prefix.length());
      }
      return null;
    }

    public String toString() {
      return "(" + prefix + "|" + replacement + ")";
    }

    @Override
    public boolean equals(Object other) {
      if (other instanceof PrefixLocationMapping) {
        return ((PrefixLocationMapping) other).prefix.equals(prefix)
            && ((PrefixLocationMapping) other).replacement.equals(replacement);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return Objects.hash(prefix, replacement);
    }
  }

  private final SourceMapGenerator generator;
  private List<? extends LocationMapping> prefixMappings = Collections.emptyList();
  private final Map<String, String> sourceLocationFixupCache =
      new HashMap<>();
  /**
   * A mapping derived from input source maps. Maps back to input sources that inputs to this
   * compilation job have been generated from, and used to create a source map that maps all the way
   * back to original inputs. {@code null} if no such mapping is wanted.
   */
  @Nullable
  private SourceFileMapping mapping;

  private SourceMap(SourceMapGenerator generator) {
    this.generator = generator;
  }

  public static SourceMap fromGenerator(SourceMapGenerator generator) {
    return new SourceMap(generator);
  }

  public void addMapping(
      LessObject lessObject,
      FilePosition outputStartPosition,
      FilePosition outputEndPosition) {
    String sourceFile = lessObject.getFileName();

    // If the node does not have an associated source file or
    // its line number is -1, then the node does not have sufficient
    // information for a mapping to be useful.
    if (sourceFile == null || lessObject.getLine() < 0) {
      return;
    }

    int lineNo = lessObject.getLine();
    int charNo = lessObject.getColumn();
     // TODO find out if this is necessary or not
//        String originalName = node.getOriginalName();
    String originalName = "";
    if (mapping != null) {
      Mapping.OriginalMapping sourceMapping = mapping.getSourceMapping(sourceFile, lineNo, charNo);
      if (sourceMapping != null) {
        sourceFile = sourceMapping.getOriginalFile();
        lineNo = sourceMapping.getLineNumber();
        charNo = sourceMapping.getColumnPosition();
        originalName = sourceMapping.getIdentifier();
      }
    }

    sourceFile = fixupSourceLocation(sourceFile);

    generator.addMapping(
        sourceFile, originalName,
        new FilePosition(lineNo, charNo),
        outputStartPosition, outputEndPosition);
  }

  public void addSourceFile(String name, String code) {
    generator.addSourcesContent(fixupSourceLocation(name), code);
  }

  /**
   * @param sourceFile The source file location to fixup.
   * @return a remapped source file.
   */
  private String fixupSourceLocation(String sourceFile) {
    if (prefixMappings.isEmpty()) {
      return sourceFile;
    }

    String fixed = sourceLocationFixupCache.get(sourceFile);
    if (fixed != null) {
      return fixed;
    }

    // Replace the first prefix found with its replacement
    for (LocationMapping mapping : prefixMappings) {
      fixed = mapping.map(sourceFile);
      if (fixed != null) {
        break;
      }
    }

    // If none of the mappings match then use the original file path.
    if (fixed == null) {
      fixed = sourceFile;
    }

    sourceLocationFixupCache.put(sourceFile, fixed);
    return fixed;
  }

  /**
   * Write out the sourcemap to the output destination.
   * @param out the apendable where you wish to append the output.
   * @param name Name of the file this sourcemap is associated with.
   * @throws IOException
   */
  public void appendTo(Appendable out, String name) throws IOException {
    generator.appendTo(out, fixupSourceLocation(name));
  }

  public void reset() {
    generator.reset();
    sourceLocationFixupCache.clear();
  }

  public void setStartingPosition(int offsetLine, int offsetIndex) {
    generator.setStartingPosition(offsetLine, offsetIndex);
  }

  public void setWrapperPrefix(String prefix) {
    generator.setWrapperPrefix(prefix);
  }

  public void validate(boolean validate) {
    generator.validate(validate);
  }

  /**
   * @param sourceMapLocationMappings
   */
  public void setPrefixMappings(List<? extends LocationMapping> sourceMapLocationMappings) {
    this.prefixMappings = sourceMapLocationMappings;
  }

  public void setSourceFileMapping(SourceFileMapping mapping) {
    this.mapping = mapping;
  }
}
