import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import ProseA from "./ProseA.global.vue";

describe("ProseA", () => {
  it("renders without error with default props", async () => {
    await renderSuspended(ProseA, {
      props: { href: "/about" },
      slots: { default: () => "Test Link" },
    });

    expect(screen.getByRole("link", { name: "Test Link" })).toBeInTheDocument();
  });

  it("renders as NuxtLink for relative paths", async () => {
    await renderSuspended(ProseA, {
      props: { href: "/contact" },
      slots: { default: () => "Relative Link" },
    });

    const link = screen.getByRole("link", { name: "Relative Link" });

    expect(link).toHaveAttribute("href", "/contact");
  });

  it("renders as NuxtLink for hash links", async () => {
    await renderSuspended(ProseA, {
      props: { href: "#section" },
      slots: { default: () => "Hash Link" },
    });

    const link = screen.getByRole("link", { name: "Hash Link" });

    expect(link).toHaveAttribute("href", expect.stringContaining("#section"));
  });

  it("renders as ExternalLink for http URLs", async () => {
    await renderSuspended(ProseA, {
      props: { href: "http://example.com" },
      slots: { default: () => "HTTP Link" },
    });

    const link = screen.getByRole("link", {
      name: "HTTP Link(öffnet in einem neuen Tab)",
    });

    expect(link).toHaveAttribute("href", "http://example.com");
    expect(link).toHaveAttribute("target", "_blank");
  });

  it("renders as ExternalLink for https URLs", async () => {
    await renderSuspended(ProseA, {
      props: { href: "https://example.com" },
      slots: { default: () => "HTTPS Link" },
    });

    const link = screen.getByRole("link", {
      name: "HTTPS Link(öffnet in einem neuen Tab)",
    });

    expect(link).toHaveAttribute("href", "https://example.com");
    expect(link).toHaveAttribute("target", "_blank");
  });

  it("passes target attribute to NuxtLink for internal links", async () => {
    await renderSuspended(ProseA, {
      props: { href: "/imprint", target: "_blank" },
      slots: { default: () => "Internal with target" },
    });

    const link = screen.getByRole("link", { name: "Internal with target" });

    expect(link).toHaveAttribute("target", "_blank");
  });

  it("renders slot content correctly", async () => {
    await renderSuspended(ProseA, {
      props: { href: "/about" },
      slots: { default: () => "Custom slot content" },
    });

    expect(screen.getByText("Custom slot content")).toBeInTheDocument();
  });

  it("renders mailto links", async () => {
    await renderSuspended(ProseA, {
      props: { href: "mailto:test@example.com" },
      slots: { default: () => "Email Link" },
    });

    const link = screen.getByRole("link", { name: "Email Link" });

    expect(link).not.toHaveAttribute("target", "_blank");
  });
});
