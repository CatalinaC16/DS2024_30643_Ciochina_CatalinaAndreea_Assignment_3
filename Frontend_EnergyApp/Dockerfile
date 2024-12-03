FROM node:18.18.2 AS builder

WORKDIR /frontend-energy-app
COPY package*.json ./
RUN npm install --f
COPY . .
RUN npm run build

CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
