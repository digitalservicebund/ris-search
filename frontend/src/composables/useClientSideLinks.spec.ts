import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event";
import { screen } from "@testing-library/vue";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { useClientSideLinks } from "./useClientSideLinks";

const { mockNavigateTo } = vi.hoisted(() => ({ mockNavigateTo: vi.fn() }));
mockNuxtImport("navigateTo", () => mockNavigateTo);

const normPath = "/gesetze/eli/bund/bgbl-1/2000/s1016/2023-04-26/10/deu";

const TestComponent = defineComponent({
  props: { html: { type: String, required: true } },
  setup(props) {
    const element = ref<HTMLElement>();
    useClientSideLinks(element);
    return () => h("div", { ref: element, innerHTML: props.html });
  },
});

function renderLinks(html: string) {
  return renderSuspended(TestComponent, { props: { html } });
}

// jsdom can't navigate and logs an error for every link it would follow
function preventBrowserNavigation(event: Event) {
  event.preventDefault();
}

describe("useClientSideLinks", () => {
  beforeEach(() => {
    mockNavigateTo.mockClear();
    document.addEventListener("click", preventBrowserNavigation);
  });

  afterEach(() => {
    document.removeEventListener("click", preventBrowserNavigation);
  });

  it("navigates with the router when clicking an internal link", async () => {
    await renderLinks(`<a href="${normPath}/art-z1?from=suche">§ 1</a>`);

    await userEvent.click(screen.getByRole("link", { name: "§ 1" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/art-z1?from=suche`,
    );
  });

  it("navigates with the router when clicking inside a link", async () => {
    await renderLinks(`<a href="${normPath}/art-z1"><h2>§ 1</h2></a>`);

    await userEvent.click(screen.getByRole("heading", { name: "§ 1" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/art-z1`,
    );
  });

  it("navigates with the router when pressing enter on a focused link", async () => {
    await renderLinks(`<a href="${normPath}/art-z1">§ 1</a>`);
    const user = userEvent.setup();

    screen.getByRole("link", { name: "§ 1" }).focus();
    await user.keyboard("{Enter}");

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/art-z1`,
    );
  });

  it("navigates with the router for absolute links to the same origin", async () => {
    const href = `${globalThis.location.origin}${normPath}/art-z1`;
    await renderLinks(`<a href="${href}">§ 1</a>`);

    await userEvent.click(screen.getByRole("link", { name: "§ 1" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/art-z1`,
    );
  });

  it("keeps encoded characters in the path", async () => {
    const eId = "art-z%C2%A7%C2%A7%2018%20bis%2021";
    await renderLinks(`<a href="${normPath}/${eId}">§§ 18 bis 21</a>`);

    await userEvent.click(screen.getByRole("link", { name: "§§ 18 bis 21" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(
      `${normPath}/${eId}`,
    );
  });

  it("leaves links to other origins to the browser", async () => {
    await renderLinks(`<a href="https://example.com${normPath}">Extern</a>`);

    await userEvent.click(screen.getByRole("link", { name: "Extern" }));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("leaves links with a different target to the browser", async () => {
    await renderLinks(`<a href="${normPath}" target="_blank">PDF</a>`);

    await userEvent.click(screen.getByRole("link", { name: "PDF" }));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("navigates with the router for links targeting the same window", async () => {
    await renderLinks(`<a href="${normPath}" target="_self">Norm</a>`);

    await userEvent.click(screen.getByRole("link", { name: "Norm" }));

    expect(mockNavigateTo).toHaveBeenCalledExactlyOnceWith(normPath);
  });

  it("leaves download links to the browser", async () => {
    await renderLinks(`<a href="${normPath}" download>Download</a>`);

    await userEvent.click(screen.getByRole("link", { name: "Download" }));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it.each(["Control", "Meta", "Shift", "Alt"])(
    "leaves clicks with the %s key held to the browser",
    async (key) => {
      await renderLinks(`<a href="${normPath}">Norm</a>`);
      const user = userEvent.setup();

      await user.keyboard(`{${key}>}`);
      await user.click(screen.getByRole("link", { name: "Norm" }));
      await user.keyboard(`{/${key}}`);

      expect(mockNavigateTo).not.toHaveBeenCalled();
    },
  );

  it("leaves middle clicks to the browser", async () => {
    await renderLinks(`<a href="${normPath}">Norm</a>`);
    const user = userEvent.setup();

    await user.pointer({
      keys: "[MouseMiddle]",
      target: screen.getByRole("link", { name: "Norm" }),
    });

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("leaves anchors on the current page to the browser", async () => {
    const currentPath = globalThis.location.pathname;
    await renderLinks(
      `<a href="#fussnote-1">1</a><a href="${currentPath}#fussnote-2">2</a>`,
    );

    await userEvent.click(screen.getByRole("link", { name: "1" }));
    await userEvent.click(screen.getByRole("link", { name: "2" }));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("leaves paths without a matching route to the browser", async () => {
    await renderLinks(`<a href="/v1/legislation/file.pdf">Datei</a>`);

    await userEvent.click(screen.getByRole("link", { name: "Datei" }));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("ignores clicks another handler already handled", async () => {
    await renderLinks(`<a href="${normPath}"><span>Norm</span></a>`);
    const span = screen.getByText("Norm");
    span.addEventListener("click", (event) => event.preventDefault());

    await userEvent.click(span);

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("ignores links without href", async () => {
    await renderLinks(`<a>Kein Link</a>`);

    await userEvent.click(screen.getByText("Kein Link"));

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it("stops handling clicks after unmounting", async () => {
    const { unmount } = await renderLinks(`<a href="${normPath}">Norm</a>`);
    const link = screen.getByRole("link", { name: "Norm" });
    link.addEventListener("click", preventBrowserNavigation);

    unmount();
    link.click();

    expect(mockNavigateTo).not.toHaveBeenCalled();
  });
});
