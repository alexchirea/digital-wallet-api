# Contributing to Lexera Digital Wallet 🤝

Thank you for your interest in contributing! As a personal project, it aims to maintain high standards of code quality and security.

## How to Contribute

### 1. Reporting Bugs 🐛
- Use the GitHub Issues tab.
- Provide a clear description of the bug and steps to reproduce it.
- Include your environment details (Java version, OS).

### 2. Suggesting Enhancements 💡
- Open an issue to discuss the proposed change before starting work.
- Focus on security-first features (e.g., adding Biometric Auth logic or new Encryption algorithms).

### 3. Pull Requests (PRs) 🚀
1. Fork the repository.
2. Create a new branch: `git checkout -b feature/your-feature-name`.
3. Follow the **Coding Standards** below.
4. Ensure all tests pass (`mvn test`).
5. Submit your PR with a detailed description of the changes.

## Coding Standards 📏

- **Naming:** Follow the `ro.lexera.wallet` package structure.
- **Security:** Never commit plain-text PII. Always use the `JasyptAttributeConverter` for sensitive entity fields.
- **Clean Code:** Use `java.time.Instant` for timestamps. Avoid raw milliseconds.
- **Documentation:** Every new `DocumentProvider` must be documented in the README.

## Security Policy 🛡️
If you find a security vulnerability, please do not open a public issue. Instead, contact the maintainer.
