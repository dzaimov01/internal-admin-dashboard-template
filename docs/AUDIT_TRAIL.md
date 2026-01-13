# Audit Trail

## What is Logged
- Actor email
- Action (CREATE/UPDATE/DELETE/IMPORT)
- Entity type + ID
- Change summary (human-readable)
- Timestamp
- IP address + user agent

## Why It Matters
- Traceability for internal actions
- Incident response and accountability
- Operational compliance requirements

## Extension Ideas
- Store JSON diffs
- Mask sensitive fields
- Stream to SIEM or log pipeline
