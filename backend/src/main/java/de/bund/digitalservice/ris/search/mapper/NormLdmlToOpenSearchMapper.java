package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.cleanText;

import de.bund.digitalservice.ris.search.models.Attachment;
import de.bund.digitalservice.ris.search.models.ldml.TimeInterval;
import de.bund.digitalservice.ris.search.models.opensearch.Article;
import de.bund.digitalservice.ris.search.models.opensearch.Norm;
import de.bund.digitalservice.ris.search.models.opensearch.TableOfContentsItem;
import de.bund.digitalservice.ris.search.utils.LdmlTemporalData;
import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.xpath.XPathExpressionException;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NormLdmlToOpenSearchMapper {
  private static final Logger logger = LogManager.getLogger(NormLdmlToOpenSearchMapper.class);

  private static final String X_PATH_SHORT_TITLE_ALTERNATE_NAME =
      "//*[local-name()='shortTitle']/text()";
  private static final String X_PATH_DOC_TITLE_NAME = "//*[local-name()='docTitle']/text()";
  private static final String X_PATH_WORK_THIS =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRthis']/@value";
  private static final String X_PATH_EXPRESSION_THIS =
      "//*[local-name()='FRBRExpression']/*[local-name()='FRBRthis']/@value";
  private static final String X_PATH_MANIFESTATION_THIS =
      "//*[local-name()='FRBRManifestation']/*[local-name()='FRBRthis']/@value";
  private static final String X_PATH_SHORT_TITLE_ABBREVIATION =
      "//*[local-name()='shortTitle']/*[local-name()='inline']/text()";
  private static final String X_PATH_DOC_TITLE_ABBREVIATION =
      "//*[local-name()='docTitle']/*[local-name()='inline']/text()";
  private static final String X_PATH_WORK_DATE =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRdate']/@date";
  private static final String X_PATH_DATE_AUSFERTIGUNG =
      "//*[local-name()='preface']/*[local-name()='block']/*[local-name()='date' and @refersTo='ausfertigung-datum']/@date";
  private static final String X_PATH_WORK_NUMBER =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRnumber']/@value";
  private static final String X_PATH_WORK_NAME =
      "//*[local-name()='FRBRWork']/*[local-name()='FRBRname']/@value";
  private static final String X_PATH_ARTICLE = "//*[local-name()='article']";
  private static final String X_PATH_ARTICLE_HEADING = ".//*[local-name()='heading']";
  private static final String X_PATH_ARTICLE_NUM = ".//*[local-name()='num']/text()";
  private static final String X_PATH_ARTICLE_PARAGRAPHS = ".//*[local-name()='paragraph']";
  private static final String X_PATH_ENTRY_INTO_FORCE_DATE =
      "/akn:akomaNtoso/akn:act/akn:meta/akn:proprietary/ris:legalDocML.de_metadaten/ris:inkraft/@date";
  private static final String X_PATH_EXPIRY_DATE =
      "/akn:akomaNtoso/akn:act/akn:meta/akn:proprietary/ris:legalDocML.de_metadaten/ris:ausserkraft/@date";

  private static final String X_PATH_BODY = "//*[local-name()='body']";
  private static final String X_PATH_CONCLUSIONS_FORMULA =
      "//*[local-name()='conclusions']/*[local-name()='formula']";
  private static final String X_PATH_PREAMBLE_FORMULA =
      "//*[local-name()='preamble']/*[local-name()='formula']";
  private static final String EINGANGSFORMEL = "Eingangsformel";
  private static final String SCHLUSSFORMEL = "Schlussformel";

  private NormLdmlToOpenSearchMapper() {}

  public static Optional<Norm> parseNorm(
      String xmlFile, Map<String, String> attachmentFileContents) {
    try {
      var xmlDocument = new XmlDocument(xmlFile.getBytes(StandardCharsets.UTF_8));
      @Nullable String workEli = xmlDocument.getElementByXpath(X_PATH_WORK_THIS);
      @Nullable String expressionEli = xmlDocument.getElementByXpath(X_PATH_EXPRESSION_THIS);
      @Nullable String manifestationEli = xmlDocument.getElementByXpath(X_PATH_MANIFESTATION_THIS);

      if (workEli == null || expressionEli == null || manifestationEli == null) {
        logger.warn("Could not parse ELI");
        return Optional.empty();
      }

      List<Attachment> attachments =
          NormAttachmentMapper.parseAttachments(xmlDocument, attachmentFileContents);

      final String officialAbbreviation = getOfficialAbbreviationByXmlDocument(xmlDocument);

      List<Article> articles =
          getArticlesByXmlDocument(xmlDocument, attachments, officialAbbreviation);
      List<String> articleNames = articles.stream().map(Article::name).toList();
      List<String> articleTexts = articles.stream().map(Article::text).toList();
      LocalDate expiryDate = getDateByXpath(xmlDocument, X_PATH_EXPIRY_DATE);
      LocalDate entryIntoForceDate = getDateByXpath(xmlDocument, X_PATH_ENTRY_INTO_FORCE_DATE);

      /*
      For differentiation of legislationDate and datePublished, see comments on Norm::normsDate and Norm::datePublished
      */
      LocalDate legislationDate = getDateByXpath(xmlDocument, X_PATH_DATE_AUSFERTIGUNG);
      LocalDate datePublished = getDateByXpath(xmlDocument, X_PATH_WORK_DATE);

      final List<TableOfContentsItem> tableOfContents =
          getTableOfContents(expressionEli, xmlDocument, attachments);

      return Optional.of(
          Norm.builder()
              .id(expressionEli)
              .tableOfContents(tableOfContents)
              .workEli(workEli)
              .expressionEli(expressionEli)
              .manifestationEliExample(manifestationEli)
              .officialTitle(getOfficialTitleByXmlDocument(xmlDocument))
              .officialShortTitle(getOfficialShortTitleByXmlDocument(xmlDocument))
              .officialAbbreviation(officialAbbreviation)
              .normsDate(legislationDate)
              .datePublished(datePublished)
              .publishedIn(getPublishedInByXmlDocument(xmlDocument, datePublished))
              .expiryDate(expiryDate)
              .entryIntoForceDate(entryIntoForceDate)
              .articles(articles)
              .articleNames(articleNames)
              .articleTexts(articleTexts)
              .indexedAt(Instant.now().toString())
              .build());
    } catch (Exception e) {
      logger.warn("Error to create Norms from XML content.", e);
      return Optional.empty();
    }
  }

  private static String appendWithSeparator(String base, String addition, String separator) {
    return StringUtils.isNotEmpty(base) ? base + separator + addition : addition;
  }

  private static String getPublishedInByXmlDocument(XmlDocument xmlDocument, LocalDate normsDate) {
    String name = xmlDocument.getElementByXpath(X_PATH_WORK_NAME);
    String number = xmlDocument.getElementByXpath(X_PATH_WORK_NUMBER);
    Map<String, String> mappedName = Map.of("bgbl-1", "BGBl I", "bgbl-2", "BGBl II");
    String publishedIn = "";

    if (StringUtils.isNotEmpty(name)) {
      publishedIn = mappedName.getOrDefault(name, "");
    }
    if (normsDate != null) {
      publishedIn = appendWithSeparator(publishedIn, String.valueOf(normsDate.getYear()), ", ");
    }
    if (StringUtils.isNotEmpty(number)) {
      number = number.replaceFirst("^s", "");
      publishedIn = appendWithSeparator(publishedIn, number, " ");
    }

    return StringUtils.isNotEmpty(publishedIn) ? publishedIn.trim() : StringUtils.EMPTY;
  }

  private static String getOfficialTitleByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentOfficialTitle = xmlDocument.getElementByXpath(X_PATH_DOC_TITLE_NAME);

    return StringUtils.isNotEmpty(xmlDocumentOfficialTitle)
        ? StringUtils.trimToNull(
                xmlDocumentOfficialTitle.replace(")", "").replace("(", "").replace("\n", " "))
            .replaceAll("\\s{2,}", " ")
        : StringUtils.EMPTY;
  }

  private static @Nullable String getOfficialShortTitleByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentAlternateName =
        xmlDocument.getElementByXpath(X_PATH_SHORT_TITLE_ALTERNATE_NAME);

    if (StringUtils.isEmpty(xmlDocumentAlternateName)) return null;

    String cleaned = xmlDocumentAlternateName.replace("(", "").replace("–", "").replace(" -", "");
    return StringUtils.trimToNull(cleaned);
  }

  private static String getOfficialAbbreviationByXmlDocument(XmlDocument xmlDocument) {
    String xmlDocumentShortTitleAbbreviation =
        xmlDocument.getElementByXpath(X_PATH_SHORT_TITLE_ABBREVIATION);

    if (StringUtils.isNotEmpty(xmlDocumentShortTitleAbbreviation)) {
      return xmlDocumentShortTitleAbbreviation;
    }

    String xmlDocumentDocTitleAbbreviation =
        xmlDocument.getElementByXpath(X_PATH_DOC_TITLE_ABBREVIATION);

    if (StringUtils.isNotEmpty(xmlDocumentDocTitleAbbreviation)) {
      return xmlDocumentDocTitleAbbreviation;
    }

    return StringUtils.EMPTY;
  }

  private static LocalDate getDateByXpath(XmlDocument xmlDocument, String xpath) {
    return toLocalDate(xmlDocument.getElementByXpath(xpath));
  }

  private static LocalDate toLocalDate(String date) {
    if (StringUtils.isEmpty(date)) {
      return null;
    }

    try {
      return LocalDate.parse(date.trim());
    } catch (DateTimeParseException exception) {
      logger.warn(String.format("Error parsing date: %s", date), exception);
      return null;
    }
  }

  private static List<TableOfContentsItem> getTableOfContents(
      String workEli, XmlDocument xmlDocument, List<Attachment> attachments) {
    List<TableOfContentsItem> mainToc =
        xmlDocument
            .getFirstMatchedNodeByXpath(X_PATH_BODY)
            .map(NormLdmlToOpenSearchMapper::getChildren)
            .orElseGet(
                () -> {
                  logger.warn("Error finding body of Norm {}", workEli);
                  return List.of();
                });

    List<TableOfContentsItem> preambleFormulaToc =
        getStaticTableOfContentsItem(xmlDocument, X_PATH_PREAMBLE_FORMULA, EINGANGSFORMEL);
    List<TableOfContentsItem> conclusionsFormulaToc =
        getStaticTableOfContentsItem(xmlDocument, X_PATH_CONCLUSIONS_FORMULA, SCHLUSSFORMEL);

    Stream<TableOfContentsItem> attachmentItems =
        attachments.stream()
            .map(a -> new TableOfContentsItem(a.eId(), a.marker(), a.docTitle(), List.of()));

    return StreamEx.of(preambleFormulaToc.stream())
        .append(mainToc.stream())
        .append(conclusionsFormulaToc.stream())
        .append(attachmentItems)
        .toList();
  }

  private static List<TableOfContentsItem> getStaticTableOfContentsItem(
      XmlDocument xmlDocument, String path, String heading) {
    return xmlDocument
        .getFirstMatchedNodeByXpath(path)
        .map(
            n ->
                List.of(
                    new TableOfContentsItem(
                        n.getAttributes().getNamedItem("eId").getNodeValue(),
                        "",
                        heading,
                        List.of())))
        .orElse(List.of());
  }

  private static List<TableOfContentsItem> getChildren(Node node) {
    NodeList nodes = node.getChildNodes();
    List<TableOfContentsItem> tableOfContents = new ArrayList<>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node childNode = nodes.item(i);
      getTableOfContentsItem(childNode).ifPresent(tableOfContents::add);
    }
    return tableOfContents;
  }

  private static Optional<TableOfContentsItem> getTableOfContentsItem(Node node) {
    if (node instanceof Element element) {
      String id = element.getAttribute("eId").trim();
      String nodeName = node.getNodeName();
      NodeList markers = element.getElementsByTagName("akn:num");
      NodeList headings = element.getElementsByTagName("akn:heading");
      if (markers.getLength() > 0 && !nodeName.equals("akn:paragraph")) {
        String marker = cleanText(markers.item(0).getTextContent());
        String heading = "";
        if (headings.getLength() > 0) {
          heading = cleanText(XmlDocument.extractDirectChildText(headings.item(0)));
        }
        List<TableOfContentsItem> children = new ArrayList<>();
        if (!nodeName.equals("akn:article")) {
          children = getChildren(node);
        }
        return Optional.of(new TableOfContentsItem(id, marker, heading, children));
      }
    }
    return Optional.empty();
  }

  private static List<Article> getArticlesByXmlDocument(
      XmlDocument xmlDocument, List<Attachment> attachments, String officialAbbreviation) {

    NodeList nodes = null;
    try {
      nodes = xmlDocument.getNodesByXpath(X_PATH_ARTICLE);
    } catch (XPathExpressionException e) {
      logger.warn("Error finding articles", e);
    }
    List<Article> articles = new ArrayList<>();

    if (nodes == null) {
      return articles;
    }

    Map<String, TimeInterval> temporalGroupsWithDates =
        LdmlTemporalData.getTemporalDataWithDatesMapping(xmlDocument);

    getNodeAsArticle(xmlDocument, X_PATH_PREAMBLE_FORMULA, EINGANGSFORMEL).ifPresent(articles::add);
    for (int i = 0; i < nodes.getLength(); i++) {
      Node articleNode = nodes.item(i);
      try {
        var articleXml = new XmlDocument(articleNode);
        String marker = cleanText(articleXml.getSimpleElementByXpath(X_PATH_ARTICLE_NUM));
        final var headingNode = articleXml.getFirstMatchedNodeByXpath(X_PATH_ARTICLE_HEADING);
        String heading =
            headingNode.map(node -> cleanText(XmlDocument.extractDirectChildText(node))).orElse("");
        String period = articleNode.getAttributes().getNamedItem("period").getTextContent();
        String eId = articleNode.getAttributes().getNamedItem("eId").getTextContent();
        String guid = articleNode.getAttributes().getNamedItem("GUID").getTextContent();
        NodeList paragraphNodes = articleXml.getNodesByXpath(X_PATH_ARTICLE_PARAGRAPHS);
        String text = "";
        for (int j = 0; j < paragraphNodes.getLength(); j++) {
          Node paragraphNode = paragraphNodes.item(j);
          String paragraphText = NormParagraphToTextMapper.extractTextFromParagraph(paragraphNode);
          text = text.concat(paragraphText).concat(" ");
        }

        LocalDate entryIntoForceDate = null;
        LocalDate expiryDate = null;

        TimeInterval timeInterval = temporalGroupsWithDates.get(period);
        if (timeInterval != null) {
          entryIntoForceDate = toLocalDate(timeInterval.start());
          expiryDate = toLocalDate(timeInterval.end());
        }

        final String articleHeader = buildArticleHeader(marker, heading);

        final @Nullable String searchKeyword = getSearchKeyword(marker, officialAbbreviation);

        articles.add(
            Article.builder()
                .guid(guid)
                .eId(eId)
                .name(articleHeader)
                .text(cleanText(text))
                .entryIntoForceDate(entryIntoForceDate)
                .expiryDate(expiryDate)
                .searchKeyword(searchKeyword)
                .build());
      } catch (Exception e) {
        logger.warn("Error parsing xml", e);
      }
    }

    getNodeAsArticle(xmlDocument, X_PATH_CONCLUSIONS_FORMULA, SCHLUSSFORMEL)
        .ifPresent(articles::add);

    var attachmentsAsArticles =
        attachments.stream()
            .map(
                a -> {
                  String name =
                      Stream.of(a.marker(), a.docTitle())
                          .filter(StringUtils::isNotBlank)
                          .collect(Collectors.joining(" "));
                  return Article.builder()
                      .name(name)
                      .eId(a.eId())
                      .text(a.textContent())
                      .manifestationEli(a.manifestationEli())
                      .build();
                })
            .toList();
    articles.addAll(attachmentsAsArticles);

    return articles;
  }

  /**
   * Builds a value for targeted article search using the article marker and abbreviation. For
   * instance, users might type "97 BGB", which should reveal that article first.
   *
   * @param marker The first part, e.g. "§ 97".
   * @param officialAbbreviation E.g. "BGB".
   * @return null if either part is missing, or the concatenation of both.
   */
  @Nullable
  private static String getSearchKeyword(String marker, String officialAbbreviation) {
    if (StringUtils.isBlank(officialAbbreviation) || StringUtils.isBlank(marker)) {
      return null;
    }
    return "%s %s".formatted(marker, officialAbbreviation);
  }

  private static Optional<Article> getNodeAsArticle(
      XmlDocument xmlDocument, String path, String name) {
    return xmlDocument
        .getFirstMatchedNodeByXpath(path)
        .map(
            node ->
                Article.builder()
                    .eId(
                        Optional.ofNullable(node.getAttributes().getNamedItem("eId"))
                            .map(Node::getTextContent)
                            .orElse(null))
                    .text(cleanText(node.getTextContent()))
                    .name(cleanText(name))
                    .build());
  }

  private static String buildArticleHeader(String articleMarker, String articleHeading) {
    if (!articleMarker.isEmpty() && !articleHeading.isEmpty()) {
      return String.format("%s %s", articleMarker, articleHeading);
    } else if (!articleMarker.isEmpty()) {
      return articleMarker;
    } else return Objects.requireNonNullElse(articleHeading, "");
  }
}
