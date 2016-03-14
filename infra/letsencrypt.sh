#!/bin/bash

LETSENCRYPT=/opt/letsencrypt/letsencrypt-auto
DOMAINS=("rss.barmic.eu" "cotize.barmic.eu" "cotize2.barmic.eu")
EMAIL="michel.barret@gmail.com"

SCRIPT=$(realpath "$0")
DIR=$(dirname "$SCRIPT")

CERTS="$DIR/cert"
WWW="/etc/nginx/letsencrypt"

LE_CERTS="$CERTS/letsencrypt"

mkdir -p "$LE_CERTS"

DOMAINS_ARG=$(printf -- " -d %s" "${DOMAINS[@]}")

${LETSENCRYPT} certonly -t --agree-tos --renew-by-default --mail "$EMAIL" --webroot -w "$WWW" $DOMAINS_ARG --logs-dir "$LE_CERTS" --work-dir "$LE_CERTS" --config-dir "$LE_CERTS"