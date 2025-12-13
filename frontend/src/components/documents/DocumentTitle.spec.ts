import { render, screen } from "@testing-library/vue";
import DocumentTitle from "./DocumentTitle.vue";

describe("DocumentTitle", () => {
  it("renders title", () => {
    render(DocumentTitle, {
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
    render(DocumentTitle, {
      props: {
        placeholder: "Placeholder value",
      },
    });

    const title = screen.getByRole("heading");
    expect(title).toHaveTextContent("Placeholder value");
    expect(title).toHaveClass("text-gray-900");
  });
});
