import { mount } from "@vue/test-utils";
import { describe, it, expect } from "vitest";
import { nextTick } from "vue";
import SortOptionsComponent from "./SortSelect.vue";
import { DocumentKind } from "~/types";

describe("SortSelect", () => {
  it("computes correct validSortOptions for DocumentKind.All", async () => {
    const wrapper = mount(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });
    await nextTick();
    const select = wrapper.findComponent({ name: "Select" });
    expect(select.props("options")).toEqual([
      { label: "Relevanz", value: "default" },
      { label: "Datum: Älteste zuerst", value: "date" },
      { label: "Datum: Neueste zuerst", value: "-date" },
    ]);
  });

  it("computes correct validSortOptions for DocumentKind.Norm", async () => {
    const wrapper = mount(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.Norm,
      },
    });

    const select = wrapper.findComponent({ name: "Select" });
    expect(select.props("options")).toEqual([
      { label: "Relevanz", value: "default" },
      { label: "Ausfertigungsdatum: Älteste zuerst", value: "date" },
      { label: "Ausfertigungsdatum: Neueste zuerst", value: "-date" },
    ]);
  });

  it("computes correct validSortOptions for DocumentKind.CaseLaw", async () => {
    const wrapper = mount(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.CaseLaw,
      },
    });

    const select = wrapper.findComponent({ name: "Select" });
    expect(select.props("options")).toEqual([
      { label: "Relevanz", value: "default" },
      { label: "Gericht: Von A nach Z", value: "courtName" },
      { label: "Gericht: Von Z nach A", value: "-courtName" },
      { label: "Entscheidungsdatum: Älteste zuerst", value: "date" },
      { label: "Entscheidungsdatum: Neueste zuerst", value: "-date" },
    ]);
  });

  it("updates validSortOptions when documentKind prop changes", async () => {
    const wrapper = mount(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });

    await wrapper.setProps({ documentKind: DocumentKind.CaseLaw });
    await nextTick();

    const select = wrapper.findComponent({ name: "Select" });
    expect(select.props("options")).toEqual([
      { label: "Relevanz", value: "default" },
      { label: "Gericht: Von A nach Z", value: "courtName" },
      { label: "Gericht: Von Z nach A", value: "-courtName" },
      { label: "Entscheidungsdatum: Älteste zuerst", value: "date" },
      { label: "Entscheidungsdatum: Neueste zuerst", value: "-date" },
    ]);
  });

  it("emits update:modelValue event when dropdown value changes", async () => {
    const wrapper = mount(SortOptionsComponent, {
      props: {
        documentKind: DocumentKind.All,
      },
    });

    const dropdownInput = wrapper.findComponent({ name: "Select" });
    await dropdownInput.setValue("date");

    expect(wrapper.emitted("update:modelValue")).toBeTruthy();
    expect(wrapper.emitted("update:modelValue")![0]).toEqual(["date"]);
  });
});
