# Names containing commas are Maven licence names for the backend. npm packages use SPDX IDs, and
# the checker warns on commas. Ignoring them for the license check.
cat < ../allowed-licenses.json | jq -r '.allowedLicenses | map(.moduleLicense | select(contains(",") | not)) | join(";")'
