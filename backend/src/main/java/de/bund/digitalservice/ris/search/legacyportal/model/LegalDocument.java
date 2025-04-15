package de.bund.digitalservice.ris.search.legacyportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.legacyportal.enums.LegalDocumentType;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@Builder
@EqualsAndHashCode
@Document(indexName = "legaldocuments")
@Setting(settingPath = "/legacyportal/elasticsearch/es_german.json")
public class LegalDocument {

  @Id
  @Field(type = FieldType.Text, name = "id")
  private String id;

  @MultiField(
      mainField = @Field(type = FieldType.Text, name = "identifier"),
      otherFields = {@InnerField(type = FieldType.Keyword, suffix = "keyword")})
  private String identifier;

  @MultiField(
      mainField =
          @Field(type = FieldType.Text, name = "title", analyzer = "custom_german_analyzer"),
      otherFields = {@InnerField(type = FieldType.Keyword, suffix = "keyword")})
  private String name;

  @MultiField(
      mainField = @Field(type = FieldType.Text, name = "documenttype"),
      otherFields = {@InnerField(type = FieldType.Keyword, suffix = "keyword")})
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private LegalDocumentType documenttype;

  @Field(type = FieldType.Text, name = "documentUri")
  private String documentUri;

  @Field(type = FieldType.Text, name = "alternateName")
  private String alternateName;

  @Field(type = FieldType.Text, name = "docTitle")
  private String docTitle;

  @Field(type = FieldType.Text, name = "content", analyzer = "custom_german_analyzer")
  private String text;

  @Field(type = FieldType.Text, name = "xmlFilePath")
  private String xmlFilePath;

  @Field(type = FieldType.Text, name = "printAnnouncementGazette")
  private String printAnnouncementGazette;

  @Field(type = FieldType.Integer, name = "printAnnouncementYear")
  private int printAnnouncementYear;

  @Field(type = FieldType.Text, name = "printAnnouncementPage")
  private String printAnnouncementPage;

  @Field(type = FieldType.Nested, name = "tableOfContents", enabled = false)
  private List<ContentItem> listOfContentItem;

  @Field(type = FieldType.Text, name = "version")
  private String version;

  @Field(type = FieldType.Text, name = "globalUID")
  private String globalUID;
}
