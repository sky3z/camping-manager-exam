# Camping Manager

Gestionale per campeggio sviluppato con Spring Boot e PostgreSQL.  
Permette la gestione di alloggi, soggiorni, noleggio bici e pagamenti online.

---

## Tecnologie

- Java 21
- Spring Boot 3.5
- PostgreSQL
- Spring Security + JWT
- Stripe (pagamenti)
- Cloudinary (immagini profilo)
- OpenWeatherMap (meteo)

---

## Funzionalità principali

- **Autenticazione JWT** con tre ruoli: Admin, Staff, Ospite
- **Gestione alloggi**: 10 chalet e 10 piazzole con verifica disponibilità per date
- **Check-in / Check-out**: lo staff crea l'ospite al check-in; l'account scade automaticamente al check-out
- **Noleggio bici**: prenotazione con pagamento Stripe integrato
- **Pagamenti**: flusso Stripe Checkout con conferma via webhook
- **Meteo**: integrazione OpenWeatherMap per la città del campeggio
- **Immagini profilo**: upload su Cloudinary
- **Statistiche admin**: occupazione alloggi e stato noleggi in tempo reale

---

## Prerequisiti

- Java 21
- Maven
- PostgreSQL (database: `camping_manager_exam`)
- Account Stripe (modalità test)
- Account Cloudinary
- API Key OpenWeatherMap

---

## Configurazione

Creare il file `src/main/resources/application-local.properties` (non committato) con le proprie chiavi:

```properties
stripe.secret.key=sk_test_...
stripe.webhook.secret=whsec_...
openweather.api.key=...
cloudinary.cloud-name=...
cloudinary.api-key=...
cloudinary.api-secret=...
```

Il profilo `local` è già attivo in `application.properties`.

---

## Avvio

```bash
./mvnw spring-boot:run
```

L'app si avvia su `http://localhost:8080`.

All'avvio vengono creati automaticamente:
- 1 utente Admin (`admin@camping.it` / `Admin123!`)
- 10 Chalet (80 €/notte)
- 10 Piazzole (25 €/notte)

---

## Variabili d'ambiente

| Variabile | Descrizione | Default |
|-----------|-------------|---------|
| `DB_NAME` | Nome database PostgreSQL | `camping_manager_exam` |
| `DB_USERNAME` | Utente PostgreSQL | `postgres` |
| `DB_PASSWORD` | Password PostgreSQL | `postgres` |
| `JWT_SECRET` | Chiave segreta per i token JWT | valore di default incluso |
| `JWT_EXPIRATION_MS` | Durata token in millisecondi | `86400000` (24h) |
| `STRIPE_SECRET_KEY` | Chiave segreta Stripe | — |
| `STRIPE_WEBHOOK_SECRET` | Secret per verifica webhook Stripe | — |
| `OPENWEATHER_API_KEY` | API key OpenWeatherMap | — |
| `CLOUDINARY_CLOUD_NAME` | Nome cloud Cloudinary | — |
| `CLOUDINARY_API_KEY` | API key Cloudinary | — |
| `CLOUDINARY_API_SECRET` | API secret Cloudinary | — |
| `ADMIN_EMAIL` | Email admin iniziale | `admin@camping.it` |
| `ADMIN_PASSWORD` | Password admin iniziale | `Admin123!` |

---

## Struttura del database

9 tabelle:

| Tabella | Descrizione |
|---------|-------------|
| `users` | Tabella base (ereditarietà JOINED) |
| `admins` | Sotto-tipo Admin |
| `staffs` | Sotto-tipo Staff |
| `ospites` | Sotto-tipo Ospite (con `account_valid_until`) |
| `accommodations` | Chalet e piazzole |
| `soggiorni` | Prenotazioni alloggio |
| `bikes` | Bici disponibili al noleggio |
| `bike_rentals` | Noleggi bici |
| `payments` | Pagamenti Stripe |

---

## Endpoints principali

| Metodo | URL | Accesso |
|--------|-----|---------|
| POST | `/api/auth/login` | Pubblico |
| GET | `/api/auth/me` | Autenticato |
| PATCH | `/api/users/me/profile-image` | Autenticato |
| GET | `/api/accommodations` | Autenticato |
| GET | `/api/accommodations/available?checkIn=&checkOut=` | Autenticato |
| POST | `/api/accommodations` | Admin |
| POST | `/api/stays/checkin` | Staff/Admin |
| POST | `/api/stays/checkout/{id}` | Staff/Admin |
| GET | `/api/stays` | Autenticato |
| GET | `/api/bikes` | Autenticato |
| POST | `/api/bikes` | Admin |
| POST | `/api/bikes/rentals` | Ospite |
| GET | `/api/bikes/rentals` | Autenticato |
| PATCH | `/api/bikes/rentals/{id}/return` | Staff/Admin |
| POST | `/api/payments/checkout/{rentalId}` | Ospite |
| POST | `/api/payments/webhook` | Pubblico (Stripe) |
| GET | `/api/weather` | Autenticato |
| GET | `/api/stats` | Admin |

---

## Pagamenti Stripe (test)

Per testare in locale avviare la Stripe CLI:

```bash
stripe listen --forward-to http://localhost:8080/api/payments/webhook
```

Carta di test: `4242 4242 4242 4242` — qualsiasi data futura e CVC.

---

## Postman

La collection con tutte le request è inclusa nel file `camping-manager-exam.postman_collection.json` nella root del progetto.
