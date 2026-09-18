import { html, tw } from "./tags";

describe("tags", () => {
  describe.each([
    ["html", html],
    ["tw", tw],
  ])("%s", (_name, tag) => {
    it("returns a plain template string unchanged", () => {
      expect(tag`<div>Hello world</div>`).toBe("<div>Hello world</div>");
    });

    it("interpolates values", () => {
      const name = "world";
      expect(tag`<div>Hello ${name}</div>`).toBe("<div>Hello world</div>");
    });
  });
});
