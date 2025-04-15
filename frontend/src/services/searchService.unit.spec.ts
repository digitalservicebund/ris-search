import { describe, it, expect, vi, type Mock } from "vitest";
import { axiosInstance } from "./httpClient";
import { advancedSearch, search } from "./searchService";
import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { DocumentKind } from "~/types";

vi.mock("./httpClient");

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
  describe("search", () => {
    it("should call axios.get with the correct URL and parameters", async () => {
      // Arrange
      const params = {
        query: "test",
        itemsPerPage: 10,
        pageNumber: 1,
        sort: "date",
        category: "R.Urteil",
        date: "2023-01-01",
        court: "court1",
        temporalCoverage: "0000",
      };

      const expectedParams = new URLSearchParams({
        searchTerm: "test",
        size: "10",
        pageIndex: "1",
        sort: "date",
        dateFrom: "2023-01-01",
        dateTo: "2023-01-01",
        typeGroup: "Urteil",
        court: "court1",
        temporalCoverageFrom: "0000",
        temporalCoverageTo: "0000",
      });
      const expectedURL = "/v1/case-law?" + expectedParams;
      const expectedConfig = {
        timeout: 10000,
        headers: {
          Accept: "application/json",
        },
        baseURL: "https://backend",
      };

      (axiosInstance.get as Mock).mockResolvedValue({ data: {} });

      // Act
      await search(params);

      // Assert
      expect(axiosInstance.get).toHaveBeenCalledWith(
        expectedURL,
        expectedConfig,
      );
    });
  });

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
