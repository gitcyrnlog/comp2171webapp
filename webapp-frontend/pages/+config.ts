import type { Config } from "vike/types";
import vikeVue from "vike-vue/config";

// Default config (can be overridden by pages)
// https://vike.dev/config

const config: Config = {
  // https://vike.dev/head-tags
  title: "GAH Facilities Web Platform",
  description: "Resident and Staff web workflows for facilities operations",

  extends: [vikeVue],
};

export default config;
