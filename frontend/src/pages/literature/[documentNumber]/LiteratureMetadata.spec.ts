import { render, screen } from "@testing-library/vue";
import LiteratureMetadata from "./LiteratureMetadata.vue";

describe("LiteratureMetadata", () => {
  it("renders all property labels", () => {
    render(LiteratureMetadata, {
      props: {
        documentTypes: [],
        references: [],
        authors: [],
        yearsOfPublication: [],
      },
    });

    expect(screen.getByText("Dokumenttyp")).toBeInTheDocument();
    expect(screen.getByText("Fundstelle")).toBeInTheDocument();
    expect(screen.getByText("Author")).toBeInTheDocument();
    expect(screen.getByText("Veröffentlichungsjahr")).toBeInTheDocument();
  });

  it("renders placeholders if property not given", () => {
    render(LiteratureMetadata, {
      props: {
        documentTypes: [],
        references: [],
        authors: [],
        yearsOfPublication: [],
      },
    });

    expect(screen.getByLabelText("Dokumenttyp")).toHaveTextContent("-");
    expect(screen.getByLabelText("Fundstelle")).toHaveTextContent("-");
    expect(screen.getByLabelText("Author")).toHaveTextContent("-");
    expect(screen.getByLabelText("Veröffentlichungsjahr")).toHaveTextContent(
      "-",
    );
  });

  it("renders properties when only one value given", () => {
    render(LiteratureMetadata, {
      props: {
        documentTypes: ["Foo"],
        references: ["Ref"],
        authors: ["Mustermann, Max"],
        yearsOfPublication: ["2015"],
      },
    });

    expect(screen.getByLabelText("Dokumenttyp")).toHaveTextContent("Foo");
    expect(screen.getByLabelText("Fundstelle")).toHaveTextContent("Ref");
    expect(screen.getByLabelText("Author")).toHaveTextContent("Max Mustermann");
    expect(screen.getByLabelText("Veröffentlichungsjahr")).toHaveTextContent(
      "2015",
    );
  });

  it("renders properties when multiple values given", () => {
    render(LiteratureMetadata, {
      props: {
        documentTypes: ["Foo", "Bar"],
        references: ["Ref1", "Ref2"],
        authors: ["Mustermann, Max", "Musterfrau, Sabine"],
        yearsOfPublication: ["2015", "2016"],
      },
    });

    expect(screen.getByLabelText("Dokumenttyp")).toHaveTextContent("Foo, Bar");
    expect(screen.getByLabelText("Fundstelle")).toHaveTextContent("Ref1, Ref2");
    expect(screen.getByLabelText("Author")).toHaveTextContent(
      "Max Mustermann, Sabine Musterfrau",
    );
    expect(screen.getByLabelText("Veröffentlichungsjahr")).toHaveTextContent(
      "2015, 2016",
    );
  });
});
