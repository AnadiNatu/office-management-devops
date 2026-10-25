FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG VITE_API_BASE_URL=
ARG VITE_WS_URL=
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_WS_URL=${VITE_WS_URL}

RUN npm run build

FROM nginx:1.29-alpine AS runtime

COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY nginx/nginx-http.conf /etc/nginx/templates/nginx-http.conf.template
COPY nginx/nginx-ssl.conf /etc/nginx/templates/nginx-ssl.conf.template
COPY docker-entrypoint.sh /docker-entrypoint.sh

COPY --from=build /app/dist /usr/share/nginx/html

RUN chmod +x /docker-entrypoint.sh \
    && mkdir -p /var/www/certbot

EXPOSE 80 443

ENTRYPOINT ["/docker-entrypoint.sh"]
