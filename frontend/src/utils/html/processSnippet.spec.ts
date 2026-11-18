import {
  HIGHLIGHT_ONLY_ALLOWLIST,
  processSnippet,
  SEARCH_SNIPPET_ALLOWLIST,
  snippetToText,
} from "./processSnippet";

describe("processSnippet", () => {
  it("does not wrap the snippet in an element", () => {
    expect(processSnippet("<mark>a</mark> b", SEARCH_SNIPPET_ALLOWLIST)).toBe(
      "<mark>a</mark> b",
    );
  });

  it("returns an empty string for an empty snippet", () => {
    expect(processSnippet("", SEARCH_SNIPPET_ALLOWLIST)).toBe("");
  });

  it("uses the given allowlist", () => {
    expect(processSnippet("<p>a</p>", { allowedTags: ["p"] })).toBe("<p>a</p>");
    expect(processSnippet("<p>a</p>", { allowedTags: [] })).toBe("a");
  });

  it("search snippet allowlist removes all tags except i, b, and mark", () => {
    const text =
      '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"><script>alert("xss")</script> <div>div</div> plain_text.';
    const expectedSanitized =
      "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";

    expect(processSnippet(text, SEARCH_SNIPPET_ALLOWLIST)).toBe(
      expectedSanitized,
    );
  });

  it("search snippet allowlist removes attributes from allowed tags", () => {
    expect(
      processSnippet(
        '<mark class="x" onclick="alert(1)">a</mark>',
        SEARCH_SNIPPET_ALLOWLIST,
      ),
    ).toBe("<mark>a</mark>");
  });

  it("highlight-only allowlist keeps mark", () => {
    expect(
      processSnippet(
        "<mark>Highlighted Content</mark>",
        HIGHLIGHT_ONLY_ALLOWLIST,
      ),
    ).toBe("<mark>Highlighted Content</mark>");
  });

  it("highlight-only allowlist removes scripts and their content", () => {
    expect(
      processSnippet(
        "<mark><script>CSS</script>Highlighted Content</mark>",
        HIGHLIGHT_ONLY_ALLOWLIST,
      ),
    ).toBe("<mark>Highlighted Content</mark>");
  });

  it("highlight-only allowlist removes b and i, which search snippets allow", () => {
    expect(processSnippet("<b>b</b> <i>i</i>", HIGHLIGHT_ONLY_ALLOWLIST)).toBe(
      "b i",
    );
  });
});

describe("snippetToText", () => {
  it("removes all tags and keeps the text", () => {
    expect(snippetToText("<mark>BGH</mark> <b>I</b> ZR 1/20")).toBe(
      "BGH I ZR 1/20",
    );
  });

  it("removes scripts and their content", () => {
    expect(snippetToText("a<script>alert(1)</script>b")).toBe("ab");
  });

  it("keeps special characters HTML-escaped", () => {
    expect(snippetToText("<mark>A &amp; B</mark> &lt; C")).toBe(
      "A &amp; B &lt; C",
    );
  });

  it("returns an empty string for an empty snippet", () => {
    expect(snippetToText("")).toBe("");
  });
});
