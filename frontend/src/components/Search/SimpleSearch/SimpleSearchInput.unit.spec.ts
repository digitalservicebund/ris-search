import { describe, it, expect, vi, beforeEach } from "vitest";
import { mount, type VueWrapper } from "@vue/test-utils";
import { ref } from "vue";
import SimpleSearchInput from "./SimpleSearchInput.vue";

vi.mock("primevue/button", () => ({
  default: {
    name: "Button",
    template: "<button><slot></slot></button>",
  },
}));
vi.mock("primevue/inputtext", () => ({
  default: {
    name: "InputText",
    template: "<input />",
  },
}));

// Mock Nuxt functionality
vi.mock("#imports", () => {
  return {
    onNuxtReady: vi.fn((callback) => callback()),
    defineModel: vi.fn(() => ref("")),
  };
});

describe("SearchComponent", () => {
  let wrapper: VueWrapper;

  beforeEach(() => {
    wrapper = mount(SimpleSearchInput);
  });

  it("renders correctly", () => {
    expect(wrapper.find("input").exists()).toBe(true);
    expect(wrapper.find("button").exists()).toBe(true);
  });

  it("enables input after Nuxt is ready", async () => {
    expect(wrapper.find("input").attributes("disabled")).toBeFalsy();
  });

  it("updates currentText, but not model on input change", async () => {
    const input = wrapper.findComponent("input") as VueWrapper;
    input.vm.$emit("update:modelValue", "test query");
    // @ts-expect-error private property access
    expect(wrapper.vm.currentText).toBe("test query");
    // @ts-expect-error private property access
    expect(wrapper.vm.model).toBeFalsy();
  });

  it("updates input on model change", async () => {
    await wrapper.setProps({ modelValue: "updated model" });
    const input = wrapper.findComponent("input") as VueWrapper;
    expect(input.attributes("modelvalue")).toBe("updated model");
  });

  it("submits search on Enter key press", async () => {
    const input = wrapper.findComponent("input") as VueWrapper;
    input.vm.$emit("update:modelValue", "test query");
    input.vm.$emit("keyup", { key: "Enter" });
    // @ts-expect-error private property access
    expect(wrapper.vm.model).toBe("test query");
  });

  it("submits search on button click", async () => {
    const input = wrapper.findComponent("input") as VueWrapper;
    input.vm.$emit("update:modelValue", "test query");
    // @ts-expect-error private property access
    expect(wrapper.vm.currentText).toBe("test query");
    // @ts-expect-error private property access
    expect(wrapper.vm.model).toBeFalsy();

    const button = wrapper.findComponent("button") as VueWrapper;
    button.vm.$emit("click");
    // @ts-expect-error private property access
    expect(wrapper.vm.model).toBe("test query");
  });
});
