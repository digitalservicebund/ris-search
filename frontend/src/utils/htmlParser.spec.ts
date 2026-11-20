import { parseDocument } from "./htmlParser";

describe("parseDocument", () => {
  it("parses full HTML without wrapping", () => {
    const doc = parseDocument("<html><body><p>Hi</p></body></html>");
    expect(doc.querySelector("p")?.textContent).toBe("Hi");
  });

  it("parses fragments into the body", () => {
    const doc = parseDocument("<p>Hello</p><p>World</p>");
    expect(doc.body?.textContent).toMatch(/Hello\s*World/);
  });
});

describe("isDocumentEmpty", () => {
  it("returns true if document is undefined", () => {
    expect(isDocumentEmpty()).toBeTruthy();
  });

  it("returns true if html body is empty", () => {
    const docString = "<!DOCTYPE HTML><html><body></body></html>";
    const parser = new DOMParser();
    const doc = parser.parseFromString(docString, "text/html");

    expect(isDocumentEmpty(doc)).toBeTruthy();
  });

  it("returns true if html body is empty in an indented document", () => {
    const docString = `
      <!DOCTYPE HTML>
      <html>
        <head>
          <title></title>
        </head>
        <body></body>
      </html>
    `;
    const parser = new DOMParser();
    const doc = parser.parseFromString(docString, "text/html");

    expect(isDocumentEmpty(doc)).toBeTruthy();
  });

  it("returns true if html body contains only single h1", () => {
    const docString =
      "<!DOCTYPE HTML><html><body><h1>Still Empty</h1></body></html>";
    const parser = new DOMParser();
    const doc = parser.parseFromString(docString, "text/html");

    expect(isDocumentEmpty(doc)).toBeTruthy();
  });

  it.each(["Foo", "<p>Foo Bar</p>", "<h1>Not Empty</h1><p>Foo Bar</p>"])(
    "returns false if html body contains '%s'",
    (bodyContent) => {
      const docString = `<!DOCTYPE HTML><html><body>${bodyContent}</body></html>`;
      const parser = new DOMParser();
      const doc = parser.parseFromString(docString, "text/html");
      expect(isDocumentEmpty(doc)).toBeFalsy();
    },
  );
});
