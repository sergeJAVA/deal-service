# 🚀 Сервис для Управления Сделками

Этот проект представляет собой RESTful сервис для комплексного управления **сделками**, **контрагентами** и их **ролями**.  

## ⚙️ Технологии

- **Java 21+**
- **Spring Boot**
- **PostgreSQL**
- **Liquibase** (для инициализации схемы БД)
- **Docker** (опционально, для запуска PostgreSQL)
- **Apache POI**

---

## 📌 API Эндпоинты

### ➕ Создание сделки

`POST /deal/save`

```json
{
  "description": "Сделка №1",
  "agreementNumber": "1234-12345",
  "agreementDate": "01-01-2025",
  "agreementStartDt": "01-02-2025 00:00:00",
  "availabilityDate": "01-01-2026",
  "type": { "id": "CREDIT" },
  "sum": [
    {
      "value": 20000.00,
      "currency": "RUB",
      "isMain": true
    }
  ]
}
```

---

*Примечание*: Для изменения сделки, а не сохранения новой, требуется указать поле **"id"** в теле **JSON** и передать UUID сделки, которую нужно изменить.

### 🔄 Изменение статуса сделки

`PATCH /deal/change/status`

```json
{
  "dealId": "ID_СДЕЛКИ",
  "newStatusId": "ACTIVE"
}
```

---

### 👥 Добавление контрагента к сделке

`PUT /deal-contractor/save`

```json
{
  "dealId": "ID_СДЕЛКИ",
  "contractorId": "Contractor 1",
  "name": "ИП Петр Иванович",
  "inn": "123456791012",
  "main": true
}
```

---

### 🧩 Назначение роли контрагенту

`POST /contractor-to-role/add`

```json
{
  "dealContractorId": "ID_КОНТРАГЕНТА",
  "roleId": "BORROWER"
}
```

---

### 📤 Экспорт сделок по статусу

`POST /deal/search/export`

```json
{
  "statusIds": ["ACTIVE", "CLOSED"]
}
```

---

## 🧪 Тестирование API (Postman, cURL и т.д.)

### 1. Создание сделки

`POST http://localhost:8085/deal/save`

```json
{
  "description": "Сделка №1",
  "agreementNumber": "1234-12345",
  "agreementDate": "01-01-2025",
  "agreementStartDt": "01-02-2025 00:00:00",
  "availabilityDate": "01-01-2026",
  "type": { "id": "CREDIT" },
  "sum": [
    { 
      "value": 20000.00,
      "currency": "RUB",
      "isMain": true
    }
  ]
}
```

---

### 2. Изменение статуса

`PATCH http://localhost:8085/deal/change/status`

```json
{
  "dealId": "ID_СДЕЛКИ",
  "newStatusId": "ACTIVE"
}
```

---

### 3. Добавление контрагента

`PUT http://localhost:8085/deal-contractor/save`

```json
{
  "dealId": "ID_СДЕЛКИ",
  "contractorId": "Contractor 1",
  "name": "ИП Петр Иванович",
  "inn": "123456791012",
  "main": true
}
```

---

### 4. Назначение роли

`POST http://localhost:8085/contractor-to-role/add`

```json
{
  "dealContractorId": "ID_КОНТРАГЕНТА",
  "roleId": "BORROWER"
}
```

---

### 5. Экспорт по статусу

`POST http://localhost:8085/deal/search/export`

```json
{
  // Другие фильтры:
  //  "closeDtFrom": null,
  //  "closeDtTo": null,
  //  "borrowerSearch": "Контрагент",
  //  "warrantySearch": null,
  //  "sumValue": null,
  //  "sumCurrency": null,
  //  "agreementNumber": null,
  //  "agreementDateFrom": "01-01-2023",
  //  "agreementDateTo": "30-12-2025",
  //  "availabilityDateFrom": null,
  //  "availabilityDateTo": null,
  //  "typeIds": ["CREDIT", "OTHER"],
  "statusIds": ["ACTIVE", "CLOSED"]
}
```

---
