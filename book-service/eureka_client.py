import os
import py_eureka_client.eureka_client as eureka_client

def register_with_eureka():
    if os.getenv("RUNNING_IN_DOCKER") != "true":
        return

    print("Registering BOOK-SERVICE with Eureka...")

    eureka_client.init(
        eureka_server="http://discovery-service:8761/eureka/",
        app_name="BOOK-SERVICE",
        instance_port=8002,          # ✅ MUST MATCH Docker port
        instance_host="book-service"
    )
