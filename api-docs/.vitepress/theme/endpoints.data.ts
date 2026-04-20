import * as fs from "node:fs/promises";
import { defineLoader } from "vitepress";
import MarkdownIt from "markdown-it";
import type { LocalizedText } from "./utils/classes";

export interface Endpoints {
  paths: Record<string, Record<string, Endpoint>>;
}

export interface Endpoint {
  operationId: string;
  parameters: EndpointParameter[];
}

export interface EndpointParameter {
  id: string;
  default: any;
  descriptions: LocalizedText;
  in: "query" | "path";
  name: string;
  required: boolean;
  type: "string" | "array" | "number";
  enum?: string[];
  items?: { type: "string" };
}

declare const data: Endpoints;
export { data };

export default defineLoader({
  watch: ["../../docs/data/*.endpoint.json"],
  async load(files) {
    const endpoints: Endpoints = { paths: {} };
    const md = new MarkdownIt({ html: true });

    for (const file of files) {
      const buffer = await fs.readFile(file, "utf-8");
      const json = JSON.parse(buffer) as Endpoints["paths"];

      Object.entries(json).forEach(([_path, def]) => {
        Object.entries(def).forEach(([_method, endpoint]) => {
          endpoint.parameters.forEach((param) => {
            param.descriptions = {
              en: md.renderInline(param.descriptions.en || ""),
              de: md.renderInline(param.descriptions.de || ""),
            };

            param.id =
              `${endpoint.operationId}-${param.name}`.toLocaleLowerCase();
          });
        });
      });

      endpoints.paths = Object.assign(endpoints.paths, json);
    }

    return endpoints;
  },
});
