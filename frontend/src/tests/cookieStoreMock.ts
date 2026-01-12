import { vi } from "vitest";

/** In-memory cookie storage for mocking the CookieStore API. */
export const cookieStoreBackend = new Map<
  string,
  { name: string; value: string }
>();

/** Mock implementation of the browser's CookieStore API for use in tests. */
export const cookieStoreMock = {
  get: vi
    .fn()
    .mockImplementation((name: string) =>
      Promise.resolve(cookieStoreBackend.get(name)),
    ),

  getAll: vi
    .fn()
    .mockImplementation(() =>
      Promise.resolve([...cookieStoreBackend.values()]),
    ),

  set: vi
    .fn()
    .mockImplementation((options: { name: string; value: string }) => {
      cookieStoreBackend.set(options.name, {
        name: options.name,
        value: options.value,
      });
      return Promise.resolve();
    }),

  delete: vi.fn().mockImplementation((options: { name: string }) => {
    cookieStoreBackend.delete(options.name);
    return Promise.resolve();
  }),
};

vi.stubGlobal("cookieStore", cookieStoreMock);
