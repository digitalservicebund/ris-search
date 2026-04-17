export interface JSONLDContext {
  "@context": Record<string, string>;
  "@graph": JSONLDType[];
}

export interface JSONLDType {
  "@type": string | string[];
  [prop: string]: any;
}

export function isType(def: JSONLDType, value: string) {
  if (Array.isArray(def["@type"])) {
    return def["@type"].includes(value);
  } else if (typeof def["@type"] === "string") {
    return def["@type"] === value;
  }
  return false;
}

export function matchesId(def: JSONLDType, prop: string, value: string) {
  if (Array.isArray(def[prop])) {
    return def[prop].find((e: JSONLDType) => e["@id"] === value);
  } else if (typeof def[prop] === "string") {
    return def[prop] === value;
  } else if (def[prop] && def[prop]["@id"]) {
    return def[prop]["@id"] === value;
  }
  return false;
}
