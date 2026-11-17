import { getSections, takeElements } from "./extract";

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
});

// Same fixture and expectations as the official toc tests in
// useFetchNormContent.spec.ts, which used querySelectorAll + outerHTML.
describe("takeElements (compatibility with the official toc extraction)", () => {
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

  it("adds the toc footnotes to the official toc", () => {
    const document = parse(preambleWithTocHtml);

    const toc = parse(takeElements(document, officialTocSelector)!);

    expect(toc.querySelector(".official-toc")).not.toBeNull();
    expect(toc.querySelector(".fussnote")).not.toBeNull();
    expect(
      toc.querySelector("#meta-n1_editfnote-n2_text-n1")?.textContent,
    ).toBe("Inhaltsübersicht: präambel Fußnote");
  });

  it("keeps Eingangsformel footnotes out of the official toc and in the body", () => {
    const document = parse(preambleWithTocHtml);

    const toc = takeElements(document, officialTocSelector);

    expect(toc).not.toContain("Eingangsformel: Eingangsformel Fußnote");
    expect(document.body.innerHTML).toContain(
      "Eingangsformel: Eingangsformel Fußnote",
    );
  });

  it("returns undefined without a toc, like the old extraction", () => {
    const document = parse("<p>No toc</p>");

    expect(takeElements(document, officialTocSelector)).toBeUndefined();
  });
});
