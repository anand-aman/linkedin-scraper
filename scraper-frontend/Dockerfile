FROM ghcr.io/cirruslabs/flutter:stable AS build
USER root
WORKDIR /workspace/app

COPY scraper-frontend/ /workspace/app/

RUN flutter pub get
RUN flutter build web --release --dart-define=API_BASE_URL=

FROM nginx:1.27-alpine

COPY scraper-frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/app/build/web /usr/share/nginx/html

EXPOSE 80
