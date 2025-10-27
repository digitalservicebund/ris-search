import { render, screen } from "@testing-library/vue";
import PropertiesItem from "./PropertiesItem.vue";

describe("PropertiesItem.vue", () => {
  it('displays "nicht vorhanden" when no values provided', () => {
    render(PropertiesItem, {
      props: {
        label: "Test Label",
      },
    });

    expect(screen.getByRole("term")).toHaveTextContent("Test Label");
    const definition = screen.getByRole("definition", { name: "Test Label" });
    expect(definition).toHaveTextContent("nicht vorhanden");
    expect(definition).toHaveAttribute("data-empty", "empty");
  });

  it("displays provided placeholder when no values provided", () => {
    render(PropertiesItem, {
      props: {
        label: "Test Label",
        placeholder: "placeholder",
      },
    });

    expect(screen.getByRole("term")).toHaveTextContent("Test Label");
    const definition = screen.getByRole("definition", { name: "Test Label" });
    expect(definition).toHaveTextContent("placeholder");
    expect(definition).toHaveAttribute("data-empty", "empty");
  });

  it("prefers slot over value and valueList", () => {
    render(PropertiesItem, {
      slots: {
        default: "Default Slot",
      },
      props: {
        label: "Test Label",
        valueList: ["value 1"],
        value: "Some value",
      },
    });

    expect(screen.getByRole("term")).toHaveTextContent("Test Label");
    const definition = screen.getByRole("definition", { name: "Test Label" });
    expect(definition).toHaveTextContent("Default Slot");
    expect(definition).toHaveClass("ris-label1-regular");
  });

  it("prefers valueList over value if no slot given", () => {
    render(PropertiesItem, {
      props: {
        label: "Test Label",
        valueList: ["value 1", "value 2"],
        value: "Some value",
      },
    });

    expect(screen.getByRole("term")).toHaveTextContent("Test Label");
    const definitions = screen.getAllByRole("definition", {
      name: "Test Label",
    });
    expect(definitions[0]).toHaveTextContent("value 1");
    expect(definitions[0]).toHaveClass("ris-label1-regular");

    expect(definitions[1]).toHaveTextContent("value 2");
    expect(definitions[1]).toHaveClass("ris-label1-regular");
  });

  it("renders provided value if no slot and valueList given", () => {
    render(PropertiesItem, {
      props: {
        label: "Test Label",
        value: "Some value",
      },
    });

    expect(screen.getByRole("term")).toHaveTextContent("Test Label");
    const definition = screen.getByRole("definition", { name: "Test Label" });
    expect(definition).toHaveTextContent("Some value");
    expect(definition).toHaveClass("ris-label1-regular");
  });
});
