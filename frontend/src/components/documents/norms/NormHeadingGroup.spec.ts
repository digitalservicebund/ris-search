import { render, screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import { nextTick } from "vue";
import NormHeadingGroup from "./NormHeadingGroup.vue";
import type { LegislationWork } from "~/types";

const createDefaultProps = () => ({
  htmlParts: {
    heading: "<h1>Test Heading</h1>",
    headingAuthorialNotes: "<p>Test Notes</p>",
    headingAuthorialNotesLength: 180,
  },
  metadata: {
    name: "Test Law Name",
    alternateName: "Test Alternate Name",
  } as LegislationWork,
});

vi.mock("@digitalservicebund/ris-ui/components", () => ({
  RisExpandableText: {
    name: "RisExpandableText",
    props: ["length"],
    template:
      '<div data-testid="ris-expandable-text" class="mock-expandable-text"><slot /></div>',
  },
}));

vi.mock("./titles", () => ({
  getNormTitle: vi.fn(() => `getNormTitle title`),
}));

const NormHeadingFootnotesStub = {
  name: "NormHeadingFootnotes",
  props: ["html", "textLength"],
  template:
    '<div data-testid="norm-heading-footnotes" :data-html="html" :data-text-length="textLength"></div>',
};

describe("HeadingGroup", () => {
  it("renders correctly with all props provided", async () => {
    render(NormHeadingGroup, {
      props: createDefaultProps(),
      global: { stubs: { NormHeadingFootnotes: NormHeadingFootnotesStub } },
    });
    await nextTick();

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
    expect(document.querySelector(".dokumentenkopf")).toBeInTheDocument();
    expect(document.querySelector(".ris-heading3-regular")).toHaveTextContent(
      "Test Alternate Name",
    );
    expect(screen.getAllByTestId("ris-expandable-text")).toHaveLength(1);

    const notesStub = screen.getByTestId("norm-heading-footnotes");
    expect(notesStub.dataset.html).toBe("<p>Test Notes</p>");
    expect(notesStub.dataset.textLength).toBe("180");
  });

  it("adds data-longtitle attribute for long titles", async () => {
    const longName =
      "Beschwerde- und Rechtsmittelzulassungsgesetz zur Sicherstellung der Glaubwürdigkeit von Entscheidungen der Einzelhandelsgerichte in Angelegenheiten des E-Commerce und der Vertragsabwicklung durch besondere Anforderungen an die Präsentation digitaler Gerichtsentscheidungen sowie zur Änderung des E-Commerce-Marktgesetzes und der Verbraucherinformationsgesetzgebung";
    const props = createDefaultProps();
    props.metadata.name = longName;

    render(NormHeadingGroup, {
      props,
    });

    await nextTick();

    const headingWrapper = document.querySelector("div[data-longtitle]");
    expect(headingWrapper).toHaveAttribute("data-longtitle", "true");
  });

  it("renders just one RisExpandableText if no headingAuthorialNotes are provided", async () => {
    const props = createDefaultProps();
    props.htmlParts.headingAuthorialNotes = "";

    render(NormHeadingGroup, {
      props,
    });

    await nextTick();

    // Should only have one RisExpandableText component
    expect(screen.getAllByTestId("ris-expandable-text")).toHaveLength(1);
  });

  it("renders the client-only fallback content correctly", async () => {
    // To test the fallback content, we need to mock the client-only behavior
    // In this test, we'll just verify that the fallback template exists

    const ClientOnlyStub = {
      name: "ClientOnly",
      template: '<div class="fallback"><slot name="fallback" /></div>',
    };

    render(NormHeadingGroup, {
      props: createDefaultProps(),
      global: {
        stubs: {
          "client-only": ClientOnlyStub,
          ClientOnly: ClientOnlyStub,
          NormHeadingFootnotes: NormHeadingFootnotesStub,
        },
      },
    });

    await nextTick();

    // Check if fallback content is rendered (heading exists, so .titel should not)
    expect(document.querySelector(".titel")).not.toBeInTheDocument();
  });

  it("renders the fallback content without heading correctly", async () => {
    const props = createDefaultProps();
    props.htmlParts.heading = "";

    const ClientOnlyStub = {
      name: "ClientOnly",
      template: '<div><slot name="fallback" /></div>',
    };

    render(NormHeadingGroup, {
      props,
      global: {
        stubs: {
          "client-only": ClientOnlyStub,
          ClientOnly: ClientOnlyStub,
        },
      },
    });

    await nextTick();

    // Should render the .titel element in fallback
    expect(document.querySelector(".titel")).toBeInTheDocument();
  });
});
