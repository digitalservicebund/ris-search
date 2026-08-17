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
    expect(screen.getByText("Badge 1")).toHaveClass("border-gray-400");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(screen.getByText("Badge 2")).toHaveClass("border-gray-400");
    expect(screen.getByText("Badge 3")).toHaveClass("border-gray-400");
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
    expect(screen.getByText("Badge 1")).toHaveClass("border-gray-400");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("Value 1");
  });
});
