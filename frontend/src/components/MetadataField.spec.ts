import { render, screen } from "@testing-library/vue";
import MetadataField from "~/components/MetadataField.vue";

describe("MetadataField.vue", () => {
  it("displays label and '—' as placeholder if no value given", () => {
    render(MetadataField, {
      props: {
        label: "Test Label",
      },
    });

    expect(screen.getByText("Test Label")).toBeInTheDocument();
    expect(screen.getByText("Test Label")).toHaveClass("text-gray-900");

    expect(screen.getByLabelText("Test Label")).toBeInTheDocument();
  });

  it("displays label and value", () => {
    render(MetadataField, {
      props: {
        label: "Test Label",
        value: "Test Value",
      },
    });

    expect(screen.getByText("Test Label")).toBeInTheDocument();
    expect(screen.getByLabelText("Test Label")).toHaveTextContent("Test Value");
  });
});
