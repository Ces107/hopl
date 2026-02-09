# Contributing to HOPL

Thank you for your interest in contributing to HOPL! This guide will help you get started.

## How to Contribute

### Reporting Bugs

1. Check existing [issues](https://github.com/ces107/hopl/issues) to avoid duplicates.
2. Use the **Bug Report** issue template.
3. Include steps to reproduce, expected behavior, and environment details.

### Suggesting Features

1. Open a **Feature Request** issue.
2. Describe the problem you're trying to solve, not just the solution.
3. Consider if it fits the project's scope.

### Submitting Code

1. Fork the repository.
2. Create a feature branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes following our coding standards.
4. Test your changes locally.
5. Commit with clear, descriptive messages.
6. Push and open a Pull Request against `main`.

## Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/hopl.git
cd hopl

# Backend (Java 17 required)
./mvnw spring-boot:run

# Frontend (Node 18+ required)
cd frontend
npm install
npm run dev
```

## Coding Standards

### Java (Backend)

- Follow existing project patterns and conventions.
- Use Java 8+ features: lambdas, Optional, Streams.
- Public methods require JavaDoc.
- Minimal comments — code should be self-documenting.

### TypeScript (Frontend)

- Follow existing component patterns.
- Use TypeScript types for all props and state.
- Keep components focused and small.

## Pull Request Guidelines

- Keep PRs focused on a single change.
- Reference related issues (`Closes #123`).
- Ensure CI checks pass before requesting review.
- Update documentation if your change affects public APIs.

## Code of Conduct

By participating, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).
