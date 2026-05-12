import { mockNuxtImport } from "@nuxt/test-utils/runtime"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { computed, ref } from "vue"
import { useSeo } from "./useSeo"

const TEST_URL = "https://testphase.rechtsinformationen.bund.de/example"

const { useHead, useSeoMeta, useRequestURL } = vi.hoisted(() => ({
  useHead: vi.fn(),
  useSeoMeta: vi.fn(),
  useRequestURL: vi.fn(() => new URL(TEST_URL)),
}))

mockNuxtImport("useHead", () => useHead)
mockNuxtImport("useSeoMeta", () => useSeoMeta)
mockNuxtImport("useRequestURL", () => useRequestURL)

describe("useSeo composable", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    useRequestURL.mockReturnValue(new URL(TEST_URL))
  })

  describe("static (plain string) usage", () => {
    it("sets all meta tags and canonical link", () => {
      useSeo({ title: "Test Title", description: "Test Description" })

      expect(useSeoMeta).toHaveBeenCalledWith(
        expect.objectContaining({
          title: expect.objectContaining({ value: "Test Title" }),
          description: expect.objectContaining({ value: "Test Description" }),
          ogType: "article",
          ogTitle: expect.objectContaining({ value: "Test Title" }),
          ogDescription: expect.objectContaining({ value: "Test Description" }),
          ogUrl: TEST_URL,
          twitterTitle: expect.objectContaining({ value: "Test Title" }),
          twitterDescription: expect.objectContaining({ value: "Test Description" }),
        }),
      )

      expect(useHead).toHaveBeenCalledWith({
        link: [{ rel: "canonical", href: TEST_URL }],
      })
    })

    it("uses ogTitle override when provided", () => {
      useSeo({ title: "Test Title", description: "Test Description", ogTitle: "Custom OG Title" })

      expect(useSeoMeta).toHaveBeenCalledWith(
        expect.objectContaining({
          ogTitle: expect.objectContaining({ value: "Custom OG Title" }),
          twitterTitle: expect.objectContaining({ value: "Custom OG Title" }),
        }),
      )
    })
  })

  describe("dynamic (computed/ref) usage", () => {
    it("sets correct meta tags with computed title and description", () => {
      useSeo({ title: computed(() => "Test Title"), description: computed(() => "Test Description") })

      expect(useSeoMeta).toHaveBeenCalledWith(
        expect.objectContaining({
          title: expect.objectContaining({ value: "Test Title" }),
          description: expect.objectContaining({ value: "Test Description" }),
          ogType: "article",
          ogTitle: expect.objectContaining({ value: "Test Title" }),
          ogDescription: expect.objectContaining({ value: "Test Description" }),
          ogUrl: TEST_URL,
          twitterTitle: expect.objectContaining({ value: "Test Title" }),
          twitterDescription: expect.objectContaining({ value: "Test Description" }),
        }),
      )
    })

    it("accepts a ref as input", () => {
      const title = ref("Ref Title")
      const description = ref("Ref Description")

      useSeo({ title, description })

      expect(useSeoMeta).toHaveBeenCalledWith(
        expect.objectContaining({
          title: expect.objectContaining({ value: "Ref Title" }),
          description: expect.objectContaining({ value: "Ref Description" }),
        }),
      )
    })
  })

  describe("URL handling", () => {
    it.each([
      "https://testphase.rechtsinformationen.bund.de/custom-path",
      "https://testphase.rechtsinformationen.bund.de/page?param=value&other=test",
    ])("uses exact URL for canonical and og:url (%s)", (href) => {
      useRequestURL.mockReturnValueOnce(new URL(href))

      useSeo({ title: "Title", description: "Description" })

      expect(useSeoMeta).toHaveBeenCalledWith(expect.objectContaining({ ogUrl: href }))
      expect(useHead).toHaveBeenCalledWith({ link: [{ rel: "canonical", href }] })
    })
  })
})
