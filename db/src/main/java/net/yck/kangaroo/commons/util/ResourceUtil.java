package net.yck.kangaroo.commons.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUtil {

  public static Path getPath(Class<?> clazz, String first, String... more)
      throws URISyntaxException {
    String relativePath = Paths.get(first, more).toString();
    URL resource = clazz.getResource(File.separator + relativePath);
    return Paths.get(resource.toURI());
  }

  public static String getPathAsString(Class<?> clazz, String first, String... more)
      throws URISyntaxException {
    return getPath(clazz, first, more).toString();
  }

  public static File getPathAsFile(Class<?> clazz, String first, String... more)
      throws URISyntaxException {
    return getPath(clazz, first, more).toFile();
  }
}
