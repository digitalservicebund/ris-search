import type { FetchContext } from "ofetch";
import { expect, it } from "vitest";
import { extendOnRequest, PORTAL_HEADER } from "./risBackend";

it("marks requests as portal requests", async () => {
  const context = {
    request: "/v1/legislation",
    options: { headers: new Headers() },
  } as unknown as FetchContext;

  for (const hook of extendOnRequest()) await hook(context);

  expect(context.options.headers.get(PORTAL_HEADER)).toBe("true");
});
