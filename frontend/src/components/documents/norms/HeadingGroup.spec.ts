import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import type { NormHtml } from "~/composables/useFetchNormContent";
import type { LegislationExpression } from "~/types/api";
import HeadingGroup from "./HeadingGroup.vue";

const createDefaultProps = () => ({
  html: {
    parts: {
      heading: "<h1>Test Heading</h1>",
      headingAuthorialNotes: "<p>Test Notes</p>",
      headingNotes: undefined,
      officialToc: undefined,
      prefaceContainer: undefined,
    },
    text: {
      headingAuthorialNotesLength: 10,
    },
    body: "",
    isEmpty: false,
  } as NormHtml,
  metadata: {
    name: "Test Law Name",
    alternateName: "Test Alternate Name",
  } as LegislationExpression,
});

vi.mock("./titles", () => ({
  getNormTitle: vi.fn(() => `getNormTitle title`),
}));

describe("HeadingGroup", () => {
  it("renders correctly with all props provided", () => {
    render(HeadingGroup, {
      props: createDefaultProps(),
    });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
    expect(document.querySelector(".dokumentenkopf")).toBeInTheDocument();
    expect(document.querySelector(".typo-headline3-regular")).toHaveTextContent(
      "Test Alternate Name",
    );

    expect(screen.getByText("Test Notes")).toBeInTheDocument();
  });

  it("still renders heading when no headingAuthorialNotes are provided", () => {
    const props = createDefaultProps();
    props.html.parts.headingAuthorialNotes = "";

    render(HeadingGroup, { props });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
  });

  it("shows the title of the norm if there is no heading", () => {
    const props = createDefaultProps();
    props.html.parts.heading = "";

    render(HeadingGroup, { props });

    expect(screen.getByText("Test Law Name")).toBeInTheDocument();
    expect(screen.queryByText("Test Heading")).not.toBeInTheDocument();
  });

  it("only shows the alternate name together with the heading", () => {
    const props = createDefaultProps();
    props.html.parts.heading = "";

    render(HeadingGroup, { props });

    expect(screen.queryByText("Test Alternate Name")).not.toBeInTheDocument();
  });
});
