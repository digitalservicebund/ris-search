import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, type Mock } from "vitest";
import { advancedSearch } from "./searchService";
import { DocumentKind } from "~/types";
import { axiosInstance } from "~/utils/services/httpClient";

vi.mock("~/utils/services/httpClient");

const { useRuntimeConfigMock } = vi.hoisted(() => {
  return {
    useRuntimeConfigMock: vi.fn(() => {
      return {
        public: {
          backendURL: "https://backend",
        },
      };
    }),
  };
});

mockNuxtImport("useRuntimeConfig", () => {
  return useRuntimeConfigMock;
});

describe("searchService", () => {
  describe("advancedSearch", () => {
    it("should call axios.get with the correct URL and parameters", async () => {
      // Arrange
      const params = {
        query: "KEY: value",
        itemsPerPage: 10,
        pageNumber: 1,
        sort: "date",
        documentKind: DocumentKind.CaseLaw,
      };

      const expectedParams = new URLSearchParams({
        query: "KEY: value",
        size: "10",
        pageIndex: "1",
        sort: "date",
      });
      const expectedURL =
        "/v1/document/lucene-search/case-law?" + expectedParams;
      const expectedConfig = {
        timeout: 10000,
        headers: {
          Accept: "application/json",
        },
        baseURL: "https://backend",
      };

      (axiosInstance.get as Mock).mockResolvedValue({ data: {} });

      // Act
      await advancedSearch(params);

      // Assert
      expect(axiosInstance.get).toHaveBeenCalledWith(
        expectedURL,
        expectedConfig,
      );
    });
  });
});
