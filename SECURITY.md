# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.0.x   | Yes       |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it responsibly:

1. **Do NOT** open a public GitHub issue.
2. Email the maintainers or use [GitHub Security Advisories](https://github.com/ces107/hopl/security/advisories/new).
3. Include a description of the vulnerability, steps to reproduce, and potential impact.
4. Allow reasonable time for a fix before public disclosure.

We will acknowledge your report within 48 hours and provide a timeline for the fix.

## Security Measures

- JWT-based authentication with configurable expiration.
- Password hashing with BCrypt.
- CORS configuration for cross-origin requests.
- SQL injection prevention via JPA/Hibernate parameterized queries.
- Input validation on all API endpoints.
- Environment-based secret management (never committed to source).
