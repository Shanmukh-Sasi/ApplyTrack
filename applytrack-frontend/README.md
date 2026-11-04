# ApplyTrack frontend

React + Vite interface for the ApplyTrack Spring Boot API.

## Run locally

1. Start the backend on `http://localhost:8081`.
2. Open a terminal in `applytrack-frontend`.
3. Run `npm install` and then `npm run dev`.
4. Open the local URL printed by Vite (usually `http://localhost:5173`).

The Vite development server forwards `/auth`, `/user`, `/application`, and `/interview` requests to the backend. Authentication uses the backend's HTTP session cookie and CSRF token; this frontend does not use JWT.

For a separate frontend deployment, set `VITE_API_URL` to the backend base URL and configure the backend host to allow credentialed requests from the frontend origin. The frontend and backend must use HTTPS in production so session cookies remain protected.
