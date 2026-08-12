import { render, screen } from "@testing-library/vue";
import Metadata from "./Metadata.vue";

describe("Metadata", () => {
  it("renders placeholders for missing values", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            type: "text",
            label: "Label 1",
          },
          {
            type: "badge",
            label: "Label 2",
            values: [],
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Label 1");
    expect(terms[0]?.nextElementSibling).toHaveTextContent("—");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("—");
  });

  it("renders text metadata item", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            type: "text",
            label: "Label 1",
            value: "Value 1",
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Label 1");
    expect(terms[0]?.nextElementSibling).toHaveTextContent("Value 1");
  });

  it("renders badge metadata items", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            type: "badge",
            label: "Label 1",
            values: ["Badge 1"],
          },
          {
            type: "badge",
            label: "Label 2",
            values: ["Badge 2", "Badge 3"],
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");

    expect(terms[0]).toHaveTextContent("Label 1");
    const badge1 = screen.getByText("Badge 1");
    expect(badge1).toHaveClass("bg-gray-100");

    expect(terms[1]).toHaveTextContent("Label 2");
    const badge2 = screen.getByText("Badge 2");
    const badge3 = screen.getByText("Badge 3");
    expect(badge2).toHaveClass("bg-gray-100");
    expect(badge3).toHaveClass("bg-gray-100");
  });

  it("renders mixed metadata items", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            type: "badge",
            label: "Label 1",
            values: ["Badge 1"],
          },
          {
            type: "text",
            label: "Label 2",
            value: "Value 1",
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");

    expect(terms[0]).toHaveTextContent("Label 1");
    const badge1 = screen.getByText("Badge 1");
    expect(badge1).toHaveClass("bg-gray-100");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("Value 1");
  });
});
