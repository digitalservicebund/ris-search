import { takeElements } from "./extract";
import { DANGEROUSLY_SKIP_SANITIZATION, processHtml } from "./processHtml";
import {
  insertLineBreaksBetweenBracketedBlocks,
  rewriteLinks,
} from "./transforms";

function parse(html: string) {
  return new DOMParser().parseFromString(html, "text/html");
}

describe("rewriteLinks", () => {
  it("replaces the href of every link", () => {
    const document = parse('<a href="/a">A</a><p><a href="/b">B</a></p>');

    rewriteLinks((href) => `/new${href}`)(document, undefined);

    expect(document.body.innerHTML).toBe(
      '<a href="/new/a">A</a><p><a href="/new/b">B</a></p>',
    );
  });

  it("removes the href when the rewrite returns null", () => {
    const document = parse('<a href="/a">A</a>');

    rewriteLinks(() => null)(document, undefined);

    expect(document.body.innerHTML).toBe("<a>A</a>");
  });

  it("passes null for links without href and adds the href if returned", () => {
    const document = parse("<a>A</a>");

    rewriteLinks((href) => (href === null ? "#x" : href))(document, undefined);

    expect(document.body.innerHTML).toBe('<a href="#x">A</a>');
  });

  it("passes the context", () => {
    const document = parse('<a href="/a">A</a>');

    rewriteLinks<{ prefix: string }>((href, { prefix }) => prefix + href)(
      document,
      { prefix: "/p" },
    );

    expect(document.body.innerHTML).toBe('<a href="/p/a">A</a>');
  });

  it("does not touch other elements", () => {
    const document = parse('<p href="/a">A</p>');

    rewriteLinks(() => null)(document, undefined);

    expect(document.body.innerHTML).toBe('<p href="/a">A</p>');
  });

  it("rewrites links in a whole document", () => {
    const { body } = processHtml(
      `<html><body><a href="/original/path">link</a></body></html>`,
      {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        transforms: [
          rewriteLinks((href) => (href ? `/rewritten${href}` : null)),
        ],
      },
    );

    expect(body).toContain(`href="/rewritten/original/path"`);
  });
});

describe("insertLineBreaksBetweenBracketedBlocks", () => {
  const transform = insertLineBreaksBetweenBracketedBlocks(".notes");

  it("inserts a line break between two blocks", () => {
    const document = parse(
      '<div class="notes"><p>(+++ one +++) (+++ two +++)</p></div>',
    );

    transform(document, undefined);

    expect(document.body.innerHTML).toBe(
      '<div class="notes"><p>(+++ one +++)<br>(+++ two +++)</p></div>',
    );
  });

  it("handles more than two blocks and any whitespace between them", () => {
    const document = parse(
      '<div class="notes">(+++ a +++)\n  (+++ b +++) (+++ c +++)</div>',
    );

    transform(document, undefined);

    expect(document.body.innerHTML).toBe(
      '<div class="notes">(+++ a +++)<br>(+++ b +++)<br>(+++ c +++)</div>',
    );
  });

  it("leaves a single block alone", () => {
    const document = parse('<div class="notes">(+++ one +++)</div>');

    transform(document, undefined);

    expect(document.body.innerHTML).toBe(
      '<div class="notes">(+++ one +++)</div>',
    );
  });

  it("only changes elements matching the selector", () => {
    const html = "<p>(+++ one +++) (+++ two +++)</p>";
    const document = parse(html);

    transform(document, undefined);

    expect(document.body.innerHTML).toBe(html);
  });

  describe("for heading notes", () => {
    const headingNotesSelector = ".dokumentenkopf .nichtamtliche-fussnoten";

    function process(html: string) {
      return processHtml(html, {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        transforms: [
          insertLineBreaksBetweenBracketedBlocks(headingNotesSelector),
        ],
        extract: (document) => ({
          html: { headingNotes: takeElements(document, headingNotesSelector) },
          text: {},
        }),
      });
    }

    it("inserts line breaks between consecutive bracketed blocks in footnotes", () => {
      const { parts } = process(
        `<section class="dokumentenkopf"><ul class="nichtamtliche-fussnoten"><li class="fussnote"><p>(+++ Textnachweis ab: 1.1.2000 +++) (+++ Zur Anwendung vgl. § 5 +++)</p></li></ul></section>`,
      );

      expect(parts.headingNotes).toContain(
        "(+++ Textnachweis ab: 1.1.2000 +++)<br>(+++ Zur Anwendung vgl. § 5 +++)",
      );
    });

    it("leaves footnotes outside the heading alone", () => {
      const { body } = process(
        `<section class="eingangsformel"><ul class="nichtamtliche-fussnoten"><li><p>(+++ a +++) (+++ b +++)</p></li></ul></section>`,
      );

      expect(body).toContain("(+++ a +++) (+++ b +++)");
      expect(body).not.toContain("<br>");
    });
  });
});
