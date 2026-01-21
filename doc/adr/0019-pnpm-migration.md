# 19. Migrate Frontend Package Manager from Yarn to PNPM

Date: 2026-01-20

## Status

Accepted

## Context

After recent supply chain attacks in the NPM ecosystem, we identified several opportunities to improve our frontend's supply chain security. The existing setup used Yarn 4.x as the package manager with dependencies specified using caret (`^`) version ranges, which allowed automatic minor and patch updates.

Supply chain attacks often exploit the window between when a malicious package is published and when it is discovered and removed from the registry. Additionally, version range specifications can inadvertently pull in compromised versions of packages.

Other projects at DigitalService are moving to PNPM, and it is likely to become the default package manager across the organization.

## Decision Drivers

- **Supply Chain Security:** PNPM offers `minimumReleaseAge` setting that blocks installation of packages published within a configurable time window (we use 3 days, equal to our Dependebot configuration), giving time for malicious packages to be detected and removed.
- **Exact Version Pinning:** Enforcing exact versions in `package.json` prevents unintentional upgrades that could introduce vulnerabilities.
- **Organizational Alignment:** PNPM is being adopted across DigitalService projects, including other projects in NeuRIS.
- **Better Security Defaults:** PNPM has [better security defaults](https://pnpm.io/supply-chain-security) compared to Yarn and NPM.

## Decision

We will migrate the frontend package manager from Yarn to PNPM with the following security configurations:

- Switch from Yarn 4.x to PNPM 10.x
- All dependencies in `package.json` are pinned to exact versions
- `minimumReleaseAge: 4320` (3 days in minutes) configured in `pnpm-workspace.yaml`

## Consequences

- Reduced risk from supply chain attacks through minimum release age enforcement
- Predictable builds with exact version pinning
- Alignment with DigitalService organizational direction
- Dependency updates require explicit version changes in `package.json`
- Very new package releases cannot be installed immediately (must wait 3 days)
