import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import type { LegislationExpression } from "~/types/api";
import HeadingGroup from "./HeadingGroup.vue";

const createDefaultProps = () => ({
  htmlParts: {
    heading: "<h1>Test Heading</h1>",
    headingAuthorialNotes: "<p>Test Notes</p>",
    headingAuthorialNotesLength: 180,
    body: "",
  },
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
    props.htmlParts.headingAuthorialNotes = "";

    render(HeadingGroup, { props });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
  });
});
