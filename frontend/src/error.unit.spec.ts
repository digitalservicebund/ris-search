import { config, mount } from "@vue/test-utils";
import { describe } from "vitest";
import ErrorPage from "./error.vue";
import { HttpStatusCode } from "axios";

const mountComponent = (statusCode: number) =>
  mount(ErrorPage, {
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
        NuxtLayout: true,
        SimpleSearchInput: true,
      },
    },
  });

describe("error page", () => {
  beforeAll(() => {
    config.global.renderStubDefaultSlot = true;
  });

  afterAll(() => {
    config.global.renderStubDefaultSlot = false;
  });

  it("shows a specific message for Not Found errors (404)", async () => {
    const component = mountComponent(404);
    expect(component.text()).toMatchSnapshot();
  });

  it("shows a generic message for internal server errors (500)", async () => {
    const component = mountComponent(500);
    expect(component.text()).toMatchSnapshot();
  });

  it("shows a more generic message for other errors", async () => {
    const component = mountComponent(HttpStatusCode.ImATeapot);
    expect(component.text()).toMatchSnapshot();
  });
});
