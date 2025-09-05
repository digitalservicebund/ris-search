package de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "portal", name = "ecli_crawler_publications")
public class EcliSitemapMetadata {

  public EcliSitemapMetadata(
      String id, String ecli, String courtType, String decisionDate, String documentType) {
    this.id = id;
    this.ecli = ecli;
    this.courtType = courtType;
    this.decisionDate = decisionDate;
    this.documentType = documentType;
    this.isPublished = false;
  }

  @Id()
  @Column(name = "id")
  private String id;

  @Column(name = "ecli")
  private String ecli;

  @Column(name = "court_type")
  private String courtType;

  @Column(name = "decision_date")
  private String decisionDate;

  @Column(name = "document_Type")
  private String documentType;

  @Column(name = "is_published")
  private boolean isPublished;
}
