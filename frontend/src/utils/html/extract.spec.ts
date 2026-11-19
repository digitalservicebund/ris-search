import { getSections, getText, takeElements } from "./extract";

function parse(html: string) {
  return new DOMParser().parseFromString(html, "text/html");
}

describe("takeElements", () => {
  it("mutates the document it is given", () => {
    const document = parse("<p>One</p>");

    takeElements(document, "p");

    expect(document.querySelector("p")).toBeNull();
  });

  it("returns the HTML of a matching element and removes it", () => {
    const document = parse('<p class="a">One</p><p>Two</p>');

    expect(takeElements(document, ".a")).toBe('<p class="a">One</p>');
    expect(document.body.innerHTML).toBe("<p>Two</p>");
  });

  it("concatenates multiple matches in document order", () => {
    const document = parse("<p>One</p><div>x</div><p>Two</p>");

    expect(takeElements(document, "p")).toBe("<p>One</p><p>Two</p>");
    expect(document.body.innerHTML).toBe("<div>x</div>");
  });

  it("does not repeat matches nested in other matches", () => {
    const document = parse("<div><div>inner</div></div>");

    expect(takeElements(document, "div")).toBe("<div><div>inner</div></div>");
    expect(document.body.innerHTML).toBe("");
  });

  it("returns undefined and changes nothing without a match", () => {
    const document = parse("<p>One</p>");

    expect(takeElements(document, ".missing")).toBeUndefined();
    expect(document.body.innerHTML).toBe("<p>One</p>");
  });

  const officialTocSelector =
    ".official-toc, .eingangsformel .inhaltsuebersicht .nichtamtliche-fussnoten";

  const preambleWithTocHtml = `
    <section class="eingangsformel" id="praeambel-n1">
      <section class="eingangsformel" id="praeambel-n1_formel-n1">
        <p id="praeambel-n1_formel-n1_text-n1">Der Bundestag hat …</p>
        <ul class="nichtamtliche-fussnoten">
          <li class="fussnote">
            <p id="meta-n1_editfnote-n1_text-n1">Eingangsformel: Eingangsformel Fußnote</p>
          </li>
        </ul>
      </section>
      <div class="inhaltsuebersicht" id="praeambel-n1_blockcontainer-n1">
        <span class="akn-heading">Inhaltsübersicht</span>
        <div class="official-toc">
          <div class="level-1"><span class="akn-span">Abschnitt 1</span></div>
        </div>
        <ul class="nichtamtliche-fussnoten">
          <li class="fussnote">
            <p id="meta-n1_editfnote-n2_text-n1">Inhaltsübersicht: präambel Fußnote</p>
          </li>
        </ul>
      </div>
    </section>`;

  it("adds the footnotes of the table of contents to the table of contents", () => {
    const document = parse(preambleWithTocHtml);

    const toc = parse(takeElements(document, officialTocSelector)!);

    expect(toc.querySelector(".official-toc")).not.toBeNull();
    expect(toc.querySelector(".fussnote")).not.toBeNull();
    expect(
      toc.querySelector("#meta-n1_editfnote-n2_text-n1")?.textContent,
    ).toBe("Inhaltsübersicht: präambel Fußnote");
  });

  it("keeps footnotes of the preamble in the body, out of the table of contents", () => {
    const document = parse(preambleWithTocHtml);

    const toc = takeElements(document, officialTocSelector);

    expect(toc).not.toContain("Eingangsformel: Eingangsformel Fußnote");
    expect(document.body.innerHTML).toContain(
      "Eingangsformel: Eingangsformel Fußnote",
    );
  });

  it("returns undefined for a document without a table of contents", () => {
    const document = parse("<p>No toc</p>");

    expect(takeElements(document, officialTocSelector)).toBeUndefined();
  });
});

describe("getText", () => {
  it("returns the text of each match in document order", () => {
    const document = parse("<p>One</p><div><p>Two</p></div>");

    expect(getText(document, "p")).toEqual(["One", "Two"]);
  });

  it("returns plain text, not markup", () => {
    const document = parse('<p>A <b>bold</b> &amp; <a href="x">link</a></p>');

    expect(getText(document, "p")).toEqual(["A bold & link"]);
  });

  it("skips matches without text", () => {
    const document = parse("<p></p><p>Text</p>");

    expect(getText(document, "p")).toEqual(["Text"]);
  });

  it("returns an empty list without matches", () => {
    expect(getText(parse("<p>x</p>"), ".missing")).toEqual([]);
  });

  it("only searches inside the given element", () => {
    const document = parse(
      '<div id="a"><p>One</p></div><div id="b"><p>Two</p></div>',
    );

    expect(getText(document.querySelector("#b")!, "p")).toEqual(["Two"]);
  });

  it("does not mutate the document", () => {
    const document = parse("<p>One</p>");

    getText(document, "p");

    expect(document.body.innerHTML).toBe("<p>One</p>");
  });
});

describe("getSections", () => {
  it("lists the id and the first h2 of each section", () => {
    const document = parse(
      '<section id="a"><h2>Tenor</h2><h2>Other</h2></section><section id="b"><h2>Gründe</h2></section>',
    );

    expect(getSections(document, "section")).toEqual([
      { id: "a", title: "Tenor" },
      { id: "b", title: "Gründe" },
    ]);
  });

  it("uses an empty title for sections without h2", () => {
    const document = parse('<section id="a"><p>No heading</p></section>');

    expect(getSections(document, "section")).toEqual([{ id: "a", title: "" }]);
  });

  it("uses the first heading that has text as the title", () => {
    const document = parse(
      '<section id="a"><h2></h2><h2>Second</h2></section>',
    );

    expect(getSections(document, "section")).toEqual([
      { id: "a", title: "Second" },
    ]);
  });

  it("returns an empty list without sections", () => {
    expect(getSections(parse("<p>x</p>"), "section")).toEqual([]);
  });

  it("does not mutate the document", () => {
    const document = parse('<section id="a"><h2>Tenor</h2></section>');
    const before = document.body.innerHTML;

    getSections(document, "section");

    expect(document.body.innerHTML).toBe(before);
  });
});
