import { afterEach } from "vitest";
import {
  getVerweiseGroups,
  getTextFromElements,
  parseDocument,
} from "./htmlParser";

describe("getTextFromElements", () => {
  afterEach(() => {
    document.body.innerHTML = "";
  });
  it("should return an empty array when elements is undefined", () => {
    const result = getTextFromElements();
    expect(result).toEqual([]);
  });

  it("should return an empty array when elements is an empty NodeList", () => {
    const elements = document.createDocumentFragment()
      .childNodes as NodeListOf<Element>;
    const result = getTextFromElements(elements);
    expect(result).toEqual([]);
  });

  it("should return an array of text content from elements", () => {
    const div1 = document.createElement("div");
    div1.textContent = "Hello";
    const div2 = document.createElement("div");
    div2.textContent = "World";
    const div3 = document.createElement("div");
    div3.textContent = "";
    document.body.append(div1, div2, div3);
    const elements = document.querySelectorAll("div");

    const result = getTextFromElements(elements);
    expect(result).toEqual(["Hello", "World"]);
  });

  it("should filter out null or undefined text content", () => {
    const div1 = document.createElement("div");
    div1.textContent = "Hello";
    const div2 = document.createElement("div");
    div2.textContent = null;
    const div3 = document.createElement("div");
    document.body.append(div1, div2, div3);
    const elements = document.querySelectorAll("div");

    const result = getTextFromElements(elements);
    expect(result).toEqual(["Hello"]);
  });
});

describe("parseDocument", () => {
  it("parses full HTML without wrapping", () => {
    const doc = parseDocument("<html><body><p>Hi</p></body></html>");
    expect(doc.querySelector("p")?.textContent).toBe("Hi");
  });

  it("parses fragments by auto-wrapping", () => {
    const doc = parseDocument("<p>Hello</p><p>World</p>");
    expect(doc.body?.textContent).toMatch(/Hello\s*World/);
  });
});

describe("getVerweiseGroups", () => {
  const parser = new DOMParser();

  it("returns only the categories present in the document, in display order", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head>
        <template id="nachgehendeEntscheidung"><ul><li>B</li></ul></template>
        <template id="referenzNorm"><ul><li>A</li></ul></template>
      </head><body></body></html>`,
      "text/html",
    );

    const result = getVerweiseGroups(doc);

    expect(result).toEqual([
      { id: "referenzNorm", label: "Normen", html: "<ul><li>A</li></ul>" },
      {
        id: "nachgehendeEntscheidung",
        label: "Nachgehende Entscheidungen",
        html: "<ul><li>B</li></ul>",
      },
    ]);
  });

  it("returns an empty array when no verweise templates are present", () => {
    const doc = parser.parseFromString(
      "<!DOCTYPE HTML><html><head></head><body></body></html>",
      "text/html",
    );

    expect(getVerweiseGroups(doc)).toEqual([]);
  });

  it("sanitizes the list markup down to ul, li and a", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head><template id="referenzNorm"><ul><li onclick="evil()"><a href="/x" class="foo">GG Art. 1</a></li><li><script>alert(1)</script>BGB</li></ul></template></head><body></body></html>`,
      "text/html",
    );

    const [group] = getVerweiseGroups(doc);

    expect(group?.html).toBe(
      '<ul><li><a href="/x">GG Art. 1</a></li><li>BGB</li></ul>',
    );
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
