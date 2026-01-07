import { renderSuspended } from "@nuxt/test-utils/runtime";
import { describe } from "vitest";
import ErrorPage from "./error.vue";

const NuxtLayoutStub = {
  name: "NuxtLayout",
  template: "<div><slot /></div>",
};

const SimpleSearchInputStub = {
  name: "SimpleSearchInput",
  template: "<div></div>",
};

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
    global: {
      stubs: {
        NuxtLayout: NuxtLayoutStub,
        SimpleSearchInput: SimpleSearchInputStub,
      },
    },
  });

describe("Error", () => {
  it("shows a specific message for Not Found errors (404)", async () => {
    const { container } = await mountComponent(404);
    expect(container.textContent).toMatchSnapshot();
  });

  it("shows a generic message for internal server errors (500)", async () => {
    const { container } = await mountComponent(500);
    expect(container.textContent).toMatchSnapshot();
  });

  it("shows a more generic message for other errors", async () => {
    const { container } = await mountComponent(418);
    expect(container.textContent).toMatchSnapshot();
  });
});
