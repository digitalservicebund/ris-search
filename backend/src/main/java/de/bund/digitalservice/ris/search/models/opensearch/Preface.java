package de.bund.digitalservice.ris.search.models.opensearch;

import java.util.List;
import org.springframework.data.elasticsearch.annotations.Field;

public record Preface(
    @Field(name = Fields.E_ID) String eid,
    @Field(name = Fields.CONTENT) List<String> content,
    @Field(name = Fields.FOOT_NOTES) List<String> footNotes,
    @Field(name = Fields.AUTHORIAL_NOTES) List<String> authorialNotes) {

  public static class Fields {
    private Fields() {}

    public static final String E_ID = "eid";
    public static final String CONTENT = "content";
    public static final String FOOT_NOTES = "foot_notes";
    public static final String AUTHORIAL_NOTES = "authorial_notes";
  }
}
