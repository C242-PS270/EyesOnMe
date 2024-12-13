# Documentation for the Caption Generation API - EyesOnMe

## Introduction
Dokumentasi ini menjelaskan API untuk fitur Caption Generation pada aplikasi, termasuk cara berinteraksi dengan endpoint API yang tersedia. API ini digunakan untuk mengunggah gambar, menghasilkan deskripsi dari gambar (caption), dan menyimpan metadata terkait ke Google Cloud Storage dan Firestore.

### Catatan:
- Tipe data:
  - `image` (file): Gambar yang diunggah.
  - `filename` (string): Nama file gambar.
  - `caption` (string): Caption hasil prediksi model.
  - `url` (string): URL gambar yang diunggah.
  - `timestamp` (string): Tanggal dan waktu dalam format ISO 8601.
- Saat menggunakan POST, semua data yang dibutuhkan harus disediakan.

## Endpoint API

### 1. Generate Caption
**POST** `/generate-caption`

#### Deskripsi
Endpoint ini digunakan untuk menghasilkan caption dari gambar yang diunggah. Caption dibuat berdasarkan model pembelajaran mesin dan metadata disimpan di Firestore.

#### Request
- **Header:**
  - `Content-Type: multipart/form-data`
- **Body:**
  - `image` (required): File gambar yang akan diunggah.

#### Response Success
- **Status Code:** 200
- **Response Body:**
  ```json
  {
      "image": "example.jpg",
      "caption": "A beautiful sunset over the mountains.",
      "url": "https://storage.googleapis.com/eyes-on-me.firebasestorage.app/uploads/example.jpg",
      "timestamp": "2023-12-13T15:45:00Z"
  }
  ```

#### Response Error
- **Status Code:** 400
- **Response Body:**
  ```json
  {
      "error": "No image provided"
  }
  ```
- **Status Code:** 500
- **Response Body:**
  ```json
  {
      "error": "Internal Server Error",
      "details": "..."  
  }
  ```

### Proses Internal
1. Gambar yang diunggah disimpan sementara di direktori lokal.
2. Fitur gambar diekstraksi menggunakan model dasar.
3. Model pembelajaran mesin menghasilkan caption berdasarkan fitur.
4. Gambar diunggah ke Google Cloud Storage dan metadata disimpan di Firestore.
5. Data metadata dikembalikan sebagai respons.

### Contoh Metadata di Firestore
- **Collection:** `images`
- **Document Example:**
  ```json
  {
      "filename": "example.jpg",
      "caption": "A beautiful sunset over the mountains.",
      "url": "https://storage.googleapis.com/eyes-on-me.firebasestorage.app/uploads/example.jpg",
      "timestamp": "2023-12-13T15:45:00Z"
  }
  ```

# Kesimpulan
API ini memungkinkan pengguna untuk mengunggah gambar dan mendapatkan deskripsi otomatis yang dihasilkan oleh model machine learning. Metadata setiap gambar disimpan di Firestore untuk keperluan pencatatan dan pengelolaan data lebih lanjut.
