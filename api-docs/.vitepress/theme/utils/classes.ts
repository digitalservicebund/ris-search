import got from "got";
import MarkdownIt from "markdown-it";
import * as assert from "node:assert";
import * as fs from "node:fs/promises";
import * as path from "node:path";
import * as process from "node:process";
import { slugify } from "../utils";
import {
  isType,
  matchesId,
  type JSONLDContext,
  type JSONLDType,
} from "./jsonld";

export interface Env {
  cacheDir: string;
  contexts: Map<string, JSONLDContext>;
  md: MarkdownIt;
  prefixUrls: Record<string, string>;
  typeMapping: Record<string, string>;
}

export interface LocalizedText {
  en: string;
  de: string;
  [lang: string]: string;
}

export interface Class {
  id: string;
  type: "class";
  slug: string;
  labels: LocalizedText;
  descriptions: LocalizedText;
  subclassOf?: string;
  props: ClassProp[];
}

export interface ClassProp {
  id: string;
  type: "prop";
  slug: string;
  labels: LocalizedText;
  descriptions: LocalizedText;
  range: ClassPropRange[];
  subPropertyOf?: string;
}

export interface ClassPropRange {
  id: string;
  type: "date" | "string";
  labels: LocalizedText;
  url: string;
  enum?: { value: string; descriptions: LocalizedText }[];
}

export function createEnv(): Env {
  const ROOT_DIR = process.cwd();

  return {
    cacheDir: path.join(ROOT_DIR, ".cache", "ttl"),
    contexts: new Map(),
    md: new MarkdownIt({ html: true }),

    prefixUrls: {
      schema:
        "https://schema.org/version/latest/schemaorg-current-https.jsonld",
    },

    typeMapping: {
      "schema:Text": "string",
      "schema:Date": "date",
    },
  };
}

export async function resolveClasses(env: Env): Promise<Class[]> {
  const context = env.contexts.get("recht")!;
  const graph = context["@graph"];
  const classes = graph.filter(
    (e) => isType(e, "rdfs:Class") && e["@id"].startsWith("recht:")
  );

  const result: Class[] = [];
  for (const e of classes) {
    result.push({
      id: e["@id"],
      type: "class",
      slug: slugify(e["@id"]),
      labels: await resolveTexts(e, "rdfs:label", env),
      descriptions: await resolveMarkdownTexts(e, "rdfs:comment", env),
      subclassOf:
        e["rdfs:subClassOf"] !== undefined ? e["rdfs:subClassOf"]["@id"] : "",
      props: await resolveProps(e, env),
    });
  }

  return result;
}

export async function resolveProps(
  cls: JSONLDType,
  env: Env
): Promise<ClassProp[]> {
  const context = await resolveContext(cls["@id"], env);
  const graph = context["@graph"];

  const props = graph.filter(
    (e: JSONLDType) =>
      isType(e, "rdf:Property") && matchesId(e, "rdfs:domain", cls["@id"])
  );

  const result: ClassProp[] = [];
  for (const def of props) {
    result.push(await resolveProp(def, cls, env));
  }

  result.sort((a, b) => a.id.localeCompare(b.id));
  return result;
}

export async function resolveProp(
  def: JSONLDType,
  parent: JSONLDType,
  env: Env
): Promise<ClassProp> {
  const prop: ClassProp = Object.create({});
  prop.id = def["@id"];
  prop.type = "prop";
  prop.slug = slugify(parent["@id"], def["@id"]);
  prop.labels = await resolveTexts(def, "rdfs:label", env);
  prop.descriptions = await resolveMarkdownTexts(def, "rdfs:comment", env);
  prop.range = [];

  if (
    def["rdfs:subPropertyOf"] &&
    typeof def["rdfs:subPropertyOf"]["@id"] === "string"
  ) {
    prop.subPropertyOf = def["rdfs:subPropertyOf"]["@id"];
  }

  let range = [];
  if (Array.isArray(def["rdfs:range"])) {
    range = def["rdfs:range"];
  } else if (!!def["rdfs:range"]) {
    range = [def["rdfs:range"]];
  }

  for (const value of range) {
    const inner = await resolvePropRange(value, env);
    prop.range.push(inner);
  }

  return prop;
}

