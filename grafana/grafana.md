Create a new folder
```bash
curl -X POST 'https://grafana.tech.digitalservice.dev/api/folders' --cookie "grafana_session=YOUR_SESSION" -H "Content-Type: application/json" -d '{"title": "NeuRIS/Portal"}'
```
Update the permission on that folder
```bash
curl -X POST 'https://grafana.tech.digitalservice.dev/api/folders/ffpl1vm70vpq8b//permissions' \
  -H "Content-Type: application/json" \
  --cookie "grafana_session=YOUR_SESSION" \
  -d '{
    "items": [
      {
        "role": "Viewer",
        "permission": 1
      },
      {
        "role": "Editor",
        "permission": 2
      }
    ]
  }'
```
Added a new contact point here https://github.com/digitalservicebund/platform/blob/main/terraform/modules/observability/contact_points.tf
Added a new notification policy here https://github.com/digitalservicebund/platform/blob/main/terraform/modules/observability/notification_policy.tf

Add a new alert
```bash
curl -X POST 'https://grafana.tech.digitalservice.dev/api/v1/provisioning/alert-rules' --cookie "grafana_session=YOUR_SESSION" -H "Content-Type: application/json" -H "X-Disable-Provenance: true" -d @YOUR_ALERT.json
```
