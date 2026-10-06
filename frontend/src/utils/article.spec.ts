import { describe, expect, it } from "vitest";
import {
  getExpressionElis,
  getNewestExpressionEli,
  tocHeadlineAdditionLabel,
} from "~/utils/article";

describe("tocHeadlineAdditionLabel", () => {
  it("returns an empty string if temporal coverage is undefined", () => {
    expect(tocHeadlineAdditionLabel(undefined)).toBe("");
  });

  it("returns an empty string for an open start and open end temporal coverage", () => {
    expect(tocHeadlineAdditionLabel("../..")).toBe("");
  });

  it("returns from and to date for a full temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/2025-12-01")).toBe(
      "(01.09.2025 - 01.12.2025)",
    );
  });

  it("returns only the from date for an open end temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("2025-09-01/..")).toBe("(vom 01.09.2025)");
  });

  it("returns only the to date for an open start temporal coverage string", () => {
    expect(tocHeadlineAdditionLabel("../2025-12-01")).toBe("(bis 01.12.2025)");
  });
});

describe("getExpressionElis", () => {
  it("returns the ELIs of all expressions without the API prefix", () => {
    const articleVersion = {
      "@id":
        "/v1/legislation/eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu/art-z1",
      eId: "art-z1",
      name: "Art 1",
      temporalCoverage: "2022-01-01/..",
      isPartOf: [
        {
          "@id": "/v1/legislation/eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu",
        },
        {
          "@id": "/v1/legislation/eli/bund/bgbl-1/2020/s1234/2023-01-01/1/deu",
        },
      ],
    };

    expect(getExpressionElis(articleVersion)).toEqual([
      "eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu",
      "eli/bund/bgbl-1/2020/s1234/2023-01-01/1/deu",
    ]);
  });

  it("returns an empty list if the version is not part of any expression", () => {
    const articleVersion = {
      "@id":
        "/v1/legislation/eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu/art-z1",
      eId: "art-z1",
      name: "Art 1",
      temporalCoverage: "2022-01-01/..",
    };

    expect(getExpressionElis(articleVersion)).toEqual([]);
  });
});

describe("getNewestExpressionEli", () => {
  it("returns undefined for an empty list", () => {
    expect(getNewestExpressionEli([])).toBeUndefined();
  });

  it("returns the expression with the latest point in time", () => {
    expect(
      getNewestExpressionEli([
        "eli/bund/bgbl-1/2020/s1234/2021-01-01/1/deu",
        "eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu",
        "eli/bund/bgbl-1/2020/s1234/2020-01-01/1/deu",
      ]),
    ).toBe("eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu");
  });

  it("uses the version as a tie breaker for the same point in time", () => {
    expect(
      getNewestExpressionEli([
        "eli/bund/bgbl-1/2020/s1234/2022-01-01/2/deu",
        "eli/bund/bgbl-1/2020/s1234/2022-01-01/10/deu",
        "eli/bund/bgbl-1/2020/s1234/2022-01-01/1/deu",
      ]),
    ).toBe("eli/bund/bgbl-1/2020/s1234/2022-01-01/10/deu");
  });
});
