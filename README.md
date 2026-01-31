# Lexera Digital Wallet (Backend) 🛡️

[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc-sa/4.0/)

Lexera Digital Wallet is a high-security, standard-compliant digital wallet for official electronic documents (IDs, Diplomas, etc.). It leverages a "Triangle of Trust" architecture to allow for instant, tamper-proof verification of credentials in both online and offline scenarios.

## 🚀 Features & Architecture

### 1. Identity & Privacy (Phase 1)
- **Root Identity Hashing:** Implemented a SHA-256 salted hashing mechanism to generate unique "Digital Fingerprints" for users, avoiding the storage of raw PII.
- **Application-Level Encryption (ALE):** Using **Jasypt** and JPA Attribute Converters to encrypt PII (Personally Identifiable Information) before it reaches the database.
- **Zero-Trust Storage:** The database only stores encrypted strings; decryption keys are managed strictly at the application level.

### 2. Cryptographic Trust (Phase 2)
- **RSA Signing:** Documents are issued as **JSON Web Tokens (JWT)** signed with an RSA-256 private key.
- **Public Key Discovery:** Implemented a standard `/.well-known/jwks.json` endpoint for Verifier apps to fetch public keys for offline verification.
- **Modern Time API:** Precise document validity management using `java.time.Instant`.

### 3. Versatile Issuance (Phase 3)
- **Strategy Pattern:** A pluggable `DocumentProvider` interface allows for easy extension. Adding a new document type (e.g., "Health Certificate") requires zero changes to the core signing logic.

## 🛠️ Tech Stack
- **Framework:** Spring Boot 3.x (Java 21+)
- **Security:** Jasypt
- **Cryptography:** JJWT, Nimbus JOSE+JWT
- **Database:** PostgreSQL

---

## 🚦 Getting Started

### Prerequisites
- JDK 21 or higher
- Maven 3.6+

### Configuration
Update `src/main/resources/application.yml`.

## 🤝 Contributing
This is a personal project, but I welcome feedback and suggestions!

1. Fork the Project 
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📜 License
This project is licensed under the terms of the **CC BY-NC-SA 4.0** license.
