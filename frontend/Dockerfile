# Build aşaması
FROM node:18-alpine AS builder

# Çalışma dizinini ayarla
WORKDIR /app   



# Bağımlılıkları kopyala ve kur
COPY package.json package-lock.json ./
RUN npm ci

# Proje dosyalarını kopyala ve build al
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app

COPY --from=builder /app/dist ./dist

RUN npm install -g serve

# 80 portunu aç
EXPOSE 10000

CMD ["serve", "-s", "dist", "-l", "10000"]