import { isNormBodyEmpty } from "~/utils/norm";
import { takeElements } from "./extract";
import { DANGEROUSLY_SKIP_SANITIZATION, processHtml } from "./processHtml";

describe("processHtml", () => {
  it("returns the body, no parts and no text without options", () => {
    const result = processHtml("<p>Hello</p>", {
      allowlist: DANGEROUSLY_SKIP_SANITIZATION,
    });

    expect(result.body).toBe("<div><p>Hello</p></div>");
    expect(result.parts).toEqual({});
    expect(result.text).toEqual({});
    expect(result.isEmpty).toBe(false);
  });

  it("applies transforms in order and passes the context", () => {
    const calls: string[] = [];

    const result = processHtml("<p>Hello</p>", {
      allowlist: DANGEROUSLY_SKIP_SANITIZATION,
      context: { suffix: "!" },
      transforms: [
        (document, context) => {
          calls.push("first");
          document.querySelector("p")!.textContent += context.suffix;
        },
        (document) => {
          calls.push("second");
          document.querySelector("p")!.textContent += "?";
        },
      ],
    });

    expect(calls).toEqual(["first", "second"]);
    expect(result.body).toContain("<p>Hello!?</p>");
  });

  it("extracts parts and text and removes the parts from the body", () => {
    const result = processHtml(
      '<h1 class="title">Title</h1><p>Text</p><p class="note">Note</p>',
      {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        extract: (document) => ({
          html: {
            title: takeElements(document, ".title"),
            missing: takeElements(document, ".missing"),
          },
          text: {
            note: document.querySelector(".note")?.textContent ?? undefined,
          },
        }),
      },
    );

    expect(result.parts.title).toBe('<h1 class="title">Title</h1>');
    expect(result.parts.missing).toBeUndefined();
    expect(result.text.note).toBe("Note");
    expect(result.body).not.toContain("Title");
    expect(result.body).toContain("<p>Text</p>");
  });

  describe("isEmpty", () => {
    it("is true for an empty document", () => {
      expect(
        processHtml("<html><body></body></html>", {
          allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        }).isEmpty,
      ).toBe(true);
    });

    it("is true for a document with only a heading", () => {
      expect(
        processHtml("<html><body><h1>Title</h1></body></html>", {
          allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        }).isEmpty,
      ).toBe(true);
    });

    it("is checked before extracting", () => {
      const result = processHtml("<p class='part'>Text</p>", {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        extract: (document) => ({
          html: { part: takeElements(document, ".part") },
          text: {},
        }),
      });

      expect(result.body).not.toContain("Text");
      expect(result.isEmpty).toBe(false);
    });

    it("uses a custom check", () => {
      const result = processHtml("<p>Text</p>", {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        isEmpty: (document) => document.querySelector("p") !== null,
      });

      expect(result.isEmpty).toBe(true);
    });
  });

  describe("sanitizing", () => {
    const html = '<p class="keep">Hello<script>alert(1)</script></p>';

    it("does not sanitize when told to skip sanitizing", () => {
      const result = processHtml(html, {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        extract: (document) => ({
          html: { part: takeElements(document, "p") },
          text: {},
        }),
      });

      expect(result.parts.part).toContain("<script>");
      expect(result.body).not.toContain("<script>"); // the part was taken out
    });

    it("does not sanitize the body when told to skip sanitizing", () => {
      const result = processHtml(html, {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
      });

      expect(result.body).toContain("<script>alert(1)</script>");
    });

    it("sanitizes the body", () => {
      const result = processHtml(html, {
        allowlist: { allowedTags: ["p", "div"], allowedAttributes: {} },
      });

      expect(result.body).toBe("<div><p>Hello</p></div>");
    });

    it("sanitizes the parts and leaves missing parts undefined", () => {
      const result = processHtml(html, {
        allowlist: {
          allowedTags: ["p"],
          allowedAttributes: { p: ["class"] },
        },
        extract: (document) => ({
          html: {
            part: takeElements(document, "p"),
            missing: takeElements(document, ".missing"),
          },
          text: {},
        }),
      });

      expect(result.parts.part).toBe('<p class="keep">Hello</p>');
      expect(result.parts.missing).toBeUndefined();
    });

    it("does not sanitize the text", () => {
      const result = processHtml("<p>1 &lt; 2</p>", {
        allowlist: { allowedTags: [] },
        extract: (document) => ({
          html: {},
          text: { p: document.querySelector("p")?.textContent ?? undefined },
        }),
      });

      expect(result.text.p).toBe("1 < 2");
    });
  });

  describe("isEmpty with isNormBodyEmpty (as used by useFetchNormContent)", () => {
    const options = {
      allowlist: DANGEROUSLY_SKIP_SANITIZATION,
      isEmpty: isNormBodyEmpty,
    } as const;

    it("is false if akn-body has content", () => {
      const result = processHtml(
        `<html><body><div class="akn-act"><div class="akn-body">Content</div></div></body></html>`,
        options,
      );

      expect(result.isEmpty).toBe(false);
    });

    it("is true if akn-body is empty", () => {
      const result = processHtml(
        `<html><body><div class="akn-act"><div class="akn-body"></div></div></body></html>`,
        options,
      );

      expect(result.isEmpty).toBe(true);
    });

    it("is true without akn-body", () => {
      const result = processHtml(
        "<html><body><p>Text</p></body></html>",
        options,
      );

      expect(result.isEmpty).toBe(true);
    });
  });

  describe("mutation", () => {
    it("never changes the input string", () => {
      const html = '<p class="part">Text</p>';

      processHtml(html, {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        transforms: [(document) => document.querySelector("p")?.remove()],
      });

      expect(html).toBe('<p class="part">Text</p>');
    });

    it("gives every call its own document", () => {
      const html = '<p class="part">Text</p>';
      const options = {
        allowlist: DANGEROUSLY_SKIP_SANITIZATION,
        extract: (document: Document) => ({
          html: { part: takeElements(document, ".part") },
          text: {},
        }),
      } as const;

      const first = processHtml(html, options);
      const second = processHtml(html, options);

      expect(second).toEqual(first);
      expect(second.parts.part).toBe('<p class="part">Text</p>');
    });
  });
});
