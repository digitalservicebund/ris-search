import { render, screen } from "@testing-library/vue";
import { describe, expect, it } from "vitest";
import DetailsList from "./DetailsList.vue";
import type { DetailsListItem } from "./DetailsList.vue";

const nuxtLinkStub = {
  template: '<a :href="to"><slot /></a>',
  props: ["to"],
};

function renderComponent(items: DetailsListItem[]) {
  return render(DetailsList, {
    props: { items },
    global: {
      stubs: {
        NuxtLink: nuxtLinkStub,
      },
    },
  });
}

describe("DetailsList", () => {
  describe("text entries", () => {
    it("does not render entry when value is undefined", () => {
      renderComponent([{ type: "text", label: "Gericht:" }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("does not render entry when value is empty string", () => {
      renderComponent([{ type: "text", label: "Gericht:", value: "" }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("does not render entry when value is whitespace only", () => {
      renderComponent([{ type: "text", label: "Gericht:", value: "   " }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("renders label and value", () => {
      renderComponent([{ type: "text", label: "Gericht:", value: "BGH" }]);
      expect(screen.getByRole("term")).toHaveTextContent("Gericht:");
      expect(screen.getByRole("definition")).toHaveTextContent("BGH");
    });

    it("applies valueClass to the definition element", () => {
      renderComponent([
        {
          type: "text",
          label: "ECLI:",
          value: "ECLI:DE:BGH:2024:1",
          valueClass: "break-all",
        },
      ]);
      expect(screen.getByRole("definition")).toHaveClass("break-all");
    });
  });

  describe("list entries", () => {
    it("does not render entry when values array is empty", () => {
      renderComponent([{ type: "list", label: "Normen:", values: [] }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("renders one definition element per value", () => {
      renderComponent([
        { type: "list", label: "Normen:", values: ["BGB § 1", "BGB § 2"] },
      ]);
      expect(screen.getByRole("term")).toHaveTextContent("Normen:");
      const definitions = screen.getAllByRole("definition");
      expect(definitions).toHaveLength(2);
      expect(definitions[0]).toHaveTextContent("BGB § 1");
      expect(definitions[1]).toHaveTextContent("BGB § 2");
    });
  });

  describe("badge entries", () => {
    it("does not render entry when values array is empty", () => {
      renderComponent([{ type: "badge", label: "Normen:", values: [] }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("renders one badge element per value", () => {
      renderComponent([
        { type: "badge", label: "Normen:", values: ["BGB § 1", "BGB § 2"] },
      ]);
      expect(screen.getByRole("term")).toHaveTextContent("Normen:");
      expect(screen.getByText("BGB § 1")).toBeInTheDocument();
      expect(screen.getByText("BGB § 2")).toBeInTheDocument();
    });
  });

  describe("html entries", () => {
    it("does not render entry when html is undefined", () => {
      renderComponent([{ type: "html", label: "Fußnoten:" }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("does not render entry when html is empty string", () => {
      renderComponent([{ type: "html", label: "Fußnoten:", html: "" }]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("renders html content via v-html", () => {
      renderComponent([
        { type: "html", label: "Fußnoten:", html: "<b>Hinweis</b>" },
      ]);
      expect(screen.getByRole("term")).toHaveTextContent("Fußnoten:");
      const definition = screen.getByRole("definition");
      expect(definition.querySelector("b")).toHaveTextContent("Hinweis");
    });

    it("applies htmlClass to the definition element", () => {
      renderComponent([
        {
          type: "html",
          label: "Fußnoten:",
          html: "<b>Hinweis</b>",
          htmlClass: "footnotes",
        },
      ]);
      expect(screen.getByRole("definition")).toHaveClass("footnotes");
    });
  });

  describe("link entries", () => {
    it("does not render entry when url is undefined", () => {
      renderComponent([
        { type: "link", label: "Download:", text: "Herunterladen" },
      ]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("does not render entry when url is empty string", () => {
      renderComponent([
        { type: "link", label: "Download:", url: "", text: "Herunterladen" },
      ]);
      expect(screen.queryByRole("term")).not.toBeInTheDocument();
    });

    it("renders label, link text and href", () => {
      renderComponent([
        {
          type: "link",
          label: "Download:",
          url: "/file.zip",
          text: "Herunterladen",
        },
      ]);
      expect(screen.getByRole("term")).toHaveTextContent("Download:");
      const link = screen.getByRole("link", { name: /Herunterladen/ });
      expect(link).toHaveAttribute("href", "/file.zip");
    });

    it("sets data-attr when provided", () => {
      renderComponent([
        {
          type: "link",
          label: "Download:",
          url: "/file.zip",
          text: "Herunterladen",
          dataAttr: "xml-zip-view",
        },
      ]);
      expect(
        screen.getByRole("link", { name: /Herunterladen/ }),
      ).toHaveAttribute("data-attr", "xml-zip-view");
    });
  });

  describe("filtering", () => {
    it("only renders entries with content from a mixed list", () => {
      renderComponent([
        { type: "text", label: "Leer:", value: "" },
        { type: "text", label: "Datum:", value: "2024-01-01" },
        { type: "list", label: "Leere Liste:", values: [] },
        { type: "list", label: "Normen:", values: ["BGB § 1"] },
        { type: "html", label: "Kein HTML:" },
        { type: "html", label: "Hinweis:", html: "<p>Text</p>" },
        { type: "link", label: "Kein Link:", text: "Download" },
        { type: "link", label: "Download:", url: "/f.zip", text: "Download" },
      ]);

      const terms = screen.getAllByRole("term");
      expect(terms).toHaveLength(4);
      expect(terms[0]).toHaveTextContent("Datum:");
      expect(terms[1]).toHaveTextContent("Normen:");
      expect(terms[2]).toHaveTextContent("Hinweis:");
      expect(terms[3]).toHaveTextContent("Download:");
    });
  });
});
