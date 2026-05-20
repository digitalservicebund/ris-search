import { beforeEach, describe, expect, it, vi } from "vitest";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { buildValidityTitleLabel, useNormSeo } from "./useNormSeo";
import { type LegislationExpression } from "~/types/api";
import dayjs from "dayjs";

const { useSeo } = vi.hoisted(() => ({
  useSeo: vi.fn(),
}));

mockNuxtImport("useSeo", () => useSeo);

describe("useNormSeo", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("calls useSeo with title, description and ogTitle", () => {
    useNormSeo({
      norm: {
        abbreviation: "FooBar",
        alternateName: "Foo Bar Gesetz",
      } as LegislationExpression,
      validityInterval: { from: dayjs("01-01-2025") },
      validityStatus: "InForce",
    });

    expect(useSeo).toHaveBeenCalledExactlyOnceWith({
      title: "FooBar, vom 01.01.2025, Aktuell gültig",
      description: "Foo Bar Gesetz",
      ogTitle: "FooBar: Fassung vom 01.01.2025, Aktuell gültig",
    });
  });

  describe("norm page title", () => {
    it("uses only abbreviation if validity data is undefined", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "FooBar" }),
      );
    });

    it.each([
      [undefined, undefined, ""],
      [dayjs("01-01-2025"), undefined, ", vom 01.01.2025"],
      [dayjs("01-01-2025"), dayjs("01-01-2026"), ", 01.01.2025-01.01.2026"],
      [undefined, dayjs("01-01-2026"), ", bis 01.01.2026"],
    ])("appends formatted validity period to title", (from, to, expected) => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityInterval: { from, to },
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: `FooBar${expected}` }),
      );
    });

    it("appends validity status", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityStatus: "InForce",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ title: "FooBar, Aktuell gültig" }),
      );
    });

    it("validity status succeeds validity period", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityInterval: {
          from: dayjs("01-01-2025"),
          to: dayjs("01-01-2026"),
        },
        validityStatus: "InForce",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          title: "FooBar, 01.01.2025-01.01.2026, Aktuell gültig",
        }),
      );
    });
  });

  describe("norm description", () => {
    it("returns empty string if norm has no alternateName or name", () => {
      useNormSeo({ norm: {} as LegislationExpression });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "" }),
      );
    });

    it("uses alternateName as description", () => {
      useNormSeo({
        norm: {
          name: "Gesetz über Foo und Bar",
          alternateName: "Foo Bar Gesetz",
        } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Foo Bar Gesetz" }),
      );
    });

    it("falls back to name if alternateName is absent", () => {
      useNormSeo({
        norm: { name: "Gesetz über Foo und Bar" } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ description: "Gesetz über Foo und Bar" }),
      );
    });

    it("truncates description at 150 characters", () => {
      const longTitle = "Ein sehr langes Gesetz ".repeat(7).trim(); // 154 chars
      useNormSeo({
        norm: { alternateName: longTitle } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          description:
            "Ein sehr langes Gesetz Ein sehr langes Gesetz Ein sehr langes Gesetz Ein sehr langes Gesetz Ein sehr langes Gesetz Ein sehr langes Gesetz Ein sehr",
        }),
      );
    });
  });

  describe("norm og:title", () => {
    it("empty if norm has no abbreviation or alternateName", () => {
      useNormSeo({
        norm: {} as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: undefined }),
      );
    });

    it("uses abbreviation as base title", () => {
      useNormSeo({
        norm: {
          alternateName: "Foo Bar Gesetz",
          abbreviation: "FooBar",
        } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "FooBar" }),
      );
    });

    it("falls back to alternateName if abbreviation is absent", () => {
      useNormSeo({
        norm: { alternateName: "Foo Bar Gesetz" } as LegislationExpression,
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "Foo Bar Gesetz" }),
      );
    });

    it("appends validFrom date", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityInterval: { from: dayjs("01-01-2025"), to: undefined },
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "FooBar: Fassung vom 01.01.2025" }),
      );
    });

    it("does not append validTo date", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityInterval: { from: undefined, to: dayjs("01-01-2026") },
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "FooBar" }),
      );
    });

    it("appends validity status label", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityStatus: "InForce",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({ ogTitle: "FooBar: Aktuell gültig" }),
      );
    });

    it("appends both validFrom date and validity status", () => {
      useNormSeo({
        norm: { abbreviation: "FooBar" } as LegislationExpression,
        validityInterval: {
          from: dayjs("01-01-2025"),
          to: dayjs("01-01-2026"),
        },
        validityStatus: "InForce",
      });

      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "FooBar: Fassung vom 01.01.2025, Aktuell gültig",
        }),
      );
    });

    it("truncates title", () => {
      useNormSeo({
        norm: {
          abbreviation: "Sehr Langes Gesetz",
        } as LegislationExpression,
        validityInterval: { from: dayjs("01-01-2025"), to: undefined },
        validityStatus: "InForce",
      });

      // "Sehr Langes Gesetz: Fassung vom 01.01.2025, Aktuell gültig" is 58 chars
      // expect truncation at last word boundary within 55 chars
      expect(useSeo).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: "Sehr Langes Gesetz: Fassung vom 01.01.2025, Aktuell",
        }),
      );
    });
  });
});

describe("buildValidityTitleLabel", () => {
  it("returns empty string when no interval is provided", () => {
    expect(buildValidityTitleLabel()).toBe("");
  });

  it("returns empty string when interval has no dates", () => {
    expect(buildValidityTitleLabel({})).toBe("");
  });

  it.each([
    [dayjs("2025-01-01"), undefined, ", vom 01.01.2025"],
    [undefined, dayjs("2026-01-01"), ", bis 01.01.2026"],
    [dayjs("2025-01-01"), dayjs("2026-01-01"), ", 01.01.2025-01.01.2026"],
  ])("formats the validity interval correctly", (from, to, expected) => {
    expect(buildValidityTitleLabel({ from, to })).toBe(expected);
  });
});
