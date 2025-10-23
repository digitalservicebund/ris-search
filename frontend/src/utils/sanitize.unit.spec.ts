import { sanitizeSearchResult } from "./sanitize";

describe("sanitizeSearchResult", () => {
  it("removes all tags except i, b, and mark", () => {
    const text =
      '<mark>mark</mark> <i>i</i> <b>b</b> <img src="" alt="do not show"><script>alert("xss")</script> <div>div</div> plain_text.';
    const expectedSanitized =
      "<mark>mark</mark> <i>i</i> <b>b</b>  div plain_text.";
    expect(sanitizeSearchResult(text)).toBe(expectedSanitized);
  });
});
