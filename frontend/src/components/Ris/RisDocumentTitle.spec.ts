import { render, screen } from "@testing-library/vue";
import RisDocumentTitle from "./RisDocumentTitle.vue";

describe("RisDocumentTitle", () => {
  it("renders title", () => {
    render(RisDocumentTitle, {
      props: {
        title: "The Title",
        placeholder: "Placeholder value",
      },
    });

    const title = screen.getByRole("heading");
    expect(title).toHaveTextContent("The Title");
    expect(title).not.toHaveClass("text-gray-900");
  });

  it("renders placeholder if title is undefined", () => {
    render(RisDocumentTitle, {
      props: {
        placeholder: "Placeholder value",
      },
    });

    const title = screen.getByRole("heading");
    expect(title).toHaveTextContent("Placeholder value");
    expect(title).toHaveClass("text-gray-900");
  });
});
