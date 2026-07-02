import { renderSuspended } from "@nuxt/test-utils/runtime";
import { screen } from "@testing-library/vue";
import { describe } from "vitest";
import ErrorPage from "./error.vue";

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

  it("shows a generic message for internal server errors (500)", async () => {
    await mountComponent(500);
    expect(screen.getByTestId("error-message").textContent).toMatchSnapshot();
  });

  it("shows a more generic message for other errors", async () => {
    await mountComponent(418);
    expect(screen.getByTestId("error-message").textContent).toMatchSnapshot();
  });
});
