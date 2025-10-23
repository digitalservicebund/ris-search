import { render, screen } from "@testing-library/vue";
import LiteratureDetails from "~/pages/literature/[documentNumber]/LiteratureDetails.vue";

describe("LiteratureDetails", () => {
  it("renders property labels", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: [],
        collaborators: [],
        originators: [],
        languages: [],
        conferenceNotes: [],
      },
    });

    const terms = screen.getAllByRole("term");

    expect(terms).toHaveLength(5);
    expect(terms[0]).toHaveTextContent("Norm:");
    expect(terms[1]).toHaveTextContent("Mitarbeiter:");
    expect(terms[2]).toHaveTextContent("Urheber:");
    expect(terms[3]).toHaveTextContent("Sprache:");
    expect(terms[4]).toHaveTextContent("Kongress:");
  });

  it("renders placeholders if not values given", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: [],
        collaborators: [],
        originators: [],
        languages: [],
        conferenceNotes: [],
      },
    });

    const definitions = screen.getAllByRole("definition");

    expect(definitions).toHaveLength(5);
    for (const definition of definitions) {
      expect(definition).toHaveTextContent("nicht vorhanden");
    }
  });

  it("renders single values", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: ["Some Norm Reference"],
        collaborators: ["Mustermann, Max"],
        originators: ["Foo"],
        languages: ["deu"],
        conferenceNotes: ["Some Conference Note"],
      },
    });

    const definitions = screen.getAllByRole("definition");

    expect(definitions).toHaveLength(5);
    expect(definitions[0]).toHaveTextContent("Some Norm Reference");
    expect(definitions[1]).toHaveTextContent("Max Mustermann");
    expect(definitions[2]).toHaveTextContent("Foo");
    expect(definitions[3]).toHaveTextContent("deu");
    expect(definitions[4]).toHaveTextContent("Some Conference Note");
  });

  it("renders multiple values", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: ["Some Norm Reference", "Another Reference"],
        collaborators: ["Mustermann, Max", "Bar, Foo", "Baz"],
        originators: ["Foo", "Bar"],
        languages: ["deu", "eng"],
        conferenceNotes: ["Some Conference Note", "Another Note"],
      },
    });

    const definitions = screen.getAllByRole("definition");

    expect(definitions).toHaveLength(5);
    expect(definitions[0]).toHaveTextContent(
      "Some Norm Reference, Another Reference",
    );
    expect(definitions[1]).toHaveTextContent("Max Mustermann, Foo Bar, Baz");
    expect(definitions[2]).toHaveTextContent("Foo, Bar");
    expect(definitions[3]).toHaveTextContent("deu, eng");
    expect(definitions[4]).toHaveTextContent(
      "Some Conference Note, Another Note",
    );
  });
});
