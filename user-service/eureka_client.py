import os
import py_eureka_client.eureka_client as eureka_client

def register_with_eureka():
    if os.getenv("RUNNING_IN_DOCKER") != "true":
        return

    print("Registering USER-SERVICE with Eureka...")

    eureka_client.init(
        eureka_server="http://discovery-service:8761/eureka/",
        app_name="USER-SERVICE",
        instance_port=8001,          # ✅ FIXED
        instance_host="user-service"
    )