import userEvent from "@testing-library/user-event";
import { render, screen } from "@testing-library/vue";
import DocumentDetailPage from "./DocumentDetailPage.vue";

describe("DocumentDetailPage.vue", () => {
  it("renders title", async () => {
    render(DocumentDetailPage, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        breadcrumbItems: [],
        documentHtmlClass: "",
        html: "",
      },
    });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Title",
    );
  });

  it("renders title placeholder if no title provided", async () => {
    render(DocumentDetailPage, {
      props: {
        titlePlaceholder: "Title Placeholder",
        breadcrumbItems: [],
        documentHtmlClass: "",
        html: "",
      },
    });

    expect(screen.getByRole("heading", { level: 1 })).toHaveTextContent(
      "Title Placeholder",
    );
  });

  it("renders breadcrumbs", async () => {
    render(DocumentDetailPage, {
      props: {
        titlePlaceholder: "Title Placeholder",
        breadcrumbItems: [
          {
            label: "Breadcrumb 1",
            route: "/someRoute",
          },
          {
            label: "Breadcrumb 2",
          },
        ],
        documentHtmlClass: "",
        html: "",
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

    expect(screen.getByRole("link", { name: "Breadcrumb 1" })).toBeVisible();
    expect(screen.getByText("Breadcrumb 2")).toBeVisible();
  });

  it("renders html content", async () => {
    render(DocumentDetailPage, {
      props: {
        titlePlaceholder: "Title Placeholder",
        breadcrumbItems: [],
        documentHtmlClass: "fooClass",
        html: "Html Content",
      },
    });

    const htmlContent = screen.getByText("Html Content");
    expect(htmlContent).toBeVisible();
    expect(htmlContent.getAttribute("class")).toContain("fooClass");
  });

  it("renders slots", async () => {
    const user = userEvent.setup();

    render(DocumentDetailPage, {
      props: {
        title: "Title",
        titlePlaceholder: "Title Placeholder",
        breadcrumbItems: [],
        documentHtmlClass: "",
        html: "",
      },
      slots: {
        actionsMenu: "ActionsMenu",
        metadata: "Metadata",
        sidebar: "Sidebar",
        details: "Detailed Information",
      },
    });

    expect(screen.getByText("ActionsMenu")).toBeVisible();
    expect(screen.getByText("Metadata")).toBeVisible();
    expect(screen.getByText("Sidebar")).toBeVisible();

    expect(await screen.findByText("Detailed Information")).not.toBeVisible();
    await user.click(screen.getByRole("link", { name: "Details" }));
    expect(screen.getByText("Detailed Information")).toBeVisible();
  });
});
