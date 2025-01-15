FROM node:22-alpine

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

COPY . .

RUN npm run build

EXPOSE 8100

RUN echo "Frontend build complete"

CMD ["npm", "start"]
