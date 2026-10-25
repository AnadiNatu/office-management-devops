#!/bin/sh
set -eu

: "${DOMAIN:=localhost}"

mkdir -p /etc/nginx/conf.d

rm -f /etc/nginx/conf.d/*.conf

if [ -f "/etc/letsencrypt/live/${DOMAIN}/fullchain.pem" ] &&
   [ -f "/etc/letsencrypt/live/${DOMAIN}/privkey.pem" ]; then

    envsubst '${DOMAIN}' \
      < /etc/nginx/templates/nginx-http.conf.template \
      > /etc/nginx/conf.d/http.conf

    envsubst '${DOMAIN}' \
      < /etc/nginx/templates/nginx-ssl.conf.template \
      > /etc/nginx/conf.d/ssl.conf

    # Replace the HTTP application server with a redirect server.
    cat > /etc/nginx/conf.d/http.conf <<EOF
server {
    listen 80;
    listen [::]:80;

    server_name ${DOMAIN};

    location ^~ /.well-known/acme-challenge/ {
        root /var/www/certbot;
        default_type text/plain;
        try_files \$uri =404;
    }

    location / {
        return 301 https://\$host\$request_uri;
    }
}
EOF

else

    envsubst '${DOMAIN}' \
      < /etc/nginx/templates/nginx-http.conf.template \
      > /etc/nginx/conf.d/http.conf

fi

exec nginx -g 'daemon off;'
