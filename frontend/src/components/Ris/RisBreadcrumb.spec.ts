import { render, screen } from "@testing-library/vue";
import RisBreadcrumb from "./RisBreadcrumb.vue";

describe("RisBreadcrumb", () => {
  it("renders home as text when no items given", () => {
    render(RisBreadcrumb);

    expect(screen.getByText("Startseite")).toBeVisible();
  });

  it("renders home and all but the last item as links", () => {
    render(RisBreadcrumb, {
      props: {
        items: [
          { label: "Foo", route: "/foo" },
          { label: "LastItem", route: "/notUsed" },
        ],
      },
      global: {
        stubs: {
          NuxtLink: {
            template: '<a :href="to"><slot /></a>',
            props: ["to"],
          },
        },
      },
    });

    const links = screen.getAllByRole("link");
    expect(links).toHaveLength(2);

    const home = links[0];
    expect(home).toBeVisible();
    expect(home).toHaveTextContent("Startseite");
    expect(home?.getAttribute("href")).toEqual("/");

    const foo = links[1];
    expect(foo).toBeVisible();
    expect(foo).toHaveTextContent("Foo");
    expect(foo?.getAttribute("href")).toEqual("/foo");

    expect(screen.getByText("LastItem")).toBeVisible();
  });
});
