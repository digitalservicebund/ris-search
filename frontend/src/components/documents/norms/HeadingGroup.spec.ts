import { render, screen } from "@testing-library/vue";
import { describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";
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
  it("renders correctly with all props provided", async () => {
    render(HeadingGroup, {
      props: createDefaultProps(),
    });
    await nextTick();

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
    expect(document.querySelector(".dokumentenkopf")).toBeInTheDocument();
    expect(document.querySelector(".typo-headline3-regular")).toHaveTextContent(
      "Test Alternate Name",
    );

    expect(screen.getByText("Test Notes")).toBeInTheDocument();
  });

  it("still renders heading when no headingAuthorialNotes are provided", async () => {
    const props = createDefaultProps();
    props.htmlParts.headingAuthorialNotes = "";

    render(HeadingGroup, {
      props,
    });

    await nextTick();

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Test Heading",
    );
  });

  it("renders the client-only fallback content correctly", async () => {
    // To test the fallback content, we need to mock the client-only behavior
    // In this test, we'll just verify that the fallback template exists

    const ClientOnlyStub = {
      name: "ClientOnly",
      template: '<div class="fallback"><slot name="fallback" /></div>',
    };

    render(HeadingGroup, {
      props: createDefaultProps(),
      global: {
        stubs: {
          "client-only": ClientOnlyStub,
          ClientOnly: ClientOnlyStub,
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

    render(HeadingGroup, {
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
