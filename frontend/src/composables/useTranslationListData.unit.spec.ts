import { mockNuxtImport } from "@nuxt/test-utils/runtime";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { ref } from "vue";
import { useTranslationListData } from "./useTranslationListData";
import { useRisBackend } from "~/composables/useRisBackend";

const { useRisBackendMock, _executeMock } = vi.hoisted(() => {
  const _executeMock = vi.fn();

  return {
    useRisBackendMock: vi.fn(
      (_url: Ref<string>, _opts: Record<string, Ref<string>>) => ({
        status: ref("success"),
        data: computed(() => ref([{ "@id": "Cde" }, { "@id": "AbC" }])),
        error: ref(null),
        pending: ref(false),
        execute: _executeMock,
        refresh: vi.fn(),
        clear: vi.fn(),
      }),
    ),
    _executeMock,
  };
});

mockNuxtImport("useRisBackend", () => {
  return useRisBackendMock;
});

describe("useTranslationList", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should call useRisBackend with base URL when no id is provided", async () => {
    const { translations, translationsError, translationsStatus } =
      await useTranslationListData();
    expect(useRisBackend).toHaveBeenCalledWith("/v1/translatedLegislation");
    expect(unref(unref(translations))).toEqual([
      { "@id": "Cde" },
      { "@id": "AbC" },
    ]);
    expect(translationsError.value).toBeNull();
    expect(translationsStatus.value).toBe("success");
  });

  it("should include id in URL when id is provided", async () => {
    const id = ref("123");
    await useTranslationListData(id);
    expect(useRisBackend).toHaveBeenCalledWith(
      "/v1/translatedLegislation?id=123",
    );
  });
});
