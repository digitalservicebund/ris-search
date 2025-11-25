import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import LegislationListItem from "./LegislationListItem.vue";
import type { LegislationWork } from "~/types";

describe("LegislationListItem", () => {
  it("links to the norm and shows its title", async () => {
    const item: LegislationWork = {
      "@type": "Legislation",
      "@id": "id",
      name: "Gesetz zur Sache",
      abbreviation: "GS",
      legislationIdentifier: "eli/de/act/2024/1",
      alternateName: "",
      legislationDate: "2024-01-01",
      datePublished: "2024-01-02",
      isPartOf: { name: "BGBl" },
      workExample: {
        "@type": "Legislation",
        "@id": "id-expr",
        legislationIdentifier: "eli/de/act/2024/1",
        legislationLegalForce: "InForce",
        temporalCoverage: "2024-01-01/",
        encoding: [],
        tableOfContents: [],
        hasPart: [],
      },
    };

    await renderSuspended(LegislationListItem, { props: { item } });

    expect(
      screen.getByRole("link", { name: /Gesetz zur Sache \(GS\)/ }),
    ).toBeInTheDocument();
    const link = screen.getByRole("link", { name: /Gesetz zur Sache \(GS\)/ });
    expect(link).toHaveAttribute(
      "href",
      expect.stringContaining("/norms/eli/de/act/2024/1"),
    );
  });
});
