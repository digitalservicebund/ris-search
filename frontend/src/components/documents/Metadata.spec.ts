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
            color: "gray",
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
            color: "gray",
          },
          {
            type: "badge",
            label: "Label 2",
            values: ["Badge 2", "Badge 3"],
            color: "green",
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");

    expect(terms[0]).toHaveTextContent("Label 1");
    expect(screen.getByText("Badge 1")).toHaveClass(/border-gray/);

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(screen.getByText("Badge 2")).toHaveClass(/border-green/);
    expect(screen.getByText("Badge 3")).toHaveClass(/border-green/);
  });

  it("renders mixed metadata items", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            type: "badge",
            label: "Label 1",
            values: ["Badge 1"],
            color: "gray",
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
    expect(screen.getByText("Badge 1")).toHaveClass(/border-gray/);

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("Value 1");
  });
});
