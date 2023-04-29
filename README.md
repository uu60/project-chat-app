# project-android-app-team

### 1 Set Up Database

1. Install and run MySQL on the server device. (Or you can change the jdbc url in application.yaml).
2. For convenience, install navicat first. Then open navicat and create a new MySQL Connection.
3. Use navicat to create a database called team_app in MySQL.
4. Use navicat to run the team_app.sql in this database.

### 2 Run Backend

1. Open spring-boot-chat-app-backend in IDEA.
2. Modify the JDBC configurations in application.yaml first, including url, username and password.
3. Run `ChatAppBackendApplication.main`.
4. Check your server's IP and remember that.

### 3 Run Android

1. Open ChatApplication directory in Android Studio.
2. Open the `com.ph.chatapplication.utils.net.Requests` class and modify the `SERVER_IP` according to your server device.
3. Run the application and use it.
