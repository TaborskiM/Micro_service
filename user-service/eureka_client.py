import py_eureka_client.eureka_client as eureka_client

def register_with_eureka():
    eureka_client.init(
        eureka_server="http://discovery-service:8761/eureka/",
        app_name="user-service",
        instance_port=8001,
        instance_host="user-service"
    )