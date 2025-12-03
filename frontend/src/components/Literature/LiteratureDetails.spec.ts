import { render, screen } from "@testing-library/vue";
import LiteratureDetails from "./LiteratureDetails.vue";

describe("LiteratureDetails", () => {
  it("renders headline and testphase alert", () => {
    render(LiteratureDetails, {
      props: {
        details: {
          normReferences: [],
          collaborators: [],
          originators: [],
          languages: [],
          conferenceNotes: [],
        },
      },
    });

    expect(screen.getByRole("heading", { name: "Details" })).toBeVisible();
    expect(screen.getByRole("alert")).toHaveTextContent(
      "Dieser Service befindet sich in der Testphase",
    );
  });

  it("renders all terms", () => {
    render(LiteratureDetails, {
      props: {
        details: {
          normReferences: [],
          collaborators: [],
          originators: [],
          languages: [],
          conferenceNotes: [],
        },
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

  it("renders placeholders if no values given", () => {
    render(LiteratureDetails, {
      props: {
        details: {
          normReferences: [],
          collaborators: [],
          originators: [],
          languages: [],
          conferenceNotes: [],
        },
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Norm:");
    expect(terms[0]?.nextElementSibling).toHaveTextContent("nicht vorhanden");

    expect(terms[1]).toHaveTextContent("Mitarbeiter:");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("nicht vorhanden");

    expect(terms[2]).toHaveTextContent("Urheber:");
    expect(terms[2]?.nextElementSibling).toHaveTextContent("nicht vorhanden");

    expect(terms[3]).toHaveTextContent("Sprache:");
    expect(terms[3]?.nextElementSibling).toHaveTextContent("nicht vorhanden");

    expect(terms[4]).toHaveTextContent("Kongress:");
    expect(terms[4]?.nextElementSibling).toHaveTextContent("nicht vorhanden");
  });

  it("renders single values", () => {
    render(LiteratureDetails, {
      props: {
        details: {
          normReferences: ["Some Norm Reference"],
          collaborators: ["Mustermann, Max"],
          originators: ["Foo"],
          languages: ["deu"],
          conferenceNotes: ["Some Conference Note"],
        },
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Norm:");
    expect(terms[0]?.nextElementSibling).toHaveTextContent(
      "Some Norm Reference",
    );

    expect(terms[1]).toHaveTextContent("Mitarbeiter:");
    expect(terms[1]?.nextElementSibling).toHaveTextContent("Max Mustermann");

    expect(terms[2]).toHaveTextContent("Urheber:");
    expect(terms[2]?.nextElementSibling).toHaveTextContent("Foo");

    expect(terms[3]).toHaveTextContent("Sprache:");
    expect(terms[3]?.nextElementSibling).toHaveTextContent("deu");

    expect(terms[4]).toHaveTextContent("Kongress:");
    expect(terms[4]?.nextElementSibling).toHaveTextContent(
      "Some Conference Note",
    );
  });

  it("renders multiple values", () => {
    render(LiteratureDetails, {
      props: {
        details: {
          normReferences: ["Some Norm Reference", "Another Reference"],
          collaborators: ["Mustermann, Max", "Bar, Foo", "Baz"],
          originators: ["Foo", "Bar"],
          languages: ["deu", "eng"],
          conferenceNotes: ["Some Conference Note", "Another Note"],
        },
      },
    });

    const terms = screen.getAllByRole("term");
    expect(terms[0]).toHaveTextContent("Norm:");
    expect(terms[0]?.nextElementSibling).toHaveTextContent(
      "Some Norm Reference, Another Reference",
    );

    expect(terms[1]).toHaveTextContent("Mitarbeiter:");
    expect(terms[1]?.nextElementSibling).toHaveTextContent(
      "Max Mustermann, Foo Bar, Baz",
    );

    expect(terms[2]).toHaveTextContent("Urheber:");
    expect(terms[2]?.nextElementSibling).toHaveTextContent("Foo, Bar");

    expect(terms[3]).toHaveTextContent("Sprache:");
    expect(terms[3]?.nextElementSibling).toHaveTextContent("deu, eng");

    expect(terms[4]).toHaveTextContent("Kongress:");
    expect(terms[4]?.nextElementSibling).toHaveTextContent(
      "Some Conference Note, Another Note",
    );
  });
});
