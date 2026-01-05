import time
import psycopg2
import os

print("⏳ Waiting for PostgreSQL...")

while True:
    try:
        conn = psycopg2.connect(
            host=os.getenv("DB_HOST"),
            port=os.getenv("DB_PORT", "5432"),
            dbname=os.getenv("DB_NAME"),
            user=os.getenv("DB_USER"),
            password=os.getenv("DB_PASSWORD"),
        )
        conn.close()
        print("✅ PostgreSQL is ready!")
        break
    except psycopg2.OperationalError:
        time.sleep(2)
