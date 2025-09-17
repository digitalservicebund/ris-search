package de.bund.digitalservice.ris.search.config.obs;

import de.bund.digitalservice.ris.search.exception.FileTransformationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class MockS3Client implements S3Client {

  protected abstract void logObjectRetrievalError(String string);

  protected final String bucketname;

  private static final List<String> possibleProjectRoots = Arrays.asList("ris-search", "workspace");

  protected MockS3Client(String bucketname) {
    this.bucketname = bucketname;
  }

  protected Path findProjectRoot() {
    File currentDir = new File(System.getProperty("user.dir"));
    while (!possibleProjectRoots.contains(currentDir.getName())) {
      currentDir = currentDir.getParentFile();
    }
    return currentDir.toPath();
  }

  protected List<String> getFileNamesByPath(String directory) {
    try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
      return stream.filter(Files::isRegularFile).map(Path::toString).toList();
    } catch (NoSuchFileException e) {
      return new ArrayList<>();
    } catch (IOException e) {
      throw new FileTransformationException(String.format("Could not list files in %s", directory));
    }
  }

  public ListObjectsV2Response buildResponse(ListObjectsV2Request request, List<String> fileNames) {
    final long KEYS_COUNT_HARD_LIMIT = 1000;
    final String TOKEN_PREFIX = "token-";
    List<S3Object> contents;
    long offset;
    if (request.continuationToken() == null) {
      offset = 0;
    } else {
      offset = Long.parseLong(request.continuationToken().substring(TOKEN_PREFIX.length()));
    }
    if (fileNames == null || fileNames.isEmpty()) {
      contents = new ArrayList<>();
      return ListObjectsV2Response.builder().contents(contents).build();
    } else {
      contents =
          fileNames.stream()
              .skip(offset)
              .limit(KEYS_COUNT_HARD_LIMIT)
              .map(e -> S3Object.builder().key(e).build())
              .toList();
      ListObjectsV2Response.Builder response =
          ListObjectsV2Response.builder()
              .contents(contents)
              .continuationToken(request.continuationToken());
      long remainingFileCount = fileNames.size() - offset;
      if (remainingFileCount > KEYS_COUNT_HARD_LIMIT) {
        response.nextContinuationToken(TOKEN_PREFIX + (offset + KEYS_COUNT_HARD_LIMIT));
        response.isTruncated(true);
      } else {
        response.isTruncated(false);
      }
      return response.build();
    }
  }
}
