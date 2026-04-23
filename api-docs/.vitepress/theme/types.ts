export interface ThemeConfig {
  secondaryNav: NavItem[];
  contactEmail: string;
  outline: [number, number];
}

export interface NavItem {
  text: string;
  link: string;
  icon?: string;
  items?: NavItem[];
}
