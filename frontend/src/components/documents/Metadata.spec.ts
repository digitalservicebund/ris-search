import { render, screen } from "@testing-library/vue";
import Metadata from "./Metadata.vue";

describe("Metadata", () => {
  it("renders MetadataItems", async () => {
    render(Metadata, {
      props: {
        items: [
          {
            label: "Label 1",
            value: "Value 1",
          },
          {
            label: "Label 2",
          },
        ],
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Label 1");
    expect(terms[0]?.nextElementSibling).toHaveTextContent("Value 1");

    expect(terms[1]).toHaveTextContent("Label 2");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("—");
  });
});
