
---

### 📌 Project Description: Username Availability Validator (Spring Boot + Bloom Filter + Redis)

This project is a **Spring Boot service** that validates whether a given username is available or already taken. It is designed for **high-performance username checks** by combining a **Bloom filter**, **Redis cache**, and a **SQLite database**.

**How it works:**

1. **Bloom Filter (Fast Probabilistic Check)**

    * A Redis-backed Bloom filter provides an ultra-fast, memory-efficient way to quickly rule out most unavailable usernames.
    * If the Bloom filter says a username doesn’t exist → it’s *probably* available.
    * If it says a username might exist → further checks are performed.

2. **Redis Cache (Definitive Check for Known Usernames)**

    * Redis acts as a cache for username availability.
    * If Redis already knows a username is taken → the system immediately returns "unavailable".
    * If Redis says "available" → the database is checked for confirmation.

3. **SQLite Database (Source of Truth)**

    * The database stores all registered usernames.
    * On cache misses, the database is queried.
    * Results are written back into Redis for faster future lookups.

**Workflow:**

* Check Bloom filter → quick rejection if username is definitely not available.
* Query Redis cache → trust if the username is "taken", verify if the username is "available".
* Query database if necessary → update Redis cache accordingly.

**Benefits:**

* 🚀 **High speed**: Bloom filter + Redis drastically reduce DB queries.
* 💾 **Lightweight storage**: SQLite is used as a simple, reliable backing store.
* ⚡ **Scalable**: Works seamlessly with Redis (local or cloud-hosted like Upstash).
* 🛡️ **Accurate**: DB verification ensures correctness, even in edge cases.

---
