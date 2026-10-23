import { getZitierungen } from "./zitierungen";

describe("getZitierungen", () => {
  const parser = new DOMParser();

  it("extracts aktiv and passiv Rechtsprechung and passiv Literatur", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head>
        <template id="referenzSelbststaendigeLiteraturPassiv"><ul><li>C</li></ul></template>
        <template id="referenzRechtsprechungPassiv"><ul><li>B</li></ul></template>
        <template id="referenzRechtsprechungAktiv"><ul><li>A</li></ul></template>
      </head><body></body></html>`,
      "text/html",
    );

    expect(getZitierungen(doc)).toEqual({
      aktivRechtsprechung: "<ul><li>A</li></ul>",
      passivRechtsprechung: "<ul><li>B</li></ul>",
      passivLiteratur: "<ul><li>C</li></ul>",
    });
  });

  it("merges passiv selbständige and unselbständige Literatur into one list", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head>
        <template id="referenzSelbststaendigeLiteraturPassiv"><ul><li>A</li></ul></template>
        <template id="referenzUnselbststaendigeLiteraturPassiv"><ul><li>B</li></ul></template>
      </head><body></body></html>`,
      "text/html",
    );

    expect(getZitierungen(doc)).toEqual({
      passivLiteratur: "<ul><li>A</li><li>B</li></ul>",
    });
  });

  it("ignores aktiv Literatur, Verwaltungsvorschriften and Normen", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head>
        <template id="referenzSelbststaendigeLiteraturAktiv"><ul><li>A</li></ul></template>
        <template id="referenzUnselbststaendigeLiteraturAktiv"><ul><li>B</li></ul></template>
        <template id="referenzVerwaltungsvorschriftPassiv"><ul><li>C</li></ul></template>
        <template id="referenzNorm"><ul><li>D</li></ul></template>
      </head><body></body></html>`,
      "text/html",
    );

    expect(getZitierungen(doc)).toBeUndefined();
  });

  it("returns undefined when no zitierungen templates are present", () => {
    const doc = parser.parseFromString(
      "<!DOCTYPE HTML><html><head></head><body></body></html>",
      "text/html",
    );

    expect(getZitierungen(doc)).toBeUndefined();
  });

  it("sanitizes the list markup down to ul, li and a", () => {
    const doc = parser.parseFromString(
      `<!DOCTYPE HTML><html><head><template id="referenzRechtsprechungAktiv"><ul><li onclick="evil()"><a href="/x" class="foo">GG Art. 1</a></li><li><script>alert(1)</script>BGB</li></ul></template></head><body></body></html>`,
      "text/html",
    );

    expect(getZitierungen(doc)).toEqual({
      aktivRechtsprechung:
        '<ul><li><a href="/x">GG Art. 1</a></li><li>BGB</li></ul>',
    });
  });
});
