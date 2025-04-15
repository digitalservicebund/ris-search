# XSLT for legislation in LegalDocML.de format

The files in this directory are copied over ("vendored") from the [KOSIT styling initiative](https://projekte.kosit.org/ldml_de/styling) repository.

## Copying
To update the code, you may run a script:

```bash
repo=~/kosit-ldml-styling
cp "$repo/src/ris-portal.xsl" .
cp "$repo/src/include/hilfsfunktionen.xsl" "$repo/src/include/inhalt.xsl" ./include
```
