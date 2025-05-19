# 9. Automatic class sorting with Prettier

Date: 2025-01-06

## Status

Accepted

## Context

Tailwind CSS class order can significantly impact code readability and maintainability. Tailwind Labs provides an [official class order](https://tailwindcss.com/blog/automatic-class-sorting-with-prettier#how-classes-are-sorted) optimized for readability. Enforcing this order manually or through custom linting rules is error-prone and time-consuming. The prettier-plugin-tailwindcss plugin automatically sorts Tailwind CSS classes according to this default order, integrating seamlessly into Prettier workflows.

This ADR evaluates whether to adopt prettier-plugin-tailwindcss to manage Tailwind CSS class sorting in our codebase.

## Decision

We will adopt prettier-plugin-tailwindcss to automatically sort Tailwind CSS classes based on Tailwind's official default order.

## Alternatives Considered

### Manual Sorting

Developers manually sort Tailwind classes during development.

Relies on adherence to documented guidelines.

### Custom ESLint Rules

Use ESLint plugins or custom rules to enforce Tailwind class sorting.

Highlights sorting issues but does not fix them automatically.

### Prettier-Plugin-TailwindCSS

Automatically sorts classes during Prettier formatting.

Ensures consistent order across the codebase without manual effort.

## Pros of prettier-plugin-tailwindcss

### Consistency

Ensures all developers follow the same class order without subjective interpretation.

### Automation

Reduces cognitive load by automating repetitive tasks.

Integrates seamlessly with Prettier, a widely used formatter.

### Improved Readability

Aligns class order with Tailwind’s optimized defaults for readability and maintainability.

### Low Effort to Adopt

Minimal configuration required.

Works out of the box for most projects already using Prettier.

### Ecosystem Support

Maintained by Tailwind Labs, ensuring alignment with Tailwind’s updates.

Active community support and reliable updates.

## Cons of prettier-plugin-tailwindcss

### Potential Conflicts

May conflict with other Prettier plugins if not configured properly.

### Learning Curve

Developers unfamiliar with the default Tailwind class order might need time to adjust.

### Performance

Adds slight overhead to the formatting process, particularly in large files.

### Dependency Management

Adds a new dependency to the project, increasing maintenance burden.

### Edge Cases

Rare scenarios where the automated order might not align with intended visual behavior.

## Implementation Plan

Add prettier-plugin-tailwindcss to the project dependencies:

npm install --save-dev prettier-plugin-tailwindcss

Configure Prettier to use the plugin by adding or updating the `.prettierrc` file:

```json
{
"plugins": ["prettier-plugin-tailwindcss"]
}
```

Test the integration by running Prettier on existing files with Tailwind classes and reviewing changes for alignment with project needs.

Update documentation to include the use of prettier-plugin-tailwindcss as a standard development practice.

Ensure CI pipelines include a Prettier check to enforce consistency.

## Consequences

Positive Outcomes

Consistent Tailwind class ordering improves readability and reduces code review friction.

Automated sorting minimizes manual effort and human error.

## Potential Risks

Developers unfamiliar with the plugin may encounter unexpected formatting changes. This can be mitigated through onboarding and clear documentation.

Additional dependency management may increase complexity slightly, but the benefits outweigh this drawback.

## Links

[Prettier Plugin TailwindCSS Repository](https://github.com/tailwindlabs/prettier-plugin-tailwindcss)

[Tailwind CSS Class Sorting Guide](https://tailwindcss.com/blog/automatic-class-sorting-with-prettier#how-classes-are-sorted)
