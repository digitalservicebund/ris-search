import { mockNuxtImport, renderSuspended } from "@nuxt/test-utils/runtime";
import { userEvent } from "@testing-library/user-event/dist/cjs/index.js";
import { screen } from "@testing-library/vue";
import { describe, vi } from "vitest";
import ErrorPage from "./error.vue";

const { mockNavigateTo } = vi.hoisted(() => ({
  mockNavigateTo: vi.fn(),
}));
mockNuxtImport("navigateTo", () => mockNavigateTo);

const mountComponent = async (statusCode: number) =>
  await renderSuspended(ErrorPage, {
    props: {
      error: {
        statusCode,
        name: "",
        message: "",
        fatal: false,
        unhandled: false,
        toJSON: () => ({
          message: "",
          statusCode,
        }),
      },
    },
  });

describe("Error", () => {
  it("shows a specific message for Not Found errors (404)", async () => {
    await mountComponent(404);
    expect(screen.getByTestId("error-message").textContent).toMatchSnapshot();
  });

  it("can start new search from error page", async () => {
    const user = userEvent.setup();

    await mountComponent(404);
    await user.type(screen.getByRole("searchbox"), "foobar");
    await user.click(screen.getByRole("button", { name: "Suchen" }));

    expect(mockNavigateTo).toHaveBeenCalledWith({
      name: "suche",
      query: { query: "foobar" },
    });
  });

  it("shows a generic message for internal server errors (500)", async () => {
    await mountComponent(500);
    expect(screen.getByTestId("error-message").textContent).toMatchSnapshot();
  });

  it("shows a more generic message for other errors", async () => {
    await mountComponent(418);
    expect(screen.getByTestId("error-message").textContent).toMatchSnapshot();
  });
});
