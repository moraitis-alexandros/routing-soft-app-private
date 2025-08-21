#!/bin/sh
set -e

# Load DB password from Docker secret
if [ -n "$MYSQL_PASSWORD_FILE" ] && [ -f "$MYSQL_PASSWORD_FILE" ]; then
  export MYSQL_PASSWORD="$(cat $MYSQL_PASSWORD_FILE)"
fi

# Load JWT secret from Docker secret
if [ -n "$JWT_SECRET_FILE" ] && [ -f "$JWT_SECRET_FILE" ]; then
  export JWT_SECRET="$(cat $JWT_SECRET_FILE)"
fi

exec "$@"
