# 13. Replace Talisman with GitHub Secrets Check

Date: 2025-05-07

## Status

Decided

## Context

The repository previously used **Talisman** as a tool to check for secrets before pushing code. While Talisman served its purpose, it has been a source of frustration for the team due to the following reasons:

- Developers needed to adjust patterns in a configuration file and push it to the repository to ignore certain files or patterns.
- Talisman was attached to **lefthook**, adding complexity to the development workflow.
- The overall experience was not seamless for developers.

### About Github Secrets Push Prevention
GitHub Secrets Push Prevention is a built-in feature (free for public repos and paid for private ones), that helps prevent sensitive information, such as API keys and passwords, from being pushed to a GitHub repository. It works by scanning commits for known secret patterns, and if something is found, it sends it for verification to Github's partners and if the secret is real, it blocks the push that contain sensitive data without the need for a commit hook or any other tool that can be bypassed on the user side. This feature is integrated into GitHub's workflow, making it easier for developers to avoid accidentally exposing secrets and harder for them to override without needing permissions to do so.
The following diagram illustrates how GitHub Secrets Push Prevention works:

![Github Push Protection Process](https://docs.github.com/assets/cb-89330/mw-1440/images/help/security/secret-scanning-flow.webp)

Now that the repository is public, we can leverage **GitHub's built-in secrets push prevention** feature, which is free and provides an out-of-the-box solution to prevent secrets from being pushed to the repository.

## Problem Statement

We need a mechanism to:

- Prevent secrets from being pushed to the repository.
- Simplify the developer experience by reducing the need for additional tools or configuration files.
- Ensure that exceptions or overrides are handled securely.

## Decision

We will replace **Talisman** with **GitHub Secrets Push Prevention**.
Summary of the discussion around the decision in Slack: [here](https://digitalservicebund.slack.com/archives/C07EVJBF5K8/p1747900816446259)

## Key Changes

1. **Enable GitHub Secrets Push Prevention**:
   - This feature is enabled in the repository settings and prevents secrets from being pushed to the repository.

2. **Define Bypass Patterns**:
   - Specific patterns to bypass the secrets check can be defined directly in the GitHub repository settings, eliminating the need for additional configuration files in the repository.

3. **Override Permissions**:
   - To handle cases where pushing without restrictions is necessary, we have:
     - Made all members of the **Portal Team** maintainers.
     - Restricted pushing without checks to **admin accounts only**.

### Advantages
- **Improved Developer Experience:** No need to maintain additional configuration files or update lefthook: With GitHub Secrets Push Prevention, we no longer need to manage a separate tool like Talisman or adjust regex patterns in configuration files. Developers can rely on GitHub's native tools, which integrate seamlessly into their workflow.
- **Seamless integration:** GitHub's secrets push prevention works directly with the repository, removing the need for additional setup or tools. This creates a smoother experience for developers.
- **Reducing false positives:** GitHub's built-in validation against known third-party partners ensures that only real secrets are flagged, reducing the chance of false positives. This is achieved by checking secrets against a list of [partners](https://docs.github.com/en/code-security/secret-scanning/introduction/supported-secret-scanning-patterns#supported-secrets), such as AWS, which provides an extra layer of security. 
  - **Example**: GitHub's mechanism works by checking the format of a secret, and if it matches a pattern like AWS_SECRET_ACCESS_KEY, it then verifies whether it's real by calling AWS's API. If the secret is fake, the push proceeds, but an alert is still sent.  This approach avoids the unnecessary noise that comes with flagging fake tokens or dummy values used for testing. As Carl mentioned, GitHub's system is smarter in avoiding unnecessary alerts, providing a better overall experience.
- **Enhanced Security with Minimal Maintenance:** Admin-only override: By restricting the ability to push without checks to only admin accounts, we ensure that only trusted team members can bypass the prevention mechanism. This provides stricter control over the repository and its contents.
- **Custom regex patterns:** While GitHub's predefined patterns handle a wide range of common secrets (like AWS keys, Google API keys), we can still extend it with custom regex patterns for additional coverage. As Haytham mentioned, this allows us to port over Talisman's patterns to GitHub and get the benefits of both worlds.

### Downsides
- **Customization Limitations:** 
  - **GitHub's predefined patterns:** While GitHub’s push protection is designed to prevent real secret leaks, it works only with predefined patterns for high-risk services like AWS and Google Cloud. If a secret does not match one of these patterns (e.g., a password for a custom service), it won’t trigger a violation unless we manually define a custom pattern.
  - **Mitigation Strategy:** To address this, we can add custom regex patterns in the repository settings. This allows us to cover additional secret types that are not included in GitHub's predefined patterns. However, this may lead to a higher number of false positives, similar to the issues faced with Talisman.
- **Team and Role Management:**
  - Defining specific roles for bypassing the checks (i.e., making team members maintainers and restricting bypasses to admin accounts) requires additional attention and careful management. Failure to do so could lead to accidental leaks or the introduction of sensitive data without proper oversight.
  - **Mitigation Strategy:** We will ensure that only trusted team members are given admin access and that they are aware of the implications of bypassing the secrets checks. Regular audits of team roles and permissions will help maintain security.

### Consequences
1. Simplifies Developer Workflow: Developers no longer need to manage external tools like Talisman. The GitHub secrets protection integrates directly into the GitHub flow, creating a more streamlined and less error-prone process.
2. Reduced Operational Overhead: Eliminates the need for managing Talisman configurations, making it easier for developers to focus on writing code without worrying about secrets detection configuration.
3. Reduce False Positives: While GitHub's system reduces noise, the potential for false positives exists when custom regex patterns are used. This was a key downside with Talisman, but it was a trade-off between greater security and the complexity of managing regex patterns.
4. Limiting the ability to bypass secrets protection: Unlike in talisman, where you can define exceptions in the talisman configuration, this wouldn't be the case with Github Secrets since the roles and permissions control who can do what in terms of bypassing the secrets.

### References
- [Github supported partners for secrets check](https://docs.github.com/en/code-security/secret-scanning/introduction/supported-secret-scanning-patterns#supported-secrets)
- [Github Secret Scanning Process](https://docs.github.com/en/code-security/secret-scanning/secret-scanning-partnership-program/secret-scanning-partner-program#the-secret-scanning-process)