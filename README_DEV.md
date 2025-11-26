database:
docker run --name xiaozhi-db-develop -e MYSQL_ROOT_PASSWORD=123456dev -p 9050:3306 -e MYSQL_DATABASE=xiaozhi_esp32_server -e MYSQL_INITDB_ARGS="--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci" -e TZ=Asia/Shanghai -d mysql:latest
docker run --name xiaozhi-redis-develop -d -p 9051:6379 redis