for directory in _shared internal public
do
  cd "$directory" || exit 1
  npm run audit:licences
  cd ..
done

echo \"module name\",\"license\",\"repository\" > licence-report.csv
awk '(FNR > 1)' ./*/licence-report.csv | sort | uniq >> licence-report.csv
