// @vitest-environment node
import { describe, expect, it } from "vitest";
import { getSameOriginUrl } from "./sameOriginUrl";

const origin = "https://example.com";

describe("sameOriginUrl", () => {
  it.each([
    ["/suche?query=test#results", "/suche?query=test#results"],
    ["https://example.com/page?param=value", "/page?param=value"],
    ["/a/../b", "/b"],
  ])("returns the URL for %j", (target, expected) => {
    const url = getSameOriginUrl(target, origin);

    expect(`${url?.pathname}${url?.search}${url?.hash}`).toBe(expected);
  });

  it.each([
    undefined,
    ["/a", "/b"],
    "//evil.example.com/path",
    "/\\evil.example.com",
    "https://evil.example.com/path",
    "https://example.com//evil.example.com",
    "/.//evil.example.com",
    "/a/..//evil.example.com",
    "/.\\/evil.example.com",
    "http://[invalid",
  ])("returns undefined for %j", (target) => {
    expect(getSameOriginUrl(target, origin)).toBeUndefined();
  });
});
