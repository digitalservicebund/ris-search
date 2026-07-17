import { DocumentKind } from "~/types/api";

async function setup(fromParam: string | undefined) {
  vi.doMock("#app", () => ({
    useRoute: vi.fn().mockReturnValue({
      query: fromParam !== undefined ? { from: fromParam } : {},
    }),
  }));

  const { useSearchBackLink } = await import("./useSearchBackLink");
  return useSearchBackLink(DocumentKind.CaseLaw);
}

describe("useSearchBackLink", () => {
  beforeEach(() => {
    vi.resetModules();
  });

  describe("when 'from' is a valid simple search URL", () => {
    it("returns route pointing to the 'from' URL", async () => {
      const backLink = await setup(
        "/suche?query=test&documentKind=R&pageIndex=2",
      );
      expect(backLink.value.route).toBe(
        "/suche?query=test&documentKind=R&pageIndex=2",
      );
    });

    it("returns label 'Suche'", async () => {
      const backLink = await setup("/suche?query=test&documentKind=R");
      expect(backLink.value.label).toBe("Suche");
    });
  });

  describe("when 'from' is a valid advanced search URL", () => {
    it("returns route pointing to the 'from' URL", async () => {
      const backLink = await setup(
        "/advanced-search?q=BGB&documentKind=N&pageIndex=1",
      );
      expect(backLink.value.route).toBe(
        "/advanced-search?q=BGB&documentKind=N&pageIndex=1",
      );
    });

    it("returns label 'Erweiterte Suche'", async () => {
      const backLink = await setup("/advanced-search?q=BGB");
      expect(backLink.value.label).toBe("Erweiterte Suche");
    });
  });

  describe("fallback when no valid 'from' param is present", () => {
    it("uses the fallback route when 'from' is absent", async () => {
      const backLink = await setup(undefined);
      expect(backLink.value.route).toBe(
        `/suche?documentKind=${DocumentKind.CaseLaw}`,
      );
    });

    it("uses label 'Suche' when 'from' is absent", async () => {
      const backLink = await setup(undefined);
      expect(backLink.value.label).toBe("Suche");
    });

    it("uses the fallback route when 'from' is not a search path", async () => {
      const backLink = await setup("/search-other?query=test");
      expect(backLink.value.route).toBe(
        `/suche?documentKind=${DocumentKind.CaseLaw}`,
      );
    });

    it("uses the fallback when 'from' is an empty string", async () => {
      const backLink = await setup("");
      expect(backLink.value.route).toBe(
        `/suche?documentKind=${DocumentKind.CaseLaw}`,
      );
    });
  });
});
