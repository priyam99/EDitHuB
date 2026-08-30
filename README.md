# 🎬 EditHub — GitHub for Video Editing

An open collaboration platform where creators upload raw footage and editing requirements, while video editors contribute edits, collaborate through versions, build portfolios, and earn recognition.

## 🏗️ Project Architecture

```
edithub/
├── frontend/          # Next.js 16 + TypeScript + Tailwind CSS
├── backend/           # Spring Boot 4 + Java 21 + PostgreSQL + Redis
├── video-worker/      # FFmpeg video processing service (Placeholder)
├── infrastructure/    # Docker Compose, env configs
└── docs/              # Product requirements & technical specs
```

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 20+
- Docker & Docker Compose

### 1. Local Infrastructure (PostgreSQL, Redis, MinIO)

```bash
cd infrastructure/docker
cp .env.example .env
docker compose up -d
```

### 2. Backend (Spring Boot API)

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

API Health check: `http://localhost:8080/api/v1/health`

### 3. Frontend (Next.js Web App)

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

App URL: `http://localhost:3000`

## 📖 Documentation

Detailed product specs, domain model, API contracts, security guidelines, and development roadmap are available in [`docs/product-requirements.md`](docs/product-requirements.md).

## 📝 License

MIT
