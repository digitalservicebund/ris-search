package de.bund.digitalservice.ris.search.schema;

/** Collection of Strings that are used in the @type field of response objects */
public class JsonldTypes {
  private JsonldTypes() {}

  public static final String LEGISLATION = "Legislation";
  public static final String LEGISLATION_OBJECT = "LegislationObject";
  public static final String DECISION = "Decision";
  public static final String MEDIA_OBJECT = "MediaObject";
  public static final String ADMINISTRATIVE_DIRECTIVE = "AdministrativeDirective";
  public static final String HYDRA_COLLECTION = "hydra:Collection";
  public static final String PUBLICATION_ISSUE = "PublicationIssue";
  public static final String SEARCH_RESULT = "SearchResult";
  public static final String SEARCH_RESULT_MATCH = "SearchResultMatch";
  public static final String LITERATURE = "Literature";
  public static final String HYDRA_PARTIAL_COLLECTION_VIEW = "hydra:PartialCollectionView";
  public static final String DATA_CATALOG = "DataCatalog";
  public static final String DATASET = "Dataset";
  public static final String DATA_DOWNLOAD = "DataDownload";
}
