package net.yck.kangaroo.commons.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

public class CSVReaderBuilder {

  public static char SEPARATOR_COMMA = ',';
  public static char SEPARATOR_TAB = '\t';

  private static String SUFFIX_GZIPPED = ".gz";

  private final String file;

  private char separator = CSVParser.DEFAULT_SEPARATOR;
  private Charset charset = StandardCharsets.UTF_8;
  private int skipLines = CSVReader.DEFAULT_SKIP_LINES;

  public CSVReaderBuilder(String file) {
    checkArgument(!StringUtils.isEmpty(file));
    this.file = file;
  }

  public CSVReaderBuilder separator(String separator) {
    checkArgument(!StringUtils.isEmpty(separator));
    return this.separator(separator.charAt(0));
  }

  public CSVReaderBuilder separator(char separator) {
    this.separator = separator;
    return this;
  }

  public CSVReaderBuilder charset(Charset charset) {
    this.charset = charset;
    return this;
  }

  public CSVReaderBuilder skipLines(int skipLines) {
    this.skipLines = skipLines;
    return this;
  }

  public CSVReader build() throws IOException {

    InputStream fis = new FileInputStream(file);
    if (file.toLowerCase().endsWith(SUFFIX_GZIPPED)) {
      fis = new GZIPInputStream(fis);
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charset));
    return new CSVReader(reader, separator, CSVParser.DEFAULT_QUOTE_CHARACTER, skipLines);
  }
}
