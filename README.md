<div align="center">

# HOPL

**Website Compliance Scanner & AI-Powered Legal Document Generator**

[![CI](https://github.com/ces107/hopl/actions/workflows/ci.yml/badge.svg)](https://github.com/ces107/hopl/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB.svg)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](docker-compose.yml)

Scan any website for legal compliance issues and generate AI-powered legal documents tailored to your business needs.

[Getting Started](#getting-started) &bull; [Features](#features) &bull; [Architecture](#architecture) &bull; [Contributing](CONTRIBUTING.md)

</div>

---

## Features

- **Compliance Scanner** &mdash; Analyze any website for privacy policy, cookie consent, terms of service, GDPR, and accessibility issues. Generates a compliance score (0-100) with actionable recommendations.
- **AI Document Generation** &mdash; Generate 15 types of legal and business documents using OpenAI, customized with your company details and scan results.
- **Credit-Based System** &mdash; Free scanning with a pay-per-document model. Stripe integration for secure payments.
- **PDF Export** &mdash; Download generated documents as professionally formatted PDFs.
- **JWT Authentication** &mdash; Secure user registration and login with token-based auth.
- **Docker Ready** &mdash; One-command deployment with Docker Compose (PostgreSQL + app).

## Document Types

| Legal | Business | Technical |
|-------|----------|-----------|
| Privacy Policy | Business Plan | SaaS License |
| Terms of Service | Proposal | DMCA Notice |
| Cookie Policy | Job Description | Acceptable Use Policy |
| Disclaimer | Consulting Agreement | SOP |
| Refund Policy | Freelance Agreement | NDA |

## Getting Started

### Prerequisites

- **Java 17+** (for local development)
- **Node.js 18+** (for frontend development)
- **Docker & Docker Compose** (for containerized deployment)

### Quick Start with Docker

```bash
# Clone the repository
git clone https://github.com/ces107/hopl.git
cd hopl

# Start PostgreSQL + App
docker compose up --build -d

# App available at http://localhost:8080
```

### Local Development

```bash
# Backend
./mvnw spring-boot:run

# Frontend (in a separate terminal)
cd frontend
npm install
npm run dev
```

### Environment Variables

Copy `.env.example` to `.env` and configure:

| Variable | Description | Required |
|----------|-------------|----------|
| `OPENAI_API_KEY` | OpenAI API key for document generation | Yes (for AI features) |
| `STRIPE_SECRET_KEY` | Stripe secret key for payments | Yes (for payments) |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret | Yes (for payments) |
| `JWT_SECRET` | Secret key for JWT token signing | Yes (auto-generated in Docker) |

## Architecture

```
hopl/
├── frontend/                 # React 18 + Vite + Tailwind CSS
│   ├── src/
│   │   ├── pages/            # 10 page components
│   │   ├── components/       # Reusable UI components
│   │   ├── context/          # Auth context provider
│   │   └── api/              # API client
│   └── package.json
├── src/main/java/com/hopl/
│   ├── controller/           # REST API endpoints
│   ├── service/              # Business logic
│   ├── model/                # JPA entities
│   ├── repository/           # Spring Data repositories
│   ├── security/             # JWT auth filter & provider
│   ├── dto/                  # Request/Response DTOs
│   └── config/               # Spring configuration
├── src/main/resources/
│   ├── db/migration/         # Flyway migrations (H2 + PostgreSQL)
│   ├── prompts/              # AI prompt templates (15 document types)
│   └── application.yml       # Spring configuration
├── Dockerfile                # Multi-stage build
└── docker-compose.yml        # PostgreSQL + App stack
```

### Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, TypeScript, Vite, Tailwind CSS |
| Backend | Spring Boot 3.2, Java 17 |
| Database | PostgreSQL 16 (prod) / H2 (dev) |
| Migrations | Flyway |
| Auth | JWT (jjwt) |
| AI | OpenAI GPT API |
| Payments | Stripe |
| PDF | OpenPDF |
| Container | Docker multi-stage build |

### API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/scan` | No | Scan a website for compliance |
| `GET` | `/api/documents/types` | No | List available document types |
| `POST` | `/api/auth/register` | No | Register a new user |
| `POST` | `/api/auth/login` | No | Login and get JWT token |
| `POST` | `/api/documents/generate` | Yes | Generate an AI document |
| `GET` | `/api/documents/{id}` | Yes | Get a generated document |
| `GET` | `/api/documents/{id}/pdf` | Yes | Download document as PDF |
| `GET` | `/api/user/profile` | Yes | Get user profile & credits |
| `POST` | `/api/payments/checkout` | Yes | Create Stripe checkout session |

## Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md) before submitting a PR.

## Security

If you discover a security vulnerability, please follow our [Security Policy](SECURITY.md) for responsible disclosure.

## License

This project is licensed under the MIT License &mdash; see the [LICENSE](LICENSE) file for details.

---

<div align="center">
Built with Java, React, and AI
</div>
