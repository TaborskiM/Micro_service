#!/usr/bin/env python
import os
import sys
import threading

def main():
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'user_service.settings')

    # Register with Eureka BEFORE runserver blocks
    if (
        os.getenv("RUNNING_IN_DOCKER") == "true"
        and len(sys.argv) > 1
        and sys.argv[1] == "runserver"
    ):
        from eureka_client import register_with_eureka
        threading.Thread(target=register_with_eureka, daemon=True).start()

    from django.core.management import execute_from_command_line
    execute_from_command_line(sys.argv)

if __name__ == '__main__':
    main()
