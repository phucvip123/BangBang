# Bang Bang Multiplayer Game

Bang Bang là dự án game bắn tank nhiều người chơi, gồm hai phần: **server** (Java console) và **client** (JavaFX GUI). Dự án sử dụng giao tiếp socket TCP và giao thức tùy chỉnh để truyền dữ liệu giữa client và server.

---

## Cấu trúc thư mục

```
Bang_bang/
├── client/
│   ├── src/main/java/com/shot_tank/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── ...
└── server/
    ├── src/main/java/com/short_tank/
    ├── src/main/resources/config.properties
    ├── pom.xml
    └── ...
```

---

## Hướng dẫn cài đặt & chạy

### Yêu cầu

- JDK 8 trở lên
- Maven
- JavaFX (đã tích hợp qua Maven)

### 1. Build server

```sh
cd server
mvn clean package
```

### 2. Chạy server

```sh
cd target
java -cp server-1.0-SNAPSHOT.jar com.short_tank.App
```

### 3. Build client

```sh
cd ../client
mvn clean package
```

### 4. Chạy client

```sh
mvn javafx:run
```
Hoặc:
```sh
cd target
java -jar client-1.0.jar
```

---

## Chức năng chính

- Đăng nhập: Nhập tên nhân vật để bắt đầu.
- Tạo phòng: Chủ phòng đặt tên và số lượng người chơi tối đa.
- Tham gia phòng: Xem danh sách phòng và tham gia.
- Sẵn sàng/Bắt đầu: Chủ phòng có thể bắt đầu trận đấu khi đủ người chơi.
- Di chuyển, bắn: Điều khiển tank bằng bàn phím và chuột.

---

## Công nghệ sử dụng

- JavaFX: Giao diện người dùng client.
- Java Socket: Giao tiếp mạng giữa client và server.
- Maven: Quản lý dự án và phụ thuộc.
- JUnit: Kiểm thử đơn vị cho server.

---

## Cấu hình

File cấu hình nằm ở:
- Server: `server/src/main/resources/config.properties`
- Client: `client/src/main/resources/config.properties`

Ví dụ:
```
host=localhost
port=6969
max_room=10
max_members_in_room=2
```

---

## Tác giả

Nguyễn Văn Phúc

---

## License

MIT License

---

## Liên hệ

Nếu có vấn đề hoặc góp ý, vui lòng liên hệ qua email hoặc tạo issue trên repository.

---

**Chúc bạn chơi game
