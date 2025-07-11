import { afterEach } from "vitest";
import { getTextFromElements } from "./htmlParser";

describe("getTextFromElements", () => {
  afterEach(() => {
    document.body.innerHTML = "";
  });
  it("should return an empty array when elements is undefined", () => {
    const result = getTextFromElements();
    expect(result).toEqual([]);
  });

  it("should return an empty array when elements is an empty NodeList", () => {
    const elements = document.createDocumentFragment()
      .childNodes as NodeListOf<Element>;
    const result = getTextFromElements(elements);
    expect(result).toEqual([]);
  });

  it("should return an array of text content from elements", () => {
    const div1 = document.createElement("div");
    div1.textContent = "Hello";
    const div2 = document.createElement("div");
    div2.textContent = "World";
    const div3 = document.createElement("div");
    div3.textContent = "";
    document.body.append(div1, div2, div3);
    const elements = document.querySelectorAll("div");

    const result = getTextFromElements(elements);
    expect(result).toEqual(["Hello", "World"]);
  });

  it("should filter out null or undefined text content", () => {
    const div1 = document.createElement("div");
    div1.textContent = "Hello";
    const div2 = document.createElement("div");
    div2.textContent = null;
    const div3 = document.createElement("div");
    document.body.append(div1, div2, div3);
    const elements = document.querySelectorAll("div");

    const result = getTextFromElements(elements);
    expect(result).toEqual(["Hello"]);
  });
});
