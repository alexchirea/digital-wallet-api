# Lexera Digital Wallet 🛡️

[![License: CC BY-NC-SA 4.0](https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc-sa/4.0/)

Lexera Digital Wallet is a high-performance backend infrastructure designed for the secure issuance, management, and verification of **Verifiable Credentials**. Built with Spring Boot 3, it implements modern cryptographic standards to bridge the gap between physical identity and digital security.

## 🏗️ Architecture & Design Patterns

The system is built on a "Privacy-by-Design" philosophy. It ensures that PII (Personally Identifiable Information) is transformed into deterministic hashes, allowing for identity verification without exposing sensitive user data.



### Core Modules:
* **Identity Layer:** Utilizes deterministic SHA-256 hashing with server-side salting to create unique `RootIdentityHashes`.
* **Issuance Engine:** Implements a **Strategy Pattern** via `DocumentProvider` interfaces, allowing the system to support multiple credential types (e.g., Diplomas, ID Cards, Certifications) dynamically.
* **Crypto Service:** Handles asymmetric RSA-256 signing to generate non-repudiable JWT-based credentials.
* **Status Registry:** A real-time revocation system that provides signed validity proofs for third-party verifiers.

---

## 🚀 Key Features

* **Verifiable Credentials:** Issues standard-compliant JWTs containing claims, identity hashes, and unique JTIs.
* **Revocation Mechanism:** Full lifecycle management allows for immediate credential revocation with audit-trail reasons.
* **Standardized Error Handling:** A robust global exception handler providing clear, typed error responses (`ERR_UNSUPPORTED_TYPE`, `ERR_IDENTITY_NOT_FOUND`).
* **Interactive Documentation:** Automated OpenAPI 3.0 (Swagger) integration with logical grouping and pre-filled examples.

---

## 🛠️ Tech Stack

* **Java 21** & **Spring Boot 3.2+**
* **Spring Data JPA:** For persistent identity and status mapping.
* **JJWT (Java JWT):** For cryptographic operations and token construction.
* **SpringDoc OpenAPI:** For professional-grade API documentation.
* **Lombok:** To maintain a clean, boilerplate-free codebase.
* **Maven:** For dependency management and automated build cycles.

---

## 📖 API Reference & Documentation

Lexera features a fully interactive Swagger UI, allowing developers to test the issuance flow directly from the browser.

| Endpoint                        | Method | Description |
|:--------------------------------| :--- | :--- |
| `/api/v1/users`                 | `POST` | Register a user and generate a Root Identity Hash. |
| `/api/v1/credentials/issue`     | `POST` | Generate and sign a new Verifiable Credential. |
| `/api/v1/credentials/{id}/status` | `GET` | Retrieve a signed proof of a credential's validity. |
| `/api/v1/credentials/{id}/revoke` | `POST` | Revoke a credential and update the registry. |

**Access the UI at:** `http://localhost:8080/swagger-ui/index.html`

---

## 🚦 Getting Started

### Prerequisites
* JDK 21
* Maven 3.9+
* PostgreSQL (or H2 for local testing)

### Installation
1. Clone the repository:
```bash
git clone https://github.com/alexchirea/digital-wallet-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

## 🔒 Security Note
This project demonstrates identity issuance concepts. In a production environment, the `SigningService` should be integrated with a Hardware Security Module (HSM) or a secure Vault for private key management.

## 🤝 Contributing
This is a personal project, but I welcome feedback and suggestions!

1. Fork the Project 
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📜 License
This project is licensed under the terms of the **CC BY-NC-SA 4.0** license.
