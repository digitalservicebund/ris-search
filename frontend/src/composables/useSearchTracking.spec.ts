import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";
import { DocumentKind } from "~/types/api";
import { useSearchTracking } from "./useSearchTracking";

const { trackMock } = vi.hoisted(() => ({ trackMock: vi.fn() }));

mockNuxtImport("useTracking", () => () => ({ track: trackMock }));

describe("useSearchTracking", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("tracks a search", () => {
    const { trackSearch } = useSearchTracking();

    trackSearch({ query: "Mietrecht", documentKind: DocumentKind.CaseLaw });

    expect(trackMock).toHaveBeenCalledWith("search_submitted", {
      query: "Mietrecht",
      document_kind: "gerichtsentscheidungen",
    });
  });

  it.each([
    [DocumentKind.All, "alle"],
    [DocumentKind.CaseLaw, "gerichtsentscheidungen"],
    [DocumentKind.Norm, "gesetze"],
    [DocumentKind.Literature, "literaturnachweise"],
    [DocumentKind.AdministrativeDirective, "verwaltungsregelungen"],
  ])("tracks document kind %s as %s", (documentKind, expected) => {
    const { trackSearch } = useSearchTracking();

    trackSearch({ query: "Mietrecht", documentKind });

    expect(trackMock).toHaveBeenCalledWith(
      "search_submitted",
      expect.objectContaining({ document_kind: expected }),
    );
  });

  it("doesn't track a search without a query", () => {
    const { trackSearch } = useSearchTracking();

    trackSearch({ query: "", documentKind: DocumentKind.All });

    expect(trackMock).not.toHaveBeenCalled();
  });

  it("doesn't track a search with only whitespace", () => {
    const { trackSearch } = useSearchTracking();

    trackSearch({ query: "   ", documentKind: DocumentKind.All });

    expect(trackMock).not.toHaveBeenCalled();
  });

  it("tracks the trimmed query", () => {
    const { trackSearch } = useSearchTracking();

    trackSearch({ query: "  Mietrecht ", documentKind: DocumentKind.All });

    expect(trackMock).toHaveBeenCalledWith(
      "search_submitted",
      expect.objectContaining({ query: "Mietrecht" }),
    );
  });

  it("doesn't track the current search again", () => {
    const { trackSearch } = useSearchTracking({
      query: "Mietrecht",
      documentKind: DocumentKind.Norm,
    });

    trackSearch({ query: "Mietrecht", documentKind: DocumentKind.Norm });

    expect(trackMock).not.toHaveBeenCalled();
  });

  it("doesn't track the current search again if only whitespace differs", () => {
    const { trackSearch } = useSearchTracking({
      query: "Mietrecht",
      documentKind: DocumentKind.Norm,
    });

    trackSearch({ query: "Mietrecht ", documentKind: DocumentKind.Norm });

    expect(trackMock).not.toHaveBeenCalled();
  });

  it("follows changes of the current search", () => {
    const currentQuery = ref("Mietrecht");
    const { trackSearch } = useSearchTracking({
      query: currentQuery,
      documentKind: DocumentKind.Norm,
    });

    currentQuery.value = "Kündigung";
    trackSearch({ query: "Mietrecht", documentKind: DocumentKind.Norm });

    expect(trackMock).toHaveBeenCalledOnce();
  });

  it("tracks a new query for the current document kind", () => {
    const { trackSearch } = useSearchTracking({
      query: "Mietrecht",
      documentKind: DocumentKind.Norm,
    });

    trackSearch({ query: "Kündigung", documentKind: DocumentKind.Norm });

    expect(trackMock).toHaveBeenCalledOnce();
  });

  it("tracks a new document kind for the current query", () => {
    const { trackSearch } = useSearchTracking({
      query: "Mietrecht",
      documentKind: DocumentKind.Norm,
    });

    trackSearch({ query: "Mietrecht", documentKind: DocumentKind.CaseLaw });

    expect(trackMock).toHaveBeenCalledOnce();
  });
});
