# ── Stage 1 : Build Angular ──────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# Génère environment.ts depuis les build args
ARG SPOTIFY_CLIENT_ID
ARG SPOTIFY_CLIENT_SECRET
RUN printf "export const environment = {\n\
  production: true,\n\
  apiUrl: '',\n\
  spotify: {\n\
    clientId: '${SPOTIFY_CLIENT_ID}',\n\
    clientSecret: '${SPOTIFY_CLIENT_SECRET}'\n\
  }\n\
};\n" > src/environments/environment.ts

RUN npm run build -- --configuration production

# ── Stage 2 : Serve avec Nginx ───────────────────────────────
FROM nginx:alpine

COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

# Config Nginx : proxy /api/ vers le backend + routing SPA
RUN printf 'server {\n\
  listen 80;\n\
  root /usr/share/nginx/html;\n\
  index index.html;\n\
  client_max_body_size 100M;\n\
\n\
  location /api/ {\n\
    proxy_pass http://sonora-backend:8080;\n\
    proxy_set_header Host $host;\n\
    proxy_set_header X-Real-IP $remote_addr;\n\
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n\
    proxy_set_header Authorization $http_authorization;\n\
    proxy_pass_header Authorization;\n\
    proxy_request_buffering off;\n\
    proxy_buffering off;\n\
  }\n\
\n\
  location / {\n\
    try_files $uri $uri/ /index.html;\n\
  }\n\
}\n' > /etc/nginx/conf.d/default.conf

EXPOSE 80