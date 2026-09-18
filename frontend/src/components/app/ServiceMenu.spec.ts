import { renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { describe, it, expect, vi } from "vitest";
import ServiceMenu from "./ServiceMenu.vue";

const mockPrivateFeaturesEnabled = vi.fn(() => false);
vi.mock("~/composables/usePrivateFeaturesFlag", () => ({
  usePrivateFeaturesFlag: () => mockPrivateFeaturesEnabled(),
}));

describe("ServiceMenu", () => {
  it('emits "selectItem" for each NuxtLink that has been clicked', async () => {
    mockPrivateFeaturesEnabled.mockReturnValue(true);
    const user = userEvent.setup();
    const { emitted } = await renderSuspended(ServiceMenu, {
      props: {
        listClass: "test-class",
      },
    });

    const allLinks = screen.getAllByRole("link");
    for (const link of allLinks) {
      await user.click(link);
    }
    expect(emitted("selectItem")).toBeTruthy();
    // The api docs link doesn't emit because it is external
    expect(emitted("selectItem")?.length).toBe(4);
  });

  it("displays 5 links when private features enabled", async () => {
    mockPrivateFeaturesEnabled.mockReturnValue(true);
    await renderSuspended(ServiceMenu, {
      props: {
        listClass: "test-class",
      },
    });

    expect(screen.getAllByRole("link")).toHaveLength(5);

    const translationsLink = screen.getByRole("link", {
      name: "English translations",
    });
    expect(translationsLink).toBeVisible();
    expect(translationsLink).toHaveAttribute("href", "/translations");

    const apiDocsLink = screen.getByRole("link", {
      name: /API-Dokumentation/,
    });
    expect(apiDocsLink).toBeVisible();
    expect(apiDocsLink).toHaveAttribute(
      "href",
      "https://docs.rechtsinformationen.bund.de",
    );

    const signLanguageLink = screen.getByRole("link", {
      name: "Gebärdensprache",
    });
    expect(signLanguageLink).toBeVisible();
    expect(signLanguageLink).toHaveAttribute("href", "/gebaerdensprache");

    const simpleLanguageLink = screen.getByRole("link", {
      name: "Leichte Sprache",
    });
    expect(simpleLanguageLink).toBeVisible();
    expect(simpleLanguageLink).toHaveAttribute("href", "/leichte-sprache");

    const contactLink = screen.getByRole("link", { name: "Kontakt" });
    expect(contactLink).toBeVisible();
    expect(contactLink).toHaveAttribute("href", "/kontakt");
  });

  it("displays 2 links when private features disabled", async () => {
    mockPrivateFeaturesEnabled.mockReturnValue(false);
    await renderSuspended(ServiceMenu, {
      props: {
        listClass: "test-class",
      },
    });

    expect(screen.getAllByRole("link")).toHaveLength(2);

    const apiDocsLink = screen.getByRole("link", {
      name: /API-Dokumentation/,
    });
    expect(apiDocsLink).toBeVisible();
    expect(apiDocsLink).toHaveAttribute(
      "href",
      "https://docs.rechtsinformationen.bund.de",
    );

    const contactLink = screen.getByRole("link", { name: "Kontakt" });
    expect(contactLink).toBeVisible();
    expect(contactLink).toHaveAttribute("href", "/kontakt");
  });
});
