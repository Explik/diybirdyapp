FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
COPY ./src ./src
COPY ./public ./public
COPY ./angular.json ./angular.json
COPY ./tsconfig.json ./tsconfig.json
COPY ./tsconfig.app.json ./tsconfig.app.json

RUN npm install -g @angular/cli 
RUN npm install

EXPOSE 4200

CMD ["npm", "start"]