export async function resolvePropRange(
  def: JSONLDType,
  env: Env
): Promise<ClassPropRange> {
  const context = await resolveContext(def["@id"], env);
  const value = context["@graph"].find(
    (e: JSONLDType) => e["@id"] === def["@id"] && isType(e, "rdfs:Class")
  );

  assert.ok(value, `Failed to resolve value "${def["@id"]}`);

  const result: ClassPropRange = Object.create({});
  result.id = value["@id"];
  result.labels = await resolveTexts(value, "rdfs:label", env);

  result.type =
    value["meta:type"] || env.typeMapping[value["@id"]] || "unknown";

  result.url = value["@id"].includes("schema:")
    ? value["@id"].replace("schema:", "https://schema.org/")
    : null;

  if (matchesId(value, "rdfs:subClassOf", "schema:StatusEnumeration")) {
    result.enum = [];

    const values = context["@graph"].filter((e: JSONLDType) =>
      isType(e, def["@id"])
    );

    for (const v of values) {
      result.enum.push({
        value: v["rdfs:label"],
        descriptions: await resolveMarkdownTexts(v, "rdfs:comment", env),
      });
    }
  }

  return result;
}

export async function resolveTexts(
  def: JSONLDType,
  label: string,
  _env: Env
): Promise<LocalizedText> {
  const result: LocalizedText = { en: "", de: "" };

  if (!def.hasOwnProperty(label)) {
    return result;
  }

  if (typeof def[label] === "string") {
    result.en = def[label];
  } else if (Array.isArray(def[label])) {
    const de = def[label].find((e: JSONLDType) => e["@language"] === "de");
    const en =
      def[label].find((e: JSONLDType) => e["@language"] === "en") ||
      def[label].at(0);

    assert.ok(!!en);
    if (en) result.en = en["@value"];
    if (de) result.de = de["@value"];
  } else if (
    def[label].hasOwnProperty("@value") &&
    def[label].hasOwnProperty("@language")
  ) {
    result[def[label]["@language"]] = def[label]["@value"];
  }

  if (!result.de || result.de === "") {
    // console.warn(
    //   `Missing translation of "${label}" in German for "${def["@id"]}"`
    // );
    result.de = result.en;
  }

  return result;
}

export async function resolveMarkdownTexts(
  def: JSONLDType,
  label: string,
  env: Env
): Promise<LocalizedText> {
  const texts = await resolveTexts(def, label, env);
  return Object.fromEntries(
    Object.entries(texts).map(([l, t]) => [l, env.md.renderInline(t || "")])
  ) as LocalizedText;
}

/**
 * Downloads a remote context file based on the URL defined in `prefixUrls`.
 * Caches the downloaded results in `CONTEXTS_CACHE_DIR`.
 */
export async function resolveContext(id: string, env: Env) {
  const [prefix, _suffix] = id.split(":");

  if (env.contexts.has(prefix)) {
    return env.contexts.get(prefix);
  }
  if (!env.prefixUrls.hasOwnProperty(prefix)) {
    throw new Error(`Failed to resolve prefix "${prefix}"`);
  }

  const cacheFile = path.join(env.cacheDir, `${prefix}.jsonld`);
  try {
    const buffer = await fs.readFile(cacheFile, "utf-8");
    const jsonld = JSON.parse(buffer);
    env.contexts.set(prefix, jsonld);
    return jsonld;
  } catch (err) {
    // console.debug(`Fetch remote context "${prefix}"`);
    const jsonld = (await got(env.prefixUrls[prefix]).json()) as JSONLDContext;
    env.contexts.set(prefix, jsonld);

    await fs.writeFile(
      path.join(env.cacheDir, `${prefix}.jsonld`),
      JSON.stringify(jsonld, null, 2)
    );

    return jsonld;
  }
}
