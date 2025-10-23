package de.bund.digitalservice.ris.search.integration.controller.api.values;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.bund.digitalservice.ris.search.integration.controller.api.testData.CaseLawTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.LiteratureTestData;
import de.bund.digitalservice.ris.search.integration.controller.api.testData.NormsTestData;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.provider.Arguments;

public class SortingTestArguments {
  public static Stream.Builder<Arguments> provideSortingTestArguments() {
    Stream.Builder<Arguments> stream = Stream.builder();

    int caseLawSize = CaseLawTestData.allDocuments.size();
    int literatureSize = LiteratureTestData.allDocuments.size();
    int normsSize = NormsTestData.allDocuments.size();
    int combinedSize = caseLawSize + literatureSize + normsSize;

    List<String> sortedDocumentNumbers =
        Stream.concat(
                CaseLawTestData.allDocuments.stream().map(CaseLawDocumentationUnit::documentNumber),
                LiteratureTestData.allDocuments.stream().map(Literature::documentNumber))
            .sorted()
            .toList();
    stream.add(
        Arguments.of(
            "documentNumber",
            caseLawSize + literatureSize,
            jsonPath("$.member[*].item.documentNumber", Matchers.is(sortedDocumentNumbers)),
            null));

    List<String> inverseSortedDocumentNumbers = new ArrayList<>(sortedDocumentNumbers);
    Collections.reverse(inverseSortedDocumentNumbers);
    stream.add(
        Arguments.of(
            "-documentNumber",
            caseLawSize + literatureSize,
            jsonPath("$.member[*].item.documentNumber", Matchers.is(inverseSortedDocumentNumbers)),
            null));

    List<String> caseLawDates =
        new ArrayList<>(
            CaseLawTestData.allDocuments.stream()
                .map(d -> d.decisionDate().toString())
                .sorted()
                .toList());
    List<String> literatureDates =
        new ArrayList<>(
            LiteratureTestData.allDocuments.stream()
                .map(d -> d.recordingDate().toString())
                .sorted()
                .toList());
    List<String> normsDates =
        new ArrayList<>(
            NormsTestData.allDocuments.stream()
                .map(d -> d.getNormsDate().toString())
                .sorted()
                .toList());
    stream.add(
        Arguments.of(
            "date",
            combinedSize,
            jsonPath("$.member[*].item.decisionDate", Matchers.is(caseLawDates)),
            jsonPath("$.member[*].item.recordingDate", Matchers.is(literatureDates)),
            jsonPath("$.member[*].item.legislationDate", Matchers.is(normsDates))));

    List<String> invertedCaseLawDates = new ArrayList<>(caseLawDates);
    Collections.reverse(invertedCaseLawDates);
    List<String> invertedLiteratureDates = new ArrayList<>(literatureDates);
    Collections.reverse(invertedLiteratureDates);
    List<String> invertedNormsDates = new ArrayList<>(normsDates);
    Collections.reverse(invertedNormsDates);
    stream.add(
        Arguments.of(
            "-date",
            combinedSize,
            jsonPath("$.member[*].item.decisionDate", Matchers.is(invertedCaseLawDates)),
            jsonPath("$.member[*].item.recordingDate", Matchers.is(invertedLiteratureDates)),
            jsonPath("$.member[*].item.legislationDate", Matchers.is(invertedNormsDates))));

    List<String> courtNames =
        CaseLawTestData.allDocuments.stream()
            .map(CaseLawDocumentationUnit::courtKeyword)
            .sorted()
            .toList();
    stream.add(
        Arguments.of(
            "courtName",
            caseLawSize,
            jsonPath("$.member[*].item.courtName", Matchers.is(courtNames)),
            null));

    List<String> invertedCourtNames = new ArrayList<>(courtNames);
    Collections.reverse(invertedCourtNames);
    stream.add(
        Arguments.of(
            "-courtName",
            caseLawSize,
            jsonPath("$.member[*].item.courtName", Matchers.is(invertedCourtNames)),
            null));
    return stream;
  }
}
