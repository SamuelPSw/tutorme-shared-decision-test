# TutorMe frontend

Run these commands from the `frontend` directory with Node.js and npm installed.

```sh
npm ci
npm run dev
```

Open http://localhost:5173. Start the Spring Boot backend on port 8080 with CORS enabled for http://localhost:5173. The frontend connects to http://localhost:8080/api.

Use `npm run build` to produce the frontend build in `dist/`. Use `npm run preview` to serve the build locally on port 5173 after stopping the dev server.
