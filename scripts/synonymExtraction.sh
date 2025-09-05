xmlstarlet sel -t -m "//syn-entry" -v "normalize-space(baseword)" -m "values" -o ", " -v "normalize-space(synword)" -b -n ThesaurusGolem.xml > stakeholder_synonyms.txt
