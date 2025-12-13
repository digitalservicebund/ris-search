import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe, it, expect } from "vitest";
import Logo from "./AppLogo.vue";

describe("AppLogo", () => {
  it("links to home and shows brand text", async () => {
    await renderSuspended(Logo);

    const link = screen.getByRole("link", { name: "Zur Startseite" });
    expect(link).toBeInTheDocument();
    expect(link).toHaveAttribute("href", expect.stringContaining("/"));

    expect(screen.getByText("Rechtsinformationen")).toBeInTheDocument();
    expect(screen.getByText("des Bundes")).toBeInTheDocument();
  });
});
