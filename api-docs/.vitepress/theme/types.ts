export interface ThemeConfig {
  secondaryNav: NavItem[];
  contactEmail: string;
  outline: [number, number];
  swaggerUiUrl: string;
}

export interface NavItem {
  text: string;
  link: string;
  icon?: string;
  items?: NavItem[];
}
