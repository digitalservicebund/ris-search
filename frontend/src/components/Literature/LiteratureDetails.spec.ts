import { render, screen } from "@testing-library/vue";
import LiteratureDetails from "./LiteratureDetails.vue";

describe("LiteratureDetails", () => {
  it("renders headline", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: [],
        collaborators: [],
        originators: [],
        languages: [],
        conferenceNotes: [],
      },
    });

    expect(screen.getByRole("heading", { name: "Details" })).toBeVisible();
  });

  it("renders all terms", () => {
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

  it("renders placeholders if no values given", () => {
    render(LiteratureDetails, {
      props: {
        normReferences: [],
        collaborators: [],
        originators: [],
        languages: [],
        conferenceNotes: [],
      },
    });

    expect(screen.getByRole("definition", { name: "Norm:" })).toHaveTextContent(
      "nicht vorhanden",
    );

    expect(
      screen.getByRole("definition", { name: "Mitarbeiter:" }),
    ).toHaveTextContent("nicht vorhanden");

    expect(
      screen.getByRole("definition", { name: "Urheber:" }),
    ).toHaveTextContent("nicht vorhanden");

    expect(
      screen.getByRole("definition", { name: "Sprache:" }),
    ).toHaveTextContent("nicht vorhanden");

    expect(
      screen.getByRole("definition", { name: "Kongress:" }),
    ).toHaveTextContent("nicht vorhanden");
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

    expect(screen.getByRole("definition", { name: "Norm:" })).toHaveTextContent(
      "Some Norm Reference",
    );

    expect(
      screen.getByRole("definition", { name: "Mitarbeiter:" }),
    ).toHaveTextContent("Max Mustermann");

    expect(
      screen.getByRole("definition", { name: "Urheber:" }),
    ).toHaveTextContent("Foo");

    expect(
      screen.getByRole("definition", { name: "Sprache:" }),
    ).toHaveTextContent("deu");

    expect(
      screen.getByRole("definition", { name: "Kongress:" }),
    ).toHaveTextContent("Some Conference Note");
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

    expect(screen.getByRole("definition", { name: "Norm:" })).toHaveTextContent(
      "Some Norm Reference, Another Reference",
    );

    expect(
      screen.getByRole("definition", { name: "Mitarbeiter:" }),
    ).toHaveTextContent("Max Mustermann, Foo Bar, Baz");

    expect(
      screen.getByRole("definition", { name: "Urheber:" }),
    ).toHaveTextContent("Foo, Bar");

    expect(
      screen.getByRole("definition", { name: "Sprache:" }),
    ).toHaveTextContent("deu, eng");

    expect(
      screen.getByRole("definition", { name: "Kongress:" }),
    ).toHaveTextContent("Some Conference Note, Another Note");
  });
});
