# MidPoint CLI Demo

A simple interactive CLI application wrapped in Docker that allows you to connect to MidPoint and execute commands.

---

## 🚀 Prerequisites

Make sure you have installed:

- Docker
- Bash (Linux/macOS or WSL on Windows)

Verify Docker:

```bash
docker --version
```

---

## 📦 Installation

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd <repo-folder>
```

### 2. Make the script executable

```bash
chmod +x midpoint-demo.sh
```

### 3. (Optional) Install globally

To run the CLI from anywhere in your system:

```bash
mv midpoint-demo.sh /usr/local/bin/midpoint-demo
chmod +x /usr/local/bin/midpoint-demo
```

After this, you can use:

```bash
midpoint-demo start
```

---

## ▶️ Running the Application

### 🔹 Start the application

```bash
./midpoint-demo.sh start
```

(or `midpoint-demo start` if installed globally)

What happens:

1. Docker image is built (if needed)
2. Container starts
3. Application launches
4. You are prompted for login credentials

Example:

```bash
Username:
Password:
Login successful
midpoint>
```

---

### 🔹 Stop the container

```bash
./midpoint-demo.sh stop
```

---

### 🔹 Delete the container

```bash
./midpoint-demo.sh delete
```

---

### 🔹 Restart the application

```bash
./midpoint-demo.sh restart
```

---

### 🔹 Check container status

```bash
./midpoint-demo.sh status
```

---

## 💻 Interactive CLI

After logging in, you can run commands:

```bash
midpoint> help
midpoint> <your-command>
midpoint> exit
```

---

## 📚 Use Cases

### 🔍 Search for a user by username

Search is currently supported by **username only**.

✅ Example:

```bash
midpoint>search --username borgia
```

Expected behavior:

- Displays matching users
- Shows basic attributes such as username, email, and name

---

### ✏️ Update a user

The `--username` parameter is **required** for updates.

✅ Example:

```bash
midpoint>update --username borgia --email new-email@leonardo-workshop.org
```

---

### 🛠️ Fields that can be updated

In this version of the product, the following fields are supported:

- `--email` → update email address
- `--givenName` → update first name
- `--familyName` → update last name

✅ More examples:

```bash
midpoint>update --username borgia --givenName Cesare
```

```bash
midpoint>update --username borgia --familyName Borgia
```

Expected behavior:

- Updates the specified user
- Confirms success or shows an error if the user is not found or input is invalid

---

## 🔒 Security Notes

- No credentials are stored in the script
- Username and password are entered interactively
- No sensitive data is passed via environment variables

---

## 🧠 Architecture Overview

| Component   | Responsibility       |
|-------------|----------------------|
| Bash script | Container lifecycle |
| Docker      | Runtime environment |
| Java CLI    | Login & commands    |

---

## ✅ Summary

- Install via script or globally
- Run with `start`
- Login interactively
- Search users by username
- Update user fields (email, givenName, familyName)
