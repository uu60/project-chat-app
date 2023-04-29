# project-android-app-team

### 1 Set Up Database

1. Install MySQL on the server device. (Or you can change the jdbc url in application.yaml).
2. Create a database called team_app in MySQL.
3. Run the team_app.sql in this database.

### 2 Run Backend

1. Open spring-boot-chat-app-backend in IDEA.
2. Run `ChatAppBackendApplication.main`. (If MySQL is not on the same device, modify the JDBC url in application.yaml)

3. Check your server's IP and remember that.

### 3 Run Android

1. Open ChatApplication directory in Android Studio.
2. Open the `com.ph.chatapplication.utils.net.Requests` class and modify the `SERVER_IP` according to your server device.
3. Run the application and use it.